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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
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
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class SchedulerControl {

    protected final Scheduler scheduler;
    protected final JobFactoryService jobFactoryService;

    public SchedulerControl(Scheduler scheduler, JobFactoryService jobFactoryService) {
        this.scheduler = Param.notNull(scheduler, "scheduler");
        this.jobFactoryService = Param.notNull(jobFactoryService, "jobFactoryService");
    }

    /**
     * Starts the scheduler
     *
     * @throws SchedulerException
     */
    public synchronized void start() throws SchedulerException {
        scheduler.start();
    }

    /**
     * Stop the scheduler
     *
     * @throws SchedulerException
     */
    public synchronized void shutdown() throws SchedulerException {
        scheduler.shutdown(true);
    }

    /**
     * Add a job to the scheduler. If the jobKey is already exists, then quartz
     * will replace the old job by the new one
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
     * Pauses a job and all triggers (delegate method to scheduler)
     * 
     * @param job
     * @throws SchedulerException 
     */
    public synchronized void pauseJob(final JobKey job) throws SchedulerException {
        scheduler.pauseJob(job);
    }

    /**
     * Add a Wps measurement job
     *
     * @param process WpsProcessEntity instance
     * @return The jobKey, name = wpsProcess identifier, group = wps identifier
     * @throws SchedulerException
     */
    public synchronized JobKey addWpsAsJob(final WpsProcessEntity process) throws SchedulerException {
        return addWpsAsJob(process.getWps().getIdentifier(), process.getIdentifier());
    }

    /**
     * Add a Wps measurement job
     *
     * @param wpsIdentifier Wps entity identifier
     * @param processIdentifier wpsprocess entity identifier
     * @return The jobKey, name = wpsProcess identifier, group = wps identifier
     * @throws SchedulerException
     */
    public synchronized JobKey addWpsAsJob(final String wpsIdentifier, final String processIdentifier) throws SchedulerException {
        Param.notNull(wpsIdentifier, "wpsIdentifier");
        Param.notNull(processIdentifier, "processIdentifier");

        JobKey wpsJobKey = new JobKey(processIdentifier, wpsIdentifier);

        return addJob(wpsJobKey, MeasureJob.class);
    }

    /**
     * Add a trigger to a job; internally calendarIntervalSchedule is used
     *
     * @param jobKey To this job is the trigger added
     * @param config Config with schedule informations
     * @return The generated triggerkey (UUID will be used for the name part)
     * @throws SchedulerException
     */
    public synchronized TriggerKey addTriggerToJob(final JobKey jobKey, final TriggerConfig config) throws SchedulerException {
        // Get JobDetail
        JobDetail forJob = scheduler.getJobDetail(Param.notNull(jobKey, "jobKey"));
        Trigger newTrigger = createTriggerWithStartAndEnd(forJob, config);

        scheduler.scheduleJob(newTrigger);

        return newTrigger.getKey();
    }

    public synchronized TriggerKey addPermaTriggerToJob(final JobKey jobKey, final TriggerConfig config) throws SchedulerException {
        // Get JobDetail
        JobDetail forJob = scheduler.getJobDetail(Param.notNull(jobKey, "jobKey"));
        Trigger newTrigger = createTriggerWithStartAndEnd(forJob, config)
                .getTriggerBuilder()
                .endAt(null)
                .build();

        scheduler.scheduleJob(newTrigger);

        return newTrigger.getKey();
    }

    /**
     * Checks if the given triggerKey is already registred in the scheduler
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
     * Checks if the given jobKey is already registred in the scheduler
     * 
     * @param jobKey JobKey instance
     * @return True if the jobKey is already registred in the scheduler
     * @throws SchedulerException 
     */
    public synchronized Boolean isJobRegistred(final JobKey jobKey) throws SchedulerException {
        List<String> jobGroupNames = scheduler.getJobGroupNames();
        Boolean matched = false;
        
        for(String jobGroupName : jobGroupNames) {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName));
            
            for(JobKey k : jobKeys) {
                if(jobKey.equals(k)) {
                    matched = true;
                }
            }
        }
        
        return matched;
    }

    /**
     * Will update all groupname of jobs with the groupname oldWpsIdentifier to
     * the name of newWpsIdentifier. To make this possible, new jobs will be
     * created and registred in the scheduler. Then triggers will be generated
     * and added. The old ones will be removed.
     *
     * @param oldWpsIdentifier Old name to identifie the Jobs
     * @param newWpsIdentifier New name which will replace the old one
     * @throws SchedulerException
     */
    public synchronized void updateJobsWpsGroupName(final String oldWpsIdentifier, final String newWpsIdentifier) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(oldWpsIdentifier));

        for (JobKey k : jobKeys) {
            List<TriggerConfig> triggerConfigsOfJob = getTriggerConfigsOfJob(k);

            JobKey newJobKey = addWpsAsJob(newWpsIdentifier, k.getName());

            for (TriggerConfig triggerConfig : triggerConfigsOfJob) {
                addTriggerToJob(newJobKey, triggerConfig);
            }

            removeJob(k);
        }
    }

    /**
     * Creates a trigger by the given TriggerConfig
     *
     * @param forJob For which job
     * @param config TriggerConfig instance
     * @return Trigger Object
     */
    private synchronized Trigger createTriggerWithStartAndEnd(final JobDetail forJob, final TriggerConfig config) {
        Trigger newTrigger;
        ScheduleBuilder scheduleBuilder = null;

        // Build Trigger
        TriggerBuilder builder = org.quartz.TriggerBuilder.newTrigger()
                .forJob(forJob)
                .startAt(config.getStart())
                .endAt(config.getEnd())
                .withIdentity(UUID.randomUUID().toString(), forJob.getKey().getGroup());

        /**
         * enum IntervalType MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, WEEKS,
         * MONTHS
         */
        Integer interval = config.getInterval();

        switch (Param.notNull(config, "config").getIntervalType()) {
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
        }

        newTrigger = builder
                .withSchedule(scheduleBuilder)
                .build(); 

        return newTrigger;
    }

    /**
     * Removes a trigger which is identified by the given triggerkey
     *
     * @param triggerKey TriggerKey instance
     * @throws SchedulerException
     */
    public synchronized void removeTrigger(final TriggerKey triggerKey) throws SchedulerException {
        scheduler.unscheduleJob(triggerKey);
    }

    /**
     * Removes a Job which is identified by the given jobkey
     *
     * @param jobKey JobKey instance
     * @throws SchedulerException
     */
    public synchronized void removeJob(final JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }

    /**
     * Remove all Jobs for the given wpsIdentifier (groupname)
     *
     * @param wpsIdentifier Wps identifier
     * @return true if is sucessfully removed
     * @throws SchedulerException
     */
    public synchronized Boolean removeWpsJobs(final String wpsIdentifier) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(wpsIdentifier));
        Boolean result = false;

        if (jobKeys.size() > 0) {
            List<JobKey> toDelete = new ArrayList<JobKey>(jobKeys);

            result = scheduler.deleteJobs(toDelete);
        }

        return result;
    }

    /**
     * Updates a trigger - triggerKey need to be set in triggerconfig
     *
     * @param config TriggerConfig instance
     * @throws SchedulerException
     */
    public synchronized void updateTrigger(final TriggerConfig config) throws SchedulerException {

        if (config.getTriggerKey() != null) {
            JobDetail jobDetail = scheduler.getJobDetail(scheduler
                    .getTrigger(config.getTriggerKey())
                    .getJobKey()
            );

            // replace old trigger with a new one
            scheduler.rescheduleJob(config.getTriggerKey(), createTriggerWithStartAndEnd(jobDetail, config));
        }
    }

    /**
     * Get all TriggerKeys which are associated with the given jobKey
     *
     * @param jobKey JobKey instance
     * @return List of TriggerKey-instances that matches to the given jobKey
     * @throws SchedulerException
     */
    public synchronized List<TriggerKey> getTriggerKeysOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerKey> result = new ArrayList<TriggerKey>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(t.getKey());
        }

        return result;
    }

    /**
     * Get all Trigger-objects which are associated with the given jobKey
     *
     * @param jobKey JobKey instance
     * @return List of triggers
     * @throws SchedulerException
     */
    private synchronized List<? extends Trigger> getTriggers(final JobKey jobKey) throws SchedulerException {
        return scheduler.getTriggersOfJob(jobKey);
    }

    /**
     * Get a list of TriggerConfig-instances which are associated with the given
     * jobKey
     *
     * @param jobKey JobKey instance
     * @return List of trigger configs
     * @throws SchedulerException
     */
    public List<TriggerConfig> getTriggerConfigsOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerConfig> result = new ArrayList<TriggerConfig>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(getConfigOfTrigger(t));
        }

        return result;
    }

    /**
     * Get a TriggerConfig instance for the given triggerKey
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
     * Get a TriggerConfig instance for the given Trigger instance
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
                    repeatIntervalUnit,
                    trigger.getKey()
            );
        }

        return triggerConfig;
    }

    /**
     * Checks if a job is paused
     *
     * @param jobKey JobKey instance
     * @return true if paused, otherwise false
     * @throws SchedulerException
     */
    public synchronized Boolean isPaused(final JobKey jobKey) throws SchedulerException {
        for (String triggerGroup : scheduler.getPausedTriggerGroups()) {
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));

            for (TriggerKey triggerKey : triggerKeys) {
                Trigger trigger = scheduler.getTrigger(triggerKey);

                if (trigger.getJobKey().equals(jobKey)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Resumes a job
     *
     * @param jobKey JobKey instance
     * @throws SchedulerException
     */
    public synchronized void resume(final JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    /**
     * Get the scheduler instance
     *
     * @return Scheduler instance
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    public JobFactoryService getJobFactoryService() {
        return jobFactoryService;
    }
}
