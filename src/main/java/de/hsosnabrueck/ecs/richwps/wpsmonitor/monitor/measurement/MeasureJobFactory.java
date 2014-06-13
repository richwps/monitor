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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
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
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobFactory implements JobFactory {

    private ProbeService probeService;
    private WpsProcessDataAccess processDao;
    private QosDaoFactory qosDaoFactory;

    public MeasureJobFactory(final ProbeService probeService, final WpsProcessDataAccess processDao, final QosDaoFactory qosDaoFactory) {
        this.probeService = Param.notNull(probeService, "probeService");
        this.processDao = Param.notNull(processDao, "processDao");
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job job = null;
        JobDetail jobDetail = bundle.getJobDetail();

        try {
            if (jobDetail.getJobClass().equals(MeasureJob.class)) {
                // create new MeasureJob
                job = createNewMeasureJob(jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
            } else {
                // fallback to default instantiation of quartz
                job = new SimpleJobFactory().newJob(bundle, scheduler);
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MeasureJobFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MeasureJobFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return job;
    }

    private Job createNewMeasureJob(String processAsJobName, String wpsAsGroupName) throws InstantiationException, IllegalAccessException {
        // jobs are eventually threads - 
        // EntityManager and Dao's are not Thread save! So give them an own EntityManager
        QosDataAccess dao = qosDaoFactory.create();
        
        // for which WpsProcessEntity will this process created?
        WpsProcessEntity process = processDao.find(wpsAsGroupName, processAsJobName);

        return new MeasureJob(probeService.buildProbes(), process, dao);
    }
}
