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

import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientException;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import java.net.URL;
import java.util.List;

/**
 * Interface for the WpsMonitorClient.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface WpsMonitorClient {

    /**
     * Gets a WpsProcessResource instance from the the choosen WpsMonitor
     * System. The wpsEndpoint is the Endpoint of a Web Processing Service
     * Server which is registrated in the choosen Monitor.
     *
     * By default 1000 measured values by the monitor are considered.
     *
     * @param wpsEndpoint WPS-Server endpoint
     * @param wpsProcessIdentifier Process identifier of the WPS Process
     * @return WpsProcessResource instance which is comparable with the
     * WpsProcessEntity class of the WpsMonitor
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsProcessResource getWpsProcess(final URL wpsEndpoint, final String wpsProcessIdentifier)
            throws WpsMonitorClientException;

    /**
     * Gets a WpsProcessResource instance from the the choosen WpsMonitor
     * System. The wpsEndpoint is the Endpoint of a Web Processing Service
     * Server which is registrated in the choosen Monitor.
     *
     * By default 1000 measured values by the monitor are considered.
     *
     * @param wpsEndpoint WPS-Server endpoint
     * @param wpsProcessIdentifier Process identifier of the WPS Process
     * @param forceRefresh Refresh the WPS Context (it's not necessary, but if
     * you know that the Monitor has new WPS-Server entries, you should set
     * forceRefresh to true)
     * @return WpsProcessResource instance which is comparable with the
     * WpsProcessEntity class of the WpsMonitor
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsProcessResource getWpsProcess(final URL wpsEndpoint, final String wpsProcessIdentifier, final Boolean forceRefresh)
            throws WpsMonitorClientException;

    /**
     * Gets a WpsProcessResource instance from the the choosen WpsMonitor
     * System. This is a overloaded method. Internally the MonitorClient works
     * with WpsResource instancen. If you are also working with this type of
     * instances, you can use this method instead of the method which use
     * wpsEndpoint URLs.
     *
     * By default 1000 measured values by the monitor are considered.
     *
     * @param wpsResource WpsResource instance
     * @param wpsProcessIdentifier Process identifier of the WPS Process
     * @return WpsProcessResource instance which is comparable with the
     * WpsProcessEntity class of the WpsMonitor
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsProcessResource getWpsProcess(final WpsResource wpsResource, final String wpsProcessIdentifier)
            throws WpsMonitorClientException;

    /**
     * Gets a WpsProcessResource instance from the the choosen WpsMonitor
     * System. This is a overloaded method. Internally the MonitorClient works
     * with WpsResource instancen. If you are also working with this type of
     * instances, you can use this method instead of the method which use
     * wpsEndpoint URLs. If you want to specifiy how many measured values should
     * be consider for the calculation of metrics by the monitor, you can do
     * this with the consider parameter.
     *
     * @param wpsResource WpsResource instance
     * @param wpsProcessIdentifier Process identifier of the WPS Process
     * @param consider How many measured values should be considered for the
     * calculation of metrics by the monitor
     * @return WpsProcessResource instance which is comparable with the
     * WpsProcessEntity class of the WpsMonitor
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsProcessResource getWpsProcess(final WpsResource wpsResource, final String wpsProcessIdentifier, final Integer consider)
            throws WpsMonitorClientException;

    /**
     * Gets a WpsResource instance. This instances describes a registrated
     * WPS-Server.
     *
     * @param wpsEndPoint
     * @return
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsResource getWps(final URL wpsEndPoint)
            throws WpsMonitorClientException;

    /**
     *
     * @param wpsEndPoint
     * @param forceRefresh
     * @return
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public WpsResource getWps(final URL wpsEndPoint, final Boolean forceRefresh)
            throws WpsMonitorClientException;

    /**
     *
     * @return @throws WpsMonitorClientException WpsMonitorClientException
     * capsule any Exceptions.
     */
    public List<WpsResource> getAllWps()
            throws WpsMonitorClientException;

    /**
     *
     * @param forceRefresh
     * @return
     * @throws WpsMonitorClientException WpsMonitorClientException capsule any
     * Exceptions.
     */
    public List<WpsResource> getAllWps(final Boolean forceRefresh)
            throws WpsMonitorClientException;
    /**
     * Checks if the WpsMonitor is available.
     * 
     * @return true if available, otherwise false 
     */
    public Boolean isReachable();
}
