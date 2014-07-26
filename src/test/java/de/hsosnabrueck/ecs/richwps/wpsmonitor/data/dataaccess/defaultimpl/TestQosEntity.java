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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import javax.persistence.Entity;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
public class TestQosEntity extends AbstractQosEntity {

    private static final long serialVersionUID = 1L;

    private Integer someValue;

    public TestQosEntity() {
        someValue = null;
    }

    @Override
    public String getEntityName() {
        return "ResponseAvailabilityEntity";
    }

    @Override
    public String toString() {
        return (someValue != null) ? "Responsetime: " + someValue.toString() : "Was not reachable";
    }

    public Integer getSomeValue() {
        return someValue;
    }

    public void setSomeValue(Integer someValue) {
        this.someValue = someValue;
    }

    public Boolean wasAvailable() {
        return someValue != null;
    }
}
