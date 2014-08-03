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
package de.hsosnabrueck.ecs.richwps.wpsmonitor;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.GuiStarter;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.semanticproxy.SemanticProxyData;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.HttpOperation;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.MonitorRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.RestInterface;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.RestInterfaceBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes.ListMeasurementAllAliasRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes.ListMeasurementNoMetricsAliasRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes.ListMeasurementRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes.ListWpsProcessRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes.ListWpsRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.strategies.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseMetricFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.builder.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.MonitorException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Main Class of the WPS-Monitor. Here are the Monitor, Admin GUI and REST
 * Service are build and started. If a fatal error occoured, the application
 * exits.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Application {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMANY);

        try {
            new Application().run();
        } catch (Throwable ex) {
            LOG.fatal("Execution Error. Execution of WPS-Monitor aborted.", ex);

            // exit the application
            Runtime.getRuntime().exit(1);
        }
    }

    public Application() {
    }

    public void run() {
        try {
            Monitor monitor = setupMonitor();

            LOG.trace("WpsMonitor is starting up ...");
            monitor.start();

            LOG.trace("Start REST Interface ...");
            RestInterface rest = setupRest(monitor.getMonitorControl());
            rest.start();

            LOG.trace("Setup DataDriver Set ...");
            Set<DataSourceCreator> drivers = new HashSet<>();
            drivers.add(new SemanticProxyData());

            LOG.trace("Start GUI ...");
            GuiStarter.start(monitor, drivers);
        } catch (MonitorException ex) {
            throw new AssertionError("Can't start the monitor!", ex);
        } catch (BuilderException ex) {
            throw new AssertionError("Can't build the monitor!", ex);
        }
    }

    /**
     * Setup the Monitor-instance
     *
     * @return Monitor instance
     * @throws BuilderException
     */
    public Monitor setupMonitor() throws BuilderException {
        Monitor monitor = new MonitorBuilder()
                .withPersistenceUnit("de.hsosnabrueck.ecs.richwps_WPSMonitor_pu")
                .setupDefault()
                .build();

        monitor.getProbeService()
                .addProbe(new ResponseFactory());

        return monitor;
    }

    /**
     * Setup the REST interface
     *
     * @param monitor Monitor instance
     * @return RestInterface Instance
     * @throws de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException
     */
    public RestInterface setupRest(MonitorControl monitor) throws BuilderException {

        // create RESTful service
        RestInterface restInterface = new RestInterfaceBuilder()
                .withMonitorControl(monitor)
                .withStrategy(new JsonPresentateStrategy())
                .withPort(1111)
                .addMetric("ResponseAvailabilityEntity", new ResponseMetricFactory())
                .build();

        // configure RESTful service
        restInterface
                .addStatelessRoute(HttpOperation.GET, new Factory<MonitorRoute>() {
                    @Override
                    public MonitorRoute create() throws CreateException {
                        // the main measurement route
                        // /measurement/wps/:wps/process/:process/count/:count/format/:format
                        return new ListMeasurementRoute();
                    }
                })
                .addStatelessRoute(HttpOperation.GET, new Factory<MonitorRoute>() {
                    @Override
                    public MonitorRoute create() throws CreateException {
                        // Alias for the ListMeasurementRoute
                        // /measurement/wps/:wps/process/:process
                        return new ListMeasurementAllAliasRoute();
                    }
                })
                .addStatelessRoute(HttpOperation.GET, new Factory<MonitorRoute>() {
                    @Override
                    public MonitorRoute create() throws CreateException {
                        // Alias for the ListMeasurementRoute
                        // /measurement/wps/:wps/process/:process/count/:count
                        return new ListMeasurementNoMetricsAliasRoute();
                    }
                })
                .addRoute(HttpOperation.GET, new ListWpsProcessRoute())
                .addRoute(HttpOperation.GET, new ListWpsRoute());

        return restInterface;
    }
}
