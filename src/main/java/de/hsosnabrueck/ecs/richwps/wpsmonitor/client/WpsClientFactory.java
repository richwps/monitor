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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.client;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJob;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;

/**
 * This Factory is used by the MonitorBuilder and as dependency for MeasureJob.
 * WpsClientFactory is a wrapper for the specific factory of the specific
 * client. WpsClientFactory must be initiatet with this specific factory. The
 * speific factory musst implement the Factory&ltT> interface.
 *
 * @see Factory
 * @see MonitorBuilder
 * @see MeasureJob
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class WpsClientFactory implements Factory<WpsClient> {

    /**
     * Factory which will used
     */
    private final Factory<WpsClient> defaultClientImpl;

    /**
     * WpsClientFactory constructor to initiate this factory.
     *
     * @param defaultClient The default Client factory of type
     * Factory&lt;WpsClient>
     */
    public WpsClientFactory(final Factory<WpsClient> defaultClient) {
        this.defaultClientImpl = Param.notNull(defaultClient, "defaultClient");
    }

    @Override
    public WpsClient create() throws CreateException {
        return defaultClientImpl.create();
    }
}
