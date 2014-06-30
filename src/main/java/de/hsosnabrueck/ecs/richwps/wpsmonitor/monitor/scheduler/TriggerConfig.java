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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
import java.util.Date;
import org.quartz.DateBuilder;
import org.quartz.TriggerKey;

/**
 * Holds all necessary data to configure a {@link Trigger}.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class TriggerConfig {

    /**
     * Interval Type (e.g Month)
     */
    private DateBuilder.IntervalUnit intervalType;
    
    /**
     * Start Date instance
     */
    private Date start;
    
    /**
     * End Date instance
     */
    private Date end;
    
    /**
     * Interval number
     */
    private Integer interval;
    
    /**
     * TriggerKey - can be null
     */
    private TriggerKey triggerKey;
    
    public TriggerConfig(final Date start, final Date end, final Integer interval, 
            final DateBuilder.IntervalUnit intervalType) {
        
        this(start, end, interval, intervalType, null);
    }
    
    public TriggerConfig(final Date start, final Date end, final Integer interval, 
            final DateBuilder.IntervalUnit intervalType, final TriggerKey triggerKey) {
        
        this.start = Param.notNull(start, "start");
        this.end = Param.notNull(end, "end");
        this.interval = Param.notNull(interval, "interval");
        this.intervalType = Param.notNull(intervalType, "intervalType");
        this.triggerKey = triggerKey;
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

    @Override
    public String toString() {
        return "TriggerConfig{" + "intervalType=" + intervalType + ", start=" + start + ", end=" + end + ", interval=" + interval + ", triggerKey=" + triggerKey + '}';
    }
}
