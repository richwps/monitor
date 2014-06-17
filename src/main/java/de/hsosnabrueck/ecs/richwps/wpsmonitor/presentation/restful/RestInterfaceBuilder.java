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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.ConverterFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.DispatcherFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterfaceBuilder {
    private PresentateStrategy strategy;
    private MonitorControl monitorControl;
    private Map<String, ConverterFactory> converterMap;

    public RestInterfaceBuilder() {
        this.converterMap = new HashMap<String, ConverterFactory>();
    }
    
    public RestInterfaceBuilder withStrategy(final PresentateStrategy strategy) {
        this.strategy = Param.notNull(strategy, "strategy");
        
        return this;
    }
    
    public RestInterfaceBuilder withMonitorControl(final MonitorControl monitorControl) {
        this.monitorControl = Param.notNull(monitorControl, "monitorControl");
        
        return this;
    }
    
    public RestInterfaceBuilder addConverter(final String abstractQosEntityName, final ConverterFactory converterFactory) {
        converterMap.put(Param.notNull(abstractQosEntityName, "abstractQosEntityName"), Param.notNull(converterFactory, "converterFactory"));
        
        return this;
    }
    
    public RestInterface build() {
        DispatcherFactory dispatchFactory = new DispatcherFactory(converterMap);
        RestInterface rest = new RestInterface(strategy, monitorControl, dispatchFactory);
        
        return rest;
    }
}
