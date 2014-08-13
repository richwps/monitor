/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsos.ecs.richwps.wpsmonitor.control.impl;

import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControl;
import de.hsos.ecs.richwps.wpsmonitor.control.SchedulerControl;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * Implementation of the MonitorControl interface. This implementation tries to
 * work like a request-response principe. That means, that every method call
 * creates a new DataAccess-instance.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControlImpl implements MonitorControl {

    private static final Logger LOG = LogManager.getLogger();

    private final SchedulerControl schedulerControl;
    private final QosDaoFactory qosDaoFactory;
    private final WpsDaoFactory wpsDaoFactory;
    private final WpsProcessDaoFactory wpsProcessDaoFactory;
    private final MonitorEventHandler eventHandler;
    private final MonitorControlValidator validator;

    /**
     * Constructor.
     *
     * @param scheduler {@link SchedulerControl} instance.
     * @param eventHandler {@link MonitorEventHandler} instance.
     * @param qosDao {@link QosDaoFactory} instance.
     * @param wpsDao {@link WpsDaoFactory} instance.
     * @param wpsProcessDao {@link WpsProcessDaoFactory} instance.
     */
    public MonitorControlImpl(final SchedulerControl scheduler, final MonitorEventHandler eventHandler,
            final QosDaoFactory qosDao, final WpsDaoFactory wpsDao, final WpsProcessDaoFactory wpsProcessDao) {

        this.schedulerControl = Validate.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Validate.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Validate.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Validate.notNull(wpsProcessDao, "wpsProcessDao");
        this.eventHandler = Validate.notNull(eventHandler, "eventHandler");
        
        this.validator = new MonitorControlValidator(3L, 255L);
        
        initMonitorControlEvents();
    }

    /**
     * Register all necessary events which are fired by the MonitorControlImpl
     */
    private void initMonitorControlEvents() {
        String[] eventNames = new String[]{
            "monitorcontrol.pauseMonitoring",
            "monitorcontrol.resumeMonitoring",
            "monitorcontrol.deleteProcess",
            "monitorcontrol.deleteWps",
            "monitorcontrol.updateWps",
            "monitorcontrol.setTestRequest",
            "monitorcontrol.createAndScheduleProcess",
            "monitorcontrol.createWps",
            "monitorcontrol.deleteTrigger",
            "monitorcontrol.saveTrigger"
        };

        for (String eventName : eventNames) {
            eventHandler.registerEvent(eventName);
        }
    }

    @Override
    public TriggerConfig saveTrigger(final String wpsIdentifier, final String processIdentifier, TriggerConfig config) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);
        
        TriggerConfig newConfig = null;
        try {
            if (isProcessExists(wpsIdentifier, processIdentifier)) {
                if (config.getTriggerKey() == null) {
                    JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);
                    newConfig = schedulerControl.addTriggerToJob(jobKey, config);
                } else {
                    newConfig = schedulerControl.updateTrigger(config);
                }

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.saveTrigger", config));
            }
        } catch (SchedulerException ex) {
            LOG.error("Can't save trigger because of Scheduler Exception.", ex);
        }

        return newConfig;
    }

    @Override
    public List<TriggerConfig> getTriggers(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);
        
        JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);
        List<TriggerConfig> result = new ArrayList<>();
        try {
            List<TriggerKey> triggerKeysOfJob = schedulerControl.getTriggerKeysOfJob(jobKey);

            for (TriggerKey triggerKey : triggerKeysOfJob) {
                result.add(schedulerControl.getConfigOfTrigger(triggerKey));
            }

            return result;
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);

            return null;
        }
    }

    @Override
    public Boolean deleteTrigger(final TriggerConfig config) {
        try {
            schedulerControl.removeTrigger(config);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.deleteTrigger", config));
        } catch (SchedulerException ex) {
            LOG.warn("Can't delete Trigger because of Scheduler Exception.", ex);

            return false;
        }

        return true;
    }

    @Override
    public Boolean createWps(final String wpsIdentifier, final URI uri) {
        validator.validateStringParam(wpsIdentifier);
        Validate.notNull(uri, "uri");

        WpsEntity wps = new WpsEntity(wpsIdentifier, uri);
        Boolean result = false;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            result = wpsDao.persist(wps);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.createWps", wps));
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return result;
    }

    @Override
    public Boolean createAndScheduleProcess(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        Boolean isPersisted = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {

            if (isWpsExists(wpsIdentifier) && !isProcessExists(wpsIdentifier, processIdentifier)) {

                WpsProcessEntity process = new WpsProcessEntity(processIdentifier, getWps(wpsIdentifier));
                isPersisted = wpsProcessDao.persist(process);

                if (isPersisted) {
                    scheduleProcess(process);
                }
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return isPersisted;
    }

    private void scheduleProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityDeep(processEntity);
        
        try {
            schedulerControl.addWpsAsJob(processEntity);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.createAndScheduleProcess", processEntity));
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        }
    }

    @Override
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        Boolean exists = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {

            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);
            exists = (process != null);

            if (exists) {
                process.setRawRequest(testRequest);
                wpsProcessDao.update(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.setTestRequest", process));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return exists;
    }

    @Override
    public WpsEntity updateWps(final String oldWpsIdentifier, final String newWpsIdentifier, final URI newUri) {
        validator.validateStringParam(oldWpsIdentifier, newWpsIdentifier);
        Validate.notNull(newUri, "newUri");

        WpsEntity wps = getWps(oldWpsIdentifier);

        if (wps == null || (wps.getIdentifier().equals(newWpsIdentifier) && wps.getUri().equals(newUri))) {
            return wps;
        }

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            wps.setIdentifier(newWpsIdentifier);
            wps.setUri(newUri);

            wpsDao.update(wps);
            schedulerControl.updateJobsWpsGroupName(oldWpsIdentifier, newWpsIdentifier);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.updateWps", new String[]{oldWpsIdentifier, newWpsIdentifier}));
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        } catch (SchedulerException ex) {
            LOG.error("Scheduler exception while updating a wps.", ex);
        }

        return wps;
    }

    /*
     * The delete process can be called logically here, but the cascade delete
     * behavior is already implemented in the specific data access
     * implementations. If the call deleteProcess is called here, there is an
     * unnecessary redundancy.
     */
    @Override
    public Boolean deleteWps(final String wpsIdentifier) {
        Boolean deleteable = false;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {

            WpsEntity wpsEntity = getWps(wpsIdentifier);
            deleteable = (wpsEntity != null);

            if (deleteable) {
                wpsDao.remove(wpsEntity);
                removeWpsJob(wpsEntity);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteWps", wpsEntity));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return deleteable;
    }

    private void removeWpsJob(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);
        
        try {
            schedulerControl
                    .removeWpsJobs(wpsEntity.getIdentifier());
        } catch (SchedulerException ex) {
            LOG.error("MonitorControl: {}", ex);
        }
    }

    @Override
    public Boolean deleteProcess(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        Boolean deleteable = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {

            WpsProcessEntity process = wpsProcessDao
                    .find(wpsIdentifier, processIdentifier);
            deleteable = (process != null);

            if (deleteable) {
                JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

                wpsProcessDao.remove(process);
                schedulerControl.removeJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteProcess", process));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        } catch (SchedulerException ex) {
            LOG.error("Can't delete job of process {}.", processIdentifier, ex);
        }

        return deleteable;
    }

    @Override
    public List<WpsEntity> getWpsList() {
        List<WpsEntity> wpsList = null;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            wpsList = wpsDao.getAll();
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return wpsList;
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(final String identifier) {
        validator.validateStringParam(identifier);

        List<WpsProcessEntity> processes = null;

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            processes = wpsProcessDao.getAll(identifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return processes;
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier) {
        return getMeasuredData(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier, final Range range) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        List<MeasuredDataEntity> measuredData = null;

        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            measuredData = qosDao
                    .getByProcess(wpsIdentifier, processIdentifier, range);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }

        return measuredData;
    }

    @Override
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        try {
            WpsProcessEntity find = getWpsProcessEntity(wpsIdentifier, processIdentifier);

            if (find != null) {
                JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

                return schedulerControl.isPaused(jobKey);
            }
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        }

        return false;
    }

    @Override
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (process != null) {
                schedulerControl.resume(new JobKey(processIdentifier, wpsIdentifier));
                process.setWpsException(false);

                wpsProcessDao.update(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.resumeMonitoring", process));

                LOG.debug("MonitorControl: resuming monitoring of WPS Process {}.{}", wpsIdentifier, processIdentifier);
            }

        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }
    }

    public SchedulerControl getSchedulerControl() {
        return schedulerControl;
    }

    @Override
    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier) {
        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier, final Date olderAs) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            qosDao.deleteByProcess(wpsIdentifier, processIdentifier, olderAs);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }
    }

    @Override
    public void pauseMonitoring(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);
        JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

        try {
            if (schedulerControl.isJobRegistred(jobKey)) {
                WpsProcessEntity process = getWpsProcessEntity(wpsIdentifier, processIdentifier);

                schedulerControl.pauseJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", process));
            }
        } catch (SchedulerException ex) {
            LOG.error("Can't pause the monitoring of process {} because of scheduler exception.", processIdentifier, ex);
        }
    }

    @Override
    public void deleteMeasuredData(final Date olderAs) {
        Validate.notNull(olderAs, "olderAs");

        try {
            QosDataAccess qosDao = qosDaoFactory.create();
            qosDao.deleteAllOlderAs(olderAs);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }
    }

    @Override
    public Boolean createWps(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);

        return createWps(wpsEntity.getIdentifier(), wpsEntity.getUri());
    }

    @Override
    public Boolean createAndScheduleProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return createAndScheduleProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public TriggerConfig saveTrigger(final WpsProcessEntity processEntity, final TriggerConfig config) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return saveTrigger(wpsIdentifier, processIdentifier, config);
    }

    @Override
    public Boolean setTestRequest(final WpsProcessEntity processEntity, final String testRequest) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return setTestRequest(wpsIdentifier, processIdentifier, testRequest);
    }

    @Override
    public WpsEntity updateWps(final String oldWpsIdentifier, final WpsEntity newWps) {
        validator.validateWpsEntity(newWps);

        return updateWps(oldWpsIdentifier, newWps.getIdentifier(), newWps.getUri());
    }

    @Override
    public Boolean deleteWps(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);

        return deleteWps(wpsEntity.getIdentifier());
    }

    @Override
    public Boolean deleteProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return deleteProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public Boolean isPausedMonitoring(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return isPausedMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void resumeMonitoring(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        resumeMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void pauseMonitoring(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        pauseMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);

        return getProcessesOfWps(wpsEntity.getIdentifier());
    }

    @Override
    public List<TriggerConfig> getTriggers(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getTriggers(wpsIdentifier, processIdentifier);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity) {
        return getMeasuredData(processEntity, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity, final Range range) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getMeasuredData(wpsIdentifier, processIdentifier, range);
    }

    @Override
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity, final Date olderAs) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier, olderAs);
    }

    @Override
    public Boolean isProcessExists(final String wpsIdentifier, final String processIdentifier) {
        return getWpsProcessEntity(wpsIdentifier, processIdentifier) != null;
    }

    @Override
    public Boolean isProcessExists(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return isProcessExists(wpsIdentifier, processIdentifier);
    }

    private WpsProcessEntity getWpsProcessEntity(final String wpsIdentifier, final String processIdentifier) {
        validator.validateStringParam(wpsIdentifier, processIdentifier);

        WpsProcessEntity result = null;

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            result = wpsProcessDao.find(wpsIdentifier, processIdentifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return result;
    }

    @Override
    public Boolean isWpsExists(final String wpsIdentifier) {
        return getWps(wpsIdentifier) != null;
    }

    private Boolean isWpsExists(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity); 

        return isWpsExists(wpsEntity.getIdentifier());
    }

    private WpsEntity getWps(final String wpsIdentifier) {
        validator.validateStringParam(wpsIdentifier);

        WpsEntity result = null;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            result = wpsDao.find(wpsIdentifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return result;
    }

    private JobKey getJobKey(final String wpsIdentifier, final String processIdentifier) {
        return new JobKey(processIdentifier, wpsIdentifier);
    }

    private JobKey getJobKey(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getJobKey(wpsIdentifier, processIdentifier);
    }

    @Override
    public Boolean isProcessScheduled(final WpsProcessEntity processEntity) {
        validator.validateProcessEntityFlat(processEntity);

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return isProcessScheduled(wpsIdentifier, processIdentifier);
    }

    @Override
    public Boolean isProcessScheduled(final String wpsIdentifier, final String processIdentifier) {
        JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);
        Boolean result = false;

        try {
            result = schedulerControl.isJobRegistred(jobKey);
        } catch (SchedulerException ex) {
            LOG.warn("Can't check if a process is scheduled.", ex);
        }

        return result;
    }
}
