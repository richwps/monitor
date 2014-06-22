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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.ConfiguredEntityManagerFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.GuiStarter;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.HttpOperation;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterface;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterfaceBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListMeasurementRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListWpsProcessRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes.ListWpsRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.qos.response.ResponseFactory;
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
        try {            
            new Application().run();
        } catch (Exception ex) {
            log.fatal(ex);
        }
    }

    public Application() {
    }

    public void run() throws SchedulerException, Exception {
        Monitor monitor = new MonitorBuilder()
                .setupDefault()
                .build();
        
        // register JPA Shutdown thinks
        monitor.getEventHandler().registerListener("monitor.shutdown", new MonitorEventListener() {

            @Override
            public void execute(MonitorEvent event) {
                ConfiguredEntityManagerFactory.close();
            }
        });
        // register default QoS-Probes
        monitor.getProbeService().addProbe(new ResponseFactory());

        RestInterface rest = new RestInterfaceBuilder()
                .withMonitorControl(monitor.getMonitorControl())
                .withStrategy(new JsonPresentateStrategy())
                //.withStrategy(new XmlPresentateStategy())
                .build();

        rest.addRoute(HttpOperation.GET, new ListMeasurementRoute())
                .addRoute(HttpOperation.GET, new ListWpsProcessRoute())
                .addRoute(HttpOperation.GET, new ListWpsRoute());
        
        log.trace("WpsMonitor is starting up ...");
        monitor.start();
        
        log.trace("Start REST Interface ...");
        rest.start();
        
        log.trace("Start GUI ...");
        GuiStarter.start(monitor);
    }
}
