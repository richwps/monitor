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
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a WpsDataAccess interface
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDao extends AbstractDataAccess<WpsEntity> implements WpsDataAccess {

    public WpsDao(final Jpa jpa) {
        super(jpa, WpsEntity.class);
    }

    @Override
    public List<WpsEntity> get(final Range range) {
        return getBy("wps.getAll", range);
    }

    @Override
    public List<WpsEntity> getAll() {
        return get(null);
    }

    @Override
    public void remove(final WpsEntity o) {

        Validate.notNull(o, "WpsEntity");
        Validate.notNull(o.getId(), "WPS ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wpsId", o.getId());

        beginTransaction();
        // first remove from AbstractQosEntity
        doNamedQuery("abstractQos.deleteByWps", parameters);

        // remove from MeasuredDataEntity
        doNamedQuery("qos.deleteByWps", parameters);

        // delete all processes of wps
        doNamedQuery("wpsprocess.deleteByWps", parameters);

        super.remove(o);
        requestCommit();
    }

    @Override
    public WpsEntity find(final Long primaryKey) {
        return getEntityManager()
                .find(WpsEntity.class, primaryKey);
    }

    @Override
    public WpsEntity find(final URL endpoint) {
        Validate.notNull(endpoint, "Endpoint");
        
        WpsEntity result = null;
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("endpoint", endpoint.toString());
        
        List<WpsEntity> by = getBy("wps.findByEndpoint", parameters);
        
        if(!by.isEmpty()) {
            result = by.get(0);
        }
        
        return result;
    }
    
    @Override
    public Boolean persist(final WpsEntity persist) {
        if(find(persist.getEndpoint()) != null) {
            return false;
        }
        
        return super.persist(persist);
    }
}
