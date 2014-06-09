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
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
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
        switch (Param.notNull(config, "config").getIntervalType()) {
            case MILLISECONDS:
                scheduleBuilder = simpleSchedule()
                        .withIntervalInMilliseconds(config.getInterval());
                break;
            case SECONDS:
                scheduleBuilder = simpleSchedule()
                        .withIntervalInSeconds(config.getInterval().intValue());
                break;
            case MINUTES:
                scheduleBuilder = simpleSchedule()
                        .withIntervalInMinutes(config.getInterval().intValue());
                break;
            case HOURS:
                scheduleBuilder = simpleSchedule()
                        .withIntervalInHours(config.getInterval().intValue());
                break;
            case DAYS:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInDays(config.getInterval().intValue());
                break;
            case WEEKS:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInWeeks(config.getInterval().intValue());
                break;
            case MONTHS:
                scheduleBuilder = CalendarIntervalScheduleBuilder
                        .calendarIntervalSchedule()
                        .withIntervalInMonths(config.getInterval().intValue());
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
}
