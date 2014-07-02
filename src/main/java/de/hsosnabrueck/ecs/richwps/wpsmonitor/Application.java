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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.ConfiguredEntityManagerFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.GuiStarter;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.HttpOperation;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.strategies.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.MonitorRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterface;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterfaceBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListMeasurementAllAliasRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListMeasurementNoFormatAliasRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListMeasurementRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListWpsProcessRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListWpsRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseConverterFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseFactory;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Application {

    private final static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.GERMANY);

        try {
            new Application().run();
        } catch (Exception ex) {
            log.fatal(ex);
        }
    }

    public Application() {
    }

    public void run() throws SchedulerException, Exception {
        Monitor monitor = setupMonitor();

        // register JPA Shutdown
        monitor.getEventHandler()
                .registerListener("monitor.shutdown", new MonitorEventListener() {

                    @Override
                    public void execute(MonitorEvent event) {
                        ConfiguredEntityManagerFactory.close();
                    }
                }
                );

        log.trace("WpsMonitor is starting up ...");
        monitor.start();

        log.trace("Start REST Interface ...");
        RestInterface rest = setupRest(monitor.getMonitorControl());
        rest.start();

        log.trace("Start GUI ...");
        GuiStarter.start(monitor);
    }

    /**
     * Setup the Monitor-instance
     *
     * @return Monitor instance
     * @throws Exception
     */
    public Monitor setupMonitor() throws Exception {
        Monitor monitor = new MonitorBuilder()
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
     * @return
     */
    public RestInterface setupRest(MonitorControl monitor) {
        RestInterface restInterface = null;
        try {
            restInterface = new RestInterfaceBuilder()
                    .withMonitorControl(monitor)
                    .withStrategy(new JsonPresentateStrategy())
                    .addConverter("ResponseAvailabilityEntity", new ResponseConverterFactory())
                    .build();
            
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
                            return new ListMeasurementNoFormatAliasRoute();
                        }
                    })
                    .addRoute(HttpOperation.GET, new ListWpsProcessRoute())
                    .addRoute(HttpOperation.GET, new ListWpsRoute());

        } catch (BuilderException ex) {
            log.error(ex);
        }
        
        return restInterface;
    }
}
