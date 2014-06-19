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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDao extends AbstractDataAccess<WpsProcessEntity> implements WpsProcessDataAccess {

    public WpsProcessDao(EntityManager em) {
        super(em);
    }
    
    @Override
    public Boolean persist(WpsProcessEntity e) {
        if(find(e.getWps().getIdentifier(), e.getIdentifier()) != null) {
            return false;
        } 
        
        return super.persist(e);
    }

    @Override
    public WpsProcessEntity find(final Object primaryKey) {
        return em.find(WpsProcessEntity.class, Param.notNull(primaryKey, "primaryKey"));
    }

    @Override
    public List<WpsProcessEntity> get(final Range range) {
        return getBy("wpsprocess.getAll", WpsProcessEntity.class, range);
    }
    
    @Override
    public WpsProcessEntity find(final String wpsIdentifier, final String processIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsidentifier", Param.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("identifier", Param.notNull(processIdentifier, "processIdentifier"));

        List<WpsProcessEntity> resultList = getBy("wpsprocess.get", parameters, WpsProcessEntity.class);
        WpsProcessEntity result = null;

        if (resultList.size() > 0) {
            result = resultList.get(0);
        }

        return result;
    }

    @Override
    public List<WpsProcessEntity> getAll(final String wpsIdentifier) {
       Map<String, Object> parameters = new HashMap<String, Object>();
       parameters.put("identifier", Param.notNull(wpsIdentifier, "wpsidentifier"));
       
       return getBy("wpsprocess.getAllOf", parameters, WpsProcessEntity.class);
    }

    @Override
    public void deleteProcessesFromWps(String wpsIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsidentifier", Param.notNull(wpsIdentifier, "wpsIdentifier"));
        
        doNamedQuery("wpsprocess.deleteAllFromWps", parameters);
    }
    
}
