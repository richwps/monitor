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
package de.hsos.ecs.richwps.wpsmonitor.control.scheduler;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.Hide;
import de.hsos.ecs.richwps.wpsmonitor.util.Pair;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Date;

/**
 * Holds all necessary data to configure a {@link org.quartz.Trigger}.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class TriggerConfig {

    /**
     * Interval Type (e.g Month).
     */
    private IntervalUnit intervalType;

    /**
     * Start Date instance.
     */
    private Date start;

    /**
     * End Date instance.
     */
    private Date end;

    /**
     * Interval number.
     */
    private Integer interval;

    /**
     * TriggerKey - can be null.
     */
    @Hide
    private Pair<String, String> triggerKey;

    /**
     * Creates a new TriggerConfig instance
     */
    public TriggerConfig() {

    }

    /**
     * Creates a new TriggerConfig instance.
     * 
     * @param start Start time of the interval
     * @param end End time of the interval
     * @param interval Interval
     * @param intervalType Interval type
     */
    public TriggerConfig(final Date start, final Date end, final Integer interval,
            final IntervalUnit intervalType) {

        this(start, end, interval, intervalType, null);
    }

    /**
     * Creates a new TriggerConfig instance.
     * 
     * @param start Start time of the interval
     * @param end End time of the interval
     * @param interval Interval
     * @param intervalType Interval type
     * @param triggerKey Key of the trigger
     */
    public TriggerConfig(final Date start, final Date end, final Integer interval,
            final IntervalUnit intervalType, Pair<String, String> triggerKey) {

        this.start = Validate.notNull(start, "start");
        this.end = Validate.notNull(end, "end");
        this.interval = Validate.notNull(interval, "interval");
        this.intervalType = Validate.notNull(intervalType, "intervalType");
        this.triggerKey = triggerKey;
    }

    /**
     * Creates TriggerConfig instance based of the given other one.
     * 
     * @param other TriggerConfig instance.
     */
    public TriggerConfig(TriggerConfig other) {
        this.end = other.end;
        this.interval = other.interval;
        this.intervalType = other.intervalType;
        this.start = other.start;
        this.triggerKey = other.triggerKey;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public IntervalUnit getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(IntervalUnit intervalType) {
        this.intervalType = intervalType;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getInterval() {
        return interval;
    }

    public Pair<String, String> getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(Pair<String, String> triggerKey) {
        this.triggerKey = triggerKey;
    }

    public void setTriggerKey(String keyName, String keyGroup) {
        this.triggerKey = new Pair<>(keyName, keyGroup);
    }

    public String getTriggerName() {
        String name = null;

        if (triggerKey != null) {
            name = triggerKey.getLeft();
        }

        return name;
    }

    public String getTriggerGroup() {
        String group = null;

        if (triggerKey != null) {
            group = triggerKey.getRight();
        }

        return group;
    }

    public Boolean isSaved() {
        return getTriggerKey() != null;
    }

    @Override
    public String toString() {
        return "TriggerConfig{" + "intervalType=" + intervalType + ", start=" + start + ", end=" + end + ", interval=" + interval + ", triggerKey=" + triggerKey + '}';
    }

    public enum IntervalUnit {

        MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
    }
}
