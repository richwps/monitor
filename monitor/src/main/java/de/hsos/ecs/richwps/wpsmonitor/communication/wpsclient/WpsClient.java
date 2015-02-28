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
package de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient;

/**
 * Interface for WpsClients.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface WpsClient {

    /**
     * Invoke the WpsClient to execute the given wpsRequest. The WpsClient
     * should call WpsRequest.prepareRequest() at real execution Time.
     * WpsRequest.prepareRequest() sets the now-time as a new Date() Object.
     *
     * e.g.      <code><pre>
     *     public void execute(final WpsRequest request) {
     *          // prepare client
     *          // do some stuff
     *          request.prepareRequest();
     *          HttpResponse response = specificCode.executeRequest(niceThingsToDo);
     *          WpsResponse response = new WpsResponse ..
     *     }
     * </pre>
     * </code>
     *
     * The return value WpsResponse should also contain the accurate
     * response-Time. Accurate response-time means the response time of the
     * Server and not the time after the WpsClient has succesfully processed the
     * execute-Method.
     *
     * @param wpsRequest WpsRequest instance that contains all necessary
     * informations to call a server
     * @return The Answer from the called server
     */
    public WpsResponse execute(final WpsRequest wpsRequest);

    /**
     * Inits the specific client implementation with a WpsClientConfig instance.
     *
     * @param config
     */
    public void init(final WpsClientConfig config);
}
