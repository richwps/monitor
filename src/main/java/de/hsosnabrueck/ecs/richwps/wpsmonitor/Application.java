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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.GuiStarter;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterface;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.RestInterfaceBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.SchedulerException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Application {

    public static void main(String[] args) {

        try {
            new Application().run();
        } catch (SchedulerException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Application() {
    }

    public void run() throws SchedulerException {
        Monitor monitor = new MonitorBuilder()
                .setupDefault()
                .build();
        
        RestInterface rest = new RestInterfaceBuilder()
                .withMonitorControl(monitor.getMonitorControl())
                .withStrategy(new JsonPresentateStrategy())
                .build();

        monitor.start();
        rest.start();

        GuiStarter.start(monitor.getMonitorControl());
    }
}
