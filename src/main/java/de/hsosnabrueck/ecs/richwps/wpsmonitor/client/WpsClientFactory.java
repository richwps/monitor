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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.mok.WpsClientMok;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsClientFactory {
    private static Class<? extends WpsClient> defaultClientImpl = WpsClientMok.class;
    
    public static void setDefaultClient(final Class<? extends WpsClient> defaultImplementation) {
        defaultClientImpl = Param.notNull(defaultClientImpl, "defaultImplementation");
    }
    
    public static WpsClient createDefault() {
        WpsClient client = null;
        
        try {
            client = WpsClientFactory.defaultClientImpl.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(WpsClientFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WpsClientFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return client;
    }
}
