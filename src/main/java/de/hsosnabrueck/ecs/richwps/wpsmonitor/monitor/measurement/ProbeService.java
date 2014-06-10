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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ProbeService {
    private List<Class<? extends QosProbe>> probeClasses;

    public ProbeService addProbeClass(Class<? extends QosProbe> e) {
        probeClasses.add(e);

        return this;
    }

    public boolean removeProbeClass(Class<? extends QosProbe> e) {
        return probeClasses.remove(e);
    }
    
    public List<QosProbe> probesFactory() throws InstantiationException, IllegalAccessException {
        List<QosProbe> factoredObjects = new ArrayList<QosProbe>();
        
        for(Class c : probeClasses) {
            QosProbe add = (QosProbe)c.newInstance();
            factoredObjects.add(add);
        }
        
        return factoredObjects;
    }
}
