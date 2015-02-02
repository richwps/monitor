/*
 * Copyright 2015 Florian Vogelpohl <floriantobias@gmail.com>.
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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandOption;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.exception.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import java.net.URL;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class MonitorCommandWithTrigger extends MonitorCommand {

    @CommandOption(
            shortOptionName = "trigger",
            description = "The TriggerConfig Object as JSON string. "
            + "e.g. '{\"intervalType\":\"SECOND|MINUTE|HOUR|DAY|WEEK|MONTH\", "
            + "\"start\":\"Jan 31, 2015 7:20:04 PM\", \"end\":\"Feb 19, 2015 7:20:04 PM\", "
            + "\"interval\":2}'",
            hasArgument = true,
            argumentName = "triggerjson"
    )
    protected String triggerJson;

    public MonitorCommandWithTrigger(final String commandName, final String description, final Monitor monitor) {
        super(commandName, description, monitor);
    }

    protected void addTrigger(final URL endpoint, final String processIdentifier, final TriggerConfig config) {
        monitorControl.saveTrigger(endpoint, processIdentifier, config);
    }

    protected TriggerConfig unmarshallJson(final String json) throws CommandException {
        final Gson gson = new Gson();

        final TriggerConfig result = gson.fromJson(json, new TypeToken<TriggerConfig>() {
        }.getType());
        
        if(result.getStart() == null || result.getEnd() == null || result.getInterval() == null || result.getIntervalType() == null) {
            throw new CommandException("One of the required config options of the given "
                    + "trigger string are missing. "
                    + "start, end, intervalType and interval are required.");
        }
        
        return result;
    }
}
