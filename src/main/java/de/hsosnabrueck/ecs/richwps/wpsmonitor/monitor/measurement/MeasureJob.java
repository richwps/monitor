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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsProcessInfo;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsResponse;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.DataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasureJob implements Job {

    protected final WpsProcessEntity processEntity;
    protected final DataAccess dao;
    protected List<QosProbe> probes;

    protected Date startTime;
    protected Date endTime;

    protected WpsResponse response;
    protected WpsRequest request;

    public MeasureJob(final List<QosProbe> probes, final WpsProcessEntity entity, final DataAccess dao) {
        this.probes = Param.notNull(probes, "probeService");
        this.dao = Param.notNull(dao, "dao");
        this.processEntity = Param.notNull(entity, "entity");
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            initRequest();
            doMeasure();
            callProbes();
            persistData();
        } catch(Exception ex) {
            Logger.getLogger(MeasureJob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dao.close();
        }
    }

    private void persistData() {
        for (AbstractQosEntity q : getMeasuredDatas()) {
            dao.persist(q);
        }
    }

    private void callProbes() {
        for (QosProbe p : probes) {
            p.begin(request, startTime);
            p.end(response, endTime);
        }
    }

    public void initRequest() {
        WpsProcessInfo info = new WpsProcessInfo(processEntity.getWps().getRoute(), processEntity.getIdentifier());
        request = new WpsRequest(processEntity.getRawRequest(), info);
    }

    private void doMeasure() {
        WpsClient client = WpsClientFactory.createDefault();

        startTime = new Date();
        response = client.execute(request);
        endTime = new Date();
    }

    public List<AbstractQosEntity> getMeasuredDatas() {
        List<AbstractQosEntity> measuredDatas = new ArrayList<AbstractQosEntity>();
        AbstractQosEntity add = null;

        for (QosProbe p : probes) {
            // associate measured data with the specific process entity
            add = p.getMeasuredData();
            add.setProcess(processEntity);

            measuredDatas.add(add);
        }

        return measuredDatas;
    }
}
