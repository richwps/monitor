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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorJsonRequester {

    private final static String MIME_TYPE = "application/json";
    private final static String REQUEST_METHOD = "GET";

    private final static String MEASUREMENT_RESOURCE = "measurement";
    private final static String WPS_RESOURCE = "wps";
    private final static String WPS_PROCESS_RESOURCE = "process";
    private final static String COUNT_OPTION = "count";

    public static String getJson(final URL endPoint) throws HttpException {
        String result = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setRequestProperty("Accept", MIME_TYPE);

            if(connection.getResponseCode() == 404) {
                return null;
            } else if (connection.getResponseCode() != 200) {
                throw new HttpException("Request failed: HTTP Error Code was: " + connection.getResponseCode());
            }

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String tmp;
            while ((tmp = reader.readLine()) != null) {
                builder.append(tmp);
            }

            result = builder.toString();
        } catch (ProtocolException ex) {
            throw new HttpException(ex);
        } catch (IOException ex) {
            throw new HttpException(ex);
        }

        return result;
    }

    public static URL buildWpsProcessMetricsURL(final URL monitorEndpoint, final Long wpsId,
            final String wpsProcessIdentifier, final Integer count)
            throws HttpException {
        
        try {
            String buildUrl = concatUrlStr(new String[] {
                WPS_RESOURCE,
                wpsId.toString(),
                WPS_PROCESS_RESOURCE,
                wpsProcessIdentifier,
                COUNT_OPTION,
                count.toString()
            });
            
            return new URL(monitorEndpoint, buildUrl);
        } catch (MalformedURLException ex) {
            throw new HttpException("Can't build URL.", ex);
        }
    }
    
    public static URL buildWpsURL(final URL monitorEndpoint)
            throws HttpException {
        
        try {
            String buildUrl = concatUrlStr(new String[] {
                WPS_RESOURCE
            });
            
            return new URL(monitorEndpoint, buildUrl);
        } catch (MalformedURLException ex) {
            throw new HttpException("Can't build URL.", ex);
        }
    }
    
    private static String concatUrlStr(final String[] pieces) {
        StringBuilder s = new StringBuilder(MEASUREMENT_RESOURCE);
        
        for(String p : pieces) {
            if(s.charAt(s.length() - 1) != '/') {
                s.append('/');
            }
            s.append(p);
        }

        return s.toString();
    }
    
    private WpsMonitorJsonRequester() {
        
    }
}
