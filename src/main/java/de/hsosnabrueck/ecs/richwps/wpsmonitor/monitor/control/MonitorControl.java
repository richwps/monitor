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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorControl implements MonitorFacadeCUD, MonitorFacadeRead {

    private SchedulerControl schedulerControl;
    private QosDataAccess qosDao;
    private WpsDataAccess wpsDao;
    private WpsProcessDataAccess wpsProcessDao;

    MonitorControl(SchedulerControl scheduler) {
        this.schedulerControl = scheduler;
    }

    @Override
    public Boolean createWps(String wpdIdentifier, URI uri) {
        WpsEntity wps = new WpsEntity(Param.notNull(wpdIdentifier, "wpdIdentifier"), Param.notNull(uri, "uri"));

        return wpsDao.persist(wps);
    }

    @Override
    public Boolean createProcess(String wpsIdentifier, String processIdentifier) {
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
    }

    @Override
    public TriggerKey createTrigger(String wpsIdentifier, String processIdentifier, TriggerConfig config) {
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
    }

    @Override
    public Boolean setTestRequest(String wpsIdentifier, String processIdentifier, String testRequest) {
        WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
        Boolean exists = process != null;

        if (exists) {
            process.setRawRequest(testRequest);

            wpsProcessDao.update(process);
        }

        return exists;
    }

    @Override
    public Boolean updateWpsUri(String wpsIdentifier, URI newUri) {
        WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
        Boolean updateable = (wps != null);

        if (updateable) {
            wps.setRoute(newUri);
            wpsDao.update(wps);
        }
        return updateable;
    }

    @Override
    public Boolean deleteWps(String wpsIdentifier) {
        WpsEntity wps = wpsDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"));
        Boolean deleteable = (wps != null);

        if (deleteable) {
            wpsDao.remove(wps);
        }

        return deleteable;
    }

    @Override
    public Boolean deleteProcess(String wpsIdentifier, String processIdentifier) {
        WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));
        Boolean deleteable = process != null;

        if (deleteable) {
            wpsProcessDao.remove(process);
        }

        return deleteable;
    }

    @Override
    public Boolean deleteTrigger(TriggerKey triggerKey) {
        try {
            schedulerControl.removeTrigger(triggerKey);
        } catch (SchedulerException ex) {
            Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    @Override
    public List<WpsEntity> getWpsList() {
        return wpsDao.getAll();
    }

    @Override
    public List<WpsProcessEntity> getProcessesOfWps(String identifier) {
        return wpsProcessDao.getAll(Param.notNull(identifier, "identifier"));
    }

    @Override
    public List<TriggerKey> getTriggers(String wpsIdentifier, String processIdentifier) {
        JobKey jobKey = new JobKey(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));

        try {
            return schedulerControl.getTriggerKeysOfJob(jobKey);
        } catch (SchedulerException ex) {
            Logger.getLogger(MonitorControl.class.getName()).log(Level.SEVERE, null, ex);

            return null;
        }
    }

    @Override
    public String getRequestString(String wpsIdentifier, String processIdentifier) {
        WpsProcessEntity process = wpsProcessDao.find(Param.notNull(wpsIdentifier, "wpsIdentifier"), Param.notNull(processIdentifier, "processIdentifier"));

        if (process != null) {
            return process.getRawRequest();
        }

        return null;
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

}
