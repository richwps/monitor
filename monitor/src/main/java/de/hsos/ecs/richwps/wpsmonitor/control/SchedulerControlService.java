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

import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.JobFactoryService;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.measurement.MeasureJob;
import de.hsos.ecs.richwps.wpsmonitor.measurement.MeasureJobFactory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CalendarIntervalTriggerImpl;

/**
 * Holds a Quartz-{@link Scheduler} instance and delegates some complex
 * interactions.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class SchedulerControlService {

    protected final Scheduler scheduler;
    protected final JobFactoryService jobFactoryService;

    public SchedulerControlService(final Scheduler scheduler, final JobFactoryService jobFactoryService) {
        this.scheduler = Validate.notNull(scheduler, "scheduler");
        this.jobFactoryService = Validate.notNull(jobFactoryService, "jobFactoryService");
    }

    /**
     * Starts the scheduler.
     *
     * @throws SchedulerException
     */
    public synchronized void start() throws SchedulerException {
        scheduler.start();
    }

    /**
     * Stops the scheduler.
     *
     * @throws SchedulerException
     */
    public synchronized void shutdown() throws SchedulerException {
        scheduler.shutdown(true);
    }

    /**
     * Adds a job to the scheduler. If the jobKey already exists, then quartz
     * will replace the old job by the new one.
     *
     * @param jobKey JobKey instance
     * @param jobClass Class of the job, must extends Job interface
     * @return jobKey
     * @throws SchedulerException
     */
    public synchronized JobKey addJob(final JobKey jobKey, final Class<? extends Job> jobClass) throws SchedulerException {
        JobDetail newJob = org.quartz.JobBuilder.newJob(jobClass)
                .storeDurably()
                .withIdentity(jobKey)
                .build();

        scheduler.addJob(newJob, true);

        return newJob.getKey();
    }

    /**
     * Pauses a job and all triggers (delegate method to scheduler).
     *
     * @param job
     * @throws SchedulerException
     */
    public synchronized void pauseJob(final JobKey job) throws SchedulerException {
        scheduler.pauseJob(job);
    }

    /**
     * Adds a Wps measurement job.
     *
     * @param process WpsProcessEntity instance
     * @return The jobKey, name = wpsProcess identifier, group = wps identifier
     * @throws SchedulerException
     */
    public synchronized JobKey addWpsAsJob(final WpsProcessEntity process) throws SchedulerException {
        Long wpsId = process.getWps().getId(); 
        String processIdentifier = process.getIdentifier();
        
        Validate.notNull(wpsId, "wpsId");
        Validate.notNull(processIdentifier, "processIdentifier");

        JobKey wpsJobKey = new JobKey(processIdentifier, wpsId.toString());
        
        return addJob(wpsJobKey, MeasureJob.class);
    }


    /**
     * Adds a trigger to a job; internally calendarIntervalSchedule is used.
     *
     * @param jobKey To this job is the trigger added
     * @param config Config with schedule informations
     * @return A new TriggerConfig instance based on the given one but with a
     * triggerkey
     * @throws SchedulerException
     */
    public synchronized TriggerConfig addTriggerToJob(final JobKey jobKey, final TriggerConfig config) throws SchedulerException {
        // Get JobDetail
        JobDetail forJob = scheduler.getJobDetail(Validate.notNull(jobKey, "jobKey"));

        if (forJob == null) {
            throw new SchedulerException("addTriggerToJob failed because no job was found for the given JobKey: " + jobKey.toString());
        }

        Trigger newTrigger = createTriggerWithStartAndEnd(forJob, config);

        scheduler.scheduleJob(newTrigger);

        TriggerConfig newConfig = new TriggerConfig(config);
        newConfig.setTriggerKey(newTrigger.getKey().getName(), newTrigger.getKey().getGroup());

        return newConfig;
    }

    /**
     * Checks if the given triggerKey is already registred in the Scheduler.
     *
     * @param triggerKey TriggerKey instance
     * @return true if the trigger is already registred in the scheduler,
     * otherwise false
     * @throws SchedulerException
     */
    public synchronized Boolean isTriggerRegistred(final TriggerKey triggerKey) throws SchedulerException {
        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
        Boolean matched = false;

        for (String triggerGroupName : triggerGroupNames) {
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));

            for (TriggerKey k : triggerKeys) {
                if (triggerKey.equals(k)) {
                    matched = true;
                }
            }
        }

        return matched;
    }

    /**
     * Checks if the given {@link JobKey} is already registred in the
     * {@link Scheduler}.
     *
     * @param jobKey JobKey instance
     * @return True if the jobKey is already registred in the scheduler
     * @throws SchedulerException
     */
    public synchronized Boolean isJobRegistred(final JobKey jobKey) throws SchedulerException {
        List<String> jobGroupNames = scheduler.getJobGroupNames();
        Boolean matched = false;

        for (String jobGroupName : jobGroupNames) {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName));

            for (JobKey k : jobKeys) {
                if (jobKey.equals(k)) {
                    matched = true;
                }
            }
        }

        return matched;
    }

    // TODO Not necessary yet because the change of use wps ids instead of wpsidentifier
    /**
     * All of the jobs' groupnames with the groupname "oldWpsIdentifier" will be
     * updated to the groupnames of newWpsIdentifier. New jobs will be created
     * and registred in the scheduler to make this possible. After that triggers
     * will be regenerated and added to the new job. The old ones will be
     * removed.
     *
     * @param oldWpsIdentifier Old name to identify the Jobs
     * @param newWpsIdentifier New name which will replace the old one
     * @throws SchedulerException
     
    public synchronized void updateJobsWpsGroupName(final Long oldWpsId, final Long newWpsId) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(
                GroupMatcher.jobGroupEquals(Validate.notNull(oldWpsIdentifier, "oldWpsIdentifier"))
        );

        for (JobKey k : jobKeys) {
            List<TriggerConfig> triggerConfigsOfJob = getTriggerConfigsOfJob(k);

            JobKey newJobKey = addWpsAsJob(newWpsIdentifier, k.getName());

            for (TriggerConfig triggerConfig : triggerConfigsOfJob) {
                addTriggerToJob(newJobKey, triggerConfig);
            }

            removeJob(k);
        }
    }*/

    /**
     * Creates a trigger by the given {@link TriggerConfig}.
     *
     * @param forJob For which job
     * @param config TriggerConfig instance
     * @return Trigger Object
     */
    private synchronized Trigger createTriggerWithStartAndEnd(final JobDetail forJob, final TriggerConfig config) {
        Validate.notNull(forJob, "forJob");
        Validate.notNull(config, "config");

        Trigger newTrigger;

        String jGroup = forJob.getKey().getGroup();
        String tName = UUID.randomUUID().toString();

        // Build Trigger
        TriggerBuilder builder = org.quartz.TriggerBuilder.newTrigger()
                .forJob(forJob)
                .startAt(config.getStart())
                .endAt(config.getEnd())
                .withIdentity(tName, jGroup);

        ScheduleBuilder scheduleBuilder = getScheduleBuilder(config);

        newTrigger = builder
                .withSchedule(scheduleBuilder)
                .build();

        return newTrigger;
    }

    private ScheduleBuilder getScheduleBuilder(final TriggerConfig config) {
        /**
         * enum IntervalType MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, WEEKS,
         * MONTHS
         */
        ScheduleBuilder scheduleBuilder = null;
        Integer interval = config.getInterval();

        switch (config.getIntervalType()) {
            case SECOND:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInSeconds(interval);
                break;
            case MINUTE:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInMinutes(interval);
                break;
            case HOUR:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInHours(interval);
                break;
            case DAY:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInDays(interval);
                break;
            case WEEK:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInWeeks(interval);
                break;
            case MONTH:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInMonths(interval);
                break;
            default:
                throw new AssertionError(config.getIntervalType().name());
        }

        return scheduleBuilder;
    }

    /**
     * Removes a trigger which is identified by the given {@link TriggerKey}.
     *
     * @param triggerKey {@link TriggerKey} instance
     * @throws SchedulerException
     */
    public synchronized void removeTrigger(final TriggerKey triggerKey) throws SchedulerException {
        scheduler.unscheduleJob(triggerKey);
    }

    /**
     * Removes a trigger which is identified by the given {@link TriggerConfig}.
     *
     * @param triggerConfig {@link TriggerConfig} instance
     * @throws SchedulerException
     */
    public synchronized void removeTrigger(final TriggerConfig triggerConfig) throws SchedulerException {
        Validate.notNull(triggerConfig, "triggerConfig");
        if (triggerConfig.getTriggerKey() != null) {
            TriggerKey key = new TriggerKey(triggerConfig.getTriggerName(), triggerConfig.getTriggerGroup());
            removeTrigger(key);
        }
    }

    /**
     * Removes a {@link Job} which is identified by the given {@link JobKey}.
     *
     * @param jobKey JobKey instance
     * @throws SchedulerException
     */
    public synchronized void removeJob(final JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }

    /**
     * Removes all Jobs for the given wpsId (groupname).
     *
     * @param wpsId
     * @return true if is sucessfully removed
     * @throws SchedulerException
     */
    public synchronized Boolean removeWpsJobs(final Long wpsId) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(wpsId.toString()));
        Boolean result = false;

        if (!jobKeys.isEmpty()) {
            List<JobKey> toDelete = new ArrayList<>(jobKeys);

            result = scheduler.deleteJobs(toDelete);
        }

        return result;
    }

    /**
     * Updates a {@link Trigger} - {@link TriggerKey} needs to be set in the
     * given {@link TriggerConfig} instance.
     *
     * @param config TriggerConfig instance
     * @return The new TriggerConfig
     * @throws SchedulerException
     */
    public synchronized TriggerConfig updateTrigger(final TriggerConfig config) throws SchedulerException {
        Validate.notNull(config, "config");
        Validate.notNull(config.getTriggerKey(), "config's TriggerKey");

        TriggerKey key = new TriggerKey(config.getTriggerName(), config.getTriggerGroup());

        JobDetail jobDetail = scheduler.getJobDetail(scheduler
                .getTrigger(key)
                .getJobKey()
        );

        // replace old trigger with a new one
        Trigger newOne = createTriggerWithStartAndEnd(jobDetail, config);
        scheduler.rescheduleJob(key, newOne);

        TriggerConfig updatedTrigger = new TriggerConfig(config);
        updatedTrigger.setTriggerKey(newOne.getKey().getName(), newOne.getKey().getGroup());

        return updatedTrigger;
    }

    /**
     * Gets all {@link TriggerKey}s which are associated with the given
     * {@link JobKey}.
     *
     * @param jobKey JobKey instance
     * @return List of TriggerKey-instances that matches the given JobKey
     * @throws SchedulerException
     */
    public synchronized List<TriggerKey> getTriggerKeysOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerKey> result = new ArrayList<>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(t.getKey());
        }

        return result;
    }
    
    private MeasureJobFactory getMeasureJobFactory() {
        return (MeasureJobFactory) jobFactoryService.get(MeasureJob.class);
    }

    /**
     * Gets all Trigger-objects which are associated with the given
     * {@link JobKey}.
     *
     * @param jobKey JobKey instance
     * @return List of triggers
     * @throws SchedulerException
     */
    private synchronized List<? extends Trigger> getTriggers(final JobKey jobKey) throws SchedulerException {
        return scheduler.getTriggersOfJob(jobKey);
    }

    /**
     * Gets a list of TriggerConfig-instances which are associated with the
     * given {@link JobKey}.
     *
     * @param jobKey JobKey instance
     * @return List of trigger configs
     * @throws SchedulerException
     */
    public List<TriggerConfig> getTriggerConfigsOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerConfig> result = new ArrayList<>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(getConfigOfTrigger(t));
        }

        return result;
    }

    /**
     * Gets a TriggerConfig instance for the given {@link TriggerKey}.
     *
     * @param triggerKey TriggerKey instance
     * @return TriggerConfig instance
     * @throws SchedulerException
     */
    public synchronized TriggerConfig getConfigOfTrigger(final TriggerKey triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey);

        return getConfigOfTrigger(trigger);
    }

    /**
     * Gets a {@link TriggerConfig} instance for the given {@link Trigger}
     * instance.
     *
     * @param trigger Trigger instance
     * @return TriggerConfig instance
     */
    private synchronized TriggerConfig getConfigOfTrigger(final Trigger trigger) {
        TriggerConfig triggerConfig = null;

        // save cast!
        if (trigger.getClass().equals(CalendarIntervalTriggerImpl.class)) {
            CalendarIntervalTrigger calendarTrigger = (CalendarIntervalTrigger) trigger;

            DateBuilder.IntervalUnit repeatIntervalUnit = calendarTrigger.getRepeatIntervalUnit();

            triggerConfig = new TriggerConfig(
                    trigger.getStartTime(),
                    trigger.getEndTime(),
                    calendarTrigger.getRepeatInterval(),
                    fromQuartzToConfig(repeatIntervalUnit));

            TriggerKey key = trigger.getKey();
            triggerConfig.setTriggerKey(key.getName(), key.getGroup());
        }

        return triggerConfig;
    }

    /**
     * Checks if a {@link Job} is paused.
     *
     * @param jobKey {@link JobKey} instance
     * @return true if paused, otherwise false
     * @throws SchedulerException
     */
    public synchronized Boolean isPaused(final JobKey jobKey) throws SchedulerException {
        List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);

        for (Trigger t : triggersOfJob) {
            if (scheduler.getTriggerState(t.getKey()) == Trigger.TriggerState.PAUSED) {
                return true;
            }
        } 

        return false;
    }

    /**
     * Resumes a {@link Job}.
     *
     * @param jobKey JobKey instance
     * @throws SchedulerException
     */
    public synchronized void resume(final JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    /**
     * Gets the {@link Scheduler} instance.
     *
     * @return Scheduler instance
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    public JobFactoryService getJobFactoryService() {
        return jobFactoryService;
    }

    private TriggerConfig.IntervalUnit fromQuartzToConfig(DateBuilder.IntervalUnit quartzIntervalUnit) {
        switch (quartzIntervalUnit) {
            case MILLISECOND:
                return TriggerConfig.IntervalUnit.MILLISECOND;
            case SECOND:
                return TriggerConfig.IntervalUnit.SECOND;
            case MINUTE:
                return TriggerConfig.IntervalUnit.MINUTE;
            case HOUR:
                return TriggerConfig.IntervalUnit.HOUR;
            case DAY:
                return TriggerConfig.IntervalUnit.DAY;
            case WEEK:
                return TriggerConfig.IntervalUnit.WEEK;
            case MONTH:
                return TriggerConfig.IntervalUnit.MONTH;
            case YEAR:
                return TriggerConfig.IntervalUnit.YEAR;
            default:
                throw new AssertionError(quartzIntervalUnit.name());

        }
    }
}
