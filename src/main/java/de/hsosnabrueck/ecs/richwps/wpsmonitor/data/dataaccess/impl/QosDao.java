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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl;


import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class QosDao extends AbstractDataAccess<MeasuredDataEntity> implements QosDataAccess {
    public QosDao(EntityManager em) {
        super(em);
    }
    
    @Override
    public MeasuredDataEntity find(Object primaryKey) {
        return em.find(MeasuredDataEntity.class, primaryKey);
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(String identifier) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", identifier);

        return getBy("qos.getQosByProcess", parameter, MeasuredDataEntity.class);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(String identifier) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", identifier);

        return getBy("qos.getQosByWps", parameter, MeasuredDataEntity.class);
    }

    @Override
    public List<MeasuredDataEntity> get(Integer start, Integer count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
