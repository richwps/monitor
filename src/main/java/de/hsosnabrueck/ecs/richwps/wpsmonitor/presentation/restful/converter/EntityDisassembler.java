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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Disassembles the bulk of MeasuredDataEntity objects and assigns the objects to
 * the specific converters.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {

    private final ConverterFactoryMap converterMap;
    private final static Logger log = LogManager.getLogger();
    private final String NO_CONVERTER_INDEX;

    public EntityDisassembler(final ConverterFactoryMap converterMap) {
        this(converterMap, "raw");
    }

    public EntityDisassembler(final ConverterFactoryMap converterMap, final String noConverterIndex) {
        this.converterMap = Validate.notNull(converterMap, "converterMap");
        this.NO_CONVERTER_INDEX = noConverterIndex;
    }

    /**
     * Dissassembles the given dataList to the specific converters.
     *
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return
     */
    public Map<String, EntityConverter> disassembleToConverters(final List<MeasuredDataEntity> dataList) {
        Map<String, Set<EntityConverter>> converters = createNewBunchOfConverters();

        return disassembleLoop(dataList, converters);
    }

    /**
     * Disassembles the given dataList to a default converter.
     *
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return
     */
    public Map<String, EntityConverter> disassembleToDummyConverter(final List<MeasuredDataEntity> dataList) {
        return disassembleLoop(dataList);
    }

    public Map<String, EntityConverter> disassembleToConvertersWithRawData(final List<MeasuredDataEntity> dataList) {
        Map<String, Set<EntityConverter>> converters = createNewBunchOfConverters();

        Map<String, EntityConverter> merged = disassembleLoop(dataList, converters);
        merged.putAll(disassembleToDummyConverter(dataList));

        return merged;
    }

    private Map<String, EntityConverter> disassembleLoop(final List<MeasuredDataEntity> dataList) {
        return disassembleLoop(dataList, null);
    }

    /**
     * Mainloop which processes each disassemble process (very complex code ..
     * teh code of hell).
     *
     * @param converters EntityConverter list
     * @param dataList List of {@link MeasuredDataEntity} instances
     * @return
     */
    private Map<String, EntityConverter> disassembleLoop(final List<MeasuredDataEntity> dataList, final Map<String, Set<EntityConverter>> converters) {
        Map<String, EntityConverter> finalConverters = new HashMap<String, EntityConverter>();

        for (MeasuredDataEntity measuredDataEntity : dataList) {
            List<AbstractQosEntity> measureData = measuredDataEntity.getData();

            for (AbstractQosEntity abstractQosEntity : measureData) {
                String converterEntityIndex = abstractQosEntity.getEntityName();

                // if converters is null, use defaultConverter
                if (converters == null || !converters.containsKey(converterEntityIndex)) {
                    if (!finalConverters.containsKey(NO_CONVERTER_INDEX)) {
                        finalConverters.put(NO_CONVERTER_INDEX, getDummyConverter());
                    }

                    finalConverters.get(NO_CONVERTER_INDEX).add(abstractQosEntity);
                } else {

                    // assign to the specific converter
                    Set<EntityConverter> get = converters.get(converterEntityIndex);

                    for (EntityConverter conv : get) {
                        conv.add(abstractQosEntity);
                        finalConverters.put(conv.getName(), conv);
                    }
                }
            }
        }

        return finalConverters;
    }

    private EntityConverter getDummyConverter() {
        return new EntityConverter() {

            @Override
            public Object convert() {
                return getEntities();
            }

            @Override
            public String getName() {
                return NO_CONVERTER_INDEX;
            }
        };
    }

    /**
     * Creates new instances of the entity converters.
     *
     * @return Map of entity converters
     */
    private Map<String, Set<EntityConverter>> createNewBunchOfConverters() {
        Map<String, Set<EntityConverter>> entityConverters = new HashMap<String, Set<EntityConverter>>();

        for (Map.Entry e : converterMap.entrySet()) {
            try {
                Set<Factory<EntityConverter>> converterFactoryList = (Set<Factory<EntityConverter>>) e.getValue();
                String entityName = (String) e.getKey();

                if (!entityConverters.containsKey(entityName)) {
                    entityConverters.put(entityName, new HashSet<EntityConverter>());
                }

                for (Factory<EntityConverter> factory : converterFactoryList) {
                    entityConverters.get(entityName).add(factory.create());
                }
            } catch (CreateException ex) {
                log.warn(ex);
            }
        }

        return entityConverters;
    }
}
