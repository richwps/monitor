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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.DispatcherFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.EntityDispatcher;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterface {
    private final PresentateStrategy strategy;
    private final RouteRegister routeRegister;
    private final MonitorControl monitorControl;
    private final DispatcherFactory dispatchFactory;
    
    private EnumMap<HttpOperation, Set<MonitorRoute>> routeMap;

    public RestInterface(final PresentateStrategy strategy, final MonitorControl control, final DispatcherFactory dispatchFactory) {
        this.strategy = Param.notNull(strategy, "strategy");
        this.monitorControl = Param.notNull(control, "control");
        this.dispatchFactory = Param.notNull(dispatchFactory, "dispatchFactory");
        
           
        this.routeRegister = new RouteRegister();
        
        initMap();
    }
    
    private void initMap() {
        routeMap = new EnumMap<HttpOperation, Set<MonitorRoute>>(HttpOperation.class);  
        
        for(HttpOperation v : HttpOperation.values()) {
            routeMap.put(v, new HashSet<MonitorRoute>());
        }
    }

    public RestInterface addRoute(HttpOperation operation, MonitorRoute RouteObj) {
        routeMap.get(Param.notNull(operation, "operation")).add(Param.notNull(RouteObj, "RouteObj"));
        
        return this;
    }
    
    public void start() {
        initAndRegisterRoutes();
    }
    
    private void initAndRegisterRoutes() {
        for(Map.Entry routeMapEntry : routeMap.entrySet()) {
            HttpOperation op = (HttpOperation)routeMapEntry.getKey();
            Set<MonitorRoute> routeSet = (Set<MonitorRoute>)routeMapEntry.getValue();  
            
            EntityDispatcher dispatcher = dispatchFactory.create();
            
            for(MonitorRoute routeObj : routeSet) {
                routeObj.init(monitorControl, dispatcher, strategy);
                routeRegister.register(op, routeObj);
            }
            
        }
    }
}
