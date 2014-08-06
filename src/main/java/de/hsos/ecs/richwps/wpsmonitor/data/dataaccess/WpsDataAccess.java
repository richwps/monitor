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
package de.hsos.ecs.richwps.wpsmonitor.data.dataaccess;

import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import java.util.List;

/**
 * DataAcces for WpsEntity-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface WpsDataAccess extends DataAccess<WpsEntity> {

    /**
     * Gets all WpsEntity-objects.
     *
     * @return List of WpsEntity objects
     */
    public List<WpsEntity> getAll();

    /**
     * Finds a WpsEntity instance by its identifier String.
     *
     * @param wpsIdentifier WpsIdentifier String
     * @return WpsEntity instance
     */
    public WpsEntity find(String wpsIdentifier);

    /**
     * Removes a stored {@link WpsEntity}.
     *
     * @param wpsIdentifier Wps entity identifier
     */
    public void remove(final String wpsIdentifier);
}