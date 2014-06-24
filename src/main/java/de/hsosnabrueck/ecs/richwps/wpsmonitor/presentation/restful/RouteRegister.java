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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RouteRegister {
    private static final Logger log = LogManager.getLogger();
    
    public void register(final HttpOperation operation, final MonitorRoute routeObj) {
        switch (Param.notNull(operation, "operation")) {
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
        spark.Spark.get(routeObj);
    }

    public void post(final MonitorRoute routeObj) {
        spark.Spark.post(routeObj);
    }

    public void delete(final MonitorRoute routeObj) {
        spark.Spark.delete(routeObj);
    }

    public void put(final MonitorRoute routeObj) {
        spark.Spark.put(routeObj);
    }

    public void options(final MonitorRoute routeObj) {
        spark.Spark.options(routeObj);
    }
    
    public void statelessGet(final Factory<MonitorRoute> routeFactoryObj) throws CreateException {
        Route embeddedRoute = getEmbeddedRoute(routeFactoryObj);
        spark.Spark.get(embeddedRoute);
    }

    public void statelessPost(final Factory<MonitorRoute> routeFactoryObj) throws CreateException {
        Route embeddedRoute = getEmbeddedRoute(routeFactoryObj);
        spark.Spark.post(embeddedRoute);
    }

    public void statelessDelete(final Factory<MonitorRoute> routeFactoryObj) throws CreateException {
        Route embeddedRoute = getEmbeddedRoute(routeFactoryObj);
        spark.Spark.delete(embeddedRoute);
    }

    public void statelessPut(final Factory<MonitorRoute> routeFactoryObj) throws CreateException {
        Route embeddedRoute = getEmbeddedRoute(routeFactoryObj);
        spark.Spark.put(embeddedRoute);
    }

    public void statelessOptions(final Factory<MonitorRoute> routeFactoryObj) throws CreateException {
        Route embeddedRoute = getEmbeddedRoute(routeFactoryObj);
        spark.Spark.options(embeddedRoute);
    }
    
    private Route getEmbeddedRoute(final Factory<MonitorRoute> factory) throws CreateException {
        return new Route(factory.create().getRoute()) {
            
            @Override
            public Object handle(Request request, Response response) {
                Object result = null;
                
                try {
                    result = factory.create().handle(request, response);
                } catch (CreateException ex) {
                    log.error(ex); // should never happend
                    response.status(500);
                }
                
                return result;
            }
        };
    }
}
