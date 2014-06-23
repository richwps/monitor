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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.routes;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.MonitorRoute;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ListMeasurementRoute extends MonitorRoute {

    public static final Logger log = LogManager.getLogger();

    public ListMeasurementRoute() {
        super("/measurement/wps/:wps/process/:process/count/:count");

        log.debug("ListMeasurementRoute instantiated");
    }

    @Override
    public Object handle(Request request, Response response) {
        try {
            String wpsIdentifier = Param.notNull(request.params(":wps"), "Wps parameter");
            String processIdentifier = Param.notNull(request.params(":process"), "Process parameter");
            String count = request.params(":count");

            List<MeasuredDataEntity> measuredData = getMonitorControl()
                    .getMeasuredData(wpsIdentifier, processIdentifier, getRange(count));

            log.debug("ListMeasurementRoute called with parameters wpsIdentifier: {} processIdentifier: {} count: {}",
                    wpsIdentifier, processIdentifier, count
            );

            return getStrategy().presentate(getDispatch().dispatch(measuredData));
        } catch (IllegalArgumentException exception) {
            response.status(404);
        }

        return null;
    }

    private Range getRange(String countValue) {
        Range range = null;

        if (countValue != null) {
            try {
                Integer countInt = Integer.parseInt(countValue);
                range = new Range(null, countInt);
            } catch (NumberFormatException e) {

            }
        }

        return range;
    }
    /* // removed; was used for spark 2.0 framework
     @Override
     public String getRoute() {
     return "/measurement/wps/:wps/process/:process/count/:count";
     }*/
}
