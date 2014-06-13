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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDisassembler {
    private Map<String, ConverterFactory> converterMap;
    
    public EntityDisassembler() {
        converterMap = new HashMap<String, ConverterFactory>();
    }
    
    public void addConverter(final ConverterFactory converterFactory, final String qosAbstractEntityName) {
        converterMap.put(Param.notNull(qosAbstractEntityName, "qosAbstractEntityName"), 
                Param.notNull(converterFactory, "converterFactory")
        );
    }
    
    public Map<String, EntityConverter> disassemble(final List<AbstractQosEntity> dataList) {
        
        Map<String, EntityConverter> converters = createNewBunchOfConverters();
        
        for(AbstractQosEntity e : Param.notNull(dataList, "dataList")) {
            if(converterMap.containsKey(e.getEntityName())) {
                converters.get(e.getEntityName()).add(e);
            }
        }
        
        return converters;
    }
    
    private Map<String, EntityConverter> createNewBunchOfConverters() {
        Map<String, EntityConverter> entityConverters = new HashMap<String, EntityConverter>();
        
        for(Map.Entry e : converterMap.entrySet()) {
            entityConverters.put((String)e.getKey(), ((ConverterFactory)e.getValue()).create());
        }
        
        return entityConverters;
    }
}
