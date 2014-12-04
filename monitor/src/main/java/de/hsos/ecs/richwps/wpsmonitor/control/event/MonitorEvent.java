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
package de.hsos.ecs.richwps.wpsmonitor.control.event;

import java.util.Date;

/**
 * MonitorEvent instances are used to fire specific events. Events can have a
 * message for the listener. The message is of type Object. The listeners need
 * to know by themselves which concrete type the message is.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorEvent {

    /**
     * Name of the fired event.
     */
    private final String eventName;

    /**
     * Date of the fired event.
     */
    private final Date triggerDate;

    /**
     * Message for the listeners; The listener must know of which type the
     * message is.
     */
    private Object msg;

    /**
     * Constructor; eventName indicates the membership to a registred event.
     *
     * @param eventName
     */
    public MonitorEvent(String eventName) {
        this.eventName = eventName;
        this.triggerDate = initDate();
    }

    public MonitorEvent(String eventName, Object msg) {
        this.eventName = eventName;
        this.msg = msg;
        this.triggerDate = initDate();
    }

    private Date initDate() {
        return new Date();
    }

    public String getEventName() {
        return eventName;
    }

    public <T> T getRightCastedMsg() {
        return (T) msg.getClass().cast(msg);
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public Object getMsg() {
        return msg;
    }
}
