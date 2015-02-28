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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MyMetric extends QosMetric {

    @Override
    public Map<String, MeasuredValue> calculate() {
        ExampleQos q;
        Integer sum = 0;

        for (Measurement e : getEntities()) {
            q = e.getEntity();
            sum += q.getValue();
        }
        
        Map<String, MeasuredValue> r = new HashMap<>();
        r.put("presentate", new MeasuredValue(sum, MeasureUnit.BYTE));
        
        return r;
    }

    @Override
    public String getName() {
        return "MyTestConverter";
    }

}
