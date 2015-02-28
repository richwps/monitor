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
package de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasuredValue;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.Measurement;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.QosMetric;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MeasureUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metric for ResponseEntity instances. Calculates the best-, the worst- and the
 * median value for the given ResponseEntity-Instances.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseMetric extends QosMetric {

    @Override
    public Map<String, MeasuredValue> calculate() {
        double worst = 0., best = 0.;
        Double median;

        List<Integer> averageList = new ArrayList<>();

        for (Measurement measured : getEntities()) {
            ResponseEntity responseEntity = measured.getEntity();
            Integer compare = responseEntity.getResponseTime();

            if (compare != null) {
                if (compare > worst) {
                    worst = compare * 1.0;
                }

                if (compare < best) {
                    best = compare * 1.0;
                }

                averageList.add(compare);
            }
        }

        if (!getEntities().isEmpty() && !averageList.isEmpty()) {
            median = computeMedian(averageList.toArray(new Integer[averageList.size()]));
        } else {
            return null;
        }

        Map<String, MeasuredValue> data = new HashMap<>();
        data.put("median", new MeasuredValue(median / 1000, MeasureUnit.SECOND));
        data.put("worst", new MeasuredValue(worst / 1000, MeasureUnit.SECOND));
        data.put("best", new MeasuredValue(best / 1000, MeasureUnit.SECOND));

        return data;
    }

    private Double computeMedian(final Integer[] values) {
        Double median = null;

        if (values != null) {
            Integer index = values.length / 2;
            Arrays.sort(values);

            if (values.length % 2 == 0) {
                median = (values[index] + values[index - 1]) / 2.;
            } else {
                median = new Double(values[index]);
            }
        }

        return median;
    }

    @Override
    public String getName() {
        return "response_metric";
    }

}
