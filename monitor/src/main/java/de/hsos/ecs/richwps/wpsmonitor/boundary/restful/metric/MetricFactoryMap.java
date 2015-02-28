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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric;

import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates a very complex datastructure. It's a part of the RESTful
 * Webservice mechanism to map factories to the name of AbstractQosEntity names.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MetricFactoryMap {

    private final Map<String, Set<Factory<QosMetric>>> converterMap;

    /**
     * Creates a new MetricConverterMap
     */
    public MetricFactoryMap() {
        this.converterMap = new HashMap<>();
    }

    /**
     * Adds a metric to a AbstractQosEntity instances name
     *
     * @param index Name of the AbstractQosEntity instance
     * @param converterFactory The specific converter for the AbstractQosEntity
     * instance
     * @return this for method chaining
     */
    public MetricFactoryMap add(final String index, final Factory<QosMetric> converterFactory) {
        Validate.notNull(index, "index");
        Validate.notNull(converterFactory, "converterFactory");

        if (!converterMap.containsKey(index)) {
            converterMap.put(index, new HashSet<Factory<QosMetric>>());
        }

        converterMap.get(index).add(converterFactory);

        return this;
    }

    public Set<Factory<QosMetric>> get(final String index) {
        return converterMap.get(index);
    }

    public Set<Map.Entry<String, Set<Factory<QosMetric>>>> entrySet() {
        return converterMap.entrySet();
    }
}
