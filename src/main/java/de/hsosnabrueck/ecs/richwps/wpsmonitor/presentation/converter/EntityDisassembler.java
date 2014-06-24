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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Disassemble the bulk of MeasuredDataEntity objects and assigns the objects
 * to the specific converters 
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {

    private final Map<String, ConverterFactory> converterMap;
    private final static Logger log = LogManager.getLogger();

    public EntityDisassembler(final Map<String, ConverterFactory> converterMap) {
        this.converterMap = Param.notNull(converterMap, "converterMap");
    }

    /**
     * Removes the AbstractQosEntity Objects out of the MeasuredDataEntity
     * List and assign the AbstractQosEntities to the specific Converter-Object
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
                if (converters.containsKey(abstractQosEntity.getEntityName())) {
                    converters.get(abstractQosEntity.getEntityName()).add(abstractQosEntity); 
                } 
            }
        }

        return converters;
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
                entityConverters.put((String) e.getKey(), ((ConverterFactory) e.getValue()).create());
            } catch (CreateException ex) {
                log.warn(ex);
            }
        }

        return entityConverters;
    }
}
