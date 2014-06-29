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
     * @param wpsEntity WpsEntity identifier
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final WpsEntity wpsEntity);

    /**
     * Creates and persists a {@link WpsEntity} instance. Fires a
     * "monitorcontrol.createWps" event.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param uri Wps Uri
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final String wpsIdentifier, final URI uri);

    /**
     * Creates and persists a {@link WpsProcessEntity} instance and register a
     * new Job in the scheduler with the key name of process identifier and the
     * group name of wps identifier.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
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
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
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
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
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
     * @param oldWpsIdentifier Old WpsEntity identifier
     * @param newWpsIdentifier New WpsEntity identifier
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
     * @param wpsIdentifier WpsEntity identifier
     * @return true if successfully removed
     */
    public Boolean deleteWps(final String wpsIdentifier);

    /**
     * Delete the given {@link WpsEntity} instance.
     *
     * @param wpsEntity WpsEntity instance
     * @return true if successfully removed
     */
    public Boolean deleteWps(final WpsEntity wpsEntity);

    /**
     * Deletes a {@link WpsProcessEntity} which is identified through the given
     * wpsIdentifier and processIdentifier string.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if successfully removed
     */
    public Boolean deleteProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Deletes the given {@link WpsProcessEntity} instance.
     *
     * @param processEntity
     * @return true if successfully removed
     */
    public Boolean deleteProcess(final WpsProcessEntity processEntity);

    /**
     * Checks if the job is paused which is identified by the jobKey
     * &lt;wpsIdentifier, processIdentifier>.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if the job is paused
     */
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier);

    /**
     * Checks if the job is paused which is identified by the
     * &lt;WpsProcessEntity.Identifier, WpsProcessEntity.wps.processIdentifier>
     * {@link JobKey}.
     *
     * @param processEntity WpsProcessEntity instance
     * @return true if the job is paused
     */
    public Boolean isPausedMonitoring(final WpsProcessEntity processEntity);

    /**
     * Resumes the monitoring of the WpsProcess-Job which is identified by the
     * &lt;wpsIdentifier, processIdentifier> {@link JobKey} .
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier);

    /**
     * Resumes the monitoring of the WpsProcess-Job which is identified by the
     * &lt;WpsProcessEntity.Identifier, WpsProcessEntity.wps.processIdentifier>
     * {@link JobKey}.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void resumeMonitoring(final WpsProcessEntity processEntity);

    /**
     * Pauses a job which is identified by the &lt;wpsIdentifier,
     * processIdentifier> {@link JobKey} .
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void pauseMonitoring(final String wpsIdentifier, final String processIdentifier);

    /**
     * Pauses a job which is identified by the &lt;WpsProcessEntity.Identifier,
     * WpsProcessEntity.wps.processIdentifier> {@link JobKey}.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void pauseMonitoring(final WpsProcessEntity processEntity);

    /**
     * Get all {@link WpsProcessEntity} instances of the {@link WpsEntity} which
     * is identified by the identifier string.
     *
     * @param identifier WpsEntity identifier
     * @return List of WpsProcessEntity instances
     */
    public List<WpsProcessEntity> getProcessesOfWps(final String identifier);

    /**
     * Get all {@link WpsProcessEntity} instances of the {@link WpsEntity} which
     * is identified by the wpsEntity.identifier string.
     *
     * @param wpsEntity WpsEntity instance
     * @return List of WpsProcessEntity instances
     */
    public List<WpsProcessEntity> getProcessesOfWps(final WpsEntity wpsEntity);

    /**
     * Get all {@link TriggerConfig} instances of the job which is identified by
     * the &lt;wpsIdentifier, processIdentifier> {@link JobKey}.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of TriggerConfig instances
     */
    public List<TriggerConfig> getTriggers(final String wpsIdentifier, final String processIdentifier);

    /**
     * Get all {@link TriggerConfig} instances of the job which is identified by
     * the &lt;WpsProcessEntity.Identifier,
     * WpsProcessEntity.wps.processIdentifier> {@link JobKey}.
     *
     * @param processEntity WpsProcessEntity instance
     * @return
     */
    public List<TriggerConfig> getTriggers(final WpsProcessEntity processEntity);

    /**
     * Get all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the specific {@link WpsProcessEntity} which
     * is identified by the wpsIdentifier and processIdentifier string.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier);

    /**
     * Get all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the given {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity);

    /**
     * Get all {@link MeasuredDataEntity} instances in the given {@link Range}
     * which was stored in the measure process of the Job of the specific
     * {@link WpsProcessEntity} which is identified by the wpsIdentifier and
     * processIdentifier string.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @param range {@link Range} instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier, final Range range);

    /**
     * Get all {@link MeasuredDataEntity} instances in the given {@link Range}
     * which was stored in the measure process of the Job of the given
     * {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     * @param range {@link Range} instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity, final Range range);

    /**
     * Delete all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the specific {@link WpsProcessEntity} which
     * is identified by the wpsIdentifier and processIdentifier string.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier);

    /**
     * Delete all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the given {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity);

    /**
     * Delete all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the specific {@link WpsProcessEntity} which
     * is identified by the wpsIdentifier and processIdentifier string and they
     * are older as olderAs.
     *
     * @param wpsIdentifier WpsEntity identifier
     * @param processIdentifier WpsProcessEntity identifier
     * @param olderAs Date instance
     */
    public void deleteMeasuredDataOfProcess(final String wpsIdentifier, final String processIdentifier, final Date olderAs);

    /**
     * Delete all {@link MeasuredDataEntity} instances which was stored in the
     * measure process of the Job of the specific {@link WpsProcessEntity} and
     * they are older as olderAs.
     *
     * @param processEntity WpsProcessEntity instance
     * @param olderAs Date instance
     */
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity, final Date olderAs);

    /**
     * Delete a trigger whith the given triggerKey.
     *
     * @param triggerKey {@link TriggerKey} instance
     * @return true if successfully deleted
     */
    public Boolean deleteTrigger(final TriggerKey triggerKey);

    /**
     * Get all WpsEntity instances.
     *
     * @return List of {@link WpsEntity} instances
     */
    public List<WpsEntity> getWpsList();

    /**
     * Delete all {@link MeasuredDataEntity} instances which are older as
     * olderAs.
     *
     * @param olderAs Date instance
     */
    public void deleteMeasuredData(final Date olderAs);
}
