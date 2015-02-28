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
package de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.simple;

import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;

/**
 * Factory which will be registred in WpsClientFactory. This Factory describes
 * how the SimpleWpsClient should be build.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SimpleWpsClientFactory implements Factory<WpsClient> {

    @Override
    public WpsClient create() {
        return new SimpleWpsClient();
    }

}
