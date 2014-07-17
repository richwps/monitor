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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsulates the Spark API.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RouteRegister {
    private static final Logger log = LogManager.getLogger();
    
    /**
     * Register a Route.
     * 
     * @param operation {@link HttpOperation} value
     * @param routeObj {@link MonitorRoute} instance
     */
    public void register(final HttpOperation operation, final MonitorRoute routeObj) {
        switch (Validate.notNull(operation, "operation")) {
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

    /**
     * Registers a MonitorRoute instance as GET-HttpOperation.
     * 
     * @param routeObj MonitorRoute instance
     */
    public void get(final MonitorRoute routeObj) {
        spark.Spark.get(routeObj);
    }

    /**
     * Registers a MonitorRoute instance as POST-HttpOperation.
     * 
     * @param routeObj MonitorRoute instance
     */
    public void post(final MonitorRoute routeObj) {
        spark.Spark.post(routeObj);
    }

    /**
     * Registers a MonitorRoute instance as DELETE-HttpOperation.
     * 
     * @param routeObj MonitorRoute instance
     */
    public void delete(final MonitorRoute routeObj) {
        spark.Spark.delete(routeObj);
    }

    /**
     * Registers a MonitorRoute instance as PUT-HttpOperation.
     * 
     * @param routeObj MonitorRoute instance
     */
    public void put(final MonitorRoute routeObj) {
        spark.Spark.put(routeObj);
    }

    /**
     * Registers a MonitorRoute instance as OPTIONS-HttpOperation.
     * 
     * @param routeObj MonitorRoute instance
     */
    public void options(final MonitorRoute routeObj) {
        spark.Spark.options(routeObj);
    }
}
