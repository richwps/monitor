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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {
    private MonitorControl monitorControl;
    private final MonitorBuilder builderInstance;
    
    public Monitor(MonitorControl monitorControl, MonitorBuilder builder) {
        this.monitorControl = Param.notNull(monitorControl, "monitorControl");
        this.builderInstance = Param.notNull(builder, "builder");
    }
    
    public void start() throws SchedulerException {
        monitorControl.getSchedulerControl().start();
    }
    
    public void shutdown() throws SchedulerException {
        monitorControl.getSchedulerControl().shutdown();
    }

    public MonitorControl getMonitorControl() {
        return monitorControl;
    }
    
    public MonitorFacade getMonitorFacade() {
        return (MonitorFacade)monitorControl;
    }
    
    public MonitorEventHandler getEventHandler() {
        return builderInstance.getEventHandler();
    }

    public SchedulerControl getSchedulerControl() {
        return monitorControl.getSchedulerControl();
    }

    public MonitorBuilder getBuilderInstance() {
        return builderInstance;
    }
    
    public ProbeService getProbeService() {
        return builderInstance.getProbeService();
    }
}
