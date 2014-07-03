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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;

/**
 * Default implementation of a QosDataAccess-interface.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class QosDao extends AbstractDataAccess<MeasuredDataEntity> implements QosDataAccess {
/*
    @Override
    public Boolean persist(MeasuredDataEntity mDataEntity) {

        Boolean result = true;
        beginTransaction();
        
        /**
         * Split mDataEntity into two objects, then persist the one half with 
         * createDate and process and then persist the other half with 
         * the data. But set the first half to the data entities
         *//*
        try {
            MeasuredDataEntity toPersist = new MeasuredDataEntity();
            toPersist.setCreateTime(mDataEntity.getCreateTime());
            toPersist.setProcess(mDataEntity.getProcess());
            
            getEntityManager().persist(toPersist);
            
            for (AbstractQosEntity e : mDataEntity.getData()) {
                e.setOwner(toPersist);
                getEntityManager().persist(e);
            }
            
            toPersist.setData(mDataEntity.getData());
            
            getEntityManager().merge(toPersist);
            
            mDataEntity.setId(toPersist.getId());

            requestCommit();
        } catch (EntityExistsException e) {
            log.debug(e);

            result = false;
        }

        return result;
    }*/

    @Override
    public MeasuredDataEntity find(final Object primaryKey) {
        return getEntityManager().find(MeasuredDataEntity.class, Validate.notNull(primaryKey, "primaryKey"));
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final String identifier, final Range range) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", Validate.notNull(identifier, "identifier"));

        return getBy("qos.getQosByWps", parameter, MeasuredDataEntity.class, range);
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier, final Range range) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", Validate.notNull(processIdentifier, "processIdentifier"));
        parameter.put("wpsIdentifier", Validate.notNull(wpsIdentifier, "wpsIdentifier"));

        return getBy("qos.getQosByProcess", parameter, MeasuredDataEntity.class, range);
    }

    @Override
    public List<MeasuredDataEntity> get(final Range range) {
        return null; // not needed here yet
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier) {
        return getByProcess(wpsIdentifier, processIdentifier, null);
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final String identifier) {
        return getByWps(identifier, null);
    }

    @Override
    public Integer deleteByProcess(final String wpsIdentifier, final String processIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("wpsIdentifier", Validate.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("processIdentifier", Validate.notNull(processIdentifier, "processIdentifier"));

        doNamedQuery("qos.deleteByWpsProcess", parameters);

        return doNamedQuery("abstractQos.deleteByWpsProcess", parameters);
    }

    @Override
    public Integer deleteByProcess(final String wpsIdentifier, final String processIdentifier, final Date olderDate) {

        if (olderDate == null) {
            return deleteByProcess(wpsIdentifier, processIdentifier);
        }

        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("wpsIdentifier", Validate.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("processIdentifier", Validate.notNull(processIdentifier, "processIdentifier"));
        parameters.put("date", olderDate);

        doNamedQuery("qos.deleteByWpsProcessOlderAs", parameters);

        return doNamedQuery("abstractQos.deleteByWpsProcessOlderAs", parameters);
    }

    @Override
    public Integer deleteAllOlderAs(final Date date) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("date", Validate.notNull(date, "date"));

        doNamedQuery("qos.deleteOlderAs", parameters);

        return doNamedQuery("abstractQos.deleteOlderAs", parameters);
    }

    @Override
    public AbstractQosEntity findAbstractQosEntityByid(Long id) {
        Validate.notNull(id, "id");
        return getEntityManager().find(AbstractQosEntity.class, id);
    }
}
