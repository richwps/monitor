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
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * GenericDataAccess for {@link MeasuredDataEntity}-objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface QosDataAccess extends GenericDataAccess<MeasuredDataEntity> {

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * (wpsId, processIdentifier) combination
     *
     * @param wpsId Wps Entity id
     * @param processIdentifier Process Entity identifier
     * @return List of MeasuredDataEntity-Objects; null if no result found
     */
    public List<MeasuredDataEntity> getByProcess(final Long wpsId, final String processIdentifier);
    
    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * (wps endpoint, processIdentifier) combination
     *
     * @param endpoint WPS endpoint
     * @param processIdentifier Process Entity identifier
     * @return List of MeasuredDataEntity-Objects; null if no result found
     */
    public List<MeasuredDataEntity> getByProcess(final URL endpoint, final String processIdentifier);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * wpsID
     *
     * @param wpsId Wps entity id
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByWps(final Long wpsId);
    
    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * WPS Endpoint
     *
     * @param endpoint WPS Endpoint
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByWps(final URL endpoint);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects in the specific range which
     * is described by the given range object.
     *
     * @param wpsId Wps etity id
     * @param processIdentifier Process entity identifier
     * @param range Range Instance
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByProcess(final Long wpsId, final String processIdentifier, final Range range);
    
    /**
     * Gets all {@link MeasuredDataEntity}-Objects in the specific range which
     * is described by the given range object.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier Process entity identifier
     * @param range Range Instance
     * @return List of MeasuredDataEntity-Objects
     */
    public List<MeasuredDataEntity> getByProcess(final URL endpoint, final String processIdentifier, final Range range);

    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * WPS ID in the specific range which is described by the given
     * range-object.
     *
     * @param wpsId WPS ID
     * @param range Range Instance
     * @return List of {@link MeasuredDataEntity}-Objects
     */
    public List<MeasuredDataEntity> getByWps(final Long wpsId, final Range range);
    
    /**
     * Gets all {@link MeasuredDataEntity}-Objects that match the given
     * WPS Endpoint in the specific range which is described by the given
     * range-object.
     *
     * @param endpoint WPS Endpoint
     * @param range Range Instance
     * @return List of {@link MeasuredDataEntity}-Objects
     */
    public List<MeasuredDataEntity> getByWps(final URL endpoint, final Range range);

    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by WPS ID and processIdentifier.
     *
     * @param wpsId WPS ID
     * @param processIdentifier WpsProcess entity identifier
     * @return Affected rows
     */
    public Integer deleteByProcess(final Long wpsId, final String processIdentifier);
    
    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by endpoint and processIdentifier.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcess entity identifier
     * @return Affected rows
     */
    public Integer deleteByProcess(final URL endpoint, final String processIdentifier);

    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by wpsId and processIdentifier.
     *
     * @param wpsId WPS ID
     * @param processIdentifier WpsProcess entity identifier
     * @param olderDate
     * @return Affected rows
     */
    public Integer deleteByProcess(final Long wpsId, final String processIdentifier, final Date olderDate);
    
    /**
     * Deletes all measured Qos-Data that match the given Wps process which is
     * identified by endpoint and processIdentifier.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifierfinal WpsProcess entity identifier
     * @param olderDate
     * @return Affected rows
     */
    public Integer deleteByProcess(final URL endpoint, final String processIdentifierfinal, final Date olderDate);

    /**
     * Deletes all measured Qos-Data which are older as "date".
     *
     * @param date Date instance
     * @return Affected rows
     */
    public Integer deleteAllOlderAs(final Date date);

    public AbstractQosEntity findAbstractQosEntityByid(final Long id);
}
