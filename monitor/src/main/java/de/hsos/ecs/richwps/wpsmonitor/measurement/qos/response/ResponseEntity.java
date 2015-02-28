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
package de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response;

import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import javax.persistence.Entity;

/**
 * Entity for Response Time values.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
public class ResponseEntity extends AbstractQosEntity {

    private static final long serialVersionUID = 1L;

    private Integer responseTime;

    public ResponseEntity() {
        responseTime = null;
    }

    @Override
    public String getEntityName() {
        return "ResponseAvailabilityEntity";
    }

    @Override
    public String toString() {
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
