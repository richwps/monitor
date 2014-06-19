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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.client;

import java.util.Date;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsResponse {
    private Exception exception;
    private Date responseTime;
    private String responseBody;

    public WpsResponse(final Exception exception, final String responseBody, final Date responseTime) {
        this.exception = exception;
        this.responseBody = responseBody;
        this.responseTime = responseTime;
    }

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
    
    public Boolean isWpsException() {
        return isException() && exception instanceof WpsException;
    }
    
    public Boolean isConnectionException() {
        return isException() && exception instanceof ConnectionException;
    }
    
    public Boolean isOtherException() {
        return isException() && !(isWpsException() || isConnectionException());
    }
    
    public String getExceptionMessage() {
        String message = null;
        
        if(exception != null) {
            message = exception.getMessage();
        }
        
        return message;
    }
}
