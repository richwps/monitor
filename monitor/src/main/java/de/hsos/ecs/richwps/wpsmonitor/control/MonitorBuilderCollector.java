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

package de.hsos.ecs.richwps.wpsmonitor.control;

import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.JobFactoryService;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.measurement.ProbeService;
import de.hsos.ecs.richwps.wpsmonitor.creation.BuilderException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobListener;

/**
 * A container for the dependecies of the monitor.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorBuilderCollector {
    private static final Logger LOG = LogManager.getLogger();
    
    /**
     * Probe service.
     */
    private ProbeService probeService;

    /**
     * JobFactoryService for the Quartz-Scheduler.
     */
    private JobFactoryService jobFactoryService;

    /**
     * WpsClient-default-Factory.
     */
    private Factory<WpsClient> wpsDefaultClientFactory;
    
    /**
     * Data access default factory for QosDataAccess instances.
     */
    private Factory<QosDataAccess> qosDefaultDaoFactory;

    /**
     * Data access default factory for WpsDataAccess instances.
     */
    private Factory<WpsDataAccess> wpsDefaultDaoFactory;

    /**
     * Data access default factory for WpsProcessDataAccess instances.
     */
    private Factory<WpsProcessDataAccess> wpsProcessDefaultDaoFactory;

    /**
     * Monitor eventHandler system.
     */
    private MonitorEventHandler eventHandler;

    /**
     * File object which should point to a *.properties file.
     */
    private File propertiesFile;
    
    /**
     * List of Job Listeners
     */
    private final List<JobListener> jobListeners;
    
    /**
     * Name of the persistence Unit
     */
    private String persistenceUnit;
    
    public MonitorBuilderCollector() {
        this.jobListeners = new ArrayList<>();
    }

    public ProbeService getProbeService() throws BuilderException {
        return isSet(probeService);
    }

    public void setProbeService(ProbeService probeService) {
        this.probeService = probeService;
    }

    public JobFactoryService getJobFactoryService() throws BuilderException {
        return isSet(jobFactoryService);
    }

    public void setJobFactoryService(JobFactoryService jobFactoryService) {
        this.jobFactoryService = jobFactoryService;
    }

    public Factory<WpsClient> getWpsDefaultClientFactory() throws BuilderException {
        return isSet(wpsDefaultClientFactory);
    }

    public void setWpsDefaultClientFactory(Factory<WpsClient> wpsDefaultClientFactory) {
        this.wpsDefaultClientFactory = wpsDefaultClientFactory;
    }

    public Factory<QosDataAccess> getQosDefaultDaoFactory() throws BuilderException {
        return isSet(qosDefaultDaoFactory);
    }

    public void setQosDefaultDaoFactory(Factory<QosDataAccess> qosDefaultDaoFactory) {
        this.qosDefaultDaoFactory = qosDefaultDaoFactory;
    }

    public Factory<WpsDataAccess> getWpsDefaultDaoFactory() throws BuilderException {
        return isSet(wpsDefaultDaoFactory);
    }

    public void setWpsDefaultDaoFactory(Factory<WpsDataAccess> wpsDefaultDaoFactory) {
        this.wpsDefaultDaoFactory = wpsDefaultDaoFactory;
    }

    public Factory<WpsProcessDataAccess> getWpsProcessDefaultDaoFactory() throws BuilderException {
        return isSet(wpsProcessDefaultDaoFactory);
    }

    public void setWpsProcessDefaultDaoFactory(Factory<WpsProcessDataAccess> wpsProcessDefaultDaoFactory) {
        this.wpsProcessDefaultDaoFactory = wpsProcessDefaultDaoFactory;
    }

    public MonitorEventHandler getEventHandler() throws BuilderException {
        return isSet(eventHandler);
    }

    public void setEventHandler(MonitorEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public File getPropertiesFile() throws BuilderException {
        return isSet(propertiesFile);
    }

    public void setPropertiesFile(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public String getPersistenceUnit() throws BuilderException {
        return isSet(persistenceUnit);
    }

    public void setPersistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public boolean addJobListener(JobListener e) {
        return jobListeners.add(e);
    }

    public boolean remove(JobListener o) {
        return jobListeners.remove(o);
    }

    public List<JobListener> getJobListeners() throws BuilderException {
        return isSet(jobListeners);
    }
    
    public Boolean isValid() {
        Field[] fields = this.getClass().getFields();
        
        for(Field f : fields) {
            try {
                f.setAccessible(true);
                
                if(f.get(this) == null) {
                    return false;
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.error("The Reflections in the isValid Method can't set the accessible on true.", ex);
            }
        }
        
        return true;
    }
    
    private <T> T isSet(T validate) throws BuilderException {
        if(validate == null) {
            throw new BuilderException("The requested piece is not set yet.");
        }
        
        return validate;
    }
}
