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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.EntityDispatcher;
import spark.Route;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class MonitorRoute extends Route {

    private MonitorControl monitorControl;
    private EntityDispatcher dispatch;
    private PresentateStrategy strategy;
    protected final String route;

    public MonitorRoute(String routeStr) {
        super(routeStr);

        this.route = routeStr;
    }

    public void init(MonitorControl monitorControl, EntityDispatcher dispatch, PresentateStrategy strategy) {
        this.monitorControl = monitorControl;
        this.dispatch = dispatch;
        this.strategy = strategy;
    }

    public MonitorControl getMonitorControl() {
        return monitorControl;
    }

    public EntityDispatcher getDispatch() {
        return dispatch;
    }

    public PresentateStrategy getStrategy() {
        return strategy;
    }

    // was used for spark 2.0 wrapper implementation
    //public abstract String getRoute();
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
        if ((this.route == null) ? (other.route != null) : !this.route.equals(other.route)) {
            return false;
        }
        return true;
    }
}
