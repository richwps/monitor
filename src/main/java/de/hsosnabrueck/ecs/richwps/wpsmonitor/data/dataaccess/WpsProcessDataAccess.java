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
import java.util.List;

/**
 * DataAccess for WpsProcessEntity-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface WpsProcessDataAccess extends DataAccess<WpsProcessEntity> {

    /**
     * Find a WpsProcessEntity-object that matches the given wpsIdentifier &&
     * processIdentifier.
     *
     * @param wpsIdentifier Identifier of wps-entity
     * @param processIdentifier Identifier of wpsProcess-entity
     * @return WpsProcessEntity instance; null if not exists
     */
    public WpsProcessEntity find(final String wpsIdentifier, final String processIdentifier);

    /**
     * Gets all WpsProcessEntity-objects.
     *
     * @param wpsIdentifier Identifier of wps-entity
     * @return List of all WpsProcessEntity-objects; null if no result is found
     */
    public List<WpsProcessEntity> getAll(final String wpsIdentifier);

    /**
     * Deletes all processes from the wps that machtes the given wpsIdentifier.
     *
     * @param wpsIdentifier Identifier of wps-entity.
     */
    public void deleteProcessesOfWps(final String wpsIdentifier);
    
    public void remove(final String wpsIdentifier, final String processIdentifier);
}
