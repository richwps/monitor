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
package de.hsos.ecs.richwps.wpsmonitor.qos.response;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.Measurement;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.QosMetric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseMetric extends QosMetric {

    @Override
    public Object calculate() {
        Integer worst = null, best = null;
        Double median;

        List<Integer> averageList = new ArrayList<>();

        for (Measurement measured : getEntities()) {
            ResponseEntity responseEntity = measured.getEntity();
            Integer compare = responseEntity.getResponseTime();

            if (compare != null) {
                if (worst == null) {
                    worst = 0;
                }

                if (best == null) {
                    best = 0;
                }

                if (compare > worst) {
                    worst = compare;
                }

                if (compare < best) {
                    best = compare;
                }

                averageList.add(compare);
            }
        }

        if (!getEntities().isEmpty()) {
            median = computeMedian(averageList.toArray(new Integer[averageList.size()]));
        } else {
            return "No Data available";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("median", median);
        data.put("worst", worst);
        data.put("best", best);

        return data;
    }

    private Double computeMedian(final Integer[] values) {
        Double median = null;

        if (values != null && values.length > 0) {
            Integer index = values.length / 2;
            double unboxedMedian;

            Arrays.sort(values);

            if (values.length % 2 == 0) {
                unboxedMedian = (values[index] + values[index - 1]) / 2;
            } else {
                unboxedMedian = values[index];
            }

            median = unboxedMedian;
        }

        return median;
    }

    @Override
    public String getName() {
        return "response_metric";
    }

}
