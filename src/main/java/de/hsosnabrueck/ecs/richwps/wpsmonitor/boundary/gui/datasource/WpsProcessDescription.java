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

/**
 * Class to describe Wps-Processes.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDescription {

    /**
     * Identifier of the Process
     */
    private final String identifier;

    /**
     * Title of the Process
     */
    private final String title;

    /**
     * Abstract of the process
     */
    private final String strAbstract;

    /**
     * Version of the process
     */
    private final String version;

    /**
     * Constructor.
     *
     * @param identifier Identifier for the Process which is displayed in the
     * Monitor
     */
    public WpsProcessDescription(final String identifier) {
        this(identifier, "", "", "");
    }

    /**
     *
     * @param identifier Identifier for the Process which is displayed in the
     * Monitor
     * @param strAbstract A short abstract about the Wps Process
     */
    public WpsProcessDescription(final String identifier, final String strAbstract) {
        this(identifier, "", strAbstract, "");
    }

    /**
     *
     * @param identifier Identifier for the Process which is displayed in the
     * Monitor
     * @param title Title of the Wps-Process
     * @param strAbstract A short abstract about the Wps Process
     * @param version Version of the Wps-Process
     */
    public WpsProcessDescription(final String identifier, final String title, final String strAbstract, final String version) {
        this.identifier = Validate.notNull(identifier, "identifier");
        this.title = title;
        this.strAbstract = strAbstract;
        this.version = version;
    }

    /**
     * Gets the Identifier.
     *
     * @return String
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the title.
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the abstract.
     *
     * @return String
     */
    public String getAbstract() {
        return strAbstract;
    }

    /**
     * Gets the version.
     *
     * @return String
     */
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Identifier: " + identifier + " \nAbstract: " + strAbstract;
    }
}
