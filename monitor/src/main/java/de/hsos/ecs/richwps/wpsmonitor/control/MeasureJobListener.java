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

import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.measurement.MeasureJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Evaluates if a job can't be measured because of an exception. In addition,
 * the listener fires its own event over the MonitorEventHandler:
 * scheduler.job.wasexecuted with the process entity as message if a job was
 * executed and scheduler.job.paused with the process entity as message if a job
 * cant measure a wps process.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobListener implements JobListener {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * WpsProcessDataAccess instance.
     */
    private final WpsProcessDaoFactory wpsProcessDaoFactory;

    /**
     * MonitorEventHandler instance.
     */
    private final MonitorEventHandler eventHandler;

    /**
     * Constructor.
     *
     * @param dao WpsProcessDataAccess instance
     * @param eventHandler MonitorEventHandler instance
     */
    public MeasureJobListener(final WpsProcessDaoFactory dao, final MonitorEventHandler eventHandler) {
        this.wpsProcessDaoFactory = dao;
        this.eventHandler = eventHandler;
    }

    @Override
    public String getName() {
        return MeasureJobListener.class.getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        /*
         Nothing to do here yet.
         */
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        /*
         Nothing to do here yet.
         */
    }

    /**
     * Starts a new worker thread which handles the jobWasExecuted-Event because
     * within this event some DataAcces-interactions are happening which can
     * slow down the system because the listener runs in the mainthread and the
     * jobs runs in its own thread.
     *
     * @param context JobExecutionContext contexten, injected by quartz
     * @param jobException JobExecutionException injected by quartz
     */
    @Override
    public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
        try {
            if (context.getJobInstance() instanceof MeasureJob) {
                final MeasureJob specificJob = (MeasureJob) context.getJobInstance();

                if (jobException != null) {
                    throw new AssertionError(jobException.getMessage(), jobException);
                }

                Thread worker = new Thread() {
                    @Override
                    public void run() {
                        
                        WpsProcessEntity process = specificJob.getProcessEntity();

                        LOG.debug("MeasureJobListener: Fire scheduler.job.wasexecuted Event!");
                        eventHandler
                                .fireEvent(new MonitorEvent("scheduler.wpsjob.wasexecuted", process));

                        if (specificJob.cantMeasure()) {
                            try (WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create()) {
                                Scheduler scheduler = context.getScheduler();

                                handleCantMeasure(wpsProcessDao, scheduler, process);
                            } catch (CreateException ex) {
                                throw new AssertionError("Can't create Exception in Measure layer.", ex);
                            }
                        }
                    }
                };

                worker.start();
            }
        } catch (AssertionError ex) {
            // not clean, but save
            LOG.fatal("AssertionError occurred in Measure Layer. Can't throw this exception to main-Method. Exit the monitor here.", ex);
            System.exit(1);
        }
    }

    private void handleCantMeasure(final WpsProcessDataAccess wpsProcessDao, final Scheduler scheduler,
            final WpsProcessEntity wpsProcess) {

        Long wpsId = wpsProcess.getWps().getId();
        String processIdentifier = wpsProcess.getIdentifier();

        WpsProcessEntity find = wpsProcessDao.find(wpsId, processIdentifier);

        if (find != null && !find.isWpsException()) {
            wpsProcess.setWpsException(true);
            wpsProcessDao.update(wpsProcess);

            try {
                scheduler.pauseJob(new JobKey(wpsId.toString(), processIdentifier));

                eventHandler
                        .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", wpsProcess));
                eventHandler
                        .fireEvent(new MonitorEvent("measurement.wpsjob.wpsexception", wpsProcess));
            } catch (SchedulerException ex) {
                LOG.error("SchedulerException occured while trying to pause the errornous job.");
            }
        }
    }

}
