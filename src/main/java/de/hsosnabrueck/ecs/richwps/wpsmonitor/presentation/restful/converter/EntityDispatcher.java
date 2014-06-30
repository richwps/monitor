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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calls the convert method on the converter objects and dispatchtes the result
 * in a Map.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDispatcher {

    private final EntityDisassembler disassembler;

    /**
     * Constructor.
     *
     * @param disassembler EntityDisassembler instance
     */
    public EntityDispatcher(EntityDisassembler disassembler) {
        this.disassembler = Param.notNull(disassembler, "disassembler");
    }

    /**
     * Dispatches the converters into a Map. Entities which have no registred
     * converter will be ignored.
     *
     * @param data List of {@link MeasuredDataEntity} instances
     * @return Map with Entity name as key and converted data as value
     */
    public Map<String, Object> dispatchToConverter(List<MeasuredDataEntity> data) {
        Map<String, EntityConverter> disassemble = disassembler.disassembleToConverters(data);

        return dispatch(disassemble);
    }
    
    /**
     * Dispatches data to a map. Registred converter will be ignored. A raw 
     * output will be generated.
     * 
     * @param data List of {@link MeasuredDataEntity} instances
     * @return Map with Entity name as key and converted data as value
     */
    public Map<String, Object> dispatchData(List<MeasuredDataEntity> data) {
        Map<String, EntityConverter> disassemble = disassembler.disassembleToDummyConverter(data);

        return dispatch(disassemble);
    }
    
    /**
     * Dispatches the converter and all data into a map.
     * 
     * @param data List of {@link MeasuredDataEntity} instances
     * @return Map with Entity name as key and converted data as value
     */
    public Map<String, Object> dispatchTwice(List<MeasuredDataEntity> data) {
        Map<String, EntityConverter> disassemble = disassembler.disassembleToConvertersWithRawData(data);

        return dispatch(disassemble);
    }
    
    private Map<String, Object> dispatch(Map<String, EntityConverter> disassemble) {
        Map<String, Object> merged = new HashMap<String, Object>();

        for (Map.Entry e : disassemble.entrySet()) {
            merged.put((String) e.getKey(), ((EntityConverter) e.getValue()).convert());
        }
        
        return merged;
    }
}