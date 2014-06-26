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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClientConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.clean.CleanUpJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Pair;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {

    private final MonitorControlImpl monitorControl;
    private final MonitorBuilder builderInstance;
    private final Pair<JobKey, TriggerKey> qosDeletePair;

    private final static Logger log;
    public final String MONITOR_ID;

    static {
        log = LogManager.getLogger();
    }

    public Monitor(MonitorControlImpl monitorControl, MonitorBuilder builder) {
        this.monitorControl = Param.notNull(monitorControl, "monitorControl");
        this.builderInstance = Param.notNull(builder, "builder");
        this.MONITOR_ID = UUID.randomUUID().toString();
        
        /**
         * Register a Job & Triggerkey for clean-up operation.
         * for this purpose, we need an unique group-id; so i gave 
         * every monitor object its own unique id :)
         */
        JobKey jobKey = new JobKey("deleteQosData", MONITOR_ID);
        TriggerKey triggerKey = new TriggerKey("deleteQosData", MONITOR_ID);

        qosDeletePair = new Pair<JobKey, TriggerKey>(jobKey, triggerKey);

        initGeneral();
    }

    private void initGeneral() {
        builderInstance.getEventHandler()
                .registerEvent("monitor.shutdown");

        // Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    shutdown();
                } catch (Exception ex) {
                    // catch all exceptions, because this is 
                    // a criticall point in the shutdown process of the JVM
                    log.error(ex);
                }
            }
        });
    }

    private void afterStart() throws SchedulerException {
        Boolean triggerRegistred = monitorControl
                .getSchedulerControl()
                .isTriggerRegistred(qosDeletePair.getRight());

        if (!triggerRegistred) {
            monitorControl
                    .getSchedulerControl()
                    .addJob(qosDeletePair.getLeft(), CleanUpJob.class);

            monitorControl
                    .getSchedulerControl()
                    .addPermaTriggerToJob(null, null); 
        }
    }

    public void start() throws SchedulerException {
        monitorControl
                .getSchedulerControl().start();

        //afterStart();
    }

    public void shutdown() throws SchedulerException {
        log.debug("Monitor shutdown.");

        getEventHandler()
                .fireEvent(new MonitorEvent("monitor.shutdown"));

        monitorControl.getSchedulerControl()
                .shutdown();
    }

    public MonitorControl getMonitorControl() {
        log.debug("getMonitorControl called by {}", Thread.currentThread().getName());
        return monitorControl;
    }

    public void setWpsClientConfig(final WpsClientConfig config) {
        this.builderInstance
                .withWpsClientConfig(config);
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
