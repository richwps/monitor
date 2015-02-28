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
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientConfig;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.simple.SimpleWpsClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.control.MeasureJobListener;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.SchedulerControlService;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.control.threadsave.ThreadSaveMonitorControlService;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.JobFactoryService;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.SchedulerFactory;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import de.hsos.ecs.richwps.wpsmonitor.data.config.MonitorConfigException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.Jpa;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.QosDaoDefaultFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.WpsDaoDefaultFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.WpsProcessDaoDefaultFactory;
import de.hsos.ecs.richwps.wpsmonitor.measurement.ProbeService;
import de.hsos.ecs.richwps.wpsmonitor.creation.BuilderException;
import java.io.File;
import org.quartz.Scheduler;

/**
 * Builder pattern to build a Monitor-instance. First, call setupDefault() and
 * then personalize the build with the with-methods. If an exception occurs, the
 * builder will catch the exception and rethrow it as a
 * {@link BuilderException}.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorBuilder {

    private final MonitorBuilderCollector storage;

    /**
     * Config object for the monitor instance
     */
    private MonitorConfig monitorConfig;

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
     * WpsClient-Factory.
     */
    private WpsClientFactory wpsClientFactory;

    /**
     * Jpa instance which holds the EntityManagerFactory - will be used if the
     * default dataaccess implementation is used
     */
    private Jpa jpaInstance;

    /**
     * The default MeasureJobListener
     */
    private MeasureJobListener jobListener;

    /**
     * Creates a MonitorBuilder instance which can be used to configure and
     * create a Monitor instance.
     */
    public MonitorBuilder() {
        this.storage = new MonitorBuilderCollector();
    }

    /**
     * Builds a {@link Monitor} instance with all necessary dependencies. This
     * MonitorBuilder instance will be a part of the Monitor-instance. Be aware
     * to recycle this MonitorBuilder instance.
     *
     * @return Monitor instance
     * @throws BuilderException
     */
    public Monitor build() throws BuilderException {
        try {
            buildAllPieces();
            storage.addJobListener(jobListener);

            setupEventHandler();

            if (!storage.isValid()) {
                throw new BuilderException("The State of the MonitorBuilder Instance is not valid.");
            }

            Monitor m = new Monitor(this);

            if (isJpaUsed()) {
                m.addShutdownRoutine(jpaInstance);
            }

            return m;
        } catch (MonitorConfigException ex) {
            throw new BuilderException(ex);
        }
    }

    /**
     * Configures the Builder-instance which default values.
     *
     * @return MonitorBuilder instance
     * @throws BuilderException
     */
    public MonitorBuilder setupDefault() throws BuilderException {
        return withDefaultEventHandler()
                .withDefaultJobFactoryService()
                .withDefaultProbeService()
                .withDefaultPropertiesFile()
                .withDefaultQosDefaultDaoFactory()
                .withDefaultWpsDefaultClientFactory()
                .withDefaultWpsDefaultDaoFactory()
                .withDefaultWpsProcessDefaultDaoFactory();
    }

    /**
     * re configures the builder instance.
     *
     * @throws BuilderException
     */
    public void reConfigure() throws BuilderException {
        storage.remove(jobListener);
        buildAllPieces();
        storage.addJobListener(jobListener);
    }

    private MonitorBuilder buildAllPieces() throws BuilderException {
        monitorConfig = buildMonitorConfig();
        qosDaoFactory = buildQosDaoFactory();
        wpsDaoFactory = buildWpsDaoFactory();
        wpsProcessDaoFactory = buildWpsProcessDaoFactory();
        wpsClientFactory = buildWpsClientFactory(buildWpsClientConfig(monitorConfig));
        jobListener = buildMeasureJobListener();

        return this;
    }

    private Jpa getJpaInstance() throws BuilderException {
        if (jpaInstance == null) {
            jpaInstance = new Jpa(storage.getPersistenceUnit());
        }

        return jpaInstance;
    }

    /**
     * Gets the ProbeService-Instance. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return ProbeService-Instance
     * @throws BuilderException
     */
    public ProbeService getProbeService() throws BuilderException {
        return storage.getProbeService();
    }

    /**
     * Sets the {@link ProbeService} instance.
     *
     * @param probeService ProbeService instance which should be used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withProbeService(final ProbeService probeService) {
        storage.setProbeService(probeService);

        return this;
    }

    /**
     * Gets the JobFactoryService-Instance. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return JobFactoryService-Instance.
     * @throws BuilderException
     */
    public JobFactoryService getJobFactoryService() throws BuilderException {
        return storage.getJobFactoryService();
    }

    /**
     * Sets the {@link JobFactoryService}-instance.
     *
     * @param jobFactoryService JobFactoryService instance which should be used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withJobFactoryService(final JobFactoryService jobFactoryService) {
        storage.setJobFactoryService(jobFactoryService);

        return this;
    }

    /**
     * Gets the WpsClient-Default factory. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return Factory&lt;WpsClient>
     * @throws BuilderException
     */
    public Factory<WpsClient> getWpsDefaultClientFactory() throws BuilderException {
        return storage.getWpsDefaultClientFactory();
    }

    /**
     * Sets the default client factory instance.
     *
     * @param wpsDefaultClientFactory Factory&lt;WpsClient> which should be used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsDefaultClientFactory(final Factory<WpsClient> wpsDefaultClientFactory) {
        storage.setWpsDefaultClientFactory(wpsDefaultClientFactory);

        return this;
    }

    /**
     * Gets the default QosDaoFactory instance. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return Factory&lt;QosDataAccess> instance
     * @throws BuilderException
     */
    public Factory<QosDataAccess> getQosDefaultDaoFactory() throws BuilderException {
        return storage.getQosDefaultDaoFactory();
    }

    /**
     * Sets the default QosDaoFactory instance.
     *
     * @param qosDefaultDaoFactory Factory&lt;QosDataAccess> instance which
     * should be used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withQosDefaultDaoFactory(final Factory<QosDataAccess> qosDefaultDaoFactory) {
        storage.setQosDefaultDaoFactory(qosDefaultDaoFactory);

        return this;
    }

    /**
     * Gets the defaul WpsDaoFactory instance. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return Factory&lt;WpsDataAccess> instance
     * @throws BuilderException
     */
    public Factory<WpsDataAccess> getWpsDefaultDaoFactory() throws BuilderException {
        return storage.getWpsDefaultDaoFactory();
    }

    /**
     * Sets the default WpsDaoFactory instance.
     *
     * @param wpsDefaultDaoFactory Factory&lt;WpsDataAccess> instance which
     * should be use
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsDefaultDaoFactory(final Factory<WpsDataAccess> wpsDefaultDaoFactory) {
        storage.setWpsDefaultDaoFactory(wpsDefaultDaoFactory);

        return this;
    }

    /**
     * Gets the {@link WpsProcessDataAccess}-Factory instance. If the value is
     * not set, a BuilderException instance will be thrown.
     *
     * @return Factory&lt;WpsProcessDataAccess> instance
     * @throws BuilderException
     */
    public Factory<WpsProcessDataAccess> getWpsProcessDefaultDaoFactory() throws BuilderException {
        return storage.getWpsProcessDefaultDaoFactory();
    }

    /**
     * Sets the default WpsProcessDaoFactory instance.
     *
     * @param wpsProcessDefaultDaoFactory Factory&lt;WpsProcessDataAccess>
     * instance which should be used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withWpsProcessDefaultDaoFactory(final Factory<WpsProcessDataAccess> wpsProcessDefaultDaoFactory) {
        storage.setWpsProcessDefaultDaoFactory(wpsProcessDefaultDaoFactory);

        return this;
    }

    /**
     * Gets the {@link MonitorEventHandler} instance. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return {@link MonitorEventHandler} instance
     * @throws BuilderException
     */
    public MonitorEventHandler getEventHandler() throws BuilderException {
        return storage.getEventHandler();
    }

    /**
     * Sets the {@link MonitorEventHandler} instance.
     *
     * @param eventHandler {@link MonitorEventHandler} instance which should be
     * used
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withEventHandler(final MonitorEventHandler eventHandler) {
        storage.setEventHandler(eventHandler);

        return this;
    }

    /**
     * Gets the File instance which points to the properties file. If the value
     * is not set, a BuilderException instance will be thrown.
     *
     * @return File instance
     * @throws BuilderException
     */
    public File getPropertiesFile() throws BuilderException {
        return storage.getPropertiesFile();
    }

    /**
     * Sets the properties file.
     *
     * @param propertiesFile File instance
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withPropertiesFile(final File propertiesFile) {
        storage.setPropertiesFile(propertiesFile);

        return this;
    }

    /**
     * Sets the properties file by filename string.
     *
     * @param propertiesFile String to a file which should exists
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withPropertiesFile(final String propertiesFile) {
        File pFile = null;

        if (propertiesFile != null && !"".equals(propertiesFile.trim())) {
            pFile = new File(propertiesFile);
        }

        storage.setPropertiesFile(pFile);

        return this;
    }

    /**
     * Gets the name of the persistence unit. If the value is not set, a
     * BuilderException instance will be thrown.
     *
     * @return Name of the persitence unit
     * @throws BuilderException
     */
    public String getPersistenceUnit() throws BuilderException {
        return storage.getPersistenceUnit();
    }

    /**
     * Sets the name of the persistence unit. This value is only needed, if the
     * default JPA implemenation is used.
     *
     * @param persistenceUnit Name of the persistence unit
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withPersistenceUnit(final String persistenceUnit) {
        storage.setPersistenceUnit(persistenceUnit);

        return this;
    }

    /**
     * Sets the default {@link ProbeService} instance.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultProbeService() {
        return withProbeService(new ProbeService());
    }

    /**
     * Sets the default {@link JobFactoryService} instance.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultJobFactoryService() {
        return withJobFactoryService(new JobFactoryService());
    }

    /**
     * Sets the default WpsClientFactory. By default the
     * {@link de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.simple.SimpleWpsClient}
     * Implementation is used. This implementation is very simple and does not
     * support chung request or some other nice feature.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultWpsDefaultClientFactory() {
        return withWpsDefaultClientFactory(new SimpleWpsClientFactory());
    }

    /**
     * Sets the default {@link QosDataAccess} factory instance. By default,
     * {@link QosDaoDefaultFactory} is used and the {@link Jpa} class will be
     * instantiated once and used for every other default
     * {@link de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.DataAccess}
     * implementation.
     *
     * @return MonitorBuilder instance
     * @throws BuilderException
     */
    public MonitorBuilder withDefaultQosDefaultDaoFactory() throws BuilderException {
        return withQosDefaultDaoFactory(new QosDaoDefaultFactory(getJpaInstance()));
    }

    /**
     * Sets the default {@link WpsDataAccess} factory instance. By default,
     * {@link WpsDaoDefaultFactory} is used and the {@link Jpa} class will be
     * instantiated once and used for every other default
     * {@link de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.DataAccess}
     * implementation.
     *
     * @return MonitorBuilder instance
     * @throws BuilderException
     */
    public MonitorBuilder withDefaultWpsDefaultDaoFactory() throws BuilderException {
        return withWpsDefaultDaoFactory(new WpsDaoDefaultFactory(getJpaInstance()));
    }

    /**
     * Sets the default {@link WpsProcessDataAccess} factory instance. By
     * default, {@link WpsProcessDaoDefaultFactory} is used and the {@link Jpa}
     * class will be instantiated once and used for every other default
     * {@link de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.DataAccess}
     * implementation.
     *
     * @return MonitorBuilder instance
     * @throws BuilderException
     */
    public MonitorBuilder withDefaultWpsProcessDefaultDaoFactory() throws BuilderException {
        return withWpsProcessDefaultDaoFactory(new WpsProcessDaoDefaultFactory(getJpaInstance()));
    }

    /**
     * Sets the {@link MonitorEventHandler} as default instance.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultEventHandler() {
        return withEventHandler(new MonitorEventHandler());
    }

    /**
     * Sets the default Properties file. By default, the file with the name
     * monitor.properties is used. If the file does not exists, the monitor will
     * create the file.
     *
     * @return MonitorBuilder instance
     */
    public MonitorBuilder withDefaultPropertiesFile() {
        return withPropertiesFile("monitor.properties");
    }

    /**
     * Creates the {@link WpsClientFactory} with the given
     * {@link WpsClientConfig}-Instance.
     *
     * @param wpsClientConfig
     * @return {@link WpsClientFactory} instance
     * @throws BuilderException
     */
    public WpsClientFactory buildWpsClientFactory(final WpsClientConfig wpsClientConfig) throws BuilderException {
        return new WpsClientFactory(storage.getWpsDefaultClientFactory(), wpsClientConfig);
    }

    /**
     * Creates the {@link QosDaoFactory} instance.
     *
     * @return {@link QosDaoFactory} instance
     * @throws BuilderException
     */
    public QosDaoFactory buildQosDaoFactory() throws BuilderException {
        return new QosDaoFactory(storage.getQosDefaultDaoFactory());
    }

    /**
     * Creates the {@link WpsDaoFactory} instance.
     *
     * @return {@link WpsDaoFactory} instance
     * @throws BuilderException
     */
    public WpsDaoFactory buildWpsDaoFactory() throws BuilderException {
        return new WpsDaoFactory(storage.getWpsDefaultDaoFactory());
    }

    /**
     *
     * @return @throws BuilderException
     */
    public WpsProcessDaoFactory buildWpsProcessDaoFactory() throws BuilderException {
        return new WpsProcessDaoFactory(storage.getWpsProcessDefaultDaoFactory());
    }

    private MeasureJobListener buildMeasureJobListener() throws BuilderException {
        return new MeasureJobListener(buildWpsProcessDaoFactory(), storage.getEventHandler());
    }

    private MonitorConfig buildMonitorConfig() throws BuilderException {
        try {
            return new MonitorConfig(storage.getPropertiesFile());
        } catch (MonitorConfigException ex) {
            throw new BuilderException(ex);
        }
    }

    private SchedulerControlService buildSchedulerControl() throws BuilderException {
        SchedulerFactory schedulerFactory = new SchedulerFactory(storage.getJobFactoryService(), storage.getJobListeners());

        try {
            Scheduler scheduler = schedulerFactory.create();
            return new SchedulerControlService(scheduler, storage.getJobFactoryService());
        } catch (CreateException ex) {
            throw new BuilderException(ex);
        }
    }

    /**
     * Builds the
     * {@link de.hsos.ecs.richwps.wpsmonitor.control.MonitorControl}-instance
     * with the needed dependencies.
     *
     * @return {@link ThreadSaveMonitorControlService} instance.
     * @throws BuilderException
     */
    public ThreadSaveMonitorControlService buildMonitorControl() throws BuilderException {
        return new ThreadSaveMonitorControlService(buildSchedulerControl(), storage.getEventHandler(), qosDaoFactory, wpsDaoFactory, wpsProcessDaoFactory);
    }

    private WpsClientConfig buildWpsClientConfig(final MonitorConfig config) throws BuilderException {
        WpsClientConfig wpsClientConfig = new WpsClientConfig();
        wpsClientConfig.setConnectionTimeout(config.getWpsClientTimeout());

        return wpsClientConfig;
    }

    private void setupEventHandler() throws BuilderException {
        storage.getEventHandler()
                .registerEvent("scheduler.wpsjob.wasexecuted");
        storage.getEventHandler()
                .registerEvent("measurement.wpsjob.wpsexception");
        storage.getEventHandler()
                .registerEvent("monitor.start");
        storage.getEventHandler()
                .registerEvent("monitor.restart");
        storage.getEventHandler()
                .registerEvent("monitor.shutdown");
    }

    private Boolean isJpaUsed() {
        return jpaInstance != null;
    }

    /**
     * Gets the configured {@link MonitorConfig}-instance.
     *
     * @return {@link MonitorConfig}-instance
     */
    public MonitorConfig getMonitorConfig() {
        return monitorConfig;
    }

    /**
     * Gets the configured QosDaoFactory instance.
     *
     * @return Factory&lt;QosDataAcccess> instance
     */
    public QosDaoFactory getQosDaoFactory() {
        return qosDaoFactory;
    }

    /**
     * Gets the configured WpsDaoFactory instance.
     *
     * @return Factory&lt;WpsDataAccess> instance
     */
    public WpsDaoFactory getWpsDaoFactory() {
        return wpsDaoFactory;
    }

    /**
     * Gets the configured WpsProcessDaoFactory instance.
     *
     * @return Factory&lt;WpsProcessDataAccess> instance
     */
    public WpsProcessDaoFactory getWpsProcessDaoFactory() {
        return wpsProcessDaoFactory;
    }

    /**
     * Gets the configured WpsClientFactory instance.
     *
     * @return Factory&lt;WpsClient> instance
     */
    public WpsClientFactory getWpsClientFactory() {
        return wpsClientFactory;
    }
}
