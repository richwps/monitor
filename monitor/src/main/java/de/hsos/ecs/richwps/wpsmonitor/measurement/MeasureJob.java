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

import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Pair;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * {@link Job} class which uses a wps client with a request which is stored in
 * the {@link WpsProcessEntity} object to be sent to a Wps Server. The
 * {@link WpsProcessEntity} object is injected by the {@link MeasureJobFactory}.
 *
 * If no wps exception occurs, then the registred QosProbe instances are called.
 * Otherwise a error flag is set, which is evaluated by the
 * {@link de.hsos.ecs.richwps.wpsmonitor.control.MeasureJobListener}.
 *
 * The dependencies should be thread save or new instantiated by the
 * {@link MeasureJobFactory}.
 *
 * @see WpsProcessEntity
 * @see MeasureJobFactory
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJob implements Job {

    private static final Logger LOG = LogManager.getLogger();

    protected final WpsProcessEntity processEntity;
    protected final QosDataAccess dao;
    protected WpsClient wpsClient;

    protected List<QosProbe> probes;
    protected Boolean error;

    protected Boolean fatalError;

    /**
     * Creates a new MeasureJob instance.
     *
     * @param probes List of QosProbe instances
     * @param entity WpsProcessEntity which the specific job take care of
     * @param dao QosDataAcces instance
     * @param wpsClient WpsClient instance
     */
    public MeasureJob(final List<QosProbe> probes, final WpsProcessEntity entity, final QosDataAccess dao, final WpsClient wpsClient) {
        this.probes = Validate.notNull(probes, "probeService");
        this.dao = Validate.notNull(dao, "dao");
        this.processEntity = Validate.notNull(entity, "entity");
        this.wpsClient = Validate.notNull(wpsClient, "wpsClient");

        this.error = false;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Pair<WpsRequest, WpsResponse> pair = callWps();
            WpsRequest request = pair.getLeft();
            WpsResponse response = pair.getRight();

            if (request.getRequestTime() == null
                    || !response.isConnectionException()
                    && response.getResponseTime() == null) {
                throw new JobExecutionException("Request- or response time was not set correctly.", false);
            }

            // if no execption occurs (except Connection exception), then call probes and store Data 
            error = response.isOtherException() || response.isWpsException();

            if (!error) {
                MeasuredDataEntity data = callProbes(request, response);
                persistMeasuredData(data, request.getRequestTime());
            }

            logInfo(context, response);
        } finally {
            dao.close();
        }
    }

    private void logInfo(final JobExecutionContext context, final WpsResponse response) {
        LOG.info("MeasureJob with JobKey {} and TriggerKey {} of Process {} executed! isWpsException: {} isConnectionException: {} isOtherException: {}",
                context.getJobDetail().getKey(),
                context.getTrigger().getKey(),
                processEntity.toString(),
                response.isWpsException() ? "true" : "false",
                response.isConnectionException() ? "true" : "false",
                response.isOtherException() ? "true" : "false"
        );
    }

    private void persistMeasuredData(final MeasuredDataEntity data, final Date measureDate) {
        data.setProcess(processEntity);
        data.setCreateTime(measureDate);

        dao.persist(data);
    }

    /**
     * Calls the probes with the request and response data.
     */
    private MeasuredDataEntity callProbes(final WpsRequest request, final WpsResponse response) {
        MeasuredDataEntity toPersist = new MeasuredDataEntity();

        for (QosProbe p : probes) {
            p.execute(request, response);

            AbstractQosEntity measuredData = p.getMeasuredData();

            if (measuredData != null) {
                toPersist.add(measuredData);
            }
        }

        return toPersist;
    }

    /**
     * Calls the WPS Server with the specified WPS client.
     *
     * @return a pair consisting of WpsRequest and WpsResponse
     */
    private Pair<WpsRequest, WpsResponse> callWps() {
        WpsProcessInfo info = new WpsProcessInfo(processEntity.getWps().getEndpoint(), processEntity.getIdentifier());

        WpsRequest request = new WpsRequest(processEntity.getRawRequest(), info);
        WpsResponse response = wpsClient.execute(request);

        // if wps exception, then retry
        if (response.isWpsException()) {
            response = wpsClient.execute(request);
        }

        return new Pair<>(request, response);
    }

    /**
     * Indicates, that this Job can't measure the speicified Wps Process because
     * of a WpsException or an other Exception.
     *
     * @return true if the job can't measure the Wps Process
     */
    public Boolean cantMeasure() {
        return error;
    }

    /**
     * Returns the Process Entity. This is the entity of the process which
     * should be monitored by the specific job.
     *
     * @return The specified Process Entity
     */
    public WpsProcessEntity getProcessEntity() {
        return processEntity;
    }

    /**
     * Returns the WpsClient instance.
     *
     * @return WpsClient instance
     */
    public WpsClient getWpsClient() {
        return wpsClient;
    }
}
