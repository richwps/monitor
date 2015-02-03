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

import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientCreateException;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientException;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientWpsNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientWpsProcessNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsMetricResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a simple Code Demonstration of the WpsMontiorClient. This Code also
 * serves as a TestCode. To use this Code as a Test you must registred the WPS
 * "http://localhost:8080/wps/WebProcessingService" and the Process
 * "SimpleBuffer" in the WPSMonitor or simply change the static members below.
 *
 * Make sure the monitor is started and avaible (see the monitorUrl static
 * Member).
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Main {

    private static URL monitorUrl;
    private static URL registredWpsUrl;
    private static URL nonExistsWpsUrl;
    private static String registredWpsProcess;
    private static String nonExistsWpsProcess;

    static {
        try {
            monitorUrl = new URL("http://localhost:1111/");
            registredWpsUrl = new URL("http://localhost:8080/wps/WebProcessingService");
            nonExistsWpsUrl = new URL("http://example.com/this/wps/should/not/be/exists");
            registredWpsProcess = "SimpleBuffer";
            nonExistsWpsProcess = "AnyProcessWhichShouldNotExists";
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listWps(final WpsMonitorClient client) throws WpsMonitorClientException {
        for (final WpsResource wps : client.getAllWps()) {
            System.out.println(wps);
        }
    }

    public void wpsNotFound(final WpsMonitorClient client) throws WpsMonitorClientException {
        try {
            client.getWps(nonExistsWpsUrl);
        } catch (WpsMonitorClientWpsNotFoundException ex) {
            System.out.println(ex);
        }
    }

    public void wpsNotFound2(final WpsMonitorClient client) throws WpsMonitorClientException {
        try {
            client.getWpsProcess(nonExistsWpsUrl, registredWpsProcess);
        } catch (WpsMonitorClientWpsNotFoundException ex) {
            System.out.println(ex);
        }
    }

    public void wpsProcessNotFound(final WpsMonitorClient client) throws WpsMonitorClientException {
        try {
            client.getWpsProcess(registredWpsUrl, nonExistsWpsProcess);
        } catch (WpsMonitorClientWpsProcessNotFoundException ex) {
            System.out.println(ex);
        }
    }

    public void findWpsProcess(final WpsMonitorClient client) throws WpsMonitorClientException {
        WpsProcessResource wpsProcess = client.getWpsProcess(registredWpsUrl, registredWpsProcess);

        System.out.println("Wps Process:");
        System.out.println(wpsProcess.toString());
        System.out.println("Metrics:");

        for (final WpsMetricResource metric : wpsProcess.getMetricsAsList()) {
            System.out.println(metric.toString());
        }
    }

    public static void main(String[] args) {
        try {
            WpsMonitorClient wpsMonitorClient = new WpsMonitorClientFactory().create(monitorUrl);

            Main main = new Main();
            System.out.println("List WPS:");
            main.listWps(wpsMonitorClient);

            System.out.println("WPS Not Found:");
            main.wpsNotFound(wpsMonitorClient);

            System.out.println("WPS also Not Found:");
            main.wpsNotFound2(wpsMonitorClient);

            System.out.println("WPS Process not found but the WPS endpoint is registred:");
            main.wpsProcessNotFound(wpsMonitorClient);

            System.out.println("Show a registred WPS Process and the Metrics:");
            main.findWpsProcess(wpsMonitorClient);

        } catch (WpsMonitorClientException | WpsMonitorClientCreateException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
