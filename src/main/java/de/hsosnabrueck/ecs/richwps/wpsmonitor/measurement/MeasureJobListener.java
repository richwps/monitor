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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

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

    /**
     * WpsProcessDataAccess instance.
     */
    private final WpsProcessDaoFactory wpsProcessDaoFactory;

    /**
     * MonitorEventHandler instance.
     */
    private final MonitorEventHandler eventHandler;

    private static final Logger LOG = LogManager.getLogger();

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
     * @see JobExecutedHandlerThread
     * @param context JobExecutionContext contexten, injected by quartz
     * @param jobException JobExecutionException injected by quartz
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            if (context.getJobInstance() instanceof MeasureJob) {
                Thread handleJobWasExecuted = new JobExecutedHandlerThread(context, eventHandler, wpsProcessDaoFactory.create());
                handleJobWasExecuted.start();
            }
        } catch (CreateException ex) {
            LOG.error("Can't create wpsProcessDao to handel the JobWasExecuted Event. Exception was: {}", ex);
        }
    }

}
