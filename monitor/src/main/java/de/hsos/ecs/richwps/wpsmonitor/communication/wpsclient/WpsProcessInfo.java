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
package de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient;

import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URL;

/**
 * Contains all necessary data to call a Wps-Process.
 *
 * @see WpsRequest
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessInfo {

    /**
     * URL of the WPS-Server.
     */
    private URL wpsEndpoint;

    /**
     * Identifier of the Process.
     */
    private String processIdentifier;

    /**
     * Constructor.
     *
     * @param wpsEndpoint Endpoint of the WPS-Server
     * @param processIdentifier Identifier of the Process
     */
    public WpsProcessInfo(final URL wpsEndpoint, final String processIdentifier) {
        this.wpsEndpoint = Validate.notNull(wpsEndpoint, "wpsUri");
        this.processIdentifier = Validate.notNull(processIdentifier, "processIdentifier");
    }

    public URL getWpsEndpoint() {
        return wpsEndpoint;
    }

    public void setWpsEndpoint(final URL wpsUrl) {
        this.wpsEndpoint = wpsUrl;
    }

    public String getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(String processIdentifier) {
        this.processIdentifier = processIdentifier;
    }
}
