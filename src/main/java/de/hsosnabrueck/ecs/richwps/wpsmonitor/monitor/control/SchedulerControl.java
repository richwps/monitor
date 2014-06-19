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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.DateBuilder;
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
public class SchedulerControl {

    protected Scheduler scheduler;

    public SchedulerControl(Scheduler scheduler) {
        this.scheduler = Param.notNull(scheduler, "scheduler");
    }

    public void start() throws SchedulerException {
        scheduler.start();
    }

    public void shutdown() throws SchedulerException {
        scheduler.shutdown(true);
    }

    public JobKey addWpsAsJob(final WpsProcessEntity process) throws SchedulerException {
        JobDetail newWpsJob = org.quartz.JobBuilder.newJob(MeasureJob.class)
                .storeDurably()
                .withIdentity(process.getIdentifier(), process.getWps().getIdentifier())
                .build();

        scheduler.addJob(newWpsJob, true);

        return newWpsJob.getKey();
    }

    public TriggerKey addTriggerToJob(final JobKey jobKey, final TriggerConfig config) throws SchedulerException {
        // Get JobDetail
        JobDetail forJob = scheduler.getJobDetail(Param.notNull(jobKey, "jobKey"));

        Trigger newTrigger = createTrigger(forJob, config);

        scheduler.scheduleJob(newTrigger);

        return newTrigger.getKey();
    }

    private Trigger createTrigger(final JobDetail forJob, final TriggerConfig config) {
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

    public void removeTrigger(final TriggerKey triggerKey) throws SchedulerException {
        scheduler.unscheduleJob(triggerKey);
    }

    public void removeWpsJob(final JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }

    public Boolean removeWpsJobs(final String wpsIdentifier) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(wpsIdentifier));
        Boolean result = false;
        
        if (jobKeys.size() > 0) {
            List<JobKey> toDelete = new ArrayList<JobKey>(jobKeys);

            result = scheduler.deleteJobs(toDelete);
        }

        return result;
    }

    public void updateTrigger(final TriggerConfig config) throws SchedulerException {
        
        if(config.getTriggerKey() != null) {
            JobDetail jobDetail = scheduler.getJobDetail(scheduler
                    .getTrigger(config.getTriggerKey())
                    .getJobKey()
            );

            // replace old trigger with a new one
            scheduler.rescheduleJob(config.getTriggerKey(), createTrigger(jobDetail, config));
        }
    }

    public List<TriggerKey> getTriggerKeysOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerKey> result = new ArrayList<TriggerKey>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(t.getKey());
        }

        return result;
    }

    private List<? extends Trigger> getTriggers(final JobKey jobKey) throws SchedulerException {
        return scheduler.getTriggersOfJob(jobKey);
    }

    public List<TriggerConfig> getTriggerConfigsOfJob(final JobKey jobKey) throws SchedulerException {
        List<TriggerConfig> result = new ArrayList<TriggerConfig>();

        for (Trigger t : getTriggers(jobKey)) {
            result.add(getConfigOfTrigger(t));
        }

        return result;
    }

    public TriggerConfig getConfigOfTrigger(final TriggerKey triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey);

        return getConfigOfTrigger(trigger);
    }

    private TriggerConfig getConfigOfTrigger(final Trigger trigger) {
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

    public Scheduler getScheduler() {
        return scheduler;
    }
}
