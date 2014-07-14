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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseConverter extends EntityConverter {

    @Override
    public Object convert() {
        Integer worst = Integer.MIN_VALUE, best = Integer.MAX_VALUE, notAvailableCounter = 0;
        Double availability = 0., average = 0.;
        
        List<AbstractQosEntity> entities = getEntities();
        List<Integer> averageList = new ArrayList<Integer>();
        
        for (AbstractQosEntity e : entities) {
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
                    
                    averageList.add(compare);
                } else {
                    ++notAvailableCounter;
                }
            }
        }
        
        if (!getEntities().isEmpty()) {            

            Integer a, b;
            a = entities.size();
            b = notAvailableCounter;

            availability = 100 - (100.0 * b / a);
            average = computeMedian(averageList.toArray(new Integer[averageList.size()]));
        } else {
            return "No Data available";
        }
        
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("availability", availability);
        data.put("average", average);
        data.put("worst", worst);
        data.put("best", best);
        
        return data;
    }
    
    private Double computeMedian(Integer[] values) {
        Double median = null;
        
        if(values != null && values.length > 0) {
            Integer index = values.length / 2;
            double _median;
            
            Arrays.sort(values);
            
            if(index % 2 == 0) {
                _median = (double)(values[index] + values[index - 1]) / 2;
            } else {
                _median = (double)values[index];
            }
            
            median = _median; // better as (Double)(double) ..
        }
        
        return median;
    }

    @Override
    public String getName() {
        return "response_metric";
    }

}
