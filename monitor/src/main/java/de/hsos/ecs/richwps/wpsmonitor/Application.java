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
package de.hsos.ecs.richwps.wpsmonitor;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.CliBuilder;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.AddCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.CreateCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.DeleteCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.MonitorExitCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.PauseCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.ResumeCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.ShowCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.StatusCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.TestCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsole;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsoleBuilder;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.GuiStarter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasources.SemanticProxyData;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.HttpOperation;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.MonitorRoute;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.RestInterface;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.RestInterfaceBuilder;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.routes.ListMeasurementRoute;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.routes.ListWpsProcessRoute;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.routes.ListWpsProcessesRoute;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.routes.ListWpsRoute;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategies.JsonPresentateStrategy;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorException;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorBuilder;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseFactory;
import de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseMetricFactory;
import de.hsos.ecs.richwps.wpsmonitor.creation.BuilderException;
import de.hsos.ecs.richwps.wpsmonitor.util.Log4j2Utils;
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
    private static final Logger LOG;
    private final ApplicationStartOptions opt;

    public static void main(String[] args) {
        try {
            new Application(new ApplicationStartOptions(args)).run();
        } catch (Throwable ex) {
            exitApplicationImmediately(ex);
        }
    }

    public Application(final ApplicationStartOptions opt) {
        this.opt = opt;
        Log4j2Utils.setLogLevel(this.opt.getLogLevel());
    }

    public void run() {
        try {

            Monitor monitor = setupMonitor();

            LOG.trace("WpsMonitor is starting up ...");
            monitor.start();

            LOG.trace("Start REST Interface ...");
            RestInterface rest = setupRest(monitor.ServicegetMonitorControl());
            rest.start();

            monitor.addShutdownRoutine(rest);

            switch (opt.getUi()) {
                case CLI:
                    startCli(monitor);
                    break;
                case GUI:
                    startGui(monitor);
                    break;
                case NONE:
                default:
                    LOG.trace("Start Monitor in server mode...");
                    break;
            }
        } catch (MonitorException ex) {
            throw new AssertionError("Can't start the monitor!", ex);
        } catch (BuilderException ex) {
            throw new AssertionError("Can't build the monitor!", ex);
        }
    }

    public void startGui(final Monitor monitor) {
        LOG.trace("Setup DataDriver Set ...");
        Set<DataSourceCreator> drivers = new HashSet<>();
        drivers.add(new SemanticProxyData());

        LOG.trace("Start GUI ...");
        GuiStarter.start(monitor, ApplicationInfo.LOG_DIRECTORY, drivers);
    }

    /**
     * Starts the CLI with silence mode. The silence mode is a dirty hack
     * to prevent bad libraries to print content to the std out if the cli is 
     * active.
     * 
     * @param monitor Monitor instance
     */
    public void startCli(final Monitor monitor) {
        try {
            LOG.trace("Start CLI ...");
            
            final MonitorConsole console = new MonitorConsoleBuilder()
                    .withStdInAndOut()
                    .silenceMode(true)
                    .build();
            
            new CliBuilder(console)
                    .addCommand(new AddCommand(monitor))
                    .addCommand(new MonitorExitCommand(monitor))
                    .addCommand(new CreateCommand(monitor))
                    .addCommand(new ShowCommand(monitor))
                    .addCommand(new StatusCommand(monitor))
                    .addCommand(new DeleteCommand(monitor))
                    .addCommand(new ResumeCommand(monitor))
                    .addCommand(new PauseCommand(monitor))
                    .addCommand(new TestCommand(monitor))
                    .withDefaultCommands()
                    .build()
                    .run();
        } catch (CommandException ex) {
            LOG.fatal("Can't build CLI.", ex);
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
     * @throws de.hsos.ecs.richwps.wpsmonitor.creation.BuilderException
     */
    public RestInterface setupRest(final MonitorControlService monitor) throws BuilderException {

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
                .addRoute(HttpOperation.GET, new ListWpsProcessRoute())
                .addRoute(HttpOperation.GET, new ListWpsProcessesRoute())
                .addRoute(HttpOperation.GET, new ListWpsRoute());

        return restInterface;
    }
    
    public static void exitApplicationImmediately(final Throwable t) {
        LOG.fatal("Fatal execution Error.", t);
        Runtime.getRuntime().exit(1);
    }

    static {
        // forces all JUL logging prints to log4j
        System.setProperty("java.util.logging.manager", org.apache.logging.log4j.jul.LogManager.class.getName());
        Locale.setDefault(Locale.GERMANY);

        LOG = LogManager.getLogger();
    }
}
