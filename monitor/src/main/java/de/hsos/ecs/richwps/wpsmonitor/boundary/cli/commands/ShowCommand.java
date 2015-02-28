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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.MonitorCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandOption;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.Hide;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.util.List;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ShowCommand extends MonitorCommand {
    
    @CommandOption(
            shortOptionName = "md",
            longOptionName = "measured-data",
            description = "Shows the last 15 measured data of the wps process. "
                + "This functionallity is only for testing purposes."
    )
    private Boolean showMeasuredData;
    
    @CommandOption(
            shortOptionName = "triggers",
            description = "Prints all Triggers of the specified WPS Process."
    )
    private Boolean showTriggers;
    
    public ShowCommand(final Monitor monitor) {
        super("show", "shows all WPS and Processes. Can be specified by parameters. "
                + "--wps to show a list of WPS and processes, --wps <endpoint> "
                + "to show processes of a WPS. --triggers in combination with "
                + "--process <identifier> shows the triggers of the process."
                + "If the --triggers parameter missing, the testreques will be displayed.", monitor);
    }

    @Override
    public void execute() throws CommandException {
        final Gson gson = getGson();

        StringBuilder strBuilder = new StringBuilder();
        try {
            if(wpsId == null && endpoint != null) {
                wpsId = monitorControl.getWpsId(endpoint);
            }
            
            if (showTriggers) {
                if (wpsId != null && identifier != null) {
                    List<TriggerConfig> triggers = monitorControl.getTriggers(wpsId, identifier);

                    for (int i = 0; i < triggers.size(); i++) {
                        TriggerConfig t = triggers.get(i);
                        strBuilder.append("\t(")
                                .append(i)
                                .append(") ")
                                .append(gson.toJson(t))
                                .append("\n");
                    }
                } else {
                    throw new WpsProcessMissingException();
                }
            } else {
                if (wpsId != null && identifier != null) {
                    if (showMeasuredData) {
                        List<MeasuredDataEntity> measuredData = monitorControl.getMeasuredData(wpsId, identifier, new Range(0, 15));

                        for (MeasuredDataEntity m : measuredData) {
                            strBuilder.append("\t")
                                    .append(m.toString())
                                    .append("\n");
                        }
                    } else {
                        WpsProcessEntity process = monitorControl.getProcess(wpsId, identifier);
                        
                        if(process != null) {
                            strBuilder.append(process.getRawRequest());
                        } else {
                            strBuilder.append("Process not found.");
                        }
                    }
                } else if (wpsId != null) {
                    appenProcessOutput(wpsId, strBuilder);
                } else {
                    List<WpsEntity> wpsList = monitorControl.getWpsList();
                    
                    for (final WpsEntity wps : wpsList) {
                        strBuilder.append("ID (")
                                .append(wps.getId())
                                .append(") ")
                                .append(wps.getEndpoint().toString())
                                .append("\n")
                                .append("Processes: ")
                                .append("\n");

                        appenProcessOutput(wps.getId(), strBuilder);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandException("Illegal Argument: ", ex);
        }

        super.consoleProxy.printLine(strBuilder.toString());
    }
    
    private Gson getGson() {
        return new GsonBuilder().setExclusionStrategies(
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(Hide.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> arg0) {
                        return false;
                    }
                }
        ).create();
    }

    private void appenProcessOutput(final Long wpsId, final StringBuilder strBuilder) {
        List<WpsProcessEntity> processes = monitorControl.getProcesses(wpsId);
        for (final WpsProcessEntity process : processes) {
            strBuilder.append("\tIdentifier: ")
                    .append(process.getIdentifier())
                    .append("\n");
        }
    }
}
