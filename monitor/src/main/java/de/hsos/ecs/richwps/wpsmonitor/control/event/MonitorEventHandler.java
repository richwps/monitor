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

import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * EventHandler system for monitor events, like shutdown.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorEventHandler {

    private static final Logger LOG = LogManager.getLogger();
    /**
     * Map that maps eventlisteners to their specific eventnames.
     */
    private final Map<String, List<MonitorEventListener>> events;

    /**
     * Default constructor.
     */
    public MonitorEventHandler() {
        events = new HashMap<>();
    }

    /**
     * Registers an eventname; does nothing if the eventname already exists.
     *
     * @param eventName Event Name which should be registred
     */
    public void registerEvent(final String eventName) {
        if (eventName != null) {
            if (!events.containsKey(eventName)) {
                events.put(eventName, new ArrayList<MonitorEventListener>());

                LOG.debug("EventHandler: Register new event {}", eventName);
            } else {
                LOG.debug("EventHandler: This EventManager already contains an event with name {}.", eventName);
            }
        } else {
            LOG.debug("EventHandler: Parameter eventName was null.");
        }
    }

    /**
     * Fires an event.
     *
     * @param event MonitorEvent-instance
     */
    public void fireEvent(final MonitorEvent event) {
        if (events.containsKey(event.getEventName())) {
            for (MonitorEventListener listener : events.get(event.getEventName())) {
                listener.execute(event);

                LOG.debug("EventHandler: Event {} fired! Execute: {}", event.getEventName(), listener.getClass().getName());
            }
        } else {
            LOG.debug("EventHandler: No event with the given name {} is registred.", event.getEventName());
        }
    }

    /**
     * Registers a listener for a specific event.
     *
     * @param eventName name of the event which the listener should listen
     * @param eventToRegister EventListener instance
     * @throws EventNotFoundException If the event is not found
     */
    public void registerListener(final String eventName, final MonitorEventListener eventToRegister) throws EventNotFoundException {
        if (!events.containsKey(Validate.notNull(eventName, "eventName"))) {
            LOG.debug("EventHandler: registerListener: Event {} not found!", eventName);

            throw new EventNotFoundException(eventName);
        }

        events.get(eventName)
                .add(Validate.notNull(eventToRegister, "eventToRegister"));

        LOG.debug("EventHandler: Register new Listener: {}", eventToRegister.getClass().getName());
    }

    /**
     * Removes a given listener from the eventname.
     *
     * @param eventName name of the event
     * @param listenerToRemove Eventlistener object which should be removed
     * @throws EventNotFoundException If the event is not found
     */
    public void removeListener(final String eventName, final MonitorEventListener listenerToRemove) throws EventNotFoundException {
        if (!events.containsKey(Validate.notNull(eventName, "eventName"))) {
            LOG.debug("EventHandler: removeListener: Event {} not found!", eventName);

            throw new EventNotFoundException(eventName);
        }

        events.get(eventName)
                .remove(Validate.notNull(listenerToRemove, "eventToRemove"));

        LOG.debug("EventHandler: Remove Listener: {}", listenerToRemove.getClass().getName());
    }

    /**
     * Merges the given EventHandler-instance with this EventHandler instance.
     *
     * @param toMerge EventHandler instance.
     */
    public void merge(MonitorEventHandler toMerge) {
        events.putAll(toMerge.events);
    }
}
