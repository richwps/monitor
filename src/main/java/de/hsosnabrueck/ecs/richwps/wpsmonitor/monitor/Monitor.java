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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfigException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.wpsclient.WpsClientConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.wpsclient.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.clean.CleanUpJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.clean.CleanUpJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.MeasureJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControlImpl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.JobFactoryService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
import java.io.File;
import java.util.Calendar;
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
 * Representation of the WpsMonitor. To control the Monitor, call
 * getMonitorControl. MonitorControl is a facade to control the monitor (e.g.
 * create a Wps, trigger or something else). Call start() or shutdown() to start
 * or stop the monitor. The monitor fires a monitor.shutdown-event if shutdown is
 * called. shutdown() will be called through a shutdownHook if the monitor is
 * not already shutted down. Also a cleanUp-job is registred, which will try
 * to clean up old measurements. This behavior can be configured in the 
 * monitor.properties file.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {

    private final MonitorControlImpl monitorControl;
    private final MonitorBuilder builderInstance;
    private final MonitorConfig config;
    private final MonitorEventHandler eventHandler;

    private final static Logger log;

    static {
        log = LogManager.getLogger();
    }

    public Monitor(MonitorControlImpl monitorControl, File propertiesFile, MonitorEventHandler eventHandler,
            MonitorBuilder builder) throws MonitorConfigException {

        this.monitorControl = Param.notNull(monitorControl, "monitorControl");
        this.builderInstance = Param.notNull(builder, "builder");
        this.eventHandler = Param.notNull(eventHandler, "eventHandler");
        this.config = new MonitorConfig(propertiesFile);

        initGeneral();
    }

    private void initGeneral() {
        initEventHandler();

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

    private void initEventHandler() {
        eventHandler.registerEvent("scheduler.wpsjob.wasexecuted");
        eventHandler.registerEvent("measurement.wpsjob.wpsexception");
        eventHandler.registerEvent("monitor.shutdown");
    }

    private void cleanupJob() throws SchedulerException {
        try {
            SchedulerControl schedulerControl = monitorControl.getSchedulerControl();
            String schedulerName = schedulerControl.getScheduler()
                    .getSchedulerName();

            JobKey jobKey = new JobKey("deleteQosData", schedulerName);
            TriggerKey triggerKey = new TriggerKey("deleteQosData", schedulerName);

            Boolean jobRegistred = schedulerControl
                    .isJobRegistred(jobKey);

            Boolean triggerRegistred = schedulerControl
                    .isTriggerRegistred(triggerKey);

            Trigger cleanupTrigger = getCleanupTrigger(triggerKey, jobKey);

            if (!jobRegistred) {
                schedulerControl
                        .addJob(jobKey, CleanUpJob.class);
            }

            if (!triggerRegistred) {
                schedulerControl.getScheduler()
                        .scheduleJob(cleanupTrigger);
            }
            schedulerControl.getScheduler()
                    .rescheduleJob(triggerKey, cleanupTrigger);

            if (!config.isDeleteJobActiv()) {
                schedulerControl.pauseJob(jobKey);
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private Trigger getCleanupTrigger(final TriggerKey triggerKey, final JobKey jobKey) throws Exception {
        Integer hour = config.getDeleteTime()
                .get(Calendar.HOUR_OF_DAY);
        Integer minute = config.getDeleteTime()
                .get(Calendar.MINUTE);

        Trigger cleanupTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .startAt(DateBuilder.todayAt(hour, minute, 0))
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInDays(1)
                ).build();

        return cleanupTrigger;
    }

    private void afterStart() throws SchedulerException {

    }

    private void beforeStart() throws SchedulerException {
        setupJobFactories();
        cleanupJob();
    }

    private void setupJobFactories() {
        try {
            JobFactoryService jobFactoryService = monitorControl
                    .getSchedulerControl()
                    .getJobFactoryService();

            ProbeService probeService = builderInstance
                    .getProbeService();
            WpsProcessDataAccess wpsProcessDao = builderInstance
                    .getWpsProcessDaoFactory().create();
            QosDaoFactory qosDaoFactory = builderInstance
                    .getQosDaoFactory();
            WpsClientFactory wpsClientFactory = builderInstance
                    .getWpsClientFactory();

            MeasureJobFactory measureJobFactory = new MeasureJobFactory(probeService, wpsProcessDao, qosDaoFactory, wpsClientFactory);

            CleanUpJobFactory cleanupJobFactory = new CleanUpJobFactory(qosDaoFactory, config.getDeleteIntervalInDays());

            jobFactoryService.register(MeasureJob.class, measureJobFactory);
            jobFactoryService.register(CleanUpJob.class, cleanupJobFactory);
        } catch (CreateException ex) {
            log.fatal(ex);
        } catch (Exception ex) {
            log.fatal(ex);
        }
    }

    public void start() throws SchedulerException {
        if (!isActive()) {
            beforeStart();

            monitorControl
                    .getSchedulerControl()
                    .start();

            afterStart();
        }
    }

    public void shutdown() throws SchedulerException {
        if (isActive()) {
            log.debug("Monitor shutdown.");

            eventHandler
                    .fireEvent(new MonitorEvent("monitor.shutdown"));

            config.save();
            monitorControl.getSchedulerControl()
                    .shutdown();
        }
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
        return eventHandler;
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

    public MonitorConfig getConfig() {
        return config;
    }

    public Boolean isActive() {
        Boolean active;

        try {
            active = monitorControl
                    .getSchedulerControl()
                    .getScheduler()
                    .isStarted();
        } catch (SchedulerException ex) {
            active = false;

            log.error(ex);
        }

        return active;
    }
}
