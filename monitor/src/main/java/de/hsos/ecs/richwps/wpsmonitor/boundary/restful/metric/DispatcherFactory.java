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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric;

import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;

/**
 * Sets a EntityDispatcher up with the configured converterMap, which was set in
 * the {@link de.hsos.ecs.richwps.wpsmonitor.boundary.restful.RestInterfaceBuilder} instance.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DispatcherFactory implements Factory<EntityDispatcher> {

    private final MetricFactoryMap converterMap;

    public DispatcherFactory(final MetricFactoryMap converterMap) {
        this.converterMap = Validate.notNull(converterMap, "converterMap");
    }

    @Override
    public EntityDispatcher create() {
        EntityDisassembler entityDisassembler = new EntityDisassembler(converterMap);

        return new EntityDispatcher(entityDisassembler);
    }
}
