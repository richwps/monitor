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
package de.hsos.ecs.richwps.wpsmonitor.client.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasuredValue;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsMetricResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsProcessResource;
import de.hsos.ecs.richwps.wpsmonitor.client.resource.WpsResource;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;

/**
 * Class which use the WpsMonitorJsonRequest Helper to request JSON from the
 * choosen WpsMonitor. The requested JSON string would be converted to the right
 * classes by Gson.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorRequester {

    private final URL monitorEndpoint;
    private final URL wpsUrl;
    private final Gson gson;

    public WpsMonitorRequester(final URL monitorEndpoint, final Gson gson) throws HttpException {
        Validate.notNull(monitorEndpoint);
        Validate.notNull(gson);

        this.monitorEndpoint = monitorEndpoint;
        this.gson = gson;

        this.wpsUrl = buildWpsUrl();
    }
    
    public Boolean isRequestable() {
        return isRequestable(monitorEndpoint);
    }
    
    public Boolean isRequestable(final URL monitorEndpoint) {
        try {
            final URL wpsEndpoint = buildWpsUrl(monitorEndpoint);
            getJson(wpsEndpoint);
        } catch (HttpException ex) {
            if(ex.getCause() instanceof ConnectException) {
                return false;
            }
        }
        
        return true;
    }

    public List<WpsEntity> getWpsList() throws HttpException {
        String wpsListJson = getJson(wpsUrl);
        
        return gson.fromJson(wpsListJson, new TypeToken<List<WpsEntity>>() {
        }.getType());
    }
    
    public WpsProcessResource getProcess(final WpsResource wpsResource, final String wpsProcessIdentifier, final Integer considerMeasuredValue) throws HttpException {
        Validate.notNull(wpsResource, "wpsResource");
        Validate.notNull(wpsProcessIdentifier, "wpsProcessIdentifier");
        Validate.notNull(considerMeasuredValue, "considerMeasuredValue");
        
        /* Build REST endpoint URL and get the JSON Data */
        URL processURL = buildWpsProcessMetricsUrl(wpsResource.getWpsId(), wpsProcessIdentifier, considerMeasuredValue);
        String metricJson = getJson(processURL);
        
        WpsProcessResource result = null;

        if (metricJson != null && !metricJson.isEmpty()) {

            /* Restore object structure */
            Map<String, Map<String, MeasuredValue>> fromJson = gson.fromJson(metricJson, new TypeToken<Map<String, Map<String, MeasuredValue>>>() {
            }.getType());
            Map<String, WpsMetricResource> metrics = getMetrics(fromJson);
            
            result = new WpsProcessResource(wpsResource, wpsProcessIdentifier, metrics);
        }

        /* Return new WpsProcessResource instance */
        return result;
    }

    private Map<String, WpsMetricResource> getMetrics(final Map<String, Map<String, MeasuredValue>> metricsMap) {
        Map<String, WpsMetricResource> metrics = new HashMap<>();

        for (Map.Entry<String, Map<String, MeasuredValue>> entry : metricsMap.entrySet()) {
            WpsMetricResource wpsMetricResource = new WpsMetricResource(entry.getKey(), entry.getValue());
            metrics.put(entry.getKey(), wpsMetricResource);
        }

        return metrics;
    }

    public URL getMonitorEndpoint() {
        return monitorEndpoint;
    }

    public Gson getGson() {
        return gson;
    }
    
    /*
     * Encapsulate Static helpers
     */
    private String getJson(final URL endpoint) throws HttpException {
        return WpsMonitorJsonRequester.getJson(endpoint);
    }
    
    private URL buildWpsUrl() throws HttpException {
        return buildWpsUrl(monitorEndpoint);
    }
    
    private URL buildWpsUrl(final URL monitorEndpoint) throws HttpException {
        return WpsMonitorJsonRequester.buildWpsURL(monitorEndpoint);
    }
    
    private URL buildWpsProcessMetricsUrl(final Long wpsId, final String processIdentifier, final Integer considerMeasuredValue) throws HttpException {
        return WpsMonitorJsonRequester.buildWpsProcessMetricsURL(monitorEndpoint, wpsId, processIdentifier, considerMeasuredValue);
    }
}
