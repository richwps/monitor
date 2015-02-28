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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.MonitorCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandOption;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.FileUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class TestCommand extends MonitorCommand {
    
    @CommandOption(
            shortOptionName = "rf",
            longOptionName = "request-file",
            description = "Enter a valid path to a file with a xml test request to import.",
            hasArgument = true,
            argumentName = "requestfile"
    )
    private String requestFile;

    public TestCommand(final Monitor monitor) {
        super("test", "Requests a WPS with the testrequest of a file or an already "
                + "saved testrequest of the specified wps/process.", monitor);
    }

    @Override
    public void execute() throws CommandException {
        try {
            if(endpoint == null && wpsId != null) {
                final WpsEntity wps = monitorControl.getWps(wpsId);
                
                if(wps != null) {
                    endpoint = wps.getEndpoint();
                } else {
                    throw new CommandException("WPS with the given ID is not registred within the monitor.");
                }
            }
            
            if (endpoint != null && identifier != null) {
                final WpsClient client = getWpsClientInstance();
                
                String request;
                if (requestFile != null) {
                    request = FileUtils.loadFile(Paths.get(requestFile));
                } else {
                    WpsProcessEntity process = monitorControl.getProcess(endpoint, identifier);
                    
                    if(process != null) {
                        request = process.getRawRequest();
                    } else {
                        throw new CommandException("Process not found.");
                    }
                }
                
                WpsRequest wpsRequest = buildWpsRequest(endpoint, identifier, request);
                WpsResponse response = client.execute(wpsRequest);
                
                if(!response.isException()) {
                    super.consoleProxy.printLine("Request is o.k.");
                } else {
                    if(response.isConnectionException()) {
                        super.consoleProxy.printLine("Can't connect to WPS Server.");
                    }
                    
                    if(response.isWpsException()) {
                        super.consoleProxy.printLine("WPS Exception: " + response.getExceptionMessage());
                    }
                    
                    if(response.isOtherException()) {
                        super.consoleProxy.printLine("Uknow Exception: " + response.getExceptionMessage());
                    }
                }
            } else {
                throw new WpsProcessMissingException();
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandException("Illegal Argument: ", ex);
        } catch (IOException ex) {
            throw new CommandException("Can't read file.", ex);
        } catch (CreateException ex) {
            throw new CommandException("Can't create WpsClient instance!", ex);
        }
    }
    
    private WpsRequest buildWpsRequest(final URL endpoint, final String identifier, final String request) {
        WpsProcessInfo wpsProcessInfo = new WpsProcessInfo(endpoint, identifier);
        
        return new WpsRequest(request, wpsProcessInfo);
    }
    
    private WpsClient getWpsClientInstance() throws CreateException {
        return monitor.getBuilderInstance()
                .getWpsClientFactory()
                .create();
    }
}
