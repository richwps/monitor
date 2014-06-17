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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.Date;
import org.quartz.DateBuilder;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class TriggerConfig {

    private DateBuilder.IntervalUnit intervalType;
    private Date start;
    private Date end;
    private Integer interval;
    private TriggerKey triggerKey;
    
    public TriggerConfig(final Date start, final Date end, final Integer interval, final DateBuilder.IntervalUnit intervalType) {
        this.start = Param.notNull(start, "start");
        this.end = Param.notNull(end, "end");
        this.interval = Param.notNull(interval, "interval");
        this.intervalType = Param.notNull(intervalType, "intervalType");
    }

    public DateBuilder.IntervalUnit getIntervalType() {
        return intervalType;
    }
    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getInterval() {
        return interval;
    }

    public TriggerKey getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(TriggerKey triggerKey) {
        this.triggerKey = triggerKey;
    }
}
