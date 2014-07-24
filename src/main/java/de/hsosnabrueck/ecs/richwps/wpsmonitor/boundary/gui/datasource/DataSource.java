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

import java.util.List;

/**
 * A DataSource instance is created by a {@link DataDriver}. A DataSource is
 * linked to a resource, like a database or file which contains informations
 * over WPS and WPS-Processes. The DataSource need to know how the specific
 * resource is readed.
 *
 * {@link WpsDescription}-Instances should contains a set of
 * {@link WpsProcessDescription}-Instances.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface DataSource {

    /**
     * Gets a list of {@link WpsDescription}-Instances from the used resource.
     * 
     * @return List of {@link WpsDescription}-Instances.
     */
    public List<WpsDescription> getWpsList();

    /**
     * Initializes the DataSource-Instance with the used DataDriver-Instance and 
     * the expected resource-type.
     * 
     * @param driver DataDriver instance.
     * @param resource Expected resource type.
     * @throws DataSourceException 
     */
    public void init(DataDriver driver, String resource) throws DataSourceException;

    /**
     * Gets the name of the used DataDriver instance.
     * 
     * @return String
     */
    public String getUsedDriver();

    /**
     * Gets the resource String.
     * 
     * @return String
     */
    public String getRessource();
}
