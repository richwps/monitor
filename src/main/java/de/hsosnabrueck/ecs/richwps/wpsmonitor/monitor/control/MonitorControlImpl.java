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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
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
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControlImpl implements MonitorControl {

    private final SchedulerControl schedulerControl;
    private final QosDaoFactory qosDaoFactory;
    private final WpsDaoFactory wpsDaoFactory;
    private final WpsProcessDaoFactory wpsProcessDaoFactory;

    private static final Logger log = LogManager.getLogger();

    public MonitorControlImpl(SchedulerControl scheduler, QosDaoFactory qosDao, WpsDaoFactory wpsDao, WpsProcessDaoFactory wpsProcessDao) {
        this.schedulerControl = Param.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Param.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Param.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDao, "wpsProcessDao");
    }

    @Override
    public TriggerKey saveTrigger(final String wpsIdentifier, final String processIdentifier, final TriggerConfig config) {
        TriggerKey result = null;

        try {
            if (config.getTriggerKey() == null) {
                result = schedulerControl.addTriggerToJob(new JobKey(
                        Param.notNull(processIdentifier, "processIdentifier"),
                        Param.notNull(wpsIdentifier, "wpsIdentifier")
                ), Param.notNull(config, "config"));
            } else {
                schedulerControl.updateTrigger(config);
                result = config.getTriggerKey();
            }
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
        } catch (SchedulerException ex) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean createWps(final String wpdIdentifier, final URI uri) {
        WpsEntity wps = new WpsEntity(Param.notNull(wpdIdentifier, "wpdIdentifier"), Param.notNull(uri, "uri"));
        WpsDataAccess wpsDao = null;
        Boolean result = false;

        try {
            wpsDao = wpsDaoFactory.create();
            result = wpsDao.persist(wps);
        } catch (CreateException ex) {
            log.error(ex);
        }

        return result;
    }

    @Override
    public Boolean createProcess(final String wpsIdentifier, final String processIdentifier) {
        WpsDataAccess wpsDao = null;
        WpsProcessDataAccess wpsProcessDao = null;

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
        WpsProcessDataAccess wpsProcessDao = null;
        Boolean exists = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"),
                    Param.notNull(processIdentifier, "processIdentifier")
            );

            if (exists = (process != null)) {
                process.setRawRequest(testRequest);

                wpsProcessDao.update(process);
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
            WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (find != null) {
                schedulerControl.resume(new JobKey(processIdentifier, wpsIdentifier));
                find.setWpsException(false);

                wpsProcessDao.update(find);

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
}
