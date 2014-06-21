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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static Logger log = LogManager.getLogger();

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
            log.warn("MonitorControl: {}", ex);
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
            log.warn("MonitorControl: {}", ex);

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
        WpsDataAccess wpsDao = null;
        Boolean result = false;

        try {
            wpsDao = wpsDaoFactory.create();
            result = wpsDao.persist(wps);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if(wpsDao != null) {
                wpsDao.close();
            }
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
        } finally {
            if(wpsDao != null) {
                wpsDao.close();
            }

            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
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
        } finally {
            if(wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return exists;
    }

    @Override
    public Boolean updateWpsUri(final String wpsIdentifier, final URI newUri) {
        WpsDataAccess wpsDao = null;
        Boolean updateable = false;

        try {
            wpsDao = wpsDaoFactory.create();
            
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));

            if (updateable = (wps != null)) {
                wps.setUri(newUri);
                wpsDao.update(wps);
            }
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsDao != null) {
                wpsDao.close();
            }
        }

        return updateable;
    }

    @Override
    public Boolean deleteWps(final String wpsIdentifier) {
        WpsDataAccess wpsDao = null;
        WpsProcessDataAccess wpsProcessDao = null;

        Boolean deleteable = false;

        try {
            wpsDao = wpsDaoFactory.create();
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));

            if (deleteable = (wps != null)) {
                try {
                    synchronized (this) {
                        schedulerControl.removeWpsJobs(wpsIdentifier);
                    }
                    
                    wpsProcessDao.deleteProcessesFromWps(wpsIdentifier);
                    wpsDao.remove(wps);
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

            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = null;
        Boolean deleteable = false;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();

            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"),
                    Param.notNull(processIdentifier, "processIdentifier")
            );

            if (deleteable = (process != null)) {
                wpsProcessDao.remove(process);
            }
        } catch (CreateException ex) {
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
        WpsProcessDataAccess wpsProcessDao = null;
        List<WpsProcessEntity> processes = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            processes = wpsProcessDao.getAll(Param.notNull(identifier, "identifier"));
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return processes;
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier) {
        return getMeasuredData(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier, Range range) {
        QosDataAccess qosDao = null;
        List<MeasuredDataEntity> measuredData = null;

        try {
            qosDao = qosDaoFactory.create();

            measuredData = qosDao.getByProcess(Param.notNull(wpsIdentifier, "wpsIdentifier"),
                    Param.notNull(processIdentifier, "processIdentifier"),
                    range
            );
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (qosDao != null) {
                qosDao.close();
            }
        }

        return measuredData;
    }

    public SchedulerControl getSchedulerControl() {
        return schedulerControl;
    }

    public void setSchedulerControl(SchedulerControl schedulerControl) {
        this.schedulerControl = schedulerControl;
    }
    /*
     // not necessary and should not used. Because if other classes need a DAO,
     // than theses should be injected
     public QosDataAccess getQosDao() {
     QosDataAccess instance = null;
        
     try {
     instance = qosDaoFactory.create();
     } catch (CreateException ex) {
     log.error(ex);
     }
        
     return instance;
     }

     public WpsDataAccess getWpsDao() {
     WpsDataAccess instance = null;
        
     try {
     instance = wpsDaoFactory.create();
     } catch (CreateException ex) {
     log.error(ex);
     }
        
     return instance;
     }

     public WpsProcessDataAccess getWpsProcessDao() {
     WpsProcessDataAccess instance = null;
        
     try {
     instance = wpsProcessDaoFactory.create();
     } catch (CreateException ex) {
     log.error(ex);
     }
        
     return instance;
     }*/

    @Override
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
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
            log.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

        return false;
    }

    @Override
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessDataAccess wpsProcessDao = null;

        try {
            wpsProcessDao = wpsProcessDaoFactory.create();
            WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);

            if (find != null) {
                synchronized (this) {
                    schedulerControl.resume(new JobKey(processIdentifier, wpsIdentifier));
                }
                find.setWpsException(false);

                wpsProcessDao.update(find);

                log.debug("MonitorControl: resuming monitoring of WPS Process {}.{}", wpsIdentifier, processIdentifier);
            }

        } catch (SchedulerException ex) {
            log.warn("MonitorControl: {}", ex);
        } catch (CreateException ex) {
            log.error(ex);
        } finally {
            if (wpsProcessDao != null) {
                wpsProcessDao.close();
            }
        }

    }
}
