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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.routes;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.MonitorRoute;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * This is the main route of the RESTful Webservice. It's has the following
 * parameters:
 *
 * :wps - for the WPS-Identifier :process - for the WPS-Process Identifier
 * :count - for the amount of data which would be selected :display - * the
 * display mode; both for Measurements and metrics, metrics for only metrics and
 * values for only measurements
 *
 * Through this route you can select all measured datas of the monitor.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ListMeasurementRoute extends MonitorRoute {

    private static final Logger LOG = LogManager.getLogger();
    private static final Integer DEFAULT_COUNT_OF_RANGE = 100;

    public ListMeasurementRoute(final String alias) {
        super(alias);
    }

    public ListMeasurementRoute() {
        super("/measurement/wps/:wps/process/:process/count/:count");
    }

    @Override
    public Object handle(Request request, Response response) {
        try {
            String wpsIdString = Validate.notNull(request.params(":wps"), "Wps parameter");
            String processIdentifier = Validate.notNull(request.params(":process"), "Process parameter");
            String count = request.params(":count");

            Long wpsId = Long.parseLong(wpsIdString);
            if (getMonitorControl().isProcessExists(wpsId, processIdentifier)) {
                List<MeasuredDataEntity> measuredData = getMonitorControl()
                        .getMeasuredData(wpsId, processIdentifier, getRange(count));

                LOG.debug("ListMeasurementRoute called with parameters wpsIdentifier: {} processIdentifier: {} count: {}",
                        wpsIdString, processIdentifier, count
                );

                Map<String, Object> toPresentate = getMetrics(measuredData);
                
                response.type(getStrategy().getMimeType());

                return getStrategy()
                        .presentate(toPresentate);
            } 
        } catch (IllegalArgumentException ex) {
            LOG.warn("A value was null.", ex);
        }

        response.status(404);
        return null;
    }

    private Map<String, Object> getMetrics(List<MeasuredDataEntity> measuredData) {
        return getDispatch().dispatchToMetric(measuredData);
    }

    private Map<String, Object> getRaw(List<MeasuredDataEntity> measuredData) {
        return getDispatch().dispatchData(measuredData);
    }

    private Map<String, Object> getRawAndMetrics(List<MeasuredDataEntity> measuredData) {
        return getDispatch().dispatchBoth(measuredData);
    }

    private Range getRange(String countValue) {
        Integer countInt;

        if (countValue == null) {
            countInt = DEFAULT_COUNT_OF_RANGE;
        } else {
            try {
                countInt = Integer.parseInt(countValue);
            } catch (NumberFormatException e) {
                countInt = DEFAULT_COUNT_OF_RANGE;
            }
        }

        return new Range(null, countInt);
    }
}
