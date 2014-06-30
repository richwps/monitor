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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
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
     *  Constructor.
     * 
     * @param scheduler {@link SchedulerControl} instance.
     * @param eventHandler {@link MonitorEventHandler} instance.
     * @param qosDao {@link QosDaoFactory} instance.
     * @param wpsDao {@link WpsDaoFactory} instance.
     * @param wpsProcessDao {@link WpsProcessDaoFactory} instance.
     */
    public MonitorControlImpl(SchedulerControl scheduler, MonitorEventHandler eventHandler,
            QosDaoFactory qosDao, WpsDaoFactory wpsDao, WpsProcessDaoFactory wpsProcessDao) {

        this.schedulerControl = Param.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Param.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Param.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDao, "wpsProcessDao");
        this.eventHandler = Param.notNull(eventHandler, "eventHandler");

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
            if (config.getTriggerKey() == null) {
                JobKey jobKey = new JobKey(
                        Param.notNull(processIdentifier, "processIdentifier"),
                        Param.notNull(wpsIdentifier, "wpsIdentifier")
                );

                result = schedulerControl.addTriggerToJob(jobKey, Param.notNull(config, "config"));
            } else {
                schedulerControl.updateTrigger(config);
                result = config.getTriggerKey();
            }

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.saveTrigger", result));
        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);
        }

        return result;
    }

    @Override
    public List<TriggerConfig> getTriggers(String wpsIdentifier, String processIdentifier) {
        JobKey jobKey = new JobKey(Param.notNull(processIdentifier, "processIdentifier"), Param.notNull(wpsIdentifier, "wpsIdentifier"));
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
        WpsEntity wps = new WpsEntity(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(uri, "uri"));
        WpsDataAccess wpsDao;
        Boolean result = false;

        try {
            wpsDao = wpsDaoFactory.create();
            result = wpsDao.persist(wps);

            eventHandler
                    .fireEvent(new MonitorEvent("monitorcontrol.createWps", wps));
        } catch (CreateException ex) {
            log.error(ex);
        }

        return result;
    }

    @Override
    public Boolean createAndScheduleProcess(final String wpsIdentifier, final String processIdentifier) {
        WpsDataAccess wpsDao;
        WpsProcessDataAccess wpsProcessDao;

        try {
            wpsDao = wpsDaoFactory.create();
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));

            if (wps != null && wpsProcessDao.find(wpsIdentifier, processIdentifier) == null) {
                WpsProcessEntity process = new WpsProcessEntity(Param.notNull(processIdentifier, "processIdentifier"), wps);

                try {
                    synchronized (this) {
                        schedulerControl.addWpsAsJob(process);
                    }

                    eventHandler
                            .fireEvent(new MonitorEvent("monitorcontrol.createAndScheduleProcess", process));

                    return wpsProcessDao.persist(process);
                } catch (SchedulerException ex) {
                    log.warn("MonitorControl: {}", ex);
                }
            }
        } catch (CreateException ex) {
            log.error(ex);
        }

        return false;
    }

    @Override
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest) {
        WpsProcessDataAccess wpsProcessDao;
        Boolean exists = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"),
                    Param.notNull(processIdentifier, "processIdentifier")
            );

            if (exists = (process != null)) {
                process.setRawRequest(testRequest);

                wpsProcessDao.update(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.setTestRequest", process));
            }
        } catch (CreateException ex) {
            log.error(ex);
        }

        return exists;
    }

    @Override
    public WpsEntity updateWps(final String oldWpsIdentifier, final String newWpsIdentifier, final URI newUri) {
        WpsDataAccess wpsDao;
        WpsEntity wps = null;

        try {
            wpsDao = wpsDaoFactory.create();

            wps = wpsDao.find(Param.notNull(oldWpsIdentifier, "oldWpsIdentifier"));

            if (wps != null) {
                wps.setIdentifier(Param.notNull(newWpsIdentifier, "newWpsIdentifier"));
                wps.setUri(Param.notNull(newUri, "newUri"));

                schedulerControl.updateJobsWpsGroupName(oldWpsIdentifier, newWpsIdentifier);
                wpsDao.update(wps);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.updateWps", new String[]{oldWpsIdentifier, newWpsIdentifier}));
            }
        } catch (CreateException ex) {
            log.error(ex);
        } catch (SchedulerException ex) {
            log.error(ex);
        }

        return wps;
    }

    @Override
    public Boolean deleteWps(final String wpsIdentifier) {
        WpsDataAccess wpsDao;

        Boolean deleteable = false;

        try {
            wpsDao = wpsDaoFactory.create();

            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));

            if (deleteable = (wps != null)) {
                try {
                    schedulerControl.removeWpsJobs(wpsIdentifier);
                    wpsDao.remove(wps);
                    /**
                     * yes, here i can logically call delete process, but the
                     * cascade delete behavior is already in the specific data
                     * access implementations implemented. If i call
                     * deleteProcess here, a redundant unnecessary beahvior it
                     * happens
                     */

                    eventHandler
                            .fireEvent(new MonitorEvent("monitorcontrol.deleteWps", wps));

                } catch (SchedulerException ex) {
                    log.error("MonitorControl: {}", ex);
                }
            }
        } catch (CreateException ex) {
            log.error(ex);
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao;
        Boolean deleteable = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (deleteable = (process != null)) {
                schedulerControl.removeJob(
                        new JobKey(Param.notNull(processIdentifier, "processIdentifier"),
                                Param.notNull(wpsIdentifier, "wpsIdentifier"))
                );

                wpsProcessDao.remove(process);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.deleteProcess", process));
            }
        } catch (CreateException ex) {
            log.error(ex);
        } catch (SchedulerException ex) {
            log.error(ex);
        }

        return deleteable;
    }

    @Override
    public List<WpsEntity> getWpsList() {
        WpsDataAccess wpsDao;
        List<WpsEntity> wpsList = null;

        try {
            wpsDao = wpsDaoFactory.create();
            wpsList = wpsDao.getAll();
        } catch (CreateException ex) {
            log.error(ex);
        }

        return wpsList;
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(String identifier) {
        WpsProcessDataAccess wpsProcessDao;
        List<WpsProcessEntity> processes = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            processes = wpsProcessDao.getAll(Param.notNull(identifier, "identifier"));
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
        QosDataAccess qosDao;
        List<MeasuredDataEntity> measuredData = null;

        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");

        try {
            qosDao = qosDaoFactory.create();

            measuredData = qosDao.getByProcess(wpsIdentifier, processIdentifier, range);
        } catch (CreateException ex) {
            log.error(ex);
        }

        return measuredData;
    }

    @Override
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao;

        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (find != null) {
                JobKey jobKey = new JobKey(processIdentifier, wpsIdentifier);

                assert find.isWpsException() && schedulerControl.isPaused(jobKey)
                        || !find.isWpsException() && !schedulerControl.isPaused(jobKey);

                return find.isWpsException();
            }
        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            log.error(ex);
        }

        return false;
    }

    @Override
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao;

        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");

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

        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");

        try {
            qosDao = qosDaoFactory.create();

            qosDao.deleteByProcess(wpsIdentifier, processIdentifier, olderAs);
        } catch (CreateException ex) {
            log.error(ex);
        }
    }

    @Override
    public void pauseMonitoring(final String wpsIdentifier, final String processIdentifier) {
        JobKey jobKey = new JobKey(processIdentifier, wpsIdentifier);
        WpsProcessDataAccess wpsProcessDao;

        try {
            if (schedulerControl.isJobRegistred(jobKey)) {
                wpsProcessDao = wpsProcessDaoFactory.create();
                WpsProcessEntity process = wpsProcessDao.find(wpsIdentifier, processIdentifier);

                schedulerControl.pauseJob(jobKey);

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", process));
            }
        } catch (SchedulerException ex) {
            log.error(ex);
        } catch (CreateException ex) {
            log.error(ex);
        }
    }

    @Override
    public void deleteMeasuredData(final Date olderAs) {
        QosDataAccess qosDao;

        Param.notNull(olderAs, "olderAs");

        try {
            qosDao = qosDaoFactory.create();

            qosDao.deleteAllOlderAs(olderAs);

        } catch (CreateException ex) {
            log.error(ex);
        }
    }

    @Override
    public Boolean createWps(WpsEntity wpsEntity) {
        return createWps(wpsEntity.getIdentifier(), wpsEntity.getUri());
    }

    @Override
    public Boolean createAndScheduleProcess(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return createAndScheduleProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public TriggerKey saveTrigger(WpsProcessEntity processEntity, TriggerConfig config) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return saveTrigger(wpsIdentifier, processIdentifier, config);
    }

    @Override
    public Boolean setTestRequest(WpsProcessEntity processEntity, String testRequest) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return setTestRequest(wpsIdentifier, processIdentifier, testRequest);
    }

    @Override
    public WpsEntity updateWps(String oldWpsIdentifier, WpsEntity newWps) {
        return updateWps(oldWpsIdentifier, newWps.getIdentifier(), newWps.getUri());
    }

    @Override
    public Boolean deleteWps(WpsEntity wpsEntity) {
        return deleteWps(wpsEntity.getIdentifier());
    }

    @Override
    public Boolean deleteProcess(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return deleteProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public Boolean isPausedMonitoring(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return isPausedMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void resumeMonitoring(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        resumeMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public void pauseMonitoring(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        pauseMonitoring(wpsIdentifier, processIdentifier);
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(WpsEntity wpsEntity) {
        return getProcessesOfWps(wpsEntity.getIdentifier());
    }

    @Override
    public List<TriggerConfig> getTriggers(WpsProcessEntity processEntity) {
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
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        return getMeasuredData(wpsIdentifier, processIdentifier, range);
    }

    @Override
    public void deleteMeasuredDataOfProcess(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier);
    }

    @Override
    public void deleteMeasuredDataOfProcess(WpsProcessEntity processEntity, Date olderAs) {
        String wpsIdentifier = processEntity.getWps()
                .getIdentifier();
        String processIdentifier = processEntity
                .getIdentifier();

        deleteMeasuredDataOfProcess(wpsIdentifier, processIdentifier, olderAs);
    }
}
