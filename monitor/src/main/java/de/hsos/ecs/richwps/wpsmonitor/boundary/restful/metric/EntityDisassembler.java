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

import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Disassembles the bulk of MeasuredDataEntity objects and assigns the objects
 * to the specific converters.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {

    private static final Logger LOG = LogManager.getLogger();

    private final MetricFactoryMap converterMap;

    /**
     * Used as index for the map data structure if no converter is defined for
     * this type of entity
     */
    private final String noConverterIndex;

    public EntityDisassembler(final MetricFactoryMap converterMap) {
        this(converterMap, "raw");
    }

    public EntityDisassembler(final MetricFactoryMap converterMap, final String noConverterIndex) {
        this.converterMap = Validate.notNull(converterMap, "converterMap");
        this.noConverterIndex = noConverterIndex;
    }

    /**
     * Dissassembles the given dataList to the specific metrics.
     *
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return Map instance, the key is the metric name and the value the metric
     */
    public Map<String, QosMetric> disassembleToConverters(final List<MeasuredDataEntity> dataList) {
        Map<String, Set<QosMetric>> metrics = createNewBunchOfConverters();

        return disassembleLoop(dataList, metrics);
    }

    /**
     * Disassembles the given dataList to a default metric.
     *
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return Map instance, the key is the metric name and the value the metric
     */
    public Map<String, QosMetric> disassembleToDummyConverter(final List<MeasuredDataEntity> dataList) {
        return disassembleLoop(dataList);
    }

    /**
     * Disassembles the given dataList to the registred metrics.
     *
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return Map Instance, the key is the metric name and the value the metric
     */
    public Map<String, QosMetric> disassembleToMetricssWithRawData(final List<MeasuredDataEntity> dataList) {
        Map<String, Set<QosMetric>> metrics = createNewBunchOfConverters();

        Map<String, QosMetric> merged = disassembleLoop(dataList, metrics);
        merged.putAll(disassembleToDummyConverter(dataList));

        return merged;
    }

    private Map<String, QosMetric> disassembleLoop(final List<MeasuredDataEntity> dataList) {
        return disassembleLoop(dataList, null);
    }

    /**
     * Mainloop which processes each disassemble process (very complex code ..
     * teh code of hell).
     *
     * @param metrics QosMetric list
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return Map Instance, the key is the metric name and the value the metric
     */
    private Map<String, QosMetric> disassembleLoop(final List<MeasuredDataEntity> dataList, final Map<String, Set<QosMetric>> metrics) {
        Map<String, QosMetric> finalMetrics = new HashMap<>();

        for (MeasuredDataEntity measuredDataEntity : dataList) {
            List<AbstractQosEntity> measureData = measuredDataEntity.getData();

            for (AbstractQosEntity abstractQosEntity : measureData) {
                String converterEntityIndex = abstractQosEntity.getEntityName();
                Measurement measurement = new Measurement(abstractQosEntity, measuredDataEntity.getCreateTime());
                // if converters is null, use defaultConverter
                if (metrics == null || !metrics.containsKey(converterEntityIndex)) {
                    // TODO fix this
                    /*if (!finalMetrics.containsKey(noConverterIndex)) {
                        finalMetrics.put(noConverterIndex, getDummyMetric());
                    }

                    finalMetrics.get(noConverterIndex).add(measurement);*/ 
                } else {

                    // assign to the specific converter
                    Set<QosMetric> get = metrics.get(converterEntityIndex);

                    for (QosMetric conv : get) {
                        conv.add(measurement);
                        finalMetrics.put(conv.getName(), conv);
                    }
                }
            }
        }

        return finalMetrics;
    }


    /**
     * Creates new instances of the entity converters.
     *
     * @return Map of entity converters
     */
    private Map<String, Set<QosMetric>> createNewBunchOfConverters() {
        Map<String, Set<QosMetric>> entityConverters = new HashMap<>();

        for (Map.Entry e : converterMap.entrySet()) {
            try {
                Set<Factory<QosMetric>> converterFactoryList = (Set<Factory<QosMetric>>) e.getValue();
                String entityName = (String) e.getKey();

                if (!entityConverters.containsKey(entityName)) {
                    entityConverters.put(entityName, new HashSet<QosMetric>());
                }

                for (Factory<QosMetric> factory : converterFactoryList) {
                    entityConverters.get(entityName).add(factory.create());
                }
            } catch (CreateException ex) {
                LOG.warn("Can't create QoSMetric Instance for EntityDisassembler.", ex);
            }
        }

        return entityConverters;
    }
}
