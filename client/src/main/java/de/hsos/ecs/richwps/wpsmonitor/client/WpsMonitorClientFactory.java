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

import com.google.gson.Gson;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientCreateException;
import de.hsos.ecs.richwps.wpsmonitor.client.http.HttpException;
import de.hsos.ecs.richwps.wpsmonitor.client.http.WpsMonitorRequester;
import java.net.URL;

/**
 * Creates a new WpsMonitorClient instance. The monitorEndpoint defines the
 * point where the monitor is to find: [protocol][domain|ip]; e.g.
 * http://localhost:1111/
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorClientFactory {

    /**
     * Creates a new WpsMonitorClient instance. The monitorEndpoint defines the
     * point where the monitor is to find: [protocol][domain|ip]; e.g.
     * http://localhost:1111/
     *
     * @param monitorEndpoint [protocol][domain|ip]; e.g. http://localhost:1111/
     * @return A new WpsMonitorClient Exceptions
     * @throws WpsMonitorClientCreateException This exception would be thrown if
     * the factory is unable to create a WpsMonitorClient instance.
     */
    public WpsMonitorClient create(final URL monitorEndpoint) throws WpsMonitorClientCreateException {
        try {
            Gson gson = new Gson();
            WpsMonitorRequester requester = new WpsMonitorRequester(monitorEndpoint, gson);

            return new WpsMonitorClientImpl(monitorEndpoint, requester);
        } catch (HttpException ex) {
            throw new WpsMonitorClientCreateException("Can't create an Instance of the WpsMonitor Client Class.", ex);
        }
    }
}
