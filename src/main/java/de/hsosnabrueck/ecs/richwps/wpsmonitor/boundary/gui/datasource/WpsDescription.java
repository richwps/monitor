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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to describe WPS.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDescription {

    /**
     * Identifier of the Wps
     */
    private final String identifier;

    /**
     * URI to the WPS-Server
     */
    private final URI wpsUri;

    /**
     * Set of {@link WpsProcessDescription} instances.
     */
    private final Set<WpsProcessDescription> processes;

    public WpsDescription(final String identifier, final URI wpsUri) {
        this(identifier, wpsUri, null);
    }

    /**
     * Creates an imutable WpsDescription instance with a set of
     * WpsProcessDescription-Instances and the URI which should point to the
     * described WPS.
     *
     * @param identifier Identifier of the WPS
     * @param wpsUri URI instance
     * @param processes Set of WpsProcessDescription instances
     */
    public WpsDescription(final String identifier, final URI wpsUri, final Set<WpsProcessDescription> processes) {
        this.identifier = Validate.notNull(identifier, "identifier");
        this.wpsUri = Validate.notNull(wpsUri, "wpsUri");

        if (processes == null) {
            this.processes = new HashSet<>();
        } else {
            this.processes = processes;
        }
    }

    /**
     * Get the URI of the WPS.
     *
     * @return URI instance.
     */
    public URI getUri() {
        return wpsUri;
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

    /**
     * Gets the identifier of the WPS.
     *
     * @return String
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return wpsUri.toString();
    }
}
