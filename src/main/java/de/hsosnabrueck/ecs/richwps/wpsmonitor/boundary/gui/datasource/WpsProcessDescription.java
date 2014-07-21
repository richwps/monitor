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

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDescription {
    private final String identifier;
    private final String title;
    private final String strAbstract;
    private final String version;

    public WpsProcessDescription(String identifier, String title, String strAbstract, String version) {
        this.identifier = identifier;
        this.title = title;
        this.strAbstract = strAbstract;
        this.version = version;
    }
    

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstract() {
        return strAbstract;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Identifier: " + identifier +  "\nAbstract: " + strAbstract;
    }
}
