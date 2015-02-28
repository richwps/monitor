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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandOption;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import java.net.URL;

/**
 * A specialisation of the abstract command class.
 * 
 * Accessing static methods on an object
 * https://issues.apache.org/jira/browse/CLI-224
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class MonitorCommand extends Command {
    @CommandOption(
            shortOptionName = "wps", 
            description = "Specifies which WPS should be selected by endpoint.",
            hasArgument = true,
            argumentName = "endpoint"
    )
    protected URL endpoint;
    
    @CommandOption(
            shortOptionName = "wid",
            longOptionName = "wps-id",
            description = "Specifies which WPS should be selected by ID.",
            hasArgument = true,
            argumentName = "wpsid"
    )
    protected Long wpsId;
    
    @CommandOption(
            shortOptionName = "process", 
            description = "Specifies which WPS process should be selected by identifier.",
            hasArgument = true,
            argumentName = "identifier"
    )
    protected String identifier;
    
    protected final Monitor monitor;
    protected final MonitorControlService monitorControl;

    public MonitorCommand(final String commandName, final String description, final Monitor monitor) {
        super(commandName, description);

        this.monitor = monitor;
        this.monitorControl = monitor.ServicegetMonitorControl();    
    }
}
