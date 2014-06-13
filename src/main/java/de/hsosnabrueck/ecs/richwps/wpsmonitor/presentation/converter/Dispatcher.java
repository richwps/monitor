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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Dispatcher {
    private EntityDisassembler disassembler;

    public Dispatcher(EntityDisassembler disassembler) {
        this.disassembler = Param.notNull(disassembler, "disassembler");
    }
    
    public Map<String, List<Object>> dispatch(final List<AbstractQosEntity> data) {
        Map<String, EntityConverter> disassemble = disassembler.disassemble(data);
        Map<String, List<Object>> merged = new HashMap<String, List<Object>>();
        
        for(Map.Entry e : disassemble.entrySet()) {
            merged.put((String)e.getKey(), ((EntityConverter)e.getValue()).getPresentateObject());
        }
        
        return merged;
    }
}
