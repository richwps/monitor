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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Evaluates if a job cant measure because of an exception. In addition the
 * listener fire its own event over the MonitorEventHandler:
 * scheduler.job.wasexecuted with the process entity as message if an job was
 * executed and scheduler.job.paused with the process entity as message if a job
 * cant measure a wps process.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobListener implements JobListener {

    /**
     * WpsProcessDataAccess instance
     */
    private final WpsProcessDaoFactory wpsProcessDaoFactory;

    /**
     * MonitorEventHandler instance
     */
    private final MonitorEventHandler eventHandler;

    private final static Logger log = LogManager.getLogger();

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
         Nothing to do here yet
         */
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        /*
         Nothing to do here yet
         */
    }

    /**
     * Starts a new worker thread which handle the jobWasExecuted-Event because
     * of in this event happens some DataAcces-interactions which can slow the
     * system because of the listener runs in the mainthread and the jobs runs
     * in its own thread
     *
     * @see JobExecutedHandlerThread
     * @param context JobExecutionContext contexten, injected by quartz
     * @param jobException JobExecutionException injected by quartz
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            Thread handleJobWasExecuted = new JobExecutedHandlerThread(context, eventHandler, wpsProcessDaoFactory.create());
            handleJobWasExecuted.start();
        } catch (CreateException ex) {
            log.error(ex);
        }
    }

}
