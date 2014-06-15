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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class EntityDispatcher {
    private final EntityDisassembler disassembler;

    public EntityDispatcher(EntityDisassembler disassembler) {
        this.disassembler = Param.notNull(disassembler, "disassembler");
    }
    
    public Map<String, Object> dispatch(List<MeasuredDataEntity> data) {
        Map<String, EntityConverter> disassemble = disassembler.disassemble(data);
        Map<String, Object> merged = new HashMap<String, Object>();
        
        for(Map.Entry e : disassemble.entrySet()) {
            merged.put((String)e.getKey(), ((EntityConverter)e.getValue()).convert());
        }
        
        // put the rest into the map, if data not empty
        if(!data.isEmpty()) {
            merged.put("MeasuredData", data);
        }
        
        return merged;
    }
}
