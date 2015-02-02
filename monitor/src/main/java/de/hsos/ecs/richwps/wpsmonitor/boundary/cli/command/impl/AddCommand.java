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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.impl;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.MonitorCommandWithTrigger;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandOption;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.exception.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.util.FileUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class AddCommand extends MonitorCommandWithTrigger {

    @CommandOption(
            shortOptionName = "rf",
            longOptionName = "request-file",
            description = "Enter a valid path to a file with a xml test request to import.",
            hasArgument = true,
            argumentName = "requestfile"
    )
    private String requestFile;

    public AddCommand(final Monitor monitorControl) {
        super("add", "Adds a Process to a already registred WPS, "
                + "or a Trigger to a already registred WPS Process.",
                monitorControl
        );
    }

    @Override
    public void execute() throws CommandException {
        try {
            if (endpoint != null && identifier != null) {

                if (monitorControl.isProcessExists(endpoint, identifier)) {
                    if (triggerJson != null) {
                        addTrigger(endpoint, identifier, triggerJson);
                    }

                    if (requestFile != null) {
                        setTestRequest(endpoint, identifier, requestFile);
                    }
                } else if (monitorControl.isWpsExists(endpoint)) {
                    createProcess(endpoint, identifier);
                    
                    if(triggerJson != null || requestFile != null) {
                        super.consoleProxy.printLine("Only the process was added. "
                                + "To also add the request or trigger, call the command again.");
                    }
                } else {
                    throw new CommandException("WPS not found.");
                }

            } else {
                throw new WpsProcessMissingException();
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandException("Illegal Argument: ", ex);
        } catch (IOException ex) {
            throw new CommandException("Can't read file.", ex);
        }
    }

    private void createProcess(final URL endpoint, final String identifier) {
        if (monitorControl.createAndScheduleProcess(endpoint, identifier)) {
            super.consoleProxy.printLine("Process was added and registred in the scheduler.");
        }
    }

    private void setTestRequest(final URL endpoint, final String identifier, final String requestFile) throws IOException {
        final String fileContent = FileUtils.loadFile(Paths.get(requestFile));
        monitorControl.setTestRequest(endpoint, identifier, fileContent);
    }

    private void addTrigger(final URL endpoint, final String identifier, final String triggerJson) throws CommandException {
        TriggerConfig tConfig = unmarshallJson(triggerJson);
        super.addTrigger(endpoint, identifier, tConfig);
    }

}
