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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import java.util.List;

/**
 * DataAccess for MeasuredDataEntity-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface QosDataAccess extends DataAccess<MeasuredDataEntity> {

    /**
     * Get all MeasuredDataEntity-Objects that matches the given (wpsIdentifier,
     * processIdentifier) combination
     *
     * @param wpsIdentifier Wps Entity identifier
     * @param processIdentifier Process Entity identifier
     * @return List of MeasuredDataEntity-Objects; null if no result found
     */
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Get all MeasuredDataEntity-Objects that matches the given wpsIdentifier
     *
     * @param wpsIdentifier Wps Entity identifier
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByWps(final String wpsIdentifier);

    /**
     * Get all MeasuredDataEntity-Objects in the specific range which is
     * described by the given range object.
     *
     * @param wpsIdentifier Wps Entity identifier
     * @param processIdentifier Process Entity identifier
     * @param range Range Instance
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier, final Range range);

    /**
     * Get all MeasuredDataEntity-Objects that matches the given wpsIdentifier
     * in the specific range which is described by the given range object.
     *
     * @param identifier Wps Entity Identifier
     * @param range Range Instance
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByWps(final String identifier, final Range range);
}
