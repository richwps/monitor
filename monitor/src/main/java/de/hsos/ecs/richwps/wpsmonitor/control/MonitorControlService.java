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
package de.hsos.ecs.richwps.wpsmonitor.control;

import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Facade to interact with the Monitor.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface MonitorControlService {

    /**
     * Creates and stores a {@link WpsProcessEntity} instance and registers a
     * new Job in the scheduler with the JobKey name as process identifier and
     * the group name as wps id. The JobKey fact is a internally used thing.
     *
     * Here you must define to which WPS (identified through the endpoint
     * parameter) the new Process should be added.
     *
     * @param endpoint The Endpoint of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createAndScheduleProcess(final URL endpoint, final String processIdentifier);

    /**
     * Creates and stores a {@link WpsProcessEntity} instance and registers a
     * new Job in the scheduler with the JobKey name as process identifier and
     * the group name as wps id. The JobKey fact is a internally used thing.
     *
     * Here you must define to which WPS (identified through the endpoint
     * parameter) the new Process should be added.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createAndScheduleProcess(final Long wpsId, final String processIdentifier);

    /**
     * Creates and stores a {@link WpsProcessEntity} instance and registers a
     * new Job in the scheduler with the JobKey name as process identifier and
     * the group name as wps id. The JobKey fact is a internally used thing.
     *
     * @param processEntity WpsProcessEntity instance
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createAndScheduleProcess(final WpsProcessEntity processEntity);

    /**
     * Stores a {@link WpsEntity} instance. Fires a "monitorcontrol.createWps"
     * event.
     *
     * @param wpsEntity WpsEntity identifier
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final WpsEntity wpsEntity);

    /**
     * Creates and stores a {@link WpsEntity} instance. Fires a
     * "monitorcontrol.createWps" event.
     *
     * @param endpoint Wps Uri
     * @return true if sucessfully created, otherwise false
     */
    public Boolean createWps(final URL endpoint);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which are older as
     * olderAs.
     *
     * @param olderAs Date instance
     */
    public void deleteMeasuredData(final Date olderAs);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by endpoint and processIdentifier.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void deleteMeasuredDataOfProcess(final URL endpoint, final String processIdentifier);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by wpsId and processIdentifier.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void deleteMeasuredDataOfProcess(final Long wpsId, final String processIdentifier);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the given {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by endpoint and processIdentifier and they are older as
     * olderAs.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @param olderAs Date instance
     */
    public void deleteMeasuredDataOfProcess(final URL endpoint, final String processIdentifier, final Date olderAs);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by wpsId and processIdentifier and they are older as olderAs.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @param olderAs Date instance
     */
    public void deleteMeasuredDataOfProcess(final Long wpsId, final String processIdentifier, final Date olderAs);

    /**
     * Deletes all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} and they
     * are older as olderAs.
     *
     * @param processEntity WpsProcessEntity instance
     * @param olderAs Date instance
     */
    public void deleteMeasuredDataOfProcess(final WpsProcessEntity processEntity, final Date olderAs);

    /**
     * Deletes a {@link WpsProcessEntity} which is identified through the given
     * endpoint and processIdentifier string.
     *
     * @param endpoint WPS endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if successfully removed
     */
    public Boolean deleteProcess(final URL endpoint, final String processIdentifier);

    /**
     * Deletes a {@link WpsProcessEntity} which is identified through the given
     * wpsId and processIdentifier string.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if successfully removed
     */
    public Boolean deleteProcess(final Long wpsId, final String processIdentifier);

    /**
     * Deletes the given {@link WpsProcessEntity} instance.
     *
     * @param processEntity
     * @return true if successfully removed
     */
    public Boolean deleteProcess(final WpsProcessEntity processEntity);

    /**
     * Deletes a trigger whith the given {@link TriggerConfig}.
     *
     * @param config {@link TriggerConfig} instance
     * @return true if successfully deleted
     */
    public Boolean deleteTrigger(final TriggerConfig config);

    /**
     * Deletes a WpsEntity which is identified through the given endpoint.
     *
     * @param endpoint WpsEntity identifier
     * @return true if successfully removed
     */
    public Boolean deleteWps(final URL endpoint);

    /**
     * Deletes a WpsEntity which is identified through the given wpsId.
     *
     * @param wpsId The intern Database ID of the WPS
     * @return true if successfully removed
     */
    public Boolean deleteWps(final Long wpsId);

    /**
     * Deletes the given {@link WpsEntity} instance.
     *
     * @param wpsEntity WpsEntity instance
     * @return true if successfully removed
     */
    public Boolean deleteWps(final WpsEntity wpsEntity);

    /**
     * Gets all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by endpoint and processIdentifier.
     *
     * @param endpoint WPS endpoint.
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final URL endpoint, final String processIdentifier);

    /**
     * Gets all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by wpsId and processIdentifier.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final Long wpsId, final String processIdentifier);

    /**
     * Gets all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the specific {@link WpsProcessEntity} which is
     * identified by wpsId and processIdentifier.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @param range {@link Range} instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final Long wpsId, final String processIdentifier, final Range range);

    /**
     * Gets all {@link MeasuredDataEntity} instances which were stored in the
     * Jobs' measure process of the given {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity);

    /**
     * Gets all {@link MeasuredDataEntity} instances in the given {@link Range}
     * which were stored in the Jobs' measure process of the specific
     * {@link WpsProcessEntity} which is identified by endpoint and
     * processIdentifier.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @param range {@link Range} instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final URL endpoint, final String processIdentifier, final Range range);

    /**
     * Gets all {@link MeasuredDataEntity} instances in the given {@link Range}
     * which were stored in the Jobs' measure process of the given
     * {@link WpsProcessEntity}.
     *
     * @param processEntity WpsProcessEntity instance
     * @param range {@link Range} instance
     * @return List of MeasuredDataEntity instances
     */
    public List<MeasuredDataEntity> getMeasuredData(final WpsProcessEntity processEntity, final Range range);

    /**
     * Gets an WpsProcessEntity instance which matches the given WPS endpoint 
     * and Process identifier.
     * 
     * @param endpoint WPS Endpoint
     * @param identifier Process identifier
     * @return The specific WpsProcessEntity instance
     */
    public WpsProcessEntity getProcess(final URL endpoint, final String identifier);
    
    /**
     * Gets an WpsProcessEntity instance which matches the given WPS id 
     * and Process identifier.
     * 
     * @param wpsId WPS Endpoint
     * @param identifier Process identifier
     * @return The specific WpsProcessEntity instance
     */
    public WpsProcessEntity getProcess(final Long wpsId, final String identifier);
    
    /**
     * Gets all {@link WpsProcessEntity} instances of the {@link WpsEntity}
     * which are identified by the endpoint.
     *
     * @param endpoint WPS Endpoint
     * @return List of WpsProcessEntity instances
     */
    public List<WpsProcessEntity> getProcesses(final URL endpoint);

    /**
     * Gets all {@link WpsProcessEntity} instances of the {@link WpsEntity}
     * which are identified by wpsId.
     *
     * @param wpsId The intern Database ID of the WPS
     * @return List of WpsProcessEntity instances
     */
    public List<WpsProcessEntity> getProcesses(final Long wpsId);

    /**
     * Gets all {@link WpsProcessEntity} instances of the {@link WpsEntity}
     * which are identified by the wpsEntity.identifier string.
     *
     * @param wpsEntity WpsEntity instance
     * @return List of WpsProcessEntity instances
     */
    public List<WpsProcessEntity> getProcesses(final WpsEntity wpsEntity);

    /**
     * Gets all {@link TriggerConfig} instances of a specific Job.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of {@link TriggerConfig} instances
     */
    public List<TriggerConfig> getTriggers(final URL endpoint, final String processIdentifier);

    /**
     * Gets all {@link TriggerConfig} instances of a specific Job.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return List of {@link TriggerConfig} instances
     */
    public List<TriggerConfig> getTriggers(final Long wpsId, final String processIdentifier);

    /**
     * Gets all {@link TriggerConfig} instances of the job which are identified
     * by the &lt;WpsProcessEntity.Identifier,
     * WpsProcessEntity.wps.processIdentifier> {@link org.quartz.JobKey}.
     *
     * @param processEntity WpsProcessEntity instance
     * @return List of {@link TriggerConfig} instances
     */
    public List<TriggerConfig> getTriggers(final WpsProcessEntity processEntity);

    /**
     * Gets all WpsEntity instances.
     *
     * @return List of {@link WpsEntity} instances
     */
    public List<WpsEntity> getWpsList();

    /**
     * Checks if the job is paused.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if the job is paused
     */
    public Boolean isMonitoringPaused(final URL endpoint, final String processIdentifier);

    /**
     * Checks if the job is paused.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @return true if the job is paused
     */
    public Boolean isMonitoringPaused(final Long wpsId, final String processIdentifier);

    /**
     * Checks if the job is paused.
     *
     * @param processEntity WpsProcessEntity instance
     * @return true if the job is paused
     */
    public Boolean isMonitoringPaused(final WpsProcessEntity processEntity);

    /**
     * Checks if the Process is exists.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier {@link WpsProcessEntity} identifier
     * @return true if the process is exists
     */
    public Boolean isProcessExists(final URL endpoint, final String processIdentifier);

    /**
     * Checks if the Process is exists.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier {@link WpsProcessEntity} identifier
     * @return true if the process is exists
     */
    public Boolean isProcessExists(final Long wpsId, final String processIdentifier);

    /**
     * Checks if the Process is exists.
     *
     * @param wpsProcess {@link WpsProcessEntity} instance
     * @return true if the process is exists
     */
    public Boolean isProcessExists(final WpsProcessEntity wpsProcess);

    /**
     * Checks if the Job of the given {@link WpsProcessEntity} exists.
     *
     * @param wpsProcess {@link WpsProcessEntity} instance
     * @return true if the job is exists
     */
    public Boolean isProcessScheduled(final WpsProcessEntity wpsProcess);

    /**
     * Checks if the Job of the {@link WpsProcessEntity}, which is identified by
     * endpoint and processIdentifier, exists.
     *
     * @param endpoint WPS endpoint
     * @param processIdentifier {@link WpsProcessEntity} identifier
     * @return true if the job is exists
     */
    public Boolean isProcessScheduled(final URL endpoint, final String processIdentifier);

    /**
     * Checks if the Job of the {@link WpsProcessEntity}, which is identified by
     * wpsId and processIdentifier, exists.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier {@link WpsProcessEntity} identifier
     * @return true if the job is exists
     */
    public Boolean isProcessScheduled(final Long wpsId, final String processIdentifier);

    /**
     * Checks if the Wps is exists.
     *
     * @param endpoint WPS Endpoint
     * @return true if the wps is exists
     */
    public Boolean isWpsExists(final URL endpoint);

    /**
     * Checks if the Wps is exists.
     *
     * @param wpsId WPS Endpoint
     * @return true if the wps is exists
     */
    public Boolean isWpsExists(final Long wpsId);

    /**
     * Pauses a Process Job.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void pauseMonitoring(final URL endpoint, final String processIdentifier);

    /**
     * Pauses a Process Job.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void pauseMonitoring(final Long wpsId, final String processIdentifier);

    /**
     * Pauses a Process Job.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void pauseMonitoring(final WpsProcessEntity processEntity);

    /**
     * Resumes the monitoring of the Process Job.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void resumeMonitoring(final URL endpoint, final String processIdentifier);

    /**
     * Resumes the monitoring of the Process Job.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     */
    public void resumeMonitoring(final Long wpsId, final String processIdentifier);

    /**
     * Resumes the monitoring of the Process Job.
     *
     * @param processEntity WpsProcessEntity instance
     */
    public void resumeMonitoring(final WpsProcessEntity processEntity);

    /**
     * Creates and saves a trigger for the job which matches the JobKey name of
     * processIdentifier and group name of wps id.
     *
     * @param endpoint WPS endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @param config {@link TriggerConfig} instance
     * @return {@link TriggerConfig} instance
     */
    public TriggerConfig saveTrigger(final URL endpoint, final String processIdentifier, final TriggerConfig config);

    /**
     * Creates and saves a trigger for the job which matches the JobKey name of
     * processIdentifier and group name of wps id.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @param config {@link TriggerConfig} instance
     * @return {@link TriggerConfig} instance
     */
    public TriggerConfig saveTrigger(final Long wpsId, final String processIdentifier, final TriggerConfig config);

    /**
     * Creates and saves a trigger for the job which matches the JobKey name of
     * processEntity.identifier and group name of processEntity.wps.identifier.
     *
     * @param processEntity WpsProcessEntity instance
     * @param config {@link TriggerConfig} instance
     * @return {@link TriggerConfig} instance
     */
    public TriggerConfig saveTrigger(final WpsProcessEntity processEntity, final TriggerConfig config);

    /**
     * Sets a test request string to {@link WpsProcessEntity} instance which
     * matches the given WPS endpoint and processIdentifier.
     *
     * @param endpoint WPS endpoint
     * @param processIdentifier WpsProcessEntity identifier
     * @param testRequest Testrequest string as XML format
     * @return true if the request ist sucessfully added to the WpsProcessEntity
     */
    public Boolean setTestRequest(final URL endpoint, final String processIdentifier, final String testRequest);

    /**
     * Sets a test request string to {@link WpsProcessEntity} instance which
     * matches the given WPS Database ID and processIdentifier.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param processIdentifier WpsProcessEntity identifier
     * @param testRequest Testrequest string as XML format
     * @return true if the request ist sucessfully added to the WpsProcessEntity
     */
    public Boolean setTestRequest(final Long wpsId, final String processIdentifier, final String testRequest);

    /**
     * Sets a test request string to {@link WpsProcessEntity} instance.
     *
     * @param processEntity WpsProcessEntity instance.
     * @param testRequest Testrequest string as XML format
     * @return true if the request ist sucessfully added to the WpsProcessEntity
     */
    public Boolean setTestRequest(final WpsProcessEntity processEntity, final String testRequest);

    /**
     * Selects a WpsEntity instance that matches the given oldEndpoint and
     * replaces the WpsEntitys endpoint with the newEndpoint.
     *
     * @param oldEndpoint The old Endpoint
     * @param newEndpoint The new one
     * @return Updated WpsEntity instance
     */
    public WpsEntity updateWps(final URL oldEndpoint, final URL newEndpoint);

    /**
     * Selects a WpsEntity instance that matches the given wüsId and replaces
     * the WpsEntitys endpoint with newEndpoint.
     *
     * @param wpsId The intern Database ID of the WPS
     * @param newEndpoint The new one
     * @return Updated WpsEntity instance
     */
    public WpsEntity updateWps(final Long wpsId, final URL newEndpoint);

    /**
     * Gets the intern ID of a registred WPS which is fetched by the given
     * endpoint. If no WPS registred by this endpoint, null is returned.
     *
     * @param endpoint WPS Endpoint
     * @return WPS ID, null if no WPS is found
     */
    public Long getWpsId(final URL endpoint);
    
    /**
     * Gets an WPS by ID
     * 
     * @param wpsId WPS ID
     * @return The matched WpsEntity instance, otherwise null
     */
    public WpsEntity getWps(final Long wpsId);
    
    /**
     * Gets an WPS by their endpoint
     * 
     * @param endpoint WPS endpoint
     * @return The matched WpsEntity instance, otherwise null
     */
    public WpsEntity getWps(final URL endpoint);

    /**
     * Gets the intern ID of a registred WPS Process which is fetched by the
     * given endpoint and processIdentifier. If no WPS Process registred by the
     * given parameters, null is returned.
     *
     * @param endpoint WPS Endpoint
     * @param processIdentifier
     * @return WPS Process ID, null if no WPS is found
     */
    public Long getWpsProcessId(final URL endpoint, final String processIdentifier);
}
