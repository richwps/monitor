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

import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.net.URL;
import java.util.List;

/**
 * GenericDataAccess for {@link WpsProcessEntity}-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface WpsProcessDataAccess extends GenericDataAccess<WpsProcessEntity> {

    /**
     * Finds a {@link WpsProcessEntity}-object that matches the given wpsId &&
     * processIdentifier.
     *
     * @param wpsId WPS ID
     * @param processIdentifier Identifier of wpsProcess-entity
     * @return WpsProcessEntity instance; null if not exists
     */
    public WpsProcessEntity find(final Long wpsId, final String processIdentifier);

    /**
     * Finds a {@link WpsProcessEntity}-object that matches the given endpoint
     * of a WPS && processIdentifier.
     *
     * @param endpoint Endpoint of a WPS
     * @param processIdentifier Identifier of wpsProcess-entity
     * @return WpsProcessEntity instance; null if not exists
     */
    public WpsProcessEntity find(final URL endpoint, final String processIdentifier);

    /**
     * Gets all {@link WpsProcessEntity}-objects.
     *
     * @param wpsId WPS ID
     * @return List of all WpsProcessEntity-objects; null if no result is found
     */
    public List<WpsProcessEntity> getAll(final Long wpsId);

    /**
     * Gets all {@link WpsProcessEntity}-objects.
     *
     * @param endpoint Endpoint of a WPS
     * @return List of all WpsProcessEntity-objects; null if no result is found
     */
    public List<WpsProcessEntity> getAll(final URL endpoint);

    /**
     * Deletes all processes from the wps that match the given WPS ID.
     *
     * @param wpsId WPS ID
     * @return Affected rows
     */
    public Integer deleteProcessesOfWps(final Long wpsId);

    /**
     * Deletes all processes from the wps that match the given WPS Endpoint.
     *
     * @param endpoint Endpoint of a WPS
     * @return Affected rows
     */
    public Integer deleteProcessesOfWps(final URL endpoint);

    /**
     * Removes a stored {@link WpsProcessEntity}
     *
     * @param wpsId WPS ID
     * @param processIdentifier WpsProcess entity identifier
     */
    public void remove(final Long wpsId, final String processIdentifier);

    /**
     * Removes a stored {@link WpsProcessEntity}
     *
     * @param endpoint Endpoint of a WPS
     * @param processIdentifier WpsProcess entity identifier
     */
    public void remove(final URL endpoint, final String processIdentifier);
}
