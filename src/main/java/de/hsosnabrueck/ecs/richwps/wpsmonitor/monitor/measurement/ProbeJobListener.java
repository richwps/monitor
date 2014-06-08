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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ProbeJobListener implements JobListener {
    private final WpsProcessDataAccess dao;
    
    public ProbeJobListener(final WpsProcessDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getName() {
        return ProbeJobListener.class.getName();
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

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Job generalJob = context.getJobInstance();

        if (generalJob instanceof MeasureJob) {
            MeasureJob specificJob = (MeasureJob)generalJob;
            
            if(specificJob.cantMeasure()) {
                // markiere, dass ein problem aufgetreten ist
                WpsProcessEntity process = specificJob.getProcessEntity();
                process.setWpsException(true);
                
                dao.merge(process);
                
                try {
                    // delete Job! must be re-created if the problem is solved
                    context.getScheduler().deleteJob(JobKey.jobKey(process.getIdentifier(), process.getWps().getIdentifier()));
                } catch (SchedulerException ex) {
                    Logger.getLogger(ProbeJobListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
