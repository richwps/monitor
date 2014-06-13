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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl.QosDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl.WpsDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl.WpsProcessDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.SchedulerFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.Map;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Builder {
    private ProbeService probeService;
    
    private QosDaoFactory qosDaoFactory;
    private WpsDaoFactory wpsDaoFactory;
    private WpsProcessDaoFactory wpsProcessDaoFactory;
    
    public Builder withProbeService(ProbeService probeService) {
        this.probeService = Param.notNull(probeService, "probeService");
        
        return this;
    }
    
    public Builder withQosDaoFactory(QosDaoFactory qosDaoFactory) {
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");
        
        return this;
    }
    
    public Builder withWpsDaoFactory(WpsDaoFactory wpsDaoFactory) {
        this.wpsDaoFactory = Param.notNull(wpsDaoFactory, "wpsDaoFactory");
        
        return this;
    }
    
    public Builder withWpsProcessDaoFactory(WpsProcessDaoFactory wpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDaoFactory, "wpsProcessDaoFactory");
        
        return this;
    }
    
    public Builder withQosDaoFactory(Factory<QosDataAccess> defaultQosDaoFactory) {
        this.qosDaoFactory = new QosDaoFactory(defaultQosDaoFactory);
        
        return this;
    }
    
    public Builder withWpsDaoFactory(Factory<WpsDataAccess> defaultWpsDaoFactory) {
        this.wpsDaoFactory = new WpsDaoFactory(defaultWpsDaoFactory);
        
        return this;
    }
    
    public Builder withWpsProcessDaoFactory(Factory<WpsProcessDataAccess> defaultWpsProcessDaoFactory) {
        this.wpsProcessDaoFactory = new WpsProcessDaoFactory(defaultWpsProcessDaoFactory);
        
        return this;
    }
    
    public Builder withDefaultProbeService() {
        return withProbeService(new ProbeService());
    }
    
    public Builder withDefaultQosDaoFactory() {
        return withQosDaoFactory(new QosDaoDefaultFactory());
    }
    
    public Builder withDefaultWpsDaoFactory() {
        return withWpsDaoFactory(new WpsDaoDefaultFactory());
    }
    
    public Builder withDefaultWpsProcessDaoFactory() {
        return withWpsProcessDaoFactory(new WpsProcessDaoDefaultFactory());
    }

    public Builder setupDefault() {
        return withDefaultProbeService()
                .withDefaultQosDaoFactory()
                .withDefaultWpsDaoFactory()
                .withDefaultWpsProcessDaoFactory();
    }
    
    private SchedulerFactory setupSchedulerFactory() {
        if(this.probeService == null) {
            withDefaultProbeService();
        }
        
        if(this.wpsProcessDaoFactory == null) {
            withDefaultWpsProcessDaoFactory();
        }
        
        if(this.qosDaoFactory == null) {
            withDefaultQosDaoFactory();
        }
        
        return new SchedulerFactory(probeService, wpsProcessDaoFactory, qosDaoFactory);
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
    
    public Boolean isValid() {
        return !(probeService == null || wpsDaoFactory == null || qosDaoFactory == null || wpsProcessDaoFactory == null);
    }
    
    public WpsDataAccess buildWpsDataAccess() {
        return wpsDaoFactory.create();
    }
    
    public WpsProcessDataAccess buildWpsProcessDataAccess() {
        return wpsProcessDaoFactory.create();
    }
    
    public QosDataAccess buildQosDataAccess() {
        return qosDaoFactory.create();
    }
    
    public Scheduler buildScheduler() throws SchedulerException {
        return setupSchedulerFactory().create();
    }
    
    public SchedulerControl buildSchedulerControl() throws SchedulerException {
        return new SchedulerControl(buildScheduler());
    }
    
    public Monitor build() throws SchedulerException {
        if(!isValid()) {
            setupDefault();
        }
        
        MonitorControl monitorControl = new MonitorControl(buildSchedulerControl(), 
                buildQosDataAccess(), 
                buildWpsDataAccess(), 
                buildWpsProcessDataAccess()
        );
        
        return new Monitor(monitorControl, this);
    }
}
