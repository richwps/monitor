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

import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import java.util.Date;
import java.util.List;

/**
 * DataAccess for {@link MeasuredDataEntity}-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface QosDataAccess extends DataAccess<MeasuredDataEntity> {

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * (wpsIdentifier, processIdentifier) combination
     *
     * @param wpsIdentifier Wps Entity identifier
     * @param processIdentifier Process Entity identifier
     * @return List of MeasuredDataEntity-Objects; null if no result found
     */
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * wpsIdentifier
     *
     * @param wpsIdentifier Wps entity identifier
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByWps(final String wpsIdentifier);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects in the specific range which
     * is described by the given range object.
     *
     * @param wpsIdentifier Wps etity identifier
     * @param processIdentifier Process entity identifier
     * @param range Range Instance
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByProcess(final String wpsIdentifier, final String processIdentifier, final Range range);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * wpsIdentifier in the specific range which is described by the given
     * range-object.
     *
     * @param identifier Wps entity Identifier
     * @param range Range Instance
     * @return List of {@link MeasuredDataEntity}-Objects
     */
    public List<MeasuredDataEntity> getByWps(final String identifier, final Range range);

    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by wpsIdentifier and processIdentifier.
     *
     * @param wpsIdentifier Wps entity identifier
     * @param processIdentifier WpsProcess entity identifier
     * @return Affected rows
     */
    public Integer deleteByProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by wpsIdentifier and processIdentifier.
     *
     * @param wpsIdentifier Wps entity identifier
     * @param processIdentifierfinal WpsProcess entity identifier
     * @param olderDate
     * @return Affected rows
     */
    public Integer deleteByProcess(final String wpsIdentifier, final String processIdentifierfinal, final Date olderDate);

    /**
     * Deletes all measured Qos-Data which are older as "date".
     *
     * @param date Date instance
     * @return Affected rows
     */
    public Integer deleteAllOlderAs(final Date date);

    public AbstractQosEntity findAbstractQosEntityByid(Long id);
}
