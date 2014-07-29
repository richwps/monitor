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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import static de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate.notNull;
import java.util.Date;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Measurement {

    private final AbstractQosEntity values;
    private final Date measurementDate;

    public Measurement(final AbstractQosEntity entity, final Date date) {
        this.values = notNull(entity, "entity");
        this.measurementDate = notNull(date, "date");
    }

    public <T> T getEntity() {
        return (T) values.getClass()
                .cast(values);
    }

    public Date getDate() {
        return measurementDate;
    }
}
