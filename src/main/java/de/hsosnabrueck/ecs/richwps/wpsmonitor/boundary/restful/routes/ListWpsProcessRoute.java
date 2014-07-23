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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.routes;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.MonitorRoute;
import java.util.List;
import spark.Request;
import spark.Response;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ListWpsProcessRoute extends MonitorRoute {

    public ListWpsProcessRoute() {
        super("/measurement/wps/:wpsidentifier/processes");
    }

    @Override
    public Object handle(Request request, Response response) {

        String wpsIdentifier = request.params(":wpsidentifier");

        List<WpsProcessEntity> processesOfWps = getMonitorControl().getProcessesOfWps(wpsIdentifier);

        response.type(getStrategy().getMimeType());

        return getStrategy().presentate(processesOfWps);
    }
}
