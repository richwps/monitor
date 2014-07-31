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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfigException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.MeasureJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.clean.CleanUpJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.clean.CleanUpJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControlImpl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.SchedulerControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.JobFactoryService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Calendar;
import java.util.logging.Level;
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
 * create a WPS and trigger processes for it). Call start() or shutdown() to
 * start or stop the monitor. The monitor fires a monitor.shutdown-event if
 * shutdown() is called. shutdown() will be called through a shutdownHook if the
 * monitor is not shut down already. Also a cleanUp-job is registred, which will
 * try to clean up old measurements. This behavior can be configured in the
 * monitor.properties file.
 *
 * This instance initializt the Monitor with the MonitorBuilder instance.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Monitor {

    private static final Logger LOG = LogManager.getLogger();

    private MonitorControlImpl monitorControl;
    private MonitorBuilder builderInstance;
    private MonitorConfig config;
    private MonitorEventHandler eventHandler;

    public Monitor(MonitorBuilder builder) throws MonitorConfigException {
        Validate.notNull(builder, "builder");

        initMonitorWithBuilder(builder);
        registerShutdownHook();
    }

    /**
     * Starts the monitor if not already running.
     * 
     * @throws MonitorException 
     */
    public void start() throws MonitorException {
        if (!isActive()) {
            beforeStart();

            eventHandler
                    .fireEvent(new MonitorEvent("monitor.start"));

            try {
                monitorControl
                        .getSchedulerControl()
                        .start();
            } catch (SchedulerException ex) {
                throw new MonitorException("Can't start monitor.", ex);
            }
        }
    }

    /**
     * Shutting down the monitor if it is running.
     * 
     * @throws MonitorException 
     */
    public void shutdown() throws MonitorException {
        if (isActive()) {
            LOG.debug("Monitor shutdown.");

            eventHandler
                    .fireEvent(new MonitorEvent("monitor.shutdown"));

            config.save();
            try {
                monitorControl.getSchedulerControl()
                        .shutdown();
            } catch (SchedulerException ex) {
                throw new MonitorException("Can't shutdown monitor.", ex);
            }
        }
    }

    public void restart() throws MonitorException, MonitorConfigException {
        eventHandler
                .fireEvent(new MonitorEvent("monitor.restart"));

        shutdown();
        
        try {
            builderInstance.reConfigure();
        } catch (BuilderException ex) {
            throw new MonitorException("A Builder Exception is occourd.", ex);
        }
        
        initMonitorWithBuilder(builderInstance);
        start();
    }

    private void initMonitorWithBuilder(MonitorBuilder builder) throws MonitorConfigException {
        try {
            this.monitorControl = builder.buildMonitorControl();
            this.builderInstance = builder;
            this.config = builder.getMonitorConfig();

            // to prevent that all listeners must be reregistred after a restart
            MonitorEventHandler tmpEventHandler = builder.getEventHandler();

            if (eventHandler != null) {
                this.eventHandler.merge(tmpEventHandler);
            } else {
                this.eventHandler = tmpEventHandler;
            }
        } catch (BuilderException ex) {
            throw new AssertionError("Builder exception at initialising procedure of the monitor instance. Execution aborted.", ex);
        }
    }

    private void registerShutdownHook() {
        // Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    shutdown();
                } catch (Exception ex) {
                    // catch all exceptions, because this is 
                    // a critical point of the JVM shutdown process 
                    LOG.error("Unknown Exception occourd at shutdown hook.", ex);
                }
            }
        });
    }

    private void cleanupJob() {
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
        } catch (SchedulerException ex) {
            LOG.error("Exception occourd at configuring the CleanUpJob.", ex);
        }
    }

    private Trigger getCleanupTrigger(final TriggerKey triggerKey, final JobKey jobKey) {
        Integer hour = config.getDeleteTime()
                .get(Calendar.HOUR_OF_DAY);
        Integer minute = config.getDeleteTime()
                .get(Calendar.MINUTE);

        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .startAt(DateBuilder.todayAt(hour, minute, 0))
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInDays(1)
                ).build();
    }

    private void beforeStart() {
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
        } catch (CreateException | BuilderException ex) {
            throw new AssertionError("Can't setup the Jobfactories. Execution aborted.", ex);
        } 
    }

    public MonitorControl getMonitorControl() {
        return monitorControl;
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

    public MonitorConfig getConfig() {
        return config;
    }

    public ProbeService getProbeService() {
        try {
            return builderInstance
                    .getProbeService();
        } catch (BuilderException ex) {
            // at this point, this exception should never be thrown
            throw new AssertionError("ProbeService seems to not builded yet.", ex);
        }
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

            LOG.error("Can't check if the Scheduler is active.", ex);
        }

        return active;
    }
}
