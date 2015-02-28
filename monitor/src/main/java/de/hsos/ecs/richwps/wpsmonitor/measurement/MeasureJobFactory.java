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
package de.hsos.ecs.richwps.wpsmonitor.measurement;

import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClientFactory;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Creates a new MeasureJob instance if the quartz scheduler starts a specific
 * job.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJobFactory implements JobFactory {

    private final static Logger LOG = LogManager.getLogger();
    /**
     * Probeservice instance.
     */
    private final ProbeService probeService;

    /**
     * ProcessDao .. Select Process Informations after a restart
     */
    private final WpsProcessDataAccess processDao;

    /**
     * Wps client factory - each job needs its own wps client.
     */
    private final WpsClientFactory wpsClientFactory;

    /**
     * QosDaoFactory instance to create a new data access for a new job.
     */
    private final QosDaoFactory qosDaoFactory;

    /**
     * This Map is a cache to prevent multiple request for the WpsProcessEntity
     * of a MeasureJob. Because of every Job is created if fired, so for every
     * job a database query is also fired - the cache should prevent this
     * unecessary behavior. On the other hands, renames or endpoint changes are
     * not so good
     *
     * the Long template parameter is the wpsId
     */
    private final Map<Long, WpsProcessEntity> wpsProcessEntities;

    /**
     * Constructor.
     *
     * @param probeService Probeservice instance
     * @param processDao WpsProcessDataAccess instance
     * @param qosDaoFactory QosDaoFactory instance to create a new data access
     * for a new job
     * @param wpsClientFactory Wps client factory - each job should have its own
     * WPS client instance
     */
    public MeasureJobFactory(final ProbeService probeService, final WpsProcessDataAccess processDao,
            final QosDaoFactory qosDaoFactory, final WpsClientFactory wpsClientFactory) {

        this.probeService = Validate.notNull(probeService, "probeService");
        this.processDao = Validate.notNull(processDao, "processDao");
        this.qosDaoFactory = Validate.notNull(qosDaoFactory, "qosDaoFactory");
        this.wpsClientFactory = Validate.notNull(wpsClientFactory, "wpsClientFactory");

        this.wpsProcessEntities = new HashMap<>();
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        JobDetail jobDetail = bundle.getJobDetail();

        // create new MeasureJob, but getProcessEntity first
        WpsProcessEntity processEntity = getProcessEntity(
                Long.parseLong(jobDetail.getKey().getGroup()),
                jobDetail.getKey().getName()
        );

        if (processEntity == null) {
            throw new AssertionError("Fatal exception occourd. Can't create MeasureJob. The WpsProcessEntity instance was not registred before.");
        }

        return createNewMeasureJob(processEntity);
    }

    public void saveWpsProcessEntity(final WpsProcessEntity entity) {
        if (entity == null || entity.getWps() == null
                || entity.getWps().getId() == null) {
            throw new AssertionError("Malformed WpsProcessEntity given.");
        }

        LOG.debug("WpsProcessEntity instance for MesaureJob saved({}).", entity);
        wpsProcessEntities.put(entity.getWps().getId(), entity);
    }

    private Job createNewMeasureJob(final WpsProcessEntity wpsProcessEntity) {
        Job measureJob = null;

        try {
            // jobs are possibly threads -
            // DAOs are maybe not Thread save! So give them an own DAO.
            QosDataAccess dao = qosDaoFactory.create();

            LOG.debug("MeasureJob created for {}", wpsProcessEntity);
            measureJob = new MeasureJob(probeService.buildProbes(), wpsProcessEntity, dao, wpsClientFactory.create());
        } catch (CreateException ex) {
            throw new AssertionError("Fatal exception occourd. Can't create one of the Dependencies. Without these dependencies i can't do my work.", ex);
        }

        return measureJob;
    }

    /*
        Tries to get the WpsProcessEntity instance
    */
    private WpsProcessEntity getProcessEntity(final Long wpsId, final String processIdentifier) {
        WpsProcessEntity result = wpsProcessEntities.get(wpsId);

        if (result == null) {
            result = processDao.find(wpsId, processIdentifier);
        }

        return result;
    }
}
