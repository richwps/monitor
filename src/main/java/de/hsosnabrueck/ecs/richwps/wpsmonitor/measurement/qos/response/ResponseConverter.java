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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.converter.EntityConverter;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseConverter extends EntityConverter {

    @Override
    public Object convert() {
        Integer worst = Integer.MIN_VALUE, best = Integer.MAX_VALUE, average = 0, notAvailableCounter = 0;
        Double availability = 0.;

        for (AbstractQosEntity e : getEntities()) {
            if (e instanceof ResponseEntity) {
                ResponseEntity responseEntity = (ResponseEntity) e;

                Integer compare = responseEntity.getResponseTime();

                if (compare != null) {
                    if (compare > worst) {
                        worst = compare;
                    }

                    if (compare < best) {
                        best = compare;
                    }

                    average += compare;
                } else {
                    ++notAvailableCounter;
                }
            }
        }
        
        if (!getEntities().isEmpty()) {
            average = average / getEntities().size();
            Integer a, b;
            a = getEntities().size();
            b = notAvailableCounter;
            System.out.println("NotAvailable " + a);
            availability = a - (100. * b / a);
        } else {
            return "No Data available";
        }

        return new ResponseConverted(best, worst, average, availability);
    }

    @Override
    public String getName() {
        return "response_metric";
    }

}
