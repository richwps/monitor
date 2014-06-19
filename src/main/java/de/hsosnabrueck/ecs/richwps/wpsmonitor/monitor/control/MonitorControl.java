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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControl implements MonitorFacade {

    private SchedulerControl schedulerControl;
    private final QosDaoFactory qosDaoFactory;
    private final WpsDaoFactory wpsDaoFactory;
    private final WpsProcessDaoFactory wpsProcessDaoFactory;
    
    private static Logger logger = Logger.getLogger(MonitorControl.class.getName());

    public MonitorControl(SchedulerControl scheduler, QosDaoFactory qosDao, WpsDaoFactory wpsDao, WpsProcessDaoFactory wpsProcessDao) {
        this.schedulerControl = Param.notNull(scheduler, "scheduler");
        this.qosDaoFactory = Param.notNull(qosDao, "qosDao");
        this.wpsDaoFactory = Param.notNull(wpsDao, "wpsDao");
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDao, "wpsProcessDao");
    }

    @Override
    public final synchronized TriggerKey saveTrigger(final String wpsIdentifier, final String processIdentifier, final TriggerConfig config) {
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
            logger.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public final synchronized List<TriggerConfig> getTriggers(String wpsIdentifier, String processIdentifier) {
        JobKey jobKey = new JobKey(Param.notNull(processIdentifier, "processIdentifier"), Param.notNull(wpsIdentifier, "wpsIdentifier"));
        List<TriggerConfig> result = new ArrayList<TriggerConfig>();

        try {
            List<TriggerKey> triggerKeysOfJob = schedulerControl.getTriggerKeysOfJob(jobKey);

            for (TriggerKey triggerKey : triggerKeysOfJob) {
                result.add(schedulerControl.getConfigOfTrigger(triggerKey));
            }

            return result;
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);

            return null;
        }
    }

    @Override
    public final synchronized Boolean deleteTrigger(TriggerKey triggerKey) {
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
        WpsDataAccess wpsDao = wpsDaoFactory.create();
        Boolean result = false;

        try {
            result = wpsDao.persist(wps);
        } finally {
            wpsDao.close();
        }

        return result;
    }

    @Override
    public Boolean createProcess(final String wpsIdentifier, final String processIdentifier) {
        WpsDataAccess wpsDao = wpsDaoFactory.create();
        WpsProcessDataAccess wpsProcessDao = null;

        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
            wpsProcessDao = wpsProcessDaoFactory.create();

            if (wps != null && wpsProcessDao.find(wpsIdentifier, processIdentifier) == null) {
                WpsProcessEntity process = new WpsProcessEntity(Param.notNull(processIdentifier, "processIdentifier"), wps);

                try {
                    synchronized (this) {
                        schedulerControl.addWpsAsJob(process);
                    }

                    return wpsProcessDao.persist(process);
                } catch (SchedulerException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            wpsDao.close();

            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return false;
    }

    @Override
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest) {
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();
        Boolean exists = false;

        try {
            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
            exists = process != null;

            if (exists) {
                process.setRawRequest(testRequest);

                wpsProcessDao.update(process);
            }
        } finally {
            wpsProcessDao.close();
        }

        return exists;
    }

    @Override
    public Boolean updateWpsUri(final String wpsIdentifier, final URI newUri) {
        WpsDataAccess wpsDao = wpsDaoFactory.create();
        Boolean updateable = false;

        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
            updateable = (wps != null);

            if (updateable) {
                wps.setUri(newUri);
                wpsDao.update(wps);
            }
        } finally {
            wpsDao.close();
        }

        return updateable;
    }

    @Override
    public Boolean deleteWps(final String wpsIdentifier) {
        WpsDataAccess wpsDao = wpsDaoFactory.create();
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();

        Boolean deleteable = false;

        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
            deleteable = (wps != null);

            if (deleteable) {
                synchronized (this) {
                    try {
                        schedulerControl.removeWpsJobs(wpsIdentifier);
                    } catch (SchedulerException ex) {
                        Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                wpsProcessDao.deleteProcessesFromWps(wpsIdentifier);
                wpsDao.remove(wps);
            }
        } finally {
            wpsDao.close();
            wpsProcessDao.close();
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();
        Boolean deleteable = false;

        try {
            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
            deleteable = (process != null);

            if (deleteable) {
                wpsProcessDao.remove(process);
            }
        } finally {
            wpsProcessDao.close();
        }

        return deleteable;
    }

    @Override
    public List<WpsEntity> getWpsList() {
        WpsDataAccess wpsDao = wpsDaoFactory.create();

        try {
            return wpsDao.getAll();
        } finally {
            wpsDao.close();
        }
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(String identifier) {
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();

        try {
            return wpsProcessDao.getAll(Param.notNull(identifier, "identifier"));
        } finally {
            wpsProcessDao.close();
        }
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier) {
        return getMeasuredData(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier, Range range) {
        QosDataAccess qosDao = qosDaoFactory.create();

        try {
            return qosDao.getByProcess(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"), range);
        } finally {
            qosDao.close();
        }
    }

    public SchedulerControl getSchedulerControl() {
        return schedulerControl;
    }

    public void setSchedulerControl(SchedulerControl schedulerControl) {
        this.schedulerControl = schedulerControl;
    }

    public QosDataAccess getQosDao() {
        return qosDaoFactory.create();
    }

    public WpsDataAccess getWpsDao() {
        return wpsDaoFactory.create();
    }

    public WpsProcessDataAccess getWpsProcessDao() {
        return wpsProcessDaoFactory.create();
    }

    @Override
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();

        try {
            WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (find != null) {
                synchronized (this) {
                    JobKey jobKey = new JobKey(processIdentifier, wpsIdentifier);

                    assert find.isWpsException() && schedulerControl.isPaused(jobKey)
                            || !find.isWpsException() && !schedulerControl.isPaused(jobKey);

                    return find.isWpsException();
                }
            }
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            wpsProcessDao.close();
        }

        return false;
    }

    @Override
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();
        
        try {
            WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);
            
            if(find != null) {
                synchronized(this) {
                    schedulerControl.resume(new JobKey(processIdentifier, wpsIdentifier));
                }
                find.setWpsException(false);
                
                wpsProcessDao.update(find);
            }
            
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            wpsProcessDao.close();
        }
    }
}
