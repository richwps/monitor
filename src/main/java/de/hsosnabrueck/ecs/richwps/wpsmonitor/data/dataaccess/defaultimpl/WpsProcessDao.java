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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
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
        super(jpa);
    }

    @Override
    public Boolean persist(WpsProcessEntity e) {
        if (find(e.getWps().getIdentifier(), e.getIdentifier()) != null) {
            return false;
        }

        return super.persist(e);
    }

    @Override
    public WpsProcessEntity find(final Object primaryKey) {
        return getEntityManager().find(WpsProcessEntity.class, Validate.notNull(primaryKey, "primaryKey"));
    }

    @Override
    public List<WpsProcessEntity> get(final Range range) {
        return getBy("wpsprocess.getAll", WpsProcessEntity.class, range);
    }

    @Override
    public WpsProcessEntity find(final String wpsIdentifier, final String processIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsidentifier", Validate.notNull(wpsIdentifier, "wpsIdentifier"));
        parameters.put("identifier", Validate.notNull(processIdentifier, "processIdentifier"));

        List<WpsProcessEntity> resultList = getBy("wpsprocess.get", parameters, WpsProcessEntity.class);
        WpsProcessEntity result = null;

        if (!resultList.isEmpty()) {
            result = resultList.get(0);
        }

        return result;
    }

    @Override
    public List<WpsProcessEntity> getAll(final String wpsIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("identifier", Validate.notNull(wpsIdentifier, "wpsidentifier"));

        return getBy("wpsprocess.getAllOf", parameters, WpsProcessEntity.class);
    }

    @Override
    public Integer deleteProcessesOfWps(String wpsIdentifier) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsIdentifier", Validate.notNull(wpsIdentifier, "wpsIdentifier"));

        return doNamedQuery("wpsprocess.deleteByWps", parameters);
    }

    @Override
    public void remove(final String wpsIdentifier, final String processIdentifier) {
        WpsProcessEntity find = find(wpsIdentifier, processIdentifier);

        remove(find);
    }

    @Override
    public void remove(final WpsProcessEntity o) {
        beginTransaction();

        if (o == null
                || o.getWps() == null
                || o.getWps().getIdentifier() == null
                || o.getIdentifier() == null) {

            Validate.notNull(null, "Given WpsProcessEntity");
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsIdentifier", o.getWps().getIdentifier());
        parameters.put("processIdentifier", o.getIdentifier());

        // first remove from AbstractQosEntity
        doNamedQuery("abstractQos.deleteByWpsProcess", parameters);

        // remove from MeasuredDataEntity
        doNamedQuery("qos.deleteByWpsProcess", parameters);

        super.remove(o);
        requestCommit();
    }
}
