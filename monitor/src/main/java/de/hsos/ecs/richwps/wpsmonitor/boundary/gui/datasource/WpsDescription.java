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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource;

import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to describe WPS. WPS-Identifier- and URI are mandatory.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDescription {

    /**
     * Endpoint to the WPS-Server
     */
    private final URL endpoint;

    /**
     * Set of {@link WpsProcessDescription} instances.
     */
    private final Set<WpsProcessDescription> processes;

    /**
     * Creates an immutable WpsDescription instance with the WPS identifier and
     * uri.
     * 
     * @param endpoint URI instance
     */
    public WpsDescription(final URL endpoint) {
        this(endpoint, null);
    }

    /**
     * Creates an immutable WpsDescription instance with a set of
     * WpsProcessDescription-Instances and the URI which should point to the
     * described WPS.
     *
     * @param endpoint URI instance
     * @param processes Set of WpsProcessDescription instances
     */
    public WpsDescription(final URL endpoint, final Set<WpsProcessDescription> processes) {
        this.endpoint = Validate.notNull(endpoint, "endpoint");

        if (processes == null) {
            this.processes = new HashSet<>();
        } else {
            this.processes = processes;
        }
    }

    /**
     * Get the Endpoint of the WPS.
     *
     * @return URL instance.
     */
    public URL getEndpoint() {
        return endpoint;
    }

    /**
     * Get the Set of {@link WpsProcessDescription} Instances.
     *
     * @return Set of {@link WpsProcessDescription} Instances.
     */
    public Set<WpsProcessDescription> getProcesses() {
        return processes;
    }

    /**
     * Add a {@link WpsProcessDescription} instance to the internal set.
     *
     * @param e WpsProcessDescription Instance
     * @return true if successfully added
     */
    public boolean add(WpsProcessDescription e) {
        return processes.add(e);
    }

    @Override
    public String toString() {
        return endpoint.toString();
    }
}
