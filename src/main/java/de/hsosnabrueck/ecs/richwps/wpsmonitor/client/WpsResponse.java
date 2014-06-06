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

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsResponse {
    private Exception exception;
    //private Exception exception;
    private String responseBody;

    public WpsResponse(Exception exception, String responseBody) {
        this.exception = exception;
        this.responseBody = responseBody;
    }

    public WpsResponse(String responseBody) {
        this(null, responseBody);
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
    
    public Boolean isWpsException() {
        return exception instanceof WpsException;
    }
    
    public Boolean isConnectionException() {
        return exception instanceof ConnectionException;
    }
    
    public Boolean isOtherException() {
        return !(isWpsException() || isConnectionException());
    }
    
    public String getExceptionMessage() {
        String message = null;
        
        if(exception != null) {
            message = exception.getMessage();
        }
        
        return message;
    }
}
