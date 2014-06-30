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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
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

    @Override
    public MeasuredDataEntity find(final Object primaryKey) {
        return getEntityManager().find(MeasuredDataEntity.class, Param.notNull(primaryKey, "primaryKey"));
    }

    @Override
    public List<MeasuredDataEntity> getByWps(final String identifier, final Range range) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", Param.notNull(identifier, "identifier"));

        return getBy("qos.getQosByWps", parameter, MeasuredDataEntity.class, range);
    }

    @Override
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier, final Range range) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("identifier", Param.notNull(processIdentifier, "processIdentifier"));
        parameter.put("wpsIdentifier", Param.notNull(wpsIdentifier, "wpsIdentifier"));

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

        parameters.put("wpsIdentifier", Param.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("processIdentifier", Param.notNull(processIdentifier, "processIdentifier"));

        doNamedQuery("qos.deleteByWpsProcess", parameters);

        return doNamedQuery("abstractQos.deleteByWpsProcess", parameters);
    }

    @Override
    public Integer deleteByProcess(final String wpsIdentifier, final String processIdentifier, final Date olderDate) {

        if (olderDate == null) {
            return deleteByProcess(wpsIdentifier, processIdentifier);
        }

        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("wpsIdentifier", Param.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("processIdentifier", Param.notNull(processIdentifier, "processIdentifier"));
        parameters.put("date", olderDate);

        doNamedQuery("qos.deleteByWpsProcessOlderAs", parameters);

        return doNamedQuery("abstractQos.deleteByWpsProcessOlderAs", parameters);
    }

    @Override
    public Integer deleteAllOlderAs(final Date date) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("date", Param.notNull(date, "date"));
        
        doNamedQuery("qos.deleteOlderAs", parameters);
        
        return doNamedQuery("abstractQos.deleteOlderAs", parameters);
    }
}
