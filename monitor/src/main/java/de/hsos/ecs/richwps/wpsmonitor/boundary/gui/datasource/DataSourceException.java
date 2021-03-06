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

/**
 * This exception can be thrown if a DataSource implementation wants to
 * signalize an exception.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DataSourceException extends Exception {

    public DataSourceException(final String msg) {
        super(msg);
    }
    
    public DataSourceException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
    
    public DataSourceException(final Throwable ex) {
        super(ex);
    }

}
