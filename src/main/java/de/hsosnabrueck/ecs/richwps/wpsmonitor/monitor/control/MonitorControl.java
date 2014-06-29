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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.net.URI;
import java.util.Date;
import java.util.List;
import org.quartz.TriggerKey;

/**
 * Facade to interact with the Monitor.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface MonitorControl {

    /**
     * Persists a {@link WpsEntity} instance. Fires a "monitorcontrol.createWps"
     * event.
     *
     * @param wpsEntity WpsEntity instance
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final WpsEntity wpsEntity);

    /**
     * Creates and persists a {@link WpsEntity} instance. Fires a
     * "monitorcontrol.createWps" event.
     *
     * @param wpsIdentifier Wps identifier
     * @param uri Wps Uri
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final String wpsIdentifier, final URI uri);

    /**
     * Creates and persists a {@link WpsProcessEntity} instance and register a
     * new Job in the scheduler with the key name of process identifier and the
     * group name of wps identifier.
     *
     * @param wpsIdentifier Wps identifier
     * @param processIdentifier Wps process identifier
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createAndScheduleProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Persists a {@link WpsProcessEntity} instance and register a new Job in
     * the scheduler with the key name of process identifier and the group name
     * of wps identifier.
     *
     * @param processEntity WpsProcessEntity instance
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createAndScheduleProcess(final WpsProcessEntity processEntity);

    /**
     * Creates and saves a trigger for the job which matched the JobKey name of
     * processIdentifier and group name of wpsIdentifier.
     *
     * @param wpsIdentifier Wps identifier
     * @param processIdentifier Wps process identifier
     * @param config TriggerConfig instance
     * @return TriggerKey instance; the TriggerKey name can be random
     */
    public TriggerKey saveTrigger(final String wpsIdentifier, final String processIdentifier, final TriggerConfig config);

    /**
     * Creates and saves a trigger for the job which matched the JobKey name of
     * processEntity.identifier and group name of processEntity.wps.identifier.
     *
     * @param processEntity WpsProcessEntity instance
     * @param config TriggerConfig instance
     * @return TriggerKey instance; the TriggerKey name can be random
     */
    public TriggerKey saveTrigger(final WpsProcessEntity processEntity, final TriggerConfig config);

    /**
     * Set a test request string to {@link WpsProcessEntity} instance which is
     * matched by the given wpsIdentifier and processIdentifier String.
     *
     * @param wpsIdentifier Wps identifier
     * @param processIdentifier Wps process identifier
     * @param testRequest Testrequest string as XML format
     * @return true if the request ist sucessfully added to the WpsProcessEntity
     */
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest);

    /**
     * Set a test request string to {@link WpsProcessEntity} instance.
     *
     * @param processEntity WpsProcessEntity instance.
     * @param testRequest Testrequest string as XML format
     * @return true if the request ist sucessfully added to the WpsProcessEntity
     */
    public Boolean setTestRequest(final WpsProcessEntity processEntity, final String testRequest);

    /**
     * Selects a WpsEntity instance that matches the given oldWpsIdentifier
     * string and replaces the WpsEntity's identifier and uri with the
     * newWpsIdentifier and newUri instances.
     *
     * @param oldWpsIdentifier Old Wps identifier
     * @param newWpsIdentifier New Wps identifier
     * @param newUri New Wps Uri
     * @return Updated WpsEntity instance
     */
    public WpsEntity updateWps(final String oldWpsIdentifier, final String newWpsIdentifier, final URI newUri);

    /**
     * Updates a WpsEntity instance that matches the given oldWpsIdentifier
     * string and replaces the WpsEntity with the WpsEntity instance of newWps.
     *
     * @param oldWpsIdentifier Old Wps identifier
     * @param newWps WpsEntity instance
     * @return Updated WpsEntity instance
     */
    public WpsEntity updateWps(final String oldWpsIdentifier, final WpsEntity newWps);

    /**
     * Deletes a WpsEntity which is identified through the given wpsIdentifier
     * string.
     *
     * @param wpsIdentifier Wps identifier String
     * @return true if successfully removed
     */
    public Boolean deleteWps(final String wpsIdentifier);

    /**
     * Deletes the given wpsEntity instance.
     * 
     * @param wpsEntity WpsEntity instance
     * @return true if successfully removed
     */
    public Boolean deleteWps(final WpsEntity wpsEntity);

    public Boolean deleteProcess(final String wpsIdentifier, final String processIdentifier);

    public Boolean deleteProcess(final WpsProcessEntity processEntity);

    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier);

    public Boolean isPausedMonitoring(final WpsProcessEntity processEntity);

    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier);

    public void resumeMonitoring(final WpsProcessEntity processEntity);

    public void pauseMonitoring(final String wpsIdentifier, final String processIdentifier);

    public void pauseMonitoring(final WpsProcessEntity processEntity);

    public List<WpsProcessEntity> getProcessesOfWps(final String identifier);

    public List<WpsProcessEntity> getProcessesOfWps(final WpsEntity wpsEntity);

    public List<TriggerConfig> getTriggers(final String wpsIdentifier, final String processIdentifier);

    public List<TriggerConfig> getTriggers(final WpsProcessEntity processEntity);

    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier);

    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity);

    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier, final Range range);

    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity, final Range range);

    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier);

    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity);

    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier, final Date olderAs);

    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity, final Date olderAs);

    public Boolean deleteTrigger(final TriggerKey triggerKey);

    public List<WpsEntity> getWpsList();

    public void deleteMeasuredData(final Date olderAs);

}
