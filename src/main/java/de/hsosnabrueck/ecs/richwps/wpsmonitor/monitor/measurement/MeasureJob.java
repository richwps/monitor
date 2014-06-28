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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsProcessInfo;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsResponse;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Pair;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job class which calls a wps client with a request, which is stored in the
 * WpsProcessEntity object. The WpsProcessEntity object is injected by the
 * MeasureJobFactory.
 *
 * If occours no wps exception, then the registred QosProbe instances are
 * called. Otherwise a error flag is set, which is evaluated by
 * MeasureJobListener.
 *
 * The dependencies should be thread save or new instantiated by the
 * MeasureJobFactory.
 *
 * @see WpsProcessEntity
 * @see MeasureJobListener
 * @see MeasureJobFactory
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJob implements Job {

    protected final WpsProcessEntity processEntity;
    protected final QosDataAccess dao;
    protected WpsClient wpsClient;

    protected List<QosProbe> probes;
    protected Boolean error;

    private final static Logger log = LogManager.getLogger();

    /**
     * Constructor.
     * 
     * @param probes List of QosProbe instances
     * @param entity WpsProcessEntity which the specific job take care of
     * @param dao QosDataAcces instance
     * @param wpsClient WpsClient instance
     */
    public MeasureJob(final List<QosProbe> probes, final WpsProcessEntity entity, final QosDataAccess dao, final WpsClient wpsClient) {
        this.probes = Param.notNull(probes, "probeService");
        this.dao = Param.notNull(dao, "dao");
        this.processEntity = Param.notNull(entity, "entity");
        this.wpsClient = Param.notNull(wpsClient, "wpsClient");

        error = false;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Pair<WpsRequest, WpsResponse> pair = callWps();
            WpsRequest request = pair.getLeft();
            WpsResponse response = pair.getRight();

            // if no execption occurs (except Connection exception), than call probes and persist Data 
            error = response.isOtherException() || response.isWpsException();

            if (!error) {
                callProbes(request, response);
                persistMeasuredData(getMeasuredDatas());
            }
            
            log.debug("MeasureJob with JobKey {} and TriggerKey {} of Process {} executed! isWpsException: {} isConnectionException: {} isOtherException: {}",
                    context.getJobDetail().getKey(),
                    context.getTrigger().getKey(),
                    processEntity.toString(),
                    response.isWpsException() ? "true" : "false",
                    response.isConnectionException() ? "true" : "false",
                    response.isOtherException() ? "true" : "false"
            );
        } catch (Exception ex) {
            log.warn(ex);
        }
    }

    private void persistMeasuredData(final List<AbstractQosEntity> measuredData) {
        MeasuredDataEntity toPersist = new MeasuredDataEntity();
        toPersist.setProcess(processEntity);
        toPersist.setData(measuredData);
        toPersist.setCreateTime(new Date());

        log.debug("MeasureJob: persist {}", toPersist.getClass().getName());
        dao.persist(toPersist);
    }

    /**
     * call the probes with the request and response data
     */
    private void callProbes(final WpsRequest request, final WpsResponse response) {
        for (QosProbe p : probes) {
            p.execute(request, response);
        }
    }

    /**
     * Calls the WPS Server with the specified WPS client
     *
     * @return a pair consisting of WpsRequest and WpsResponse
     */
    private Pair<WpsRequest, WpsResponse> callWps() {
        WpsProcessInfo info = new WpsProcessInfo(processEntity.getWps().getUri(), processEntity.getIdentifier());

        WpsRequest request = new WpsRequest(processEntity.getRawRequest(), info);
        WpsResponse response = wpsClient.execute(request);
        
        // if wps exception, then retry
        if(response.isWpsException()) {
            response = wpsClient.execute(request);
        }

        return new Pair<WpsRequest, WpsResponse>(request, response);
    }

    /**
     * Extracts the Entities out of the probes
     *
     * @return List with the Entities
     */
    public List<AbstractQosEntity> getMeasuredDatas() {
        List<AbstractQosEntity> measuredDatas = new ArrayList<AbstractQosEntity>();

        for (QosProbe p : probes) {
            measuredDatas.add(p.getMeasuredData());
        }

        return measuredDatas;
    }

    /**
     * Indicates, that this Job can't measure the speicified Wps Process because
     * of a WpsException or an other Exception
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
     * Returns the WpsClient instance
     * 
     * @return WpsClient instance 
     */
    public WpsClient getWpsClient() {
        return wpsClient;
    }
}
