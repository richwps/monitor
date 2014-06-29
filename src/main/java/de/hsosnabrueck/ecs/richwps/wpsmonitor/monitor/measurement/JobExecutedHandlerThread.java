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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * Helper Thread for the {@link MeasureJobListener}. This approach tries to
 * relieve the main thread in which the {@link MeasureJobListener} is working.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JobExecutedHandlerThread extends Thread {

    private final JobExecutionContext executionContext;
    private final WpsProcessDataAccess wpsProcessDao;
    private final MonitorEventHandler eventHandler;

    private final static Logger log = LogManager.getLogger();

    public JobExecutedHandlerThread(final JobExecutionContext scheduler, final MonitorEventHandler eventHandler, final WpsProcessDataAccess wpsProcessDao) {
        this.executionContext = scheduler;
        this.wpsProcessDao = wpsProcessDao;
        this.eventHandler = eventHandler;

        super.setName("JobExecutedHandler");
    }

    @Override
    public void run() {
        Job generalJob = executionContext.getJobInstance();

        if (generalJob instanceof MeasureJob) {
            MeasureJob specificJob = (MeasureJob) generalJob;
            WpsProcessEntity process = specificJob.getProcessEntity();

            log.debug("MeasureJobListener: Fire scheduler.job.wasexecuted Event!");
            eventHandler
                    .fireEvent(new MonitorEvent("scheduler.wpsjob.wasexecuted", process));

            if (specificJob.cantMeasure()) {

                WpsProcessEntity find = wpsProcessDao.find(process.getWps().getIdentifier(), process.getIdentifier());
                // check if no thread was faster
                if (!find.isWpsException()) {
                    log.debug("MeasureJobListener: Can't measure process {}, because of WpsException || otherException!"
                            + " try to pause this job.", process);

                    JobKey jobKey = new JobKey(process.getIdentifier(), process.getWps().getIdentifier());

                    // markiere & persistiere, dass ein problem aufgetreten ist
                    process
                            .setWpsException(true);
                    wpsProcessDao
                            .update(process);

                    try {
                        // pause job if an error is triggered
                        executionContext
                                .getScheduler()
                                .pauseJob(jobKey);

                        // todo: maybe cleanup 
                        eventHandler
                                .fireEvent(new MonitorEvent("monitorcontrol.pauseMonitoring", process));

                        log.debug("MeasureJobListener: Fire monitor.wpsjob.wpsexception Event!");
                        eventHandler
                                .fireEvent(new MonitorEvent("measurement.wpsjob.wpsexception", process));
                    } catch (SchedulerException ex) {
                        log.error(ex);
                    }
                }
            }
        }
    }
}
