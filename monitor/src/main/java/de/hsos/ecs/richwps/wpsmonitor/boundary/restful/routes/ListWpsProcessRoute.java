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
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * Lists all WPS-Processes of registered WPS-Server.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ListWpsProcessRoute extends MonitorRoute {

    private final static Logger LOG = LogManager.getLogger();
    public ListWpsProcessRoute() {
        super("/measurement/wps/:wpsid/process");
    }

    @Override
    public Object handle(final Request request, final Response response) {

        try {
            final Long wpsId = Long.parseLong(request.params(":wpsid"));
            List<WpsProcessEntity> processesOfWps = getMonitorControl().getProcesses(wpsId);

            response.type(getStrategy().getMimeType());
            return getStrategy().presentate(processesOfWps);
        } catch (NumberFormatException ex) {
            LOG.warn("Can't cast wpsid Parameter to Long.", ex);
        }

        response.status(404);
        return null;
    }
}
