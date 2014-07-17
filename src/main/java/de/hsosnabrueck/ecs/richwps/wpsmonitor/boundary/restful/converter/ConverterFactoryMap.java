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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates a complex datastructure.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ConverterFactoryMap  {
    private final Map<String, Set<Factory<EntityConverter>>> converterMap;

    public ConverterFactoryMap() {
        this.converterMap = new HashMap<String, Set<Factory<EntityConverter>>>();
    }
    
    public ConverterFactoryMap add(final String index, final Factory<EntityConverter> converterFactory) {
        Validate.notNull(index, "index");
        Validate.notNull(converterFactory, "converterFactory");
                
        if(!converterMap.containsKey(index)) {
            converterMap.put(index, new HashSet<Factory<EntityConverter>>());
        }
        
        converterMap.get(index).add(converterFactory);
        
        return this;
    }
    
    public Set<Factory<EntityConverter>> get(final String index) {
        return converterMap.get(index);
    }

    public Set<Map.Entry<String, Set<Factory<EntityConverter>>>> entrySet() {
        return converterMap.entrySet();
    }
}
