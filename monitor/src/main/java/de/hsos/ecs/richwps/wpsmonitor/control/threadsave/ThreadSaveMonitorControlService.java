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
package de.hsos.ecs.richwps.wpsmonitor.control.threadsave;

import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import de.hsos.ecs.richwps.wpsmonitor.control.SchedulerControlService;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * Implementation of the MonitorControlService interface. This implementation tries to
 * work like a request-response principe. That means, that every method call
 * creates a new DataAccess-instance.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ThreadSaveMonitorControlService implements MonitorControlService {

    private static final Logger LOG = LogManager.getLogger();

    private final MonitorEventHandler eventHandler;
    private final QosDaoFactory qosDaoFactory;
    private final SchedulerControlService schedulerControl;
    private final MonitorControlValidator validator;
    private final WpsDaoFactory wpsDaoFactory;
    private final WpsProcessDaoFactory wpsProcessDaoFactory;

    /**
     * Constructor.
     *
     * @param scheduler {@link SchedulerControlService} instance.
     * @param eventHandler {@link MonitorEventHandler} instance.
     * @param qosDao {@link QosDaoFactory} instance.
     * @param wpsDao {@link WpsDaoFactory} instance.
     * @param wpsProcessDao {@link WpsProcessDaoFactory} instance.
     */
    public ThreadSaveMonitorControlService(final SchedulerControlService scheduler, final MonitorEventHandler eventHandler,
            final QosDaoFactory qosDao, final WpsDaoFactory wpsDao, final WpsProcessDaoFactory wpsProcessDao) {

        this.schedulerControl = Validate.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Validate.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Validate.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Validate.notNull(wpsProcessDao, "wpsProcessDao");
        this.eventHandler = Validate.notNull(eventHandler, "eventHandler");

        this.validator = new MonitorControlValidator(3L, 255L);

        initMonitorControlEvents();
    }

    private void checkEndpointUnique(final List<WpsEntity> wpsEntities, final URL compare) {
        if (isWpsEndpoindAlreadyExists(wpsEntities, compare)) {
            throw new IllegalArgumentException("A WPS Server with the same endpoint already exists.");
        }
    }

    private JobKey getJobKey(final Long wpsId, final String processIdentifier) {
        return new JobKey(processIdentifier, wpsId.toString());
    }

    private JobKey getJobKey(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString(), processIdentifier);
        
        WpsEntity wps = getWps(endpoint);
        JobKey result = null;

        if (wps != null) {
            result = getJobKey(wps.getId(), processIdentifier);
        }

        return result;
    }

    private JobKey getJobKey(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        Long wpsId = processEntity.getWps()
                .getId();
        String processIdentifier = processEntity
                .getIdentifier();

        JobKey result;
        if (wpsId == null) {
            result = getJobKey(processEntity.getWps().getEndpoint(), processIdentifier);
        } else {
            result = getJobKey(wpsId, processIdentifier);
        }

        return result;
    }

    @Override
    public WpsEntity getWps(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            return wpsDao.find(endpoint);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }
    }

    @Override
    public WpsEntity getWps(final Long wpsId) {
        Validate.notNull(wpsId, "wpsid");

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            return wpsDao.find(wpsId);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }
    }

    private WpsProcessEntity getWpsProcessEntity(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString(), processIdentifier);

        WpsProcessEntity result = null;

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            result = wpsProcessDao.find(endpoint, processIdentifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return result;
    }

    private WpsProcessEntity getWpsProcessEntity(final Long wpsId, final String processIdentifier) {
        validator.validateStringParam(processIdentifier);

        WpsProcessEntity result = null;

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            result = wpsProcessDao.find(wpsId, processIdentifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return result;
    }

    /**
     * Register all necessary events which are fired by the ThreadSaveMonitorControlService
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

    private Boolean isWpsEndpoindAlreadyExists(final List<WpsEntity> elements, final URL compare) {
        Boolean result = false;

        for (WpsEntity e : elements) {
            if (e.getEndpoint().equals(compare)) {
                return true;
            }
        }

        return result;
    }

    private void removeWpsJob(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity, true);

        try {
            schedulerControl
                    .removeWpsJobs(wpsEntity.getId());
        } catch (SchedulerException ex) {
            LOG.error("MonitorControl: {}", ex);
        }
    }

    private void scheduleProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        try {
            schedulerControl.addWpsAsJob(processEntity);
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        }
    }

    @Override
    public Boolean createAndScheduleProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        if (processEntity.getId() != null && processEntity.getId() > 0) {
            return createAndScheduleProcess(
                    processEntity.getWps().getId(),
                    processEntity.getIdentifier()
            );
        } else {
            return createAndScheduleProcess(
                    processEntity.getWps().getEndpoint(),
                    processEntity.getIdentifier()
            );
        }
    }

    @Override
    public Boolean createAndScheduleProcess(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());
        WpsEntity wps = getWps(endpoint);

        return wps != null && createAndScheduleProcess(wps, processIdentifier);
    }

    @Override
    public final Boolean createAndScheduleProcess(final Long wpsId, final String processIdentifier) {
        WpsEntity wps = getWps(wpsId);

        return wps != null && createAndScheduleProcess(wps, processIdentifier);
    }

    private Boolean createAndScheduleProcess(final WpsEntity wps, final String processIdentifier) {

        validator.validateStringParam(processIdentifier);
        validator.validateWpsEntity(wps);

        WpsEntity usedWpsEntity = wps;

        if (wps.getId() == null) {
            if (!createWps(wps)) {
                throw new IllegalArgumentException("WPS ID was null and the attempt to create a new WPS entry with the given endpoint has failed.");
            } else {
                usedWpsEntity = getWps(wps.getEndpoint());
            }
        }

        Boolean isPersisted = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            WpsProcessEntity process = new WpsProcessEntity(processIdentifier, usedWpsEntity);
            isPersisted = wpsProcessDao.persist(process);

            if (isPersisted) {
                scheduleProcess(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.createAndScheduleProcess", process));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return isPersisted;
    }

    @Override
    public Boolean createWps(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);
        return createWps(wpsEntity.getEndpoint());
    }

    @Override
    public Boolean createWps(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());

        WpsEntity wps = new WpsEntity(endpoint);
        Boolean result = false;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            checkEndpointUnique(wpsDao.getAll(), endpoint);
            result = wpsDao.persist(wps);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.createWps", wps));
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return result;
    }

    @Override
    public void deleteMeasuredData(final Date olderAs) {
        Validate.notNull(olderAs, "olderAs");

        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            qosDao.deleteAllOlderAs(olderAs);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }
    }

    @Override
    public void deleteMeasuredDataOfProcess(final URL endpoint, final String processIdentifier) {
        deleteMeasuredDataOfProcess(endpoint, processIdentifier, null);
    }

    @Override
    public void deleteMeasuredDataOfProcess(final Long wpsId, final String processIdentifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity) {
        deleteMeasuredDataOfProcess(processEntity, null);
    }

    @Override
    public void deleteMeasuredDataOfProcess(final URL endpoint, final String processIdentifier, final Date olderAs) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());

        WpsEntity wps = getWps(endpoint);

        if (wps != null) {
            deleteMeasuredDataOfProcess(wps.getId(), processIdentifier, olderAs);
        }
    }

    @Override
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity, final Date olderAs) {
        validator.validateProcessEntity(processEntity);

        final Long wpsId = processEntity.getWps().getId();
        if (wpsId != null && wpsId > 0) {
            deleteMeasuredDataOfProcess(
                    wpsId,
                    processEntity.getIdentifier(),
                    olderAs
            );
        } else {
            deleteMeasuredDataOfProcess(
                    processEntity.getWps().getEndpoint(),
                    processEntity.getIdentifier(),
                    olderAs
            );
        }
    }

    @Override
    public void deleteMeasuredDataOfProcess(final Long wpsId, final String processIdentifier, final Date olderAs) {
        Validate.notNull(wpsId, "WPS ID");
        validator.validateStringParam(processIdentifier);

        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            qosDao.deleteByProcess(wpsId, processIdentifier, olderAs);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }
    }

    @Override
    public Boolean deleteProcess(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        Boolean deleteable = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            WpsProcessEntity founded = processEntity;
            WpsEntity wps = processEntity.getWps();
            Long wpsId = wps.getId();

            // Check the IDS
            // this is necessary to prevent unecessary DB queries
            if (processEntity.getId() == null || processEntity.getId() <= 0
                    || wpsId == null || wpsId <= 0) {
                if (wpsId != null && wpsId > 0) {
                    founded = wpsProcessDao.find(wpsId, processEntity.getIdentifier());
                } else {
                    founded = wpsProcessDao.find(wps.getEndpoint(), processEntity.getIdentifier());
                }
            }

            deleteable = (founded != null);
            if (deleteable) {
                JobKey jobKey = getJobKey(founded);

                schedulerControl.removeJob(jobKey);
                wpsProcessDao.remove(founded);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteProcess", processEntity));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        } catch (SchedulerException ex) {
            LOG.error("Can't delete job of process {}.", processEntity, ex);
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(final Long wpsId, final String processIdentifier) {
        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);

        return wpsProcessEntity != null && deleteProcess(wpsProcessEntity);
    }

    @Override
    public Boolean deleteProcess(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());
        
        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(endpoint, processIdentifier);

        return wpsProcessEntity != null && deleteProcess(wpsProcessEntity);
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
    public Boolean deleteWps(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        WpsEntity wps = getWps(endpoint);

        return wps != null && deleteWps(wps);
    }

    @Override
    public Boolean deleteWps(final Long wpsId) {
        WpsEntity wps = getWps(wpsId);

        return wps != null && deleteWps(wps);
    }

    /*
     * The delete process can be called logically here, but the cascade delete
     * behavior is already implemented in the specific data access
     * implementations. If the call deleteProcess is called here, there is an
     * unnecessary redundancy.
     */
    @Override
    public Boolean deleteWps(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);

        WpsEntity founded = wpsEntity;
        Boolean deleteable = false;

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            if (founded.getId() == null || founded.getId() <= 0) {
                founded = wpsDao.find(founded.getEndpoint());
            }

            deleteable = (founded != null);
            if (deleteable) {
                wpsDao.remove(founded);
                removeWpsJob(founded);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteWps", wpsEntity));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return deleteable;
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final URL endpoint, final String processIdentifier) {
        return getMeasuredData(endpoint, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final URL endpoint, final String processIdentifier, final Range range) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString(), processIdentifier);

        List<MeasuredDataEntity> measuredData = null;
        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            measuredData = qosDao
                    .getByProcess(endpoint, processIdentifier, range);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }

        return measuredData;
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity) {
        return getMeasuredData(processEntity, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity, final Range range) {
        validator.validateProcessEntity(processEntity);

        Long wpsId = processEntity.getWps().getId();
        if (wpsId != null && wpsId > 0) {
            return getMeasuredData(
                    wpsId,
                    processEntity.getIdentifier(),
                    range
            );
        } else {
            return getMeasuredData(
                    processEntity.getWps().getEndpoint(),
                    processEntity.getIdentifier(),
                    range
            );
        }
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final Long wpsId, final String processIdentifier) {
        return getMeasuredData(wpsId, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(final Long wpsId, final String processIdentifier, final Range range) {
        Validate.notNull(wpsId, "wpsId");
        validator.validateStringParam(processIdentifier);

        List<MeasuredDataEntity> measuredData = null;
        try (QosDataAccess qosDao = qosDaoFactory.create()) {
            measuredData = qosDao
                    .getByProcess(wpsId, processIdentifier, range);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create qosDao. Execution aborted.", ex);
        }

        return measuredData;
    }

    @Override
    public WpsProcessEntity getProcess(final URL endpoint, final String identifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        validator.validateStringParam(identifier);
        
        WpsProcessEntity result = null;
        try(WpsProcessDataAccess processDao = wpsProcessDaoFactory.create()) {
            result = processDao.find(endpoint, identifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create processDao. Execution aborted.", ex);
        }
        
        return result;
    }
    
    @Override
    public WpsProcessEntity getProcess(final Long wpsId, final String identifier) {
        Validate.notNull(wpsId, "endpoint");
        validator.validateStringParam(identifier);
        
        WpsProcessEntity result = null;
        try(WpsProcessDataAccess processDao = wpsProcessDaoFactory.create()) {
            result = processDao.find(wpsId, identifier);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create processDao. Execution aborted.", ex);
        }
        
        return result;
    }

    @Override
    public List<WpsProcessEntity> getProcesses(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        WpsEntity wps = getWps(endpoint);

        if (wps == null) {
            return null;
        }

        return getProcesses(wps);
    }

    @Override
    public List<WpsProcessEntity> getProcesses(final Long wpsId) {
        WpsEntity wps = getWps(wpsId);

        if (wps == null) {
            return null;
        }

        return getProcesses(wps);
    }

    @Override
    public List<WpsProcessEntity> getProcesses(final WpsEntity wpsEntity) {
        validator.validateWpsEntity(wpsEntity);

        List<WpsProcessEntity> processes = null;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            Long wpsId = wpsEntity.getId();
            if (wpsId != null && wpsId > 0) {
                processes = wpsProcessDao.getAll(wpsId);
            } else {
                processes = wpsProcessDao.getAll(wpsEntity.getEndpoint());
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return processes;
    }

    public SchedulerControlService getSchedulerControl() {
        return schedulerControl;
    }

    @Override
    public List<TriggerConfig> getTriggers(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());
        
        WpsEntity wps = getWps(endpoint);

        if (wps == null) {
            return null;
        }

        return getTriggers(wps, processIdentifier);
    }

    @Override
    public List<TriggerConfig> getTriggers(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "wpsId");
        WpsEntity wps = getWps(wpsId);

        if (wps == null) {
            return null;
        }

        return getTriggers(wps, processIdentifier);
    }

    @Override
    public List<TriggerConfig> getTriggers(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        Long wpsId = processEntity.getWps().getId();
        if (wpsId != null && wpsId > 0) {
            return getTriggers(
                    wpsId,
                    processEntity.getIdentifier()
            );
        } else {
            return getTriggers(
                    processEntity.getWps().getEndpoint(),
                    processEntity.getIdentifier()
            );
        }
    }

    private List<TriggerConfig> getTriggers(final WpsEntity wps, final String processIdentifier) {
        validator.validateStringParam(processIdentifier);
        validator.validateWpsEntity(wps);

        List<TriggerConfig> result = new ArrayList<>();
        JobKey jobKey = getJobKey(wps.getId(), processIdentifier);

        try {
            List<TriggerKey> triggerKeysOfJob = schedulerControl.getTriggerKeysOfJob(jobKey);

            for (TriggerKey triggerKey : triggerKeysOfJob) {
                result.add(schedulerControl.getConfigOfTrigger(triggerKey));
            }
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        }

        return result;
    }

    @Override
    public Long getWpsId(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        Long result = null;
        
        try(WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            WpsEntity find = wpsDao.find(endpoint);
            
            if(find != null) {
                result = find.getId();
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }
        
        return result;
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
    public Long getWpsProcessId(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());
        
        Long result = null;
        try(WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            WpsProcessEntity find = wpsProcessDao.find(endpoint, processIdentifier);
            
            if(find != null) {
                result = find.getId();
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }
        
        return result;
    }

    @Override
    public Boolean isMonitoringPaused(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        WpsEntity wps = getWps(endpoint);

        return wps != null && isMonitoringPaused(wps.getId(), processIdentifier);
    }

    @Override
    public Boolean isMonitoringPaused(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        Long wpsId = processEntity.getWps().getId();
        if (wpsId != null && wpsId > 0) {
            return isMonitoringPaused(
                    wpsId,
                    processEntity.getIdentifier()
            );
        } else {
            return isMonitoringPaused(
                    processEntity.getWps().getEndpoint(),
                    processEntity.getIdentifier()
            );
        }
    }

    @Override
    public Boolean isMonitoringPaused(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "wpsId");
        validator.validateStringParam(processIdentifier);

        try {
            JobKey jobKey = getJobKey(wpsId, processIdentifier);
            return schedulerControl.isPaused(jobKey);
        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        }

        return false;
    }

    @Override
    public Boolean isProcessExists(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");  
        validator.validateStringParam(processIdentifier, endpoint.toString());

        return getWpsProcessEntity(endpoint, processIdentifier) != null;
    }

    @Override
    public Boolean isProcessExists(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        return isProcessExists(
                processEntity.getWps().getEndpoint(),
                processEntity.getIdentifier()
        );
    }

    @Override
    public Boolean isProcessExists(final Long wpsId, final String processIdentifier) {
        validator.validateStringParam(processIdentifier);
        Validate.notNull(wpsId, "WPS ID");

        return getWpsProcessEntity(wpsId, processIdentifier) != null;
    }

    @Override
    public Boolean isProcessScheduled(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        JobKey jobKey = getJobKey(processEntity);

        if (jobKey == null) {
            throw new IllegalArgumentException("Can't fetch JobKey. The given WPS is unregistred in the monitor (and was not fetchable by endpoint).");
        }

        Boolean result = false;

        try {
            result = schedulerControl.isJobRegistred(jobKey);
        } catch (SchedulerException ex) {
            LOG.warn("Can't check if a process is scheduled.", ex);
        }

        return result;
    }

    @Override
    public Boolean isProcessScheduled(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(endpoint, processIdentifier);

        return wpsProcessEntity != null && isProcessScheduled(wpsProcessEntity);
    }

    @Override
    public Boolean isProcessScheduled(final Long wpsId, final String processIdentifier) {
        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);

        return wpsProcessEntity != null && isProcessScheduled(wpsProcessEntity);
    }

    @Override
    public Boolean isWpsExists(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(endpoint.toString());
        
        return getWps(endpoint) != null;
    }

    @Override
    public Boolean isWpsExists(final Long wpsId) {
        return getWps(wpsId) != null;
    }

    @Override
    public void pauseMonitoring(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());

        WpsProcessEntity processEntity = getWpsProcessEntity(endpoint, processIdentifier);

        if (processEntity != null) {
            pauseMonitoring(processEntity);
        }
    }

    @Override
    public void pauseMonitoring(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        JobKey jobKey = getJobKey(processEntity);
        try {
            if (schedulerControl.isJobRegistred(jobKey)) {
                schedulerControl.pauseJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", processEntity));
            }
        } catch (SchedulerException ex) {
            LOG.error("Can't pause the monitoring of process {} because of scheduler exception.", processEntity.getIdentifier(), ex);
        }
    }

    @Override
    public void pauseMonitoring(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "WPS ID");
        validator.validateStringParam(processIdentifier);

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);

        if (wpsProcessEntity != null) {
            pauseMonitoring(wpsProcessEntity);
        }
    }

    @Override
    public void resumeMonitoring(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(endpoint, processIdentifier);

        if (wpsProcessEntity != null) {
            resumeMonitoring(wpsProcessEntity);
        }
    }

    @Override
    public void resumeMonitoring(final WpsProcessEntity processEntity) {
        validator.validateProcessEntity(processEntity);

        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
            WpsProcessEntity usedProcessEntity = processEntity;

            if (processEntity.getId() == null || processEntity.getWps().getId() == null) {
                usedProcessEntity = wpsProcessDao.find(
                        processEntity.getWps().getEndpoint(),
                        processEntity.getIdentifier()
                );
            }

            if (usedProcessEntity != null) {
                schedulerControl.resume(getJobKey(usedProcessEntity));
                usedProcessEntity.setWpsException(false);

                wpsProcessDao.update(usedProcessEntity);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.resumeMonitoring", usedProcessEntity));

                LOG.debug("MonitorControl: resuming monitoring of WPS Process {}", usedProcessEntity);
            }

        } catch (SchedulerException ex) {
            LOG.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }
    }

    @Override
    public void resumeMonitoring(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "WPS ID");
        validator.validateStringParam(processIdentifier);

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);

        if (wpsProcessEntity != null) {
            resumeMonitoring(wpsProcessEntity);
        }
    }

    @Override
    public TriggerConfig saveTrigger(final URL endpoint, final String processIdentifier, final TriggerConfig config) {
        Validate.notNull(endpoint, "endpoint");
        validator.validateStringParam(processIdentifier, endpoint.toString());        

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(endpoint, processIdentifier);

        if (wpsProcessEntity == null) {
            return null;
        }

        return saveTrigger(wpsProcessEntity, config);
    }

    @Override
    public TriggerConfig saveTrigger(final WpsProcessEntity processEntity, final TriggerConfig config) {
        validator.validateProcessEntity(processEntity);

        TriggerConfig newConfig = null;
        try {
            if (config.getTriggerKey() == null) {
                JobKey jobKey = getJobKey(processEntity);
                newConfig = schedulerControl.addTriggerToJob(jobKey, config);
            } else {
                newConfig = schedulerControl.updateTrigger(config);
            }

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.saveTrigger", config));
        } catch (SchedulerException ex) {
            LOG.error("Can't save trigger because of Scheduler Exception.", ex);
        }

        return newConfig;
    }

    @Override
    public TriggerConfig saveTrigger(final Long wpsId, final String processIdentifier, final TriggerConfig config) {
        validator.validateStringParam(processIdentifier);
        Validate.notNull(wpsId, "WPS ID");

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);

        if (wpsProcessEntity == null) {
            return null;
        }

        return saveTrigger(wpsProcessEntity, config);
    }

    @Override
    public Boolean setTestRequest(final URL endpoint, final String processIdentifier, final String testRequest) {
        validator.validateStringParam(processIdentifier);

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(endpoint, processIdentifier);

        return wpsProcessEntity != null && setTestRequest(wpsProcessEntity, testRequest);
    }

    @Override
    public Boolean setTestRequest(final WpsProcessEntity processEntity, final String testRequest) {
        validator.validateProcessEntity(processEntity);

        Boolean exists = false;
        try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {

            WpsProcessEntity usedProcessEntity = processEntity;

            if (processEntity.getId() == null || processEntity.getWps().getId() == null) {
                usedProcessEntity = wpsProcessDao.find(processEntity.getWps().getEndpoint(), processEntity.getIdentifier());
            }

            exists = (usedProcessEntity != null);

            if (exists) {
                usedProcessEntity.setRawRequest(testRequest);
                wpsProcessDao.update(usedProcessEntity);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.setTestRequest", usedProcessEntity));
            }
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsProcessDao. Execution aborted.", ex);
        }

        return exists;
    }

    @Override
    public Boolean setTestRequest(final Long wpsId, final String processIdentifier, final String testRequest) {
        Validate.notNull(wpsId, "WPS ID");
        validator.validateStringParam(processIdentifier);

        WpsProcessEntity wpsProcessEntity = getWpsProcessEntity(wpsId, processIdentifier);
        return wpsProcessEntity != null && setTestRequest(wpsProcessEntity, testRequest);
    }

    @Override
    public WpsEntity updateWps(final URL oldEndpoint, final URL newEndpoint) {
        Validate.notNull(oldEndpoint, "oldEndpoint");
        Validate.notNull(newEndpoint, "newEndpoint");

        WpsEntity wps = getWps(oldEndpoint);

        if (wps == null || wps.getEndpoint().equals(newEndpoint)) {
            return wps;
        }

        try (WpsDataAccess wpsDao = wpsDaoFactory.create()) {
            checkEndpointUnique(wpsDao.getAll(), newEndpoint);

            wps.setEndpoint(newEndpoint);
            wpsDao.update(wps);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.updateWps", new URL[]{oldEndpoint, newEndpoint}));
        } catch (CreateException ex) {
            throw new AssertionError("Can't create wpsDao. Execution aborted.", ex);
        }

        return wps;
    }

    @Override
    public WpsEntity updateWps(final Long wpsId, final URL newEndpoint) {
        WpsEntity wps = getWps(wpsId); // TODO: find better solution. atm the getWps methode is called twice

        return updateWps(wps.getEndpoint(), newEndpoint);
    }
}
