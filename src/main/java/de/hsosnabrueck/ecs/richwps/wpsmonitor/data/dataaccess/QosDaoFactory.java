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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;

/**
 * Factory for {@link QosDataAccess}-Objects.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class QosDaoFactory implements Factory<QosDataAccess> {

    private final Factory<QosDataAccess> defaultFactory;

    /**
     * Constructor.
     *
     * @param defaultFactory Default Factory-instance
     */
    public QosDaoFactory(Factory<QosDataAccess> defaultFactory) {
        this.defaultFactory = Param.notNull(defaultFactory, "defaultFactory");
    }

    @Override
    public synchronized QosDataAccess create() throws CreateException {
        return defaultFactory.create();
    }
}
