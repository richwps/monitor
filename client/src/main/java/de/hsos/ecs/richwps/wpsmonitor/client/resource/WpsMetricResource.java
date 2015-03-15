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
package de.hsos.ecs.richwps.wpsmonitor.client.resource;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasuredValue;
import java.util.Map;
import org.apache.commons.lang.Validate;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMetricResource {
    private final String name;
    private final Map<String, MeasuredValue> values;

    public WpsMetricResource(final String name, final Map<String, MeasuredValue> values) {
        Validate.notNull(name);
        
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Map<String, MeasuredValue> getValues() {
        return values;
    }

    public MeasuredValue getValue(final String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return "WpsMetricResource{" + "name=" + name + ", values=" + values + '}';
    }    
}
