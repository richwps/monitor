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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {

    private final MonitorControlImpl monitorControl;
    private final MonitorBuilder builderInstance;
    private final Pair<JobKey, TriggerKey> qosDeletePair;
    private final Properties properties;
    private Boolean withCleanup;

    private final static Logger log;
    private final static Properties defaultProperties;
    public final String MONITOR_ID;

    static {
        log = LogManager.getLogger();
        defaultProperties = new Properties();

        initDefaultProperties();
    }

    private static void initDefaultProperties() {
        defaultProperties.setProperty("qos.delete.afterdays", "360");
        defaultProperties.setProperty("qos.delete.attime", "9:00");
    }

    public Monitor(MonitorControlImpl monitorControl, File propertiesFile, MonitorBuilder builder) {
        this.monitorControl = Param.notNull(monitorControl, "monitorControl");
        this.builderInstance = Param.notNull(builder, "builder");
        this.MONITOR_ID = UUID.randomUUID().toString();
        this.properties = new Properties(defaultProperties);

        this.withCleanup = true;

        /**
         * Register a Job & Triggerkey for clean-up operation. for this purpose,
         * we need an unique group-id; so i give every monitor object its own
         * unique id :)
         */
        JobKey jobKey = new JobKey("deleteQosData", MONITOR_ID);
        TriggerKey triggerKey = new TriggerKey("deleteQosData", MONITOR_ID);

        qosDeletePair = new Pair<JobKey, TriggerKey>(jobKey, triggerKey);

        initProperties(Param.notNull(propertiesFile, "propertiesFile"));
        initGeneral();
    }

    private void initProperties(final File propertiesFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesFile);

            properties.load(fileInputStream);
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
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
                    // a criticall point of the JVM shutdown process 
                    log.error(ex);
                }
            }
        });
    }

    private void cleanupJob() throws SchedulerException {
        
        Boolean jobRegistred = monitorControl
                .getSchedulerControl()
                .isJobRegistred(qosDeletePair.getLeft());

        if (!jobRegistred) {
            monitorControl
                    .getSchedulerControl()
                    .addJob(qosDeletePair.getLeft(), CleanUpJob.class);
        }

        monitorControl.getSchedulerControl()
                .getScheduler()
                .rescheduleJob(qosDeletePair.getRight(), getCleanupTrigger());

        if (!withCleanup) {
            monitorControl
                    .getSchedulerControl()
                    .pauseJob(qosDeletePair.getLeft());
        }
    }

    private Trigger getCleanupTrigger() {
        Integer dayInterval, hour, minute;
        Integer[] dateInfos;

        try {
            dateInfos = cleanupPropertieHelper(properties);
        } catch (NumberFormatException ex) {
            dateInfos = cleanupPropertieHelper(defaultProperties);

            log.error(ex);
        }

        dayInterval = dateInfos[0];
        hour = dateInfos[1];
        minute = dateInfos[2];

        Trigger cleanupTrigger = TriggerBuilder.newTrigger()
                .withIdentity(qosDeletePair.getRight())
                .forJob(qosDeletePair.getLeft())
                .startAt(DateBuilder.tomorrowAt(hour, minute, 0))
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInDays(dayInterval)
                ).build();

        return cleanupTrigger;
    }

    private Integer[] cleanupPropertieHelper(Properties prop) {
        String[] split = prop.getProperty("qos.delete.attime")
                .split(":");

        Integer[] p = new Integer[]{
            Integer.parseInt(prop.getProperty("qos.delete.afterdays")),
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1])
        };

        return p;
    }

    private void afterStart() throws SchedulerException {
        cleanupJob();
    }

    public void start() throws SchedulerException {
        monitorControl
                .getSchedulerControl().start();

        afterStart();
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

    public Boolean isCleanup() {
        return withCleanup;
    }

    public void setCleanup(Boolean withCleanup) {
        if (withCleanup != null) {
            this.withCleanup = withCleanup;
        }
    }
}
