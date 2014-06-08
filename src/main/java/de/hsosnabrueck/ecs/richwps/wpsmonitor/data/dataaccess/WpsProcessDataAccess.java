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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDataAccess extends AbstractDataAccess<WpsProcessEntity> {

    @Override
    public WpsProcessEntity find(Object primaryKey) {
        return em.find(WpsProcessEntity.class, primaryKey);
    }
    
    public WpsProcessEntity find(String wpsIdentifier, String processIdentifier) {
        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsidentifier", wpsIdentifier);
        parameters.put("identifier", processIdentifier);
        
        List<WpsProcessEntity> resultList = getBy("wpsprocess.get", parameters, WpsProcessEntity.class);
        WpsProcessEntity result = null;
        
        if(resultList.size() > 0) {
            result = resultList.get(0);
        }
        
        return result;
    }
    
    public List<WpsProcessEntity> getAllOfWps(String identifier) {
        Param.notNull(identifier, "identifier");
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("identifier", identifier);
        
        return getBy("wpsprocess.getAllOf", parameters, WpsProcessEntity.class);
    }
}
