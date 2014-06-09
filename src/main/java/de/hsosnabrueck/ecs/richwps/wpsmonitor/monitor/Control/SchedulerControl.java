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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SchedulerControl {

    protected Scheduler scheduler;

    public SchedulerControl(Scheduler scheduler) {
        this.scheduler = Param.notNull(scheduler, "scheduler");
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
                .withIdentity("", forJob.getKey().getGroup());

        /**
         * enum IntervalType MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, WEEKS,
         * MONTHS
         */
        Integer interval = config.getInterval();

        switch (Param.notNull(config, "config").getIntervalType()) {
            case MILLISECOND:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withInterval(interval, DateBuilder.IntervalUnit.MILLISECOND);
                break;
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

    public void updateTrigger(final TriggerKey triggerKey, final TriggerConfig config) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(scheduler
                .getTrigger(triggerKey)
                .getJobKey()
        );

        // replace old trigger with a new one
        scheduler.rescheduleJob(triggerKey, createTrigger(jobDetail, config));
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
            result.add(getTriggerConfigOfTrigger(t));
        }

        return result;
    }

    public TriggerConfig getConfigOfTrigger(final TriggerKey triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey);
        
        return getTriggerConfigOfTrigger(trigger);
    }

    private TriggerConfig getTriggerConfigOfTrigger(final Trigger trigger) {
        TriggerConfig triggerConfig = null;
        
        // save cast!
        if (trigger.getClass().equals(CalendarIntervalTrigger.class)) {
            CalendarIntervalTrigger calendarTrigger = (CalendarIntervalTrigger) trigger;

            DateBuilder.IntervalUnit repeatIntervalUnit = calendarTrigger.getRepeatIntervalUnit();

            triggerConfig = new TriggerConfig(
                    trigger.getStartTime(),
                    trigger.getEndTime(),
                    calendarTrigger.getRepeatInterval(),
                    convertIntervalUnit(repeatIntervalUnit)
            );
        }

        return triggerConfig;
    }

    private IntervalType convertIntervalUnit(final DateBuilder.IntervalUnit iunit) {
        // i was unsure if i schould use DataTypes of the scheduler library
        // but i was used JobKey and TriggerKey too ..
        IntervalType result = null;
        switch (iunit) {
            case MILLISECOND:
                result = IntervalType.MILLISECOND;
                break;
            case SECOND:
                result = IntervalType.SECOND;
                break;
            case MINUTE:
                result = IntervalType.MINUTE;
                break;
            case HOUR:
                result = IntervalType.HOUR;
                break;
            case DAY:
                result = IntervalType.DAY;
                break;
            case WEEK:
                result = IntervalType.WEEK;
                break;
            case MONTH:
                result = IntervalType.MONTH;
                break;
            case YEAR:
                result = IntervalType.YEAR;
                break;
        }

        return result;
    }
}
