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
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.EntityDispatcher;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import spark.Route;

/**
 * Abstract class for routes in Spark. However the route objects need some
 * dependencies: {@link MonitorControlService}, {@link EntityDispatcher} and the
 * {@link PresentateStrategy} instance. This class is abstract because of the
 * init method, which is called before a route is registered through the
 * {@link RouteRegister} class.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class MonitorRoute extends Route {

    private MonitorControlService monitorControl;
    private EntityDispatcher dispatch;
    private PresentateStrategy strategy;
    protected final String route;

    public MonitorRoute(String routeStr) {
        super(routeStr);

        this.route = routeStr;
    }

    /**
     * Initialize a route instance with the necessary dependencies.
     *
     * @param monitorControl {@link MonitorControlService} instance
     * @param dispatch {@link EntityDispatcher} instance
     * @param strategy {@link PresentateStrategy} instance
     */
    public void init(final MonitorControlService monitorControl, final EntityDispatcher dispatch, final PresentateStrategy strategy) {
        this.monitorControl = monitorControl;
        this.dispatch = dispatch;
        this.strategy = strategy;
    }

    public MonitorControlService getMonitorControl() {
        return monitorControl;
    }

    public EntityDispatcher getDispatch() {
        return dispatch;
    }

    public PresentateStrategy getStrategy() {
        return strategy;
    }

    public String getRoute() {
        return route;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.route != null ? this.route.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorRoute other = (MonitorRoute) obj;
        return !((this.route == null) ? (other.route != null) : !this.route.equals(other.route));
    }
}
