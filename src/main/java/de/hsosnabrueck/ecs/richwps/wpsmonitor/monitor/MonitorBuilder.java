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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfigException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.QosDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.WpsDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.WpsProcessDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.MeasureJobListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControlImpl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.JobFactoryService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.defaultimpl.SimpleWpsClientFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Builder pattern to build a Monitor-instance. First, call setupDefault() and
 * then personalize the build with the with-methods. If an exception occurs, the
 * builder will catch the exception and rethrow it as a BuilderException.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorBuilder {

    /**
     * Probe service.
     */
    private ProbeService probeService;

    /**
     * JobFactoryService for the Quartz-Scheduler.
     */
    private JobFactoryService jobFactoryService;

    /**
     * WpsClient-Factory.
     */
    private WpsClientFactory wpsClientFactory;

    /**
     * WpsClientConfig instance.
     */
    private WpsClientConfig wpsClientConfig;

    /**
     * Monitor eventHandler system.
     */
    private MonitorEventHandler eventHandler;

    /**
     * Data access factory for QosDataAccess instances.
     */
    private QosDaoFactory qosDaoFactory;

    /**
     * Data access factory for WpsDataAccess instances.
     */
    private WpsDaoFactory wpsDaoFactory;

    /**
     * Data access factory for WpsProcessDataAccess instances.
     */
    private WpsProcessDaoFactory wpsProcessDaoFactory;

    /**
     * File object which should point to a *.properties file.
     */
    private File propertiesFile;
    
    /**
     * Config object for the monitor instance
     */
    private MonitorConfig monitorConfig;

    /**
     * Sets the {@link ProbeService} instance.
     *
     * @param probeService ProbeService instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withProbeService(ProbeService probeService) {
        this.probeService = Validate.notNull(probeService, "probeService");

        return this;
    }

    /**
     * Sets the properties filename which should be used.
     *
     * @param fileName Properties filename
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withPropertiesFile(String fileName) {
        File pFile = null;

        if (fileName != null && !fileName.trim().equals("")) {
            pFile = new File(fileName);
        }

        this.propertiesFile = pFile;

        return this;
    }

    /**
     * Sets the {@link JobFactoryService}-instance.
     *
     * @param jobFactoryService JobFactoryService instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withJobFactoryService(JobFactoryService jobFactoryService) {
        this.jobFactoryService = Validate.notNull(jobFactoryService, "jobFactoryService");

        return this;
    }

    /**
     * Sets the {@link QosDaoFactory} instance.
     *
     * @param qosDaoFactory QosDaoFactory instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withQosDaoFactory(QosDaoFactory qosDaoFactory) {
        this.qosDaoFactory = Validate.notNull(qosDaoFactory, "qosDaoFactory");

        return this;
    }

    /**
     * Sets the {@link WpsDaoFactory} instance.
     *
     * @param wpsDaoFactory WpsDaoFactory instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsDaoFactory(WpsDaoFactory wpsDaoFactory) {
        this.wpsDaoFactory = Validate.notNull(wpsDaoFactory, "wpsDaoFactory");

        return this;
    }

    /**
     * Sets the {@link WpsProcessDaoFactory} instance.
     *
     * @param wpsProcessDaoFactory WpsProcessDaoFactory instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsProcessDaoFactory(WpsProcessDaoFactory wpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = Validate.notNull(wpsProcessDaoFactory, "wpsProcessDaoFactory");

        return this;
    }

    /**
     * Sets the {@link Factory&lt;QosDataAccess>} instance. Will be injected in
     * a new QosDaoFactory instance.
     *
     * @param defaultQosDaoFactory Factory&lt;QosDataAccess>
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withQosDaoFactory(Factory<QosDataAccess> defaultQosDaoFactory) {
        this.qosDaoFactory = new QosDaoFactory(defaultQosDaoFactory);

        return this;
    }

    /**
     * Sets the {@link Factory&lt;WpsDataAccess>} instance. Will be injected in
     * a new WpsDaoFactory instance.
     *
     * @param defaultWpsDaoFactory Factory&lt;WpsDataAccess>
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsDaoFactory(Factory<WpsDataAccess> defaultWpsDaoFactory) {
        this.wpsDaoFactory = new WpsDaoFactory(defaultWpsDaoFactory);

        return this;
    }

    /**
     * Sets the {@link Factory&lt;WpsProcessDataAccess>} instance. Will be
     * injected in a new WpsProcessDaoFactory instance.
     *
     * @param defaultWpsProcessDaoFactory Factory&lt;WpsProcessDataAccess>
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsProcessDaoFactory(Factory<WpsProcessDataAccess> defaultWpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = new WpsProcessDaoFactory(defaultWpsProcessDaoFactory);

        return this;
    }

    /**
     * Sets the {@link Factory&lt;WpsClient>} instance. Will be injected in a
     * new WpsClientFactory instance.
     *
     * @param wpsClientFactory
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsClientFactory(Factory<WpsClient> wpsClientFactory) {
        this.wpsClientFactory = new WpsClientFactory(wpsClientFactory, wpsClientConfig);

        return this;
    }

    /**
     * Sets the {@link WpsClientFactory} instance.
     *
     * @param wpsClientFactory WpsClientFactory instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsClientFactory(WpsClientFactory wpsClientFactory) {
        this.wpsClientFactory = Validate.notNull(wpsClientFactory, "wpsClientFactory");

        return this;
    }

    /**
     * Sets the {@link WpsClientConfig} instance. Calls setWpsClientConfig on
     * the wpsClientFactory
     *
     * @param config WpsClientConfig instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsClientConfig(WpsClientConfig config) {
        Validate.notNull(config, "config");

        if (this.wpsClientFactory != null) {
            this.wpsClientFactory.setWpsClientConfig(wpsClientConfig);
        }

        this.wpsClientConfig = Validate.notNull(config, "config");

        return this;
    }

    /**
     * Sets the default {@link ProbeService} instance. {@link ProbeService} will
     * be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultProbeService() {
        return withProbeService(new ProbeService());
    }

    /**
     * Sets the default {@link QosDaoFactory} instance.
     * {@link QosDaoDefaultFactory} will be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultQosDaoFactory() {
        return withQosDaoFactory(new QosDaoDefaultFactory());
    }

    /**
     * Sets the default {@link WpsDaoFactory} instance.
     * {@link WpsDaoDefaultFactory} will be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultWpsDaoFactory() {
        return withWpsDaoFactory(new WpsDaoDefaultFactory());
    }

    /**
     * Sets the default {@link WpsProcessDaoFactory} instance.
     * {@link WpsProcessDaoDefaultFactory} will be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultWpsProcessDaoFactory() {
        return withWpsProcessDaoFactory(new WpsProcessDaoDefaultFactory());
    }

    /**
     * Sets the default {@link WpsClient} instance.
     * {@link SimpleWpsClientFactory} will be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultWpsClient() {
        return withWpsClientFactory(new SimpleWpsClientFactory());
    }

    /**
     * Sets the default {@link JobFactoryService} instance.
     * {@link JobFactoryService} will be used.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultJobFactoryService() {
        return withJobFactoryService(new JobFactoryService());
    }

    /**
     * Sets the default properties file. monitor.properties will be used as
     * filename.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultPropertiesFile() {
        withPropertiesFile("monitor.properties");

        return this;
    }
    
    private void setupMonitorConfig() throws BuilderException {
        if(propertiesFile == null) {
            withDefaultPropertiesFile();
        }
        
        try {
            monitorConfig = new MonitorConfig(propertiesFile);
        } catch (MonitorConfigException ex) {
            throw new BuilderException(ex.toString());
        }
    }

    /**
     * Calls all default methods.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder setupDefault() {
        return withDefaultProbeService()
                .withDefaultWpsClient()
                .withDefaultQosDaoFactory()
                .withDefaultWpsDaoFactory()
                .withDefaultWpsProcessDaoFactory()
                .withDefaultJobFactoryService()
                .withDefaultPropertiesFile();
    }

    /**
     * Sets the default {@link SchedulerFactory} instance up whith all
     * dependencies.
     *
     * @return SchedulerFactory SchedulerFactory instance
     * @throws CreateException
     */
    private SchedulerFactory setupSchedulerFactory() throws CreateException {
        if (this.wpsProcessDaoFactory == null) {
            withDefaultWpsProcessDaoFactory();
        }

        if (this.jobFactoryService == null) {
            withDefaultJobFactoryService();
        }

        List<JobListener> jobListeners = new ArrayList<JobListener>();
        jobListeners.add(new MeasureJobListener(wpsProcessDaoFactory, eventHandler));

        return new SchedulerFactory(jobFactoryService, jobListeners);
    }

    /**
     * Build {@link WpsDataAccess} instance.
     *
     * @return WpsDataAccess instance
     * @throws CreateException
     * @throws BuilderException
     */
    public WpsDataAccess buildWpsDataAccess() throws CreateException, BuilderException {
        if (wpsDaoFactory == null) {
            throw new BuilderException("WpsDaoFactory is not set.");
        }

        return wpsDaoFactory.create();
    }

    /**
     * Build {@link WpsClient} instance.
     *
     * @return WpsClient instance
     * @throws CreateException
     * @throws BuilderException
     */
    public WpsClient buildWpsClient() throws CreateException, BuilderException {
        if (wpsClientFactory == null) {
            throw new BuilderException("WpsClientFactory is not set.");
        }

        return wpsClientFactory.create();
    }

    /**
     * Build {@link WpsProcessDataAccess} instance.
     *
     * @return WpsProcessDataAccess instance
     * @throws CreateException
     * @throws BuilderException
     */
    public WpsProcessDataAccess buildWpsProcessDataAccess() throws CreateException, BuilderException {
        if (wpsProcessDaoFactory == null) {
            throw new BuilderException("wpsProcessDaoFactory is not set.");
        }

        return wpsProcessDaoFactory.create();
    }

    /**
     * Build {@link QosDataAccess} instance.
     *
     * @return QosDataAccess instance
     * @throws CreateException
     * @throws BuilderException
     */
    public QosDataAccess buildQosDataAccess() throws CreateException, BuilderException {
        if (qosDaoFactory == null) {
            throw new BuilderException("qosDaoFactory is not set.");
        }

        return qosDaoFactory.create();
    }

    /**
     * Build {@link MonitorEventHandler} instance.
     *
     * @return MonitorEventHandler instance
     */
    public MonitorEventHandler buildEventHandler() {
        return new MonitorEventHandler();
    }

    /**
     * Build {@link Scheduler} instance.
     *
     * @return Scheduler instance
     * @throws SchedulerException
     * @throws CreateException
     */
    public Scheduler buildScheduler() throws SchedulerException, CreateException {
        return setupSchedulerFactory().create();
    }

    /**
     * Build {@link SchedulerControl} instance.
     *
     * @return SchedulerControl instance
     * @throws SchedulerException
     * @throws CreateException
     */
    public SchedulerControl buildSchedulerControl() throws SchedulerException, CreateException {
        return new SchedulerControl(buildScheduler(), jobFactoryService);
    }

    /**
     * Evaluates if the builder has all dependencies to create a
     * {@link Monitor}-instance.
     *
     * @return true if all dependencies are known, otherwise false
     */
    public Boolean isValid() {
        return !(probeService == null
                || wpsDaoFactory == null
                || qosDaoFactory == null
                || wpsProcessDaoFactory == null
                || wpsClientFactory == null
                || propertiesFile == null);
    }

    public MonitorControlImpl buildMonitorControl() throws BuilderException {
        MonitorControlImpl control = null;

        if (isValid()) {
            try {
                control = new MonitorControlImpl(buildSchedulerControl(),
                        getEventHandler(),
                        qosDaoFactory,
                        wpsDaoFactory,
                        wpsProcessDaoFactory
                );
            } catch (SchedulerException ex) {
                throw new BuilderException(ex.toString());
            } catch (CreateException ex) {
                throw new BuilderException(ex.toString());
            }
        }

        return control;
    }

    /**
     * Build a {@link Monitor} instance with all necessary dependencies. This
     * MonitorBuilder instance will be a part of the Monitor-instance. Be aware
     * to recycle this MonitorBuilder instance.
     *
     * @return Monitor instance
     * @throws BuilderException
     */
    public Monitor build() throws BuilderException {

        try {
            if (!isValid()) {
                throw new BuilderException("Builder state is not valid because of some dependencies are missing. Call setupDefault() first.");
            }

            setupEventHandler();
            setupMonitorConfig();
            
            WpsClientConfig wpsClientConfig = new WpsClientConfig();
            wpsClientConfig.setConnectionTimeout(monitorConfig.getWpsClientTimeout());
            
            withWpsClientConfig(wpsClientConfig);
            
            return new Monitor(this);
        } catch (MonitorConfigException ex) {
            throw new BuilderException(ex.toString());
        }
    }

    /**
     * Sets the MonitorEventHandler up.
     */
    private void setupEventHandler() {
        if (this.eventHandler == null) {
            this.eventHandler = buildEventHandler();
        }
    }

    public ProbeService getProbeService() {
        return probeService;
    }

    public QosDaoFactory getQosDaoFactory() {
        return qosDaoFactory;
    }

    public WpsDaoFactory getWpsDaoFactory() {
        return wpsDaoFactory;
    }

    public WpsProcessDaoFactory getWpsProcessDaoFactory() {
        return wpsProcessDaoFactory;
    }

    public WpsClientFactory getWpsClientFactory() {
        return wpsClientFactory;
    }

    public MonitorEventHandler getEventHandler() {
        setupEventHandler();
        return eventHandler;
    }

    public WpsClientConfig getWpsClientConfig() {
        return wpsClientConfig;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public MonitorConfig getMonitorConfig() {
        return monitorConfig;
    }
}
