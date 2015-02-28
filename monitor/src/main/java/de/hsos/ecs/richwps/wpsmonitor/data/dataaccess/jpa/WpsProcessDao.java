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

import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a WpsProcessDataAccess interface.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDao extends AbstractDataAccess<WpsProcessEntity> implements WpsProcessDataAccess {

    public WpsProcessDao(final Jpa jpa) {
        super(jpa, WpsProcessEntity.class);
    }

    @Override
    public Boolean persist(final WpsProcessEntity e) {
        if (find(e.getWps().getId(), e.getIdentifier()) != null) {
            return false;
        }

        return super.persist(e);
    }

    @Override
    public WpsProcessEntity find(final Long primaryKey) {
        Validate.notNull(primaryKey, "primaryKey");
        
        return getEntityManager().find(WpsProcessEntity.class, primaryKey);
    }

    @Override
    public List<WpsProcessEntity> get(final Range range) {
        return getBy("wpsprocess.getAll", range);
    }

    @Override
    public WpsProcessEntity find(final Long wpsId, final String processIdentifier) {
        Validate.notNull(wpsId, "wpsId");
        Validate.notNull(processIdentifier, "processIdentifier");

        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", wpsId);
        parameters.put("processIdentifier", processIdentifier);

        List<WpsProcessEntity> resultList = getBy("wpsprocess.get", parameters);
        
        WpsProcessEntity result = null;
        if (!resultList.isEmpty()) {
            result = resultList.get(0);
        }

        return result;
    }

    @Override
    public List<WpsProcessEntity> getAll(final Long wpsId) {
        Validate.notNull(wpsId, "WPS ID");   
                
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", wpsId);

        return getBy("wpsprocess.getAllOf", parameters);
    }

    @Override
    public Integer deleteProcessesOfWps(final Long wpsId) {
        Validate.notNull(wpsId, "WPS ID");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", wpsId);

        return doNamedQuery("wpsprocess.deleteByWps", parameters);
    }

    @Override
    public void remove(final Long wpsId, final String processIdentifier) {
        WpsProcessEntity find = find(wpsId, processIdentifier);

        remove(find);
    }

    @Override
    public void remove(final WpsProcessEntity o) {
        beginTransaction();

        if (o == null
                || o.getWps() == null
                || o.getWps().getEndpoint() == null
                || o.getWps().getId() == null
                || o.getIdentifier() == null) {

            Validate.notNull(null, "Given WpsProcessEntity");
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", o.getWps().getId());
        parameters.put("processIdentifier", o.getIdentifier());

        // first remove from AbstractQosEntity
        doNamedQuery("abstractQos.deleteByWpsProcess", parameters);

        // remove from MeasuredDataEntity
        doNamedQuery("qos.deleteByWpsProcess", parameters);

        super.remove(o);
        requestCommit();
    }

    @Override
    public WpsProcessEntity find(final URL endpoint, final String processIdentifier) {
        Validate.notNull(endpoint, "endpoint");
        Validate.notNull(processIdentifier, "processIdentifier");

        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());
        parameters.put("processIdentifier", processIdentifier);

        List<WpsProcessEntity> resultList = getBy("wpsprocess.getByEndpoint", parameters);
        
        WpsProcessEntity result = null;
        if (!resultList.isEmpty()) {
            result = resultList.get(0);
        }

        return result;
    }

    @Override
    public List<WpsProcessEntity> getAll(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
                
                
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());

        return getBy("wpsprocess.getAllOfEndpoint", parameters);
    }

    @Override
    public Integer deleteProcessesOfWps(final URL endpoint) {
        Validate.notNull(endpoint, "endpoint");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());

        return doNamedQuery("wpsprocess.deleteByWpsEndpoint", parameters);
    }

    @Override
    public void remove(final URL endpoint, final String processIdentifier) {
        WpsProcessEntity find = find(endpoint, processIdentifier);
        
        if(find != null) {
            remove(find);
        }
    }
}
