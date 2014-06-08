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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.AbstractDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobFactory implements JobFactory {

    private final ProbeService probeService;
    private final WpsProcessDataAccess processDao;

    public MeasureJobFactory(final ProbeService probeService, final WpsProcessDataAccess processDao) {
        this.probeService = Param.notNull(probeService, "probeService");
        this.processDao = Param.notNull(processDao, "processDao");
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job job = null;
        JobDetail jobDetail = bundle.getJobDetail();

        try {
            if (jobDetail.getJobClass().equals(MeasureJob.class)) {
                String jobNameAsProcess = jobDetail.getKey().getName();
                String jobGroupAsWps = jobDetail.getKey().getGroup();

                AbstractDataAccess dao = new QosDataAccess();
                WpsProcessEntity process = processDao.find(jobGroupAsWps, jobNameAsProcess);

                job = new MeasureJob(probeService.probesFactory(), process, dao);

            } else {
                job = bundle.getJobDetail().getJobClass().newInstance();
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MeasureJobFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MeasureJobFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return job;
    }

}
