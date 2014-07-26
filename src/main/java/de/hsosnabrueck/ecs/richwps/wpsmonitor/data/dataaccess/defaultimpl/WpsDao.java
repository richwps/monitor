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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defgault implementation of a WpsDataAccess interface
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDao extends AbstractDataAccess<WpsEntity> implements WpsDataAccess {

    public WpsDao(final Jpa jpa) {
        super(jpa);
    }

    /**
     * Finds a WpsEntity instance by the wpsIdentifier String.
     *
     * @param wpsIdentifier wpsIdentifier String
     * @return WpsEntity instance or null if not found
     */
    @Override
    public WpsEntity find(String wpsIdentifier) {
        Validate.notNull(wpsIdentifier, "wpsIdentifier");
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("identifier", wpsIdentifier);

        List<WpsEntity> wpsEntities = getBy("wps.findByIdentifier", parameters, WpsEntity.class);

        if (wpsEntities != null && !wpsEntities.isEmpty()) {
            return wpsEntities.get(0);
        }

        return null;
    }

    @Override
    public List<WpsEntity> get(final Range range) {
        return getBy("wps.getAll", WpsEntity.class, range);
    }

    @Override
    public List<WpsEntity> getAll() {
        return get(null);
    }

    @Override
    public void remove(final String wpsIdentifier) {
        WpsEntity find = find(wpsIdentifier);

        remove(find);
    }

    @Override
    public void remove(final WpsEntity o) {

        Validate.notNull(o, "WpsEntity");
        Validate.notNull(o.getIdentifier(), "WpsEntity Identifier");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wpsIdentifier", o.getIdentifier());

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
    public WpsEntity find(Object primaryKey) {
        return getEntityManager()
                .find(WpsEntity.class, primaryKey);
    }
}
