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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.wpsclient.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Create a new MeasureJob instance if the quartz scheduler starts a specific
 * job.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobFactory implements JobFactory {

    /**
     * Probeservice instance
     */
    private final ProbeService probeService;
    
    /**
     * Wps client factory - each job needs its own wps client
     */
    private final WpsClientFactory wpsClientFactory;
    
    /**
     * WpsProcessDataAccess instance
     */
    private final WpsProcessDataAccess processDao;
    
    /**
     * QosDaoFactory instance to create a new data access for a new job
     */
    private final QosDaoFactory qosDaoFactory;

    private final static Logger log = LogManager.getLogger();

    /**
     * Constructor.
     * 
     * @param probeService Probeservice instance
     * @param processDao WpsProcessDataAccess instance
     * @param qosDaoFactory QosDaoFactory instance to create a new data access for a new job
     * @param wpsClientFactory Wps client factory - each job should have its own WPS client instance
     */
    public MeasureJobFactory(final ProbeService probeService, final WpsProcessDataAccess processDao,
            final QosDaoFactory qosDaoFactory, final WpsClientFactory wpsClientFactory) {
        
        this.probeService = Param.notNull(probeService, "probeService");
        this.processDao = Param.notNull(processDao, "processDao");
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");
        this.wpsClientFactory = Param.notNull(wpsClientFactory, "wpsClientFactory");
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job job;
        JobDetail jobDetail = bundle.getJobDetail();

        if (jobDetail.getJobClass().equals(MeasureJob.class)) {
            // create new MeasureJob
            job = createNewMeasureJob(jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
        } else {
            // fallback to default instantiation of quartz
            job = new SimpleJobFactory().newJob(bundle, scheduler);
        }

        return job;
    }

    private Job createNewMeasureJob(String processAsJobName, String wpsAsGroupName) {
        Job measureJob = null;

        try {
            // jobs are eventually threads -
            // EntityManager and Dao's are not Thread save! So give them an own EntityManager
            QosDataAccess dao = qosDaoFactory.create();

            // for which WpsProcessEntity will this process created?
            WpsProcessEntity process = processDao.find(wpsAsGroupName, processAsJobName);

            measureJob = new MeasureJob(probeService.buildProbes(), process, dao, wpsClientFactory.create());
        } catch (CreateException ex) {
            log.fatal(ex);
        }

        return measureJob;
    }
}