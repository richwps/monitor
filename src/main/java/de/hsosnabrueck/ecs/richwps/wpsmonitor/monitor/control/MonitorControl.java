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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControl implements MonitorFacade {

    private SchedulerControl schedulerControl;
    private QosDataAccess qosDao;
    private WpsDataAccess wpsDao;
    private WpsProcessDataAccess wpsProcessDao;

    private final Lock read;
    private final Lock write;

    public MonitorControl(SchedulerControl scheduler, QosDataAccess qosDao, WpsDataAccess wpsDao, WpsProcessDataAccess wpsProcessDao) {
        this.schedulerControl = Param.notNull(scheduler, "scheduler");
        this.qosDao = Param.notNull(qosDao, "qosDao");
        this.wpsDao = Param.notNull(wpsDao, "wpsDao");
        this.wpsProcessDao = Param.notNull(wpsProcessDao, "wpsProcessDao");

        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);
        read = reentrantReadWriteLock.readLock();
        write = reentrantReadWriteLock.writeLock();
    }

    @Override
    public Boolean createWps(String wpdIdentifier, URI uri) {
        write.lock();

        try {
            WpsEntity wps = new WpsEntity(Param.notNull(wpdIdentifier, "wpdIdentifier"), Param.notNull(uri, "uri"));

            return wpsDao.persist(wps);
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean createProcess(String wpsIdentifier, String processIdentifier) {
        write.lock();

        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));

            if (wps != null) {
                WpsProcessEntity process = new WpsProcessEntity(Param.notNull(processIdentifier, "processIdentifier"), wps);

                try {
                    schedulerControl.addWpsAsJob(process);

                    return wpsProcessDao.persist(process);
                } catch (SchedulerException ex) {
                    Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return false;
        } finally {
            write.unlock();
        }
    }

    @Override
    public TriggerKey createTrigger(String wpsIdentifier, String processIdentifier, TriggerConfig config) {
        write.lock();

        try {
            TriggerKey result = null;

            try {
                result = schedulerControl.addTriggerToJob(
                        new JobKey(
                                Param.notNull(wpsIdentifier, "wpsIdentifier"),
                                Param.notNull(processIdentifier, "processIdentifier")), Param.notNull(config, "config"));
            } catch (SchedulerException ex) {
                Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);
            }

            return result;
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean setTestRequest(String wpsIdentifier, String processIdentifier, String testRequest) {
        write.lock();
        try {
            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
            Boolean exists = process != null;

            if (exists) {
                process.setRawRequest(testRequest);

                wpsProcessDao.update(process);
            }

            return exists;
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean updateWpsUri(String wpsIdentifier, URI newUri) {
        write.lock();
        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
            Boolean updateable = (wps != null);

            if (updateable) {
                wps.setRoute(newUri);
                wpsDao.update(wps);
            }
            return updateable;
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean deleteWps(String wpsIdentifier) {
        write.lock();
        try {
            WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
            Boolean deleteable = (wps != null);

            if (deleteable) {
                wpsDao.remove(wps);
            }

            return deleteable;
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        write.lock();

        try {
            WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
            Boolean deleteable = process != null;

            if (deleteable) {
                wpsProcessDao.remove(process);
            }

            return deleteable;
        } finally {
            write.unlock();
        }
    }

    @Override
    public Boolean deleteTrigger(TriggerKey triggerKey) {
        write.lock();

        try {
            try {
                schedulerControl.removeTrigger(triggerKey);
            } catch (SchedulerException ex) {
                Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);

                return false;
            }

            return true;
        } finally {
            write.unlock();
        }
    }

    @Override
    public List<WpsEntity> getWpsList() {
        read.lock();

        try {
            return wpsDao.getAll();
        } finally {
            read.unlock();
        }
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(String identifier) {
        read.lock();

        try {
            return wpsProcessDao.getAll(Param.notNull(identifier, "identifier"));
        } finally {
            read.unlock();
        }
    }

    @Override
    public List<TriggerKey> getTriggers(String wpsIdentifier, String processIdentifier) {
        JobKey jobKey = new JobKey(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
        
        read.lock();
        try {
            return schedulerControl.getTriggerKeysOfJob(jobKey);
        } catch (SchedulerException ex) {
            return null;
        } finally {
            read.unlock();
        }
    }

    @Override
    public String getRequestString(String wpsIdentifier, String processIdentifier) {
        write.lock();
        // unsure if i should use read.lock() for atomar operation or write.lock ... 
        try {
        WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));

        if (process != null) {
            return process.getRawRequest();
        }

        return null;
        } finally {
            write.unlock();
        }
    }

    @Override
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier) {
        return qosDao.getByProcess(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
    }

    @Override
    public TriggerConfig getTriggerConfig(TriggerKey triggerKey) {
        try {
            return schedulerControl.getConfigOfTrigger(triggerKey);
        } catch (SchedulerException ex) {
            Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SchedulerControl getSchedulerControl() {
        return schedulerControl;
    }

    public void setSchedulerControl(SchedulerControl schedulerControl) {
        this.schedulerControl = schedulerControl;
    }

    public QosDataAccess getQosDao() {
        return qosDao;
    }

    public void setQosDao(QosDataAccess qosDao) {
        this.qosDao = qosDao;
    }

    public WpsDataAccess getWpsDao() {
        return wpsDao;
    }

    public void setWpsDao(WpsDataAccess wpsDao) {
        this.wpsDao = wpsDao;
    }

    public WpsProcessDataAccess getWpsProcessDao() {
        return wpsProcessDao;
    }

    public void setWpsProcessDao(WpsProcessDataAccess wpsProcessDao) {
        this.wpsProcessDao = wpsProcessDao;
    }

}
