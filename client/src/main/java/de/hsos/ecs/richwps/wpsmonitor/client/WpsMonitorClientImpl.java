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
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientWpsNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorClientWpsProcessNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.client.exception.WpsMonitorOfflineClientException;
import de.hsos.ecs.richwps.wpsmonitor.client.http.HttpException;
import de.hsos.ecs.richwps.wpsmonitor.client.http.WpsMonitorRequester;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.converter.ResourceConverter;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;

/**
 * The default implementation of the WpsMonitorClient Interface. The Monitor
 * works with virtual keys, called WpsIdentifier. Because of this behavior, the
 * Client must request all registrated WPS from the Monitor and need to search
 * for the wpsEndpoint in the restored WpsEntity Instances.
 *
 * To minimize the overhead, the client stores requested WPS-Server entries in a
 * storaged. This storage is called wpsContext.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorClientImpl implements WpsMonitorClient {

    private final URL monitorEndpoint;
    private final WpsMonitorRequester requester;
    private final Map<URL, WpsResource> wpsContext;

    /**
     * Creates a client instance for a WpsMonitor which is reachable at
     * monitorEndpoint.
     *
     * @param monitorEndpoint URL to the RESTful-Interface of a WpsMonitor
     * @param requester
     */
    public WpsMonitorClientImpl(final URL monitorEndpoint, final WpsMonitorRequester requester) {
        Validate.notNull(monitorEndpoint);
        Validate.notNull(requester);

        this.monitorEndpoint = monitorEndpoint;
        this.requester = requester;

        wpsContext = new HashMap<>();
    }

    private void initContext() throws HttpException, WpsMonitorOfflineClientException {
        initContext(false);
    }

    private void initContext(final Boolean refresh) throws HttpException, WpsMonitorOfflineClientException {
        if (!isMonitorReachable(monitorEndpoint)) {
            throw new WpsMonitorOfflineClientException(monitorEndpoint);
        }

        if (wpsContext.isEmpty() || refresh) {
            List<WpsEntity> wpsList = requester.getWpsList();

            if (wpsList == null) {
                throw new WpsMonitorOfflineClientException(monitorEndpoint);
            }

            for (final WpsEntity entity : wpsList) {
                wpsContext.put(entity.getEndpoint(), ResourceConverter.WpsEntityToResource(entity));
            }
        }
    }

    @Override
    public Boolean isReachable() {
        return isMonitorReachable(monitorEndpoint);
    }

    private Boolean isMonitorReachable(final URL endpoint) {
        return requester.isRequestable(endpoint);
    }

    @Override
    public WpsProcessResource getWpsProcess(final URL wpsEndpoint, final String wpsProcessIdentifier)
            throws WpsMonitorClientException {
        return getWpsProcess(wpsEndpoint, wpsProcessIdentifier, false);
    }

    @Override
    public WpsProcessResource getWpsProcess(final URL wpsEndpoint, final String wpsProcessIdentifier, final Boolean forceRefresh)
            throws WpsMonitorClientException {
        WpsResource wps = getWps(wpsEndpoint, forceRefresh);

        return getWpsProcess(wps, wpsProcessIdentifier);
    }

    @Override
    public WpsProcessResource getWpsProcess(final WpsResource wpsResource, final String wpsProcessIdentifier)
            throws WpsMonitorClientException {
        return getWpsProcess(wpsResource, wpsProcessIdentifier, 1000);
    }

    @Override
    public WpsProcessResource getWpsProcess(final WpsResource wpsResource, final String wpsProcessIdentifier, final Integer consider)
            throws WpsMonitorClientException {
        try {
            if (!isMonitorReachable(monitorEndpoint)) {
                throw new WpsMonitorOfflineClientException(monitorEndpoint);
            }

            WpsProcessResource process = requester.getProcess(wpsResource, wpsProcessIdentifier, consider);

            if (process == null) {
                throw new WpsMonitorClientWpsProcessNotFoundException(wpsProcessIdentifier, wpsResource.getWpsEndPoint());
            }

            return process;
        } catch (HttpException ex) {
            throw new WpsMonitorClientException("Can't Request Process metrics.", ex);
        }
    }

    @Override
    public WpsResource getWps(final URL wpsEndPoint)
            throws WpsMonitorClientException {
        return getWps(wpsEndPoint, false);
    }

    @Override
    public WpsResource getWps(final URL wpsEndPoint, final Boolean forceRefresh)
            throws WpsMonitorClientException {
        Validate.notNull(wpsEndPoint);

        try {
            initContext(forceRefresh);
            WpsResource result = wpsContext.get(wpsEndPoint);

            if (result == null) {
                throw new WpsMonitorClientWpsNotFoundException(wpsEndPoint);
            }

            return result;
        } catch (HttpException ex) {
            throw new WpsMonitorClientException("Can't initalize WpsContext.", ex);
        }
    }

    @Override
    public List<WpsResource> getAllWps()
            throws WpsMonitorClientException {
        return getAllWps(false);
    }

    @Override
    public List<WpsResource> getAllWps(final Boolean forceRefresh)
            throws WpsMonitorClientException {
        try {
            initContext(forceRefresh);
            return new ArrayList<>(wpsContext.values());
        } catch (HttpException ex) {
            throw new WpsMonitorClientException("Can't request List of WPS. Maybe the Monitor is offline (used endpoint: " + monitorEndpoint.toString() + ")", ex);
        }
    }

    public URL getMonitorEndpoint() {
        return monitorEndpoint;
    }

    public WpsMonitorRequester getRequester() {
        return requester;
    }

    public Map<URL, WpsResource> getWpsContext() {
        return wpsContext;
    }
}
