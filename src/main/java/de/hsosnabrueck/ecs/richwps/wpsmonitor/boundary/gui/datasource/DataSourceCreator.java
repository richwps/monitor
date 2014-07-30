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
 * DataSourceCreator is an abstract factory class with some abstract methods. The idea
 behind this class is, that you can create any DataDrive-Implementations which
 can be used to create specific DataSource-Instances by the given resource
 String.

 The expected content of thy resource type should be signalize through the
 getExpectedResourceType-method. E.g. if you write a DataSourceCreator for
 file-acces, the resource string should be a path to a file. For a Database it
 should be a jdbc connection string - and so on.

 Every creator need to have a name.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class DataSourceCreator {

    /**
     * Signalize the expected type of the resource string of the create method.
     * E.g. a jdbc connection string, file path or an url
     *
     * @return String which indicates the expexted resource string type
     */
    public abstract String getExpectedResourceType();

    /**
     * Gets the name of the creator.
     *
     * @return String
     */
    public abstract String getCreatorName();

    /**
     * Creation of the DataSource instance
     *
     * @return DataSource instance
     */
    protected abstract DataSource createDataSource();

    /**
     * Creates a new DataSource Instance
     *
     * @param resource resource string; jdbc string, url or something which is
     * expected by the specific DataSource
     * @return DataSource instance which is created by the protected method
     * createDataSource
     * @throws DataSourceException
     */
    public final DataSource create(String resource) throws DataSourceException {
        try {
            DataSource dataSource = createDataSource();
            dataSource.init(this, resource);

            return dataSource;
        } catch (Exception ex) {
            throw new DataSourceException(ex);
        }
    }
}
