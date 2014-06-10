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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.impl.WpsDaoDefaultFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDaoFactory {
    private static Factory<WpsDataAccess> defaultFactory = new WpsDaoDefaultFactory();

    public static WpsDataAccess create() {
        return WpsDaoFactory.defaultFactory.create();
    }

    public static Factory<WpsDataAccess> getDefaultFactory() {
        return WpsDaoFactory.defaultFactory;
    }

    public static void setDefaultFactory(Factory<WpsDataAccess> defaultFactory) {
        WpsDaoFactory.defaultFactory = defaultFactory;
    }
}
