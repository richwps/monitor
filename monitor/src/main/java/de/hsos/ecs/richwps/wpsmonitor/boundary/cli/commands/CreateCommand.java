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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.MonitorCommandWithTrigger;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import java.net.URL;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CreateCommand extends MonitorCommandWithTrigger {

    public CreateCommand(final Monitor monitor) {
        super("create", "Registers a new WPS, Process or Trigger to a Process in the monitor. "
                + "If the WPS of thr entered Processname does not exists, "
                + "the WPS will be registred also.", monitor);
    }

    @Override
    public void execute() throws CommandException {

        try {
            if (endpoint != null) {
                addWps(endpoint);

                if (identifier != null) {
                    addProcess(endpoint, identifier);

                    if (triggerStringRepresentation != null && !triggerStringRepresentation.isEmpty()) {
                        super.addTrigger(wpsId, identifier, triggerStringRepresentation);
                    }
                }
            } else {
                throw new WpsProcessMissingException();
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandException(ex.getMessage());
        }
    }

    private void addWps(final URL endpoint) throws CommandException {
        if (!monitorControl.isWpsExists(endpoint)) {
            Boolean createWps = monitorControl.createWps(endpoint);

            if (!createWps) {
                throw new CommandException("Can't create WPS entry. Maybe the entry already exists.");
            }
        }
    }

    private void addProcess(final URL endpoint, final String processIdentifier) throws CommandException {
        if (!monitorControl.isProcessExists(endpoint, processIdentifier)) {
            Boolean createAndScheduleProcess = monitorControl.createAndScheduleProcess(endpoint, processIdentifier);

            if (!createAndScheduleProcess) {
                throw new CommandException("Can't create Process entry. Maybe the entry already exists.");
            }
        }
    }

    
}
