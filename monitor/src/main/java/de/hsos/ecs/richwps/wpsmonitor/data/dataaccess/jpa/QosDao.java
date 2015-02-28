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
package de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa;

import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a QosDataAccess-interface.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class QosDao extends AbstractDataAccess<MeasuredDataEntity> implements QosDataAccess {

    public QosDao(final Jpa jpa) {
        super(jpa, MeasuredDataEntity.class);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final Long wpsId, final Range range) {
        Validate.notNull(wpsId, "WPS ID");

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("wpsId", wpsId);

        return getBy("qos.getQosByWps", parameter, range);
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final Long wpsId, final String processIdentifier, final Range range) {
        Validate.notNull(processIdentifier, "processIdentifier");
        Validate.notNull(wpsId, "WPS ID");

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("processIdentifier", processIdentifier);
        parameter.put("wpsId", wpsId);

        return getBy("qos.getQosByProcess", parameter, range);
    }

    @Override
    public List<MeasuredDataEntity> get(final Range range) {
        // not needed here yet
        return null;
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final Long wpsId, final String processIdentifier) {
        return getByProcess(wpsId, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final Long wpsId) {
        return getByWps(wpsId, null);
    }
    
    @Override
    public List<MeasuredDataEntity> getByProcess(final URL endpoint, final String processIdentifier) {
        return getByProcess(endpoint, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final URL endpoint, final String processIdentifier, final Range range) {
        Validate.notNull(processIdentifier, "processIdentifier");
        Validate.notNull(endpoint, "endpoint");
        
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("processIdentifier", processIdentifier);
        parameter.put("endpoint", endpoint.toString());
        
        return getBy("qos.getQosByProcessEndpoint", parameter, range);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final URL endpoint) {
        return getByWps(endpoint, null);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final URL endpoint, final Range range) {
        Validate.notNull(endpoint, "endpoint");
        
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("endpoint", endpoint.toString());
        
        return getBy("qos.getQosByWpsEndpoint", parameter, range);
    }

    @Override
    public Integer deleteByProcess(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        Validate.notNull(processIdentifier, "processIdentifier");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());
        parameters.put("processIdentifier", processIdentifier);

        doNamedQuery("abstractQos.deleteByWpsProcessEndpoint", parameters);
        return doNamedQuery("qos.deleteByWpsProcessEndpoint", parameters);
    }

    @Override
    public Integer deleteByProcess(final URL endpoint, final String processIdentifier, final Date olderDate) {
        if (olderDate == null) {
            return deleteByProcess(endpoint, processIdentifier);
        }

        Validate.notNull(endpoint, "endpoint");
        Validate.notNull(processIdentifier, "processIdentifier");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());
        parameters.put("processIdentifier", processIdentifier);
        parameters.put("date", olderDate);

        doNamedQuery("abstractQos.deleteByWpsProcessOlderAsEndpoint", parameters);
        return doNamedQuery("qos.deleteByWpsProcessOlderAsEndpoint", parameters);
    }

    @Override
    public Integer deleteByProcess(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "WPS ID");
        Validate.notNull(processIdentifier, "processIdentifier");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", wpsId);
        parameters.put("processIdentifier", processIdentifier);

        doNamedQuery("abstractQos.deleteByWpsProcess", parameters);
        return doNamedQuery("qos.deleteByWpsProcess", parameters);
    }

    @Override
    public Integer deleteByProcess(final Long wpsId, final String processIdentifier, final Date olderDate) {
        if (olderDate == null) {
            return deleteByProcess(wpsId, processIdentifier);
        }

        Validate.notNull(wpsId, "wpsId");
        Validate.notNull(processIdentifier, "processIdentifier");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", wpsId);
        parameters.put("processIdentifier", processIdentifier);
        parameters.put("date", olderDate);

        doNamedQuery("abstractQos.deleteByWpsProcessOlderAs", parameters);
        return doNamedQuery("qos.deleteByWpsProcessOlderAs", parameters);
    }

    @Override
    public Integer deleteAllOlderAs(final Date date) {
        Validate.notNull(date, "date");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", date);

        doNamedQuery("abstractQos.deleteOlderAs", parameters);
        return doNamedQuery("qos.deleteOlderAs", parameters);
    }

    @Override
    public AbstractQosEntity findAbstractQosEntityByid(Long id) {
        Validate.notNull(id, "id");
        return getEntityManager().find(AbstractQosEntity.class, id);
    }
}
