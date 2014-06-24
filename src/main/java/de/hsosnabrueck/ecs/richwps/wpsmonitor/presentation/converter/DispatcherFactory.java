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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.Map;

/**
 * Setups a EntityDispatcher with the configured converterMap in the 
 * MonitorBuilder instance
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DispatcherFactory {
    private final Map<String, ConverterFactory> converterMap;
    
    public DispatcherFactory(final Map<String, ConverterFactory> converterMap) {
        this.converterMap = Param.notNull(converterMap, "converterMap");
    }
    
    public EntityDispatcher create() {
        EntityDisassembler entityDisassembler = new EntityDisassembler(converterMap);
        EntityDispatcher entityDispatcher = new EntityDispatcher(entityDisassembler);
        
        return entityDispatcher;
    }
}
