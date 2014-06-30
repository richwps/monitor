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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.converter.DispatcherFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.converter.EntityDispatcher;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Param;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * A REST interface to interact with the MonitorControl instance over a
 * webbrowser.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterface {

    /**
     * Concrete PresentateStragey instance which should be used. Is used as
     * dependencie for the registred routes.
     */
    private final PresentateStrategy strategy;

    /**
     * RouteRegister instance - Wrapper for Spark's static methods
     */
    private final RouteRegister routeRegister;

    /**
     * MonitorControl instance to interact with the Monitor Is used as
     * dependencie for the registred routes.
     */
    private final MonitorControl monitorControl;

    /**
     * DispatcherFactory instance. Is used as dependencie for the registred
     * routes.
     */
    private final DispatcherFactory dispatchFactory;
    
    /**
     * Alternate port for Spark.
     * As default Port 4567 is used.
     */
    private Integer port;

    /**
     * Map in which stored the MonitorRoute instances for the right
     * {@link HttpOperation}
     */
    private EnumMap<HttpOperation, Set<MonitorRoute>> routeMap;

    private static final Logger log = LogManager.getLogger();

    /**
     * Creates a new RestInterface instance.
     *
     * @param strategy {@link PresentateStrategy} instance
     * @param control {@link MonitorControl} instance
     * @param dispatchFactory {@link DispatcherFactory} instance
     */
    public RestInterface(final PresentateStrategy strategy, final MonitorControl control, final DispatcherFactory dispatchFactory) {
        this.strategy = Param.notNull(strategy, "strategy");
        this.monitorControl = Param.notNull(control, "control");
        this.dispatchFactory = Param.notNull(dispatchFactory, "dispatchFactory");

        this.routeRegister = new RouteRegister();
        this.port = 4567;

        initMap();
    }

    private void initMap() {
        routeMap = new EnumMap<HttpOperation, Set<MonitorRoute>>(HttpOperation.class);

        for (HttpOperation v : HttpOperation.values()) {
            routeMap.put(v, new HashSet<MonitorRoute>());
        }
    }

    /**
     * Add a MonitorRoute instance to the given HttpOperation index.
     *
     * @param operation {@link HttpOperation} instance
     * @param RouteObj {@link MonitorRoute} instance
     * @return RestInterface instance
     */
    public RestInterface addRoute(HttpOperation operation, MonitorRoute RouteObj) {
        routeMap.get(Param.notNull(operation, "operation"))
                .add(Param.notNull(RouteObj, "RouteObj"));

        return this;
    }

    /**
     * Creates a MonitorRoute instance which will call instance =
     * monitorRouteFactory.create() and instance.init() at each request.
     *
     * @param operation {@link HttpOperation} instance
     * @param monitorRouteFactory Factory&lt;MonitorRoute> instance
     * @return RestInterface instance
     */
    public RestInterface addStatelessRoute(HttpOperation operation, final Factory<MonitorRoute> monitorRouteFactory) {
        MonitorRoute route;

        try {
            route = new MonitorRoute(monitorRouteFactory.create().getRoute()) {

                @Override
                public Object handle(Request request, Response response) {
                    Object result = null;

                    try {
                        // create new route object
                        MonitorRoute newRoute = monitorRouteFactory
                                .create();

                        // init route at every request
                        result = initRoute(newRoute)
                                .handle(request, response);
                    } catch (CreateException ex) {
                        log.error(ex); // should never happened
                        response.status(500);
                    }

                    return result;
                }
            };

            addRoute(operation, route);
        } catch (CreateException ex) {
            log.error(ex);
        }

        return this;
    }

    /**
     * Initializt and register all added MonitorRoute instances.
     */
    public void start() {
        spark.Spark.setPort(port);
        initAndRegisterRoutes();
    }

    private void initAndRegisterRoutes() {
        for (Map.Entry routeMapEntry : routeMap.entrySet()) {
            HttpOperation op = (HttpOperation) routeMapEntry.getKey();
            Set<MonitorRoute> routeSet = (Set<MonitorRoute>) routeMapEntry.getValue();

            for (MonitorRoute routeObj : routeSet) {
                routeRegister.register(op, initRoute(routeObj));
            }
        }
    }

    private MonitorRoute initRoute(MonitorRoute toInit) {
        EntityDispatcher dispatcher = dispatchFactory.create();
        toInit.init(monitorControl, dispatcher, strategy);

        return toInit;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        if(port != null && port > 0) {
            this.port = port;
        }
    }
}
