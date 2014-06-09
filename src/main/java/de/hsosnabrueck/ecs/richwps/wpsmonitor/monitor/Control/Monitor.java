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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {
    private ProbeService probeService;
    private MonitorControl monitorControl;
    private SchedulerControl schedulerControl;
    
    public Monitor(final ProbeService probeService) throws SchedulerException {
        this.probeService = Param.notNull(probeService, "probeService");
        this.monitorControl = initMonitorControl();
        this.schedulerControl = initSchedulerControl();
    }
    
    private SchedulerControl initSchedulerControl() throws SchedulerException {
        return new SchedulerControl(new SchedulerFactory(probeService).getConfiguredScheduler());
    }
    
    private MonitorControl initMonitorControl() {
        return new MonitorControl(this);
    }

    public ProbeService getProbeService() {
        return probeService;
    }

    public void setProbeService(ProbeService probeService) {
        this.probeService = probeService;
    }

    public MonitorControl getMonitorControl() {
        return monitorControl;
    }

    public void setMonitorControl(MonitorControl monitorControl) {
        this.monitorControl = monitorControl;
    }

    public SchedulerControl getSchedulerControl() {
        return schedulerControl;
    }

    public void setSchedulerControl(SchedulerControl schedulerControl) {
        this.schedulerControl = schedulerControl;
    }
}
