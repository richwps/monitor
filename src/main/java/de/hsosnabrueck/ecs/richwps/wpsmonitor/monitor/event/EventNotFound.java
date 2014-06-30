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

/**
 * Will be thrown if the given eventName not matched any registred eventName.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EventNotFound extends Exception {

    public EventNotFound(final String eventName) {
        super("Eventname " + eventName + " could not resolved.");
    }

}