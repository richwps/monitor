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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.wpsclient;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URI;

/**
 * Contains all necessary data to call a Wps-Process.
 *
 * @see WpsRequest
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessInfo {

    /**
     * URI of the WPS-Server
     */
    private URI wpsUri;

    /**
     * Identifier of the Process
     */
    private String processIdentifier;

    /**
     * Constructor.
     *
     * @param wpsUri URI of the WPS-Server
     * @param processIdentifier Identifier of the Process
     */
    public WpsProcessInfo(final URI wpsUri, final String processIdentifier) {
        this.wpsUri = Validate.notNull(wpsUri, "wpsUri");
        this.processIdentifier = Validate.notNull(processIdentifier, "processIdentifier");
    }

    public URI getWpsUri() {
        return wpsUri;
    }

    public void setWpsUri(URI wpsUri) {
        this.wpsUri = wpsUri;
    }

    public String getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(String processIdentifier) {
        this.processIdentifier = processIdentifier;
    }
}
