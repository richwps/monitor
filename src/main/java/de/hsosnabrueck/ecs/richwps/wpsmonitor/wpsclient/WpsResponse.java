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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.wpsclient;

import java.util.Date;

/**
 * Present the response of a wps server. Contains the full response in
 * responseBody and the response Time. In addtion it contains possible
 * exceptions at execute Time of the specific request.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsResponse {

    /**
     * Exception Object, can be null.
     */
    private Exception exception;

    /**
     * Response time of the wps server.
     */
    private Date responseTime;

    /**
     * Raw answer of the wps server.
     */
    private String responseBody;

    /**
     * Constructor.
     *
     * @param exception Exception Object, if an exception happened
     * @param responseBody The Response of wps server whitout header
     * @param responseTime The Response-Time
     */
    public WpsResponse(final Exception exception, final String responseBody, final Date responseTime) {
        this.exception = exception;
        this.responseBody = responseBody;
        this.responseTime = responseTime;
    }

    /**
     * Constructor.
     *
     * @param responseBody The Response of wps server whitout header
     * @param responseTime The Response-Time
     */
    public WpsResponse(final String responseBody, final Date responseTime) {
        this(null, responseBody, responseTime);
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Boolean isException() {
        return exception != null;
    }

    /**
     * Indicates if WpsException has occurred.
     *
     * @return true if WpsException happened
     */
    public Boolean isWpsException() {
        return isException() && exception instanceof WpsException;
    }

    /**
     * Indicates if ConnectionException has occurred.
     *
     * @return true if ConnectionException happened
     */
    public Boolean isConnectionException() {
        return isException() && exception instanceof WpsConnectionException;
    }

    /**
     * Indicates if an other Exceptions has occurred.
     *
     * @return true if other Exception type as WpsEception or
     * ConnectionException happened
     */
    public Boolean isOtherException() {
        return isException() && !(isWpsException() || isConnectionException());
    }

    /**
     * Returns the exception Message, if an exception occurred.
     *
     * @return The Exception Message
     */
    public String getExceptionMessage() {
        String message = null;

        if (exception != null) {
            message = exception.getMessage();
        }

        return message;
    }
}
