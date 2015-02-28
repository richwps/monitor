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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.PresentateStrategy;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.DispatcherFactory;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.EntityDispatcher;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * A REST interface to interact with the MonitorControlService instance over a
 webbrowser.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterface implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Concrete PresentateStragey instance which should be used. Is used as
     * dependency for the registred routes.
     */
    private final PresentateStrategy strategy;

    /**
     * RouteRegister instance - Wrapper for Spark's static methods.
     */
    private final RouteRegister routeRegister;

    /**
     * MonitorControlService instance to interact with the Monitor. It is used as
     * dependency for the registred routes.
     */
    private final MonitorControlService monitorControl;

    /**
     * DispatcherFactory instance. It is used as dependency for the registred
     * routes.
     */
    private final DispatcherFactory dispatchFactory;

    /**
     * Alternate port for Spark. As default Port 4567 is used.
     */
    private Integer port;

    /**
     * Map which stores the MonitorRoute instances for the right
     * {@link HttpOperation}.
     */
    private Map<HttpOperation, Set<MonitorRoute>> routeMap;

    /**
     * Creates a new RestInterface instance.
     *
     * @param strategy {@link PresentateStrategy} instance
     * @param control {@link MonitorControlService} instance
     * @param dispatchFactory {@link DispatcherFactory} instance
     */
    public RestInterface(final PresentateStrategy strategy, final MonitorControlService control, final DispatcherFactory dispatchFactory) {
        this.strategy = Validate.notNull(strategy, "strategy");
        this.monitorControl = Validate.notNull(control, "control");
        this.dispatchFactory = Validate.notNull(dispatchFactory, "dispatchFactory");

        this.routeRegister = new RouteRegister();
        this.port = 4567;

        initMap();
    }

    private void initMap() {
        routeMap = new EnumMap<>(HttpOperation.class);

        for (HttpOperation v : HttpOperation.values()) {
            routeMap.put(v, new HashSet<MonitorRoute>());
        }
    }

    /**
     * Adds a MonitorRoute instance to the given HttpOperation index.
     *
     * @param operation {@link HttpOperation} instance
     * @param routeObj {@link MonitorRoute} instance
     * @return RestInterface instance
     */
    public RestInterface addRoute(HttpOperation operation, MonitorRoute routeObj) {
        routeMap.get(Validate.notNull(operation, "operation"))
                .add(Validate.notNull(routeObj, "RouteObj"));

        return this;
    }

    /**
     * Creates a MonitorRoute instance which will call <code>MonitorRoute instance =
     * monitorRouteFactory.create()</code> and <code>instance.init()</code> at
     * each request.
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

                        // should never happened, because this exception is catched before
                    } catch (CreateException ex) {
                        response.status(500);
                    }

                    return result;
                }
            };

            addRoute(operation, route);
        } catch (CreateException ex) {
            LOG.error("Can't register Stateless-route.", ex);
        }

        return this;
    }

    @Override
    public void close() throws Exception {
        // Not the best soulution. Is a design error in the spark framework.
        // this is fixed in newer version (java 8)
        Method stopServer = spark.Spark.class.getDeclaredMethod("stop");
        stopServer.setAccessible(true);
        stopServer.invoke(null, new Object[] {});
    }

    /**
     * Initializes and registers all added {@link MonitorRoute} instances.
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

    /**
     * Sets the port Spark listens on. Works only before routes are added
     *
     * @param port Port number
     */
    public void setPort(Integer port) {
        if (port != null && port > 0) {
            this.port = port;
        }
    }
}
