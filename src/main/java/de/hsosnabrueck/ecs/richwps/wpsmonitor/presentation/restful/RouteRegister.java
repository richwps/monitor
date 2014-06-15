/*
 * Copyright 2014 FloH.
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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import spark.Route;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RouteRegister {
    public void register(final HttpOperation operation, final MonitorRoute routeObj) {
        switch(Param.notNull(operation, "operation")) {
            case GET:
                get(routeObj);
                break;
            case POST:
                post(routeObj);
                break;
            case DELETE:
                delete(routeObj);
                break;
            case PUT:
                put(routeObj);
                break;
            case OPTIONS:
                options(routeObj);
                break;
            default:
                
        }
    }
    
    public void get(final MonitorRoute routeObj) {
        spark.Spark.get(routeObj.getRoute(), routeObj);
    }
    
    public void post(final MonitorRoute routeObj) {
        spark.Spark.post(routeObj.getRoute(), routeObj);
    }
    
    public void delete(final MonitorRoute routeObj) {
        spark.Spark.delete(routeObj.getRoute(), routeObj);
    }
    
    public void put(final MonitorRoute routeObj) {
        spark.Spark.put(routeObj.getRoute(), routeObj);
    }
    
    public void options(final MonitorRoute routeObj) {
        spark.Spark.options(routeObj.getRoute(), routeObj);
    }
}
