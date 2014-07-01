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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * EventHandler system for monitor events like shutdown
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorEventHandler {

    /**
     * Map that maps eventlistener to the specific eventname
     */
    private Map<String, List<MonitorEventListener>> events;

    private final static Logger log = LogManager.getLogger();

    /**
     * Default constructor
     */
    public MonitorEventHandler() {
        events = new HashMap<String, List<MonitorEventListener>>();
    }

    /**
     * Registers an eventname; does nothing if the eventname is allready exists
     *
     * @param eventName Event Name which should be registred
     */
    public void registerEvent(final String eventName) {
        if (eventName != null) {
            if (!events.containsKey(eventName)) {
                events.put(eventName, new ArrayList<MonitorEventListener>());

                log.debug("EventHandler: Register new Event {}", eventName);
            } else {
                log.debug("EventHandler: This EventManager already contains this Event with name {}.", eventName);
            }
        } else {
            log.debug("EventHandler: Parameter eventName was null.");
        }
    }

    /**
     * Fires an event
     *
     * @param event MonitorEvent-instance
     */
    public void fireEvent(final MonitorEvent event) {
        if (events.containsKey(event.getEventName())) {
            for (MonitorEventListener listener : events.get(event.getEventName())) {
                listener.execute(event);

                log.debug("EventHandler: Event {} fired! Execute: {}", event.getEventName(), listener.getClass().getName());
            }
        } else {
            log.debug("EventHandler: No Event with the given Name {} is registred.", event.getEventName());
        }
    }

    /**
     * Registers a listener for a specific event
     *
     * @param eventName name of the event which the listener should listen
     * @param eventToRegister EventListener instance
     * @throws EventNotFound If the event is not found
     */
    public void registerListener(final String eventName, final MonitorEventListener eventToRegister) throws EventNotFound {
        if (!events.containsKey(Validate.notNull(eventName, "eventName"))) {
            log.debug("EventHandler: registerListener: Event {} not found!", eventName);

            throw new EventNotFound(eventName);
        }

        events.get(eventName)
                .add(Validate.notNull(eventToRegister, "eventToRegister"));

        log.debug("EventHandler: Register new Listener: {}", eventToRegister.getClass().getName());
    }

    /**
     * Removes a given listener from the eventname
     *
     * @param eventName name of the event
     * @param eventToRemove Eventlistener object which should be removed
     * @throws EventNotFound If the event is not found
     */
    public void removeListener(final String eventName, final MonitorEventListener eventToRemove) throws EventNotFound {
        if (!events.containsKey(Validate.notNull(eventName, "eventName"))) {
            log.debug("EventHandler: removeListener: Event {} not found!", eventName);

            throw new EventNotFound(eventName);
        }

        events.get(eventName)
                .remove(Validate.notNull(eventToRemove, "eventToRemove"));

        log.debug("EventHandler: Remove Listener: {}", eventToRemove.getClass().getName());
    }
}
