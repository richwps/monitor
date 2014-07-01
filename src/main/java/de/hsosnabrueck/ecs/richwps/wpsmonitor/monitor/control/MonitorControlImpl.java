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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * Implementation of the MonitorControl interface. This implementation tries to
 * work like a request-response principe. That means, that every method call
 * creates a new DataAccess-instance. Possible close operations musst be
 * handeled by the specific DataAccess implementation.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControlImpl implements MonitorControl {

    private final SchedulerControl schedulerControl;
    private final QosDaoFactory qosDaoFactory;
    private final WpsDaoFactory wpsDaoFactory;
    private final WpsProcessDaoFactory wpsProcessDaoFactory;
    private final MonitorEventHandler eventHandler;

    private static final Logger log = LogManager.getLogger();

    /**
     * Constructor.
     *
     * @param scheduler {@link SchedulerControl} instance.
     * @param eventHandler {@link MonitorEventHandler} instance.
     * @param qosDao {@link QosDaoFactory} instance.
     * @param wpsDao {@link WpsDaoFactory} instance.
     * @param wpsProcessDao {@link WpsProcessDaoFactory} instance.
     */
    public MonitorControlImpl(SchedulerControl scheduler, MonitorEventHandler eventHandler,
            QosDaoFactory qosDao, WpsDaoFactory wpsDao, WpsProcessDaoFactory wpsProcessDao) {

        this.schedulerControl = Validate.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Validate.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Validate.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Validate.notNull(wpsProcessDao, "wpsProcessDao");
        this.eventHandler = Validate.notNull(eventHandler, "eventHandler");

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
    public TriggerKey saveTrigger(final String wpsIdentifier, final String processIdentifier, final TriggerConfig config) {
        TriggerKey result = null;

        try {
            if (isProcessExists(wpsIdentifier, processIdentifier) && config.getTriggerKey() == null) {
                JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

                result = schedulerControl.addTriggerToJob(jobKey, config);
            } else {
                schedulerControl.updateTrigger(config);
                result = config.getTriggerKey();
            }
        } catch (SchedulerException ex) {
            log.error(ex);
        }

        eventHandler
                .fireEvent(new MonitorEvent("monitorcontrol.saveTrigger", result));

        return result;
    }

    @Override
    public List<TriggerConfig> getTriggers(String wpsIdentifier, String processIdentifier) {
        JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);
        List<TriggerConfig> result = new ArrayList<TriggerConfig>();

        try {
            List<TriggerKey> triggerKeysOfJob = schedulerControl.getTriggerKeysOfJob(jobKey);

            for (TriggerKey triggerKey : triggerKeysOfJob) {
                result.add(schedulerControl.getConfigOfTrigger(triggerKey));
            }

            return result;
        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);

            return null;
        }
    }

    @Override
    public Boolean deleteTrigger(TriggerKey triggerKey) {
        try {
            schedulerControl.removeTrigger(triggerKey);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.deleteTrigger", triggerKey));
        } catch (SchedulerException ex) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean createWps(final String wpsIdentifier, final URI uri) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(uri, "uri");

        WpsEntity wps = new WpsEntity(wpsIdentifier, uri);
        WpsDataAccess wpsDao = null;
        Boolean result = false;

        try {
            wpsDao = wpsDaoFactory.create();
            result = wpsDao.persist(wps);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.createWps", wps));
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return result;
    }

    @Override
    public Boolean createAndScheduleProcess(final String wpsIdentifier, final String processIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        Boolean isPersisted = false;
        WpsProcessDataAccess wpsProcessDao = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            if (isWpsExists(wpsIdentifier) && !isProcessExists(wpsIdentifier, processIdentifier)) {
                WpsProcessEntity process = new WpsProcessEntity(processIdentifier, getWps(wpsIdentifier));

                try {
                    isPersisted = wpsProcessDao.persist(process);

                    if (isPersisted) {
                        schedulerControl.addWpsAsJob(process);

                        eventHandler
                                .fireEvent(new MonitorEvent("monitorcontrol.createAndScheduleProcess", process));
                    }
                } catch (SchedulerException ex) {
                    log.warn("MonitorControl: {}", ex);
                }
            }
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return isPersisted;
    }

    @Override
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        WpsProcessDataAccess wpsProcessDao = null;
        Boolean exists = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (exists = (process != null)) {
                process.setRawRequest(testRequest);
                wpsProcessDao.update(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.setTestRequest", process));
            }
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return exists;
    }

    @Override
    public WpsEntity updateWps(final String oldWpsIdentifier, final String newWpsIdentifier, final URI newUri) {
        Validate.notNull(oldWpsIdentifier, "oldWpsIdentifier");
        Validate.notNull(newWpsIdentifier, "newWpsIdentifier");
        Validate.notNull(newUri, "newUri");

        WpsDataAccess wpsDao = null;
        WpsEntity wps = getWps(oldWpsIdentifier);

        try {
            wpsDao = wpsDaoFactory.create();

            if (wps != null) {
                wps.setIdentifier(newWpsIdentifier);
                wps.setUri(newUri);

                wpsDao.update(wps);
                schedulerControl.updateJobsWpsGroupName(oldWpsIdentifier, newWpsIdentifier);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.updateWps", new String[]{oldWpsIdentifier, newWpsIdentifier}));
            }
        } catch (CreateException ex) {
            log.error(ex);
        } catch (SchedulerException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return wps;
    }

    /**
     * yes, here i can logically call delete process, but the cascade delete
     * behavior is already in the specific data access implementations
     * implemented. If i call deleteProcess here, a redundant unnecessary
     * beahvior it happens
     */
    @Override
    public Boolean deleteWps(final String wpsIdentifier) {
        WpsDataAccess wpsDao = null;
        Boolean deleteable = false;

        try {
            wpsDao = wpsDaoFactory.create();

            WpsEntity wps = getWps(wpsIdentifier);

            if (deleteable = (wps != null)) {
                try {
                    wpsDao.remove(wps);
                    schedulerControl.removeWpsJobs(wpsIdentifier);

                    eventHandler
                            .fireEvent(new MonitorEvent("monitorcontrol.deleteWps", wps));
                } catch (SchedulerException ex) {
                    log.error("MonitorControl: {}", ex);
                }
            }
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        WpsProcessDataAccess wpsProcessDao = null;
        Boolean deleteable = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (deleteable = (process != null)) {
                JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

                wpsProcessDao.remove(process);
                schedulerControl.removeJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteProcess", process));
            }
        } catch (CreateException ex) {
            log.error(ex);
        } catch (SchedulerException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return deleteable;
    }

    @Override
    public List<WpsEntity> getWpsList() {
        WpsDataAccess wpsDao = null;
        List<WpsEntity> wpsList = null;

        try {
            wpsDao = wpsDaoFactory.create();
            wpsList = wpsDao.getAll();
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return wpsList;
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(String identifier) {
        Validate.notNull(identifier, "identifier");

        WpsProcessDataAccess wpsProcessDao;
        List<WpsProcessEntity> processes = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            processes = wpsProcessDao.getAll(identifier);
        } catch (CreateException ex) {
            log.error(ex);
        }

        return processes;
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier) {
        return getMeasuredData(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier, Range range) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        QosDataAccess qosDao = null;
        List<MeasuredDataEntity> measuredData = null;

        try {
            qosDao = qosDaoFactory.create();
            measuredData = qosDao.getByProcess(wpsIdentifier, processIdentifier, range);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            qosDao.close();
        }

        return measuredData;
    }

    @Override
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao;

        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        try {
            WpsProcessEntity find = getWpsProcessEntity(wpsIdentifier, processIdentifier);

            if (find != null) {
                JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

                assert find.isWpsException() && schedulerControl.isPaused(jobKey)
                        || !find.isWpsException() && !schedulerControl.isPaused(jobKey);

                return find.isWpsException();
            }
        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);
        }

        return false;
    }

    @Override
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");
        
        WpsProcessDataAccess wpsProcessDao = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (process != null) {
                schedulerControl.resume(new JobKey(processIdentifier, wpsIdentifier));
                process.setWpsException(false);

                wpsProcessDao.update(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.resumeMonitoring", process));

                log.debug("MonitorControl: resuming monitoring of WPS Process {}.{}", wpsIdentifier, processIdentifier);
            }

        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if(wpsProcessDao != null) {
                wpsProcessDao.close();
            }
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
        QosDataAccess qosDao;

        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        try {
            qosDao = qosDaoFactory.create();

            qosDao.deleteByProcess(wpsIdentifier, processIdentifier, olderAs);
        } catch (CreateException ex) {
            log.error(ex);
        }
    }

    @Override
    public void pauseMonitoring(final String wpsIdentifier, final String processIdentifier) {
        JobKey jobKey = getJobKey(wpsIdentifier, processIdentifier);

        try {
            if (schedulerControl.isJobRegistred(jobKey)) {
                WpsProcessEntity process = getWpsProcessEntity(wpsIdentifier, processIdentifier);

                schedulerControl.pauseJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", process));
            }
        } catch (SchedulerException ex) {
            log.error(ex);
        }
    }

    @Override
    public void deleteMeasuredData(final Date olderAs) {
        Validate.notNull(olderAs, "olderAs");
        
        QosDataAccess qosDao;

        try {
            qosDao = qosDaoFactory.create();
            qosDao.deleteAllOlderAs(olderAs);
        } catch (CreateException ex) {
            log.error(ex);
        }
    }

    @Override
    public Boolean createWps(WpsEntity wpsEntity) {
        Validate.notNull(wpsEntity, "wpsEntity");

        return createWps(wpsEntity.getIdentifier(), wpsEntity.getUri());
    }

    @Override
    public Boolean createAndScheduleProcess(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return createAndScheduleProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public TriggerKey saveTrigger(WpsProcessEntity processEntity, TriggerConfig config) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return saveTrigger(wpsIdentifier, processIdentifier, config);
    }

    @Override
    public Boolean setTestRequest(WpsProcessEntity processEntity, String testRequest) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return setTestRequest(wpsIdentifier, processIdentifier, testRequest);
    }

    @Override
    public WpsEntity updateWps(String oldWpsIdentifier, WpsEntity newWps) {
        Validate.notNull(newWps, "newWps");

        return updateWps(oldWpsIdentifier, newWps.getIdentifier(), newWps.getUri());
    }

    @Override
    public Boolean deleteWps(WpsEntity wpsEntity) {
        Validate.notNull(wpsEntity, "wpsEntity");

        return deleteWps(wpsEntity.getIdentifier());
    }

    @Override
    public Boolean deleteProcess(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return deleteProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public Boolean isPausedMonitoring(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return isPausedMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void resumeMonitoring(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        resumeMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void pauseMonitoring(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        pauseMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(WpsEntity wpsEntity) {
        Validate.notNull(wpsEntity, "wpsEntity");

        return getProcessesOfWps(wpsEntity.getIdentifier());
    }

    @Override
    public List<TriggerConfig> getTriggers(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getTriggers(wpsIdentifier, processIdentifier);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(WpsProcessEntity processEntity) {
        return getMeasuredData(processEntity, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(WpsProcessEntity processEntity, Range range) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getMeasuredData(wpsIdentifier, processIdentifier, range);
    }

    @Override
    public void deleteMeasuredDataOfProcess(WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public void deleteMeasuredDataOfProcess(WpsProcessEntity processEntity, Date olderAs) {
        Validate.notNull(processEntity, "processEntity");

        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier, olderAs);
    }

    private Boolean isProcessExists(final String wpsIdentifier, final String processIdentifier) {
        return getWpsProcessEntity(wpsIdentifier, processIdentifier) != null;
    }

    private Boolean isProcessExists(final WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");
        Validate.notNull(processEntity.getWps(), "processEntitie's Wps instance");

        return isProcessExists(processEntity.getWps().getIdentifier(), processEntity.getIdentifier());
    }

    private WpsProcessEntity getWpsProcessEntity(final String wpsIdentifier, final String processIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        WpsProcessDataAccess wpsProcessDao = null;
        WpsProcessEntity result = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            result = wpsProcessDao.find(wpsIdentifier, processIdentifier);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return result;
    }

    private Boolean isWpsExists(final String wpsIdentifier) {
        return getWps(wpsIdentifier) != null;
    }

    private Boolean isWpsExists(final WpsEntity wpsEntity) {
        Validate.notNull(wpsEntity, "wpsEntity");

        return isWpsExists(wpsEntity.getIdentifier());
    }

    private WpsEntity getWps(final String wpsIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");

        WpsDataAccess wpsDao = null;
        WpsEntity result = null;

        try {
            wpsDao = wpsDaoFactory.create();
            result = wpsDao.find(wpsIdentifier);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return result;
    }

    private JobKey getJobKey(final String wpsIdentifier, final String processIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        Validate.notNull(processIdentifier, "processIdentifier");

        return new JobKey(processIdentifier, wpsIdentifier);
    }

    private JobKey getJobKey(final WpsProcessEntity processEntity) {
        Validate.notNull(processEntity, "processEntity");
        Validate.notNull(processEntity.getWps(), "processEntitie's Wps instance");

        return getJobKey(processEntity.getWps().getIdentifier(), processEntity.getIdentifier());
    }
}
