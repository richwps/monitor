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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient;

/**
 * This Exception should be thrown if the wps server is offline, or the answer
 * is not of type XML.
 *
 * Normally this Exception is never thrown, but used from a WPS Client.
 * Instances of this Class will be created if the WPS-Client detects that the
 * server is offline or the response is not of type xml.
 *
 * @see WpsResponse
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsConnectionException extends Exception {

    public WpsConnectionException() {
        super("Can't connect to the server.");
    }
}
