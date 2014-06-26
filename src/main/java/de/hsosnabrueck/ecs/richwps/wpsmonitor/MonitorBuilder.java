/*
 * Copyright 2014 FloH.
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
package de.hsosnabrueck.ecs.richwps.wpsmonitor;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClientConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.defaultimpl.SimpleWpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.QosDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.WpsDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.WpsProcessDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControlImpl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.JobFactoryService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorBuilder {

    private ProbeService probeService;
    private JobFactoryService jobFactoryService;
    private WpsClientFactory wpsClientFactory;
    private MonitorEventHandler eventHandler;

    private QosDaoFactory qosDaoFactory;
    private WpsDaoFactory wpsDaoFactory;
    private WpsProcessDaoFactory wpsProcessDaoFactory;

    public MonitorBuilder withProbeService(ProbeService probeService) {
        this.probeService = Param.notNull(probeService, "probeService");
        
        return this;
    }
    
    public MonitorBuilder withJobFactoryService(JobFactoryService jobFactoryService) {
        this.jobFactoryService = Param.notNull(jobFactoryService, "jobFactoryService");
        
        return this;
    }

    public MonitorBuilder withQosDaoFactory(QosDaoFactory qosDaoFactory) {
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");

        return this;
    }

    public MonitorBuilder withWpsDaoFactory(WpsDaoFactory wpsDaoFactory) {
        this.wpsDaoFactory = Param.notNull(wpsDaoFactory, "wpsDaoFactory");

        return this;
    }

    public MonitorBuilder withWpsProcessDaoFactory(WpsProcessDaoFactory wpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDaoFactory, "wpsProcessDaoFactory");

        return this;
    }

    public MonitorBuilder withQosDaoFactory(Factory<QosDataAccess> defaultQosDaoFactory) {
        this.qosDaoFactory = new QosDaoFactory(defaultQosDaoFactory);

        return this;
    }

    public MonitorBuilder withWpsDaoFactory(Factory<WpsDataAccess> defaultWpsDaoFactory) {
        this.wpsDaoFactory = new WpsDaoFactory(defaultWpsDaoFactory);

        return this;
    }

    public MonitorBuilder withWpsProcessDaoFactory(Factory<WpsProcessDataAccess> defaultWpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = new WpsProcessDaoFactory(defaultWpsProcessDaoFactory);

        return this;
    }

    public MonitorBuilder withWpsClientFactory(Factory<WpsClient> wpsClientFactory) {
        this.wpsClientFactory = new WpsClientFactory(wpsClientFactory);

        return this;
    }

    public MonitorBuilder withWpsClientFactory(WpsClientFactory wpsClientFactory) {
        this.wpsClientFactory = Param.notNull(wpsClientFactory, "wpsClientFactory");

        return this;
    }
    
    public MonitorBuilder withWpsClientConfig(WpsClientConfig config) {
        this.wpsClientFactory.setWpsClientConfig(config);
        
        return this;
    }

    public MonitorBuilder withDefaultProbeService() {
        return withProbeService(new ProbeService());
    }

    public MonitorBuilder withDefaultQosDaoFactory() {
        return withQosDaoFactory(new QosDaoDefaultFactory());
    }

    public MonitorBuilder withDefaultWpsDaoFactory() {
        return withWpsDaoFactory(new WpsDaoDefaultFactory());
    }

    public MonitorBuilder withDefaultWpsProcessDaoFactory() {
        return withWpsProcessDaoFactory(new WpsProcessDaoDefaultFactory());
    }

    public MonitorBuilder withDefaultWpsClient() {
        return withWpsClientFactory(new SimpleWpsClientFactory());
    }
    
    public MonitorBuilder withDefaultJobFactoryService() {
        return withJobFactoryService(new JobFactoryService());
    }

    public MonitorBuilder setupDefault() {
        return withDefaultProbeService()
                .withDefaultWpsClient()
                .withDefaultQosDaoFactory()
                .withDefaultWpsDaoFactory()
                .withDefaultWpsProcessDaoFactory()
                .withDefaultJobFactoryService();
    }
    
    private SchedulerFactory setupSchedulerFactory() throws CreateException {
        if (this.probeService == null) {
            withDefaultProbeService();
        }

        if (this.wpsProcessDaoFactory == null) {
            withDefaultWpsProcessDaoFactory();
        }

        if (this.qosDaoFactory == null) {
            withDefaultQosDaoFactory();
        }

        if (this.wpsClientFactory == null) {
            withDefaultWpsClient();
        }
        
        if(this.jobFactoryService == null) {
            withDefaultJobFactoryService();
        }
        
        /**
         * Important! 
         */
        MeasureJobFactory measureJobFactory = new MeasureJobFactory(probeService, wpsProcessDaoFactory.create(), qosDaoFactory, wpsClientFactory);
        jobFactoryService.put(MeasureJob.class, measureJobFactory);
        
        List<JobListener> jobListeners = new ArrayList<JobListener>();
        jobListeners.add(new MeasureJobListener(wpsProcessDaoFactory, eventHandler));
        
        /**
         * Important end
         */

        return new SchedulerFactory(jobFactoryService, jobListeners);
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
        return eventHandler;
    }

    public Boolean isValid() {
        return !(probeService == null
                || wpsDaoFactory == null
                || qosDaoFactory == null
                || wpsProcessDaoFactory == null
                || wpsClientFactory == null);
    }

    public WpsDataAccess buildWpsDataAccess() throws CreateException {
        return wpsDaoFactory.create();
    }

    public WpsClient buildWpsClient() throws CreateException {
        return wpsClientFactory.create();
    }

    public WpsProcessDataAccess buildWpsProcessDataAccess() throws CreateException {
        return wpsProcessDaoFactory.create();
    }

    public QosDataAccess buildQosDataAccess() throws CreateException {
        return qosDaoFactory.create();
    }

    public MonitorEventHandler buildEventHandler() {
        return new MonitorEventHandler();
    }

    public Scheduler buildScheduler() throws SchedulerException, CreateException {
        return setupSchedulerFactory().create();
    }

    public SchedulerControl buildSchedulerControl() throws SchedulerException, CreateException {
        return new SchedulerControl(buildScheduler());
    }

    public Monitor build() throws Exception {

        Monitor builded = null;

        try {
            if (!isValid()) {
                setupDefault();
            }

            setupEventHandler();

            MonitorControlImpl monitorControl = new MonitorControlImpl(buildSchedulerControl(),
                    qosDaoFactory,
                    wpsDaoFactory,
                    wpsProcessDaoFactory
            );

            builded = new Monitor(monitorControl, this);
        } catch (CreateException ex) {
            throw new Exception(ex);
        } catch (SchedulerException ex) {
            throw new Exception(ex);
        }

        return builded;
    }

    private void setupEventHandler() {
        this.eventHandler = buildEventHandler();

        this.eventHandler.registerEvent("scheduler.job.paused");
        this.eventHandler.registerEvent("scheduler.job.wasexecuted");
    }
}
