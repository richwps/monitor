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
public class WpsClientConfig {
    /**
     * Timeout in ms
     */
    private Integer connectionTimeout;
    
    /**
     * Will set the timeout to 10 ms
     */
    public WpsClientConfig() {
       connectionTimeout = 10000;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public String toString() {
        return "WpsClientConfig{" + "connectionTimeout=" + connectionTimeout + '}';
    }
}
