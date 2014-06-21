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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.event;

import java.util.Date;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorEvent {
    private final String eventName;
    private final Date triggerDate;
    private Object msg;

    public MonitorEvent(String eventName) {
        this.eventName = eventName;
        this.triggerDate = initDate();
    }
    
    private Date initDate() {
        return new Date();
    }

    public MonitorEvent(String eventName, Object msg) {
        this.eventName = eventName;
        this.msg = msg;
        this.triggerDate = initDate();
    }

    public String getEventName() {
        return eventName;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public Object getMsg() {
        return msg;
    }
}