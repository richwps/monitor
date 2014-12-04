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
package de.hsos.ecs.richwps.wpsmonitor.client;

import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsMetricResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Main {

    public static void main(String[] args) {
        try {
            WpsMonitorClient wpsMonitorClient = new WpsMonitorClientFactory().create(new URL("http://localhost:1111/"));

            System.out.println("List of WPS are available in the Monitor");
            System.out.println("----");

            for (final WpsResource wpsResource : wpsMonitorClient.getAllWps()) {
                System.out.println(wpsResource);
            }

            System.out.println("----");

            WpsResource pickup = wpsMonitorClient.getWps(new URL("http://localhost:8080/wps/WebProcessingService"));
            
            if (pickup != null) {
                WpsProcessResource wpsProcess = wpsMonitorClient.getWpsProcess(pickup, "Blubb");

                if (wpsProcess != null) {
                    System.out.println("Metrics");
                    
                    for(WpsMetricResource r : wpsProcess.getMetricsAsList()) {
                        System.out.println(r);
                    }
                } else {
                    System.out.println("Can't demonstrate the getProcessMetrics method, because there are no WpsProcess with the name \"Blubb\" are registrated in the Monitor");
                }
            } else {
                System.out.println("Can't demonstrate the metrics method, because there are no WPS registrated in the Monitor.");
            }
        } catch (MalformedURLException | WpsMonitorClientException | WpsMonitorClientCreateException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
