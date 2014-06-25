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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Disassemble the bulk of MeasuredDataEntity objects and assigns the objects to
 * the specific converters
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {

    private final Map<String, Factory<EntityConverter>> converterMap;
    private final static Logger log = LogManager.getLogger();
    private final String NO_CONVERTER_INDEX;

    public EntityDisassembler(final Map<String, Factory<EntityConverter>> converterMap) {
        this(converterMap, "");
    }

    public EntityDisassembler(final Map<String, Factory<EntityConverter>> converterMap, final String noConverterIndex) {
        this.converterMap = Param.notNull(converterMap, "converterMap");
        this.NO_CONVERTER_INDEX = noConverterIndex;
    }

    /**
     * Removes the AbstractQosEntity Objects out of the MeasuredDataEntity List
     * and assign the AbstractQosEntities to the specific Converter-Object
     *
     * disassemble() modifieds the MeasuredDataEntity-Objects!
     *
     * @param dataList
     * @return
     */
    public Map<String, EntityConverter> disassemble(List<MeasuredDataEntity> dataList) {
        Map<String, EntityConverter> converters = createNewBunchOfConverters();

        for (MeasuredDataEntity measuredDataEntity : dataList) {
            List<AbstractQosEntity> measureData = measuredDataEntity.getData();

            for (AbstractQosEntity abstractQosEntity : measureData) {
                String converterEntityIndex = abstractQosEntity.getEntityName();

                if (!converters.containsKey(converterEntityIndex)) {
                    converterEntityIndex = NO_CONVERTER_INDEX;
                }

                // assign to the specific converter
                converters.get(converterEntityIndex)
                        .add(abstractQosEntity);

            }
        }

        return converters;
    }

    private EntityConverter getDummyConverter() {
        return new EntityConverter() {

            @Override
            public Object convert() {
                return getEntities();
            }
        };
    }

    /**
     * Creates new instances of the entity converters
     *
     * @return Map of entity converters
     */
    private Map<String, EntityConverter> createNewBunchOfConverters() {
        Map<String, EntityConverter> entityConverters = new HashMap<String, EntityConverter>();

        for (Map.Entry e : converterMap.entrySet()) {
            try {
                entityConverters.put((String) e.getKey(), ((Factory<EntityConverter>) e.getValue()).create());
            } catch (CreateException ex) {
                log.warn(ex);
            }
        }

        entityConverters.put(NO_CONVERTER_INDEX, getDummyConverter());

        return entityConverters;
    }
}
