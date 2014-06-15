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
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterface {
    private final PresentateStrategy strategy;
    private final RouteRegister routeRegister;
    private final MonitorControl monitorControl;
    private final DispatcherFactory dispatchFactory;
    
    private EnumMap<HttpOperation, MonitorRoute> routeMap;

    public RestInterface(final PresentateStrategy strategy, final MonitorControl control, final DispatcherFactory dispatchFactory) {
        this.strategy = Param.notNull(strategy, "strategy");
        this.monitorControl = Param.notNull(control, "control");
        this.dispatchFactory = Param.notNull(dispatchFactory, "dispatchFactory");
        
        routeRegister = new RouteRegister();
    }

    public MonitorRoute addRoute(HttpOperation operation, MonitorRoute RouteObj) {
        return routeMap.put(Param.notNull(operation, "operation"), Param.notNull(RouteObj, "RouteObj"));
    }
    
    public void start() {
        initAndRegisterRoutes();
    }
    
    private void initAndRegisterRoutes() {
        for(Map.Entry route : routeMap.entrySet()) {
            HttpOperation op = (HttpOperation)route.getKey();
            MonitorRoute toRegister = (MonitorRoute)route.getValue();  
            
            EntityDispatcher dispatcher = dispatchFactory.create();
            toRegister.init(monitorControl, dispatcher, strategy);
            
            routeRegister.register(op, toRegister);
        }
    }
}
