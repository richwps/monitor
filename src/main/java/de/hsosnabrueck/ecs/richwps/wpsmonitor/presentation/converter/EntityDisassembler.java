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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {

    private final Map<String, ConverterFactory> converterMap;

    public EntityDisassembler(final Map<String, ConverterFactory> converterMap) {
        this.converterMap = Param.notNull(converterMap, "converterMap");
    }

    /**
     * Removes the AbstractQosEntity Objects out of the dataList's MeasuredDataEntity
     * Object, and assign the AbstractQosEntities to the specific Converter-Object
     * 
     * disassemble() modifieds the MeasuredDataEntity-Objects!
     * 
     * @param dataList
     * @return 
     */
    public Map<String, EntityConverter> disassemble(List<MeasuredDataEntity> dataList) {
        Map<String, EntityConverter> converters = createNewBunchOfConverters();
        
        for (int j = 0; j < dataList.size(); j++) {
            MeasuredDataEntity measuredDataEntity = dataList.get(j);
            List<AbstractQosEntity> measureData = measuredDataEntity.getData();

            for (int i = 0; i < measureData.size(); i++) {
                if (converterMap.containsKey(measureData.get(i).getEntityName())) {
                    converters.get(measureData.get(i).getEntityName()).add(measureData.remove(i--)); //decrement i; remove(int) shift all elements to left
                }
            }
            
            // if the dataList empty, then remove the object
            if(measureData.isEmpty()) {
                dataList.remove(j--); //decrement j; remove(int) shift all elements to left
            }
        }

        return converters;
    }

    private Map<String, EntityConverter> createNewBunchOfConverters() {
        Map<String, EntityConverter> entityConverters = new HashMap<String, EntityConverter>();

        for (Map.Entry e : converterMap.entrySet()) {
            entityConverters.put((String) e.getKey(), ((ConverterFactory) e.getValue()).create());
        }

        return entityConverters;
    }
}
