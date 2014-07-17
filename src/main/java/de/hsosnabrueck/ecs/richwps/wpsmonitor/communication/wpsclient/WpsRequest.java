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

import java.util.Date;

/**
 * Class which contains all necessary data to request a wps server.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsRequest {

    /**
     * Raw xml request.
     */
    private final String rawRequest;

    /**
     * Information to call the wps server.
     */
    private final WpsProcessInfo processInfo;

    /**
     * Time at which the request was executed.
     */
    private Date requestTime;

    /**
     * Constructor.
     *
     * @param rawRequest Raw XML Request. WPS execute-command String.
     * @param processInfo Information to call the WPS Server.
     */
    public WpsRequest(final String rawRequest, final WpsProcessInfo processInfo) {
        this.rawRequest = rawRequest;
        this.processInfo = processInfo;
    }

    /**
     * Sets the current time as request time.
     */
    public void prepareRequest() {
        requestTime = new Date();
    }

    /**
     * Indicates if this request was executed.
     *
     * @return true if already executed, otherwise false
     */
    public Boolean isExecuted() {
        return requestTime != null;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public WpsProcessInfo getProcessInfo() {
        return processInfo;
    }

    @Override
    public String toString() {
        return rawRequest;
    }
}
