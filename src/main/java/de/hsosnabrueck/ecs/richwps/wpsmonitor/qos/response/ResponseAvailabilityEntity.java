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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.qos.response;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseAvailabilityEntity extends AbstractQosEntity {
    private Integer responseTime;

    public ResponseAvailabilityEntity() {
        responseTime = null;
    }
    
    @Override
    public String getEntityName() {
        return "ResponseAvailabilityEntity";
    }

    @Override
    public String getDataAsString() {
        return (responseTime != null) ? "Responsetime: " + responseTime.toString() : "Was not reachable";
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }
    
    public Boolean wasAvailable() {
        return responseTime != null;
    }
}
