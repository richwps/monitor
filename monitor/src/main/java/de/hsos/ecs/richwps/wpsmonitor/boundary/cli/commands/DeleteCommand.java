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
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DeleteCommand extends MonitorCommand {
    @CommandOption(
            shortOptionName = "omd",
            longOptionName = "only-measured-data",
            description = "Delete only the measured Data of the specified WPS process"
    )
    private Boolean onlyMeasuredData;
    
    @CommandOption(
            shortOptionName = "tid",
            longOptionName = "trigger-id",
            description = "Deletes the trigger with the given ID. "
                + "type show <wps> <process> --triggers to find out the right trigger id.",
            hasArgument = true,
            argumentName = "triggerid"
    )
    private Integer triggerId;
    
    @CommandOption(
            shortOptionName = "d",
            longOptionName = "date",
            description = "Specified the date at which the measured data "
                + "should be deleted. e.g. 12.03.2012",
            hasArgument = true,
            argumentName = "date"
    )
    private Date date;

    public DeleteCommand(final Monitor monitor) {
        super("delete", "Deletes a WPS, Process of a WPS, Trigger of a WPS process "
                + "or the measured Data of a WPS process. If the data option is "
                + "specified with the onley-measured-data option, all measured data "
                + "will be deleted which are older than <date>.", monitor);   
    }

    @Override
    public void execute() throws CommandException {
        if(endpoint != null && wpsId == null) {
            monitorControl.getWpsId(endpoint);
        }
        
        if(wpsId != null && identifier != null) {
            if(onlyMeasuredData) {
                monitorControl.deleteMeasuredDataOfProcess(wpsId, identifier, date);
            } else if(triggerId != null) {
                if(!deleteTrigger(wpsId, identifier, triggerId)) {
                    super.consoleProxy.printLine("No trigger found for id " + triggerId.toString());
                }
            }else {                
                if(!monitorControl.deleteProcess(wpsId, identifier)) {
                    super.consoleProxy.printLine("No process found for deletetion.");
                }
            }
        } else if(wpsId != null) {
            if(!monitorControl.deleteWps(wpsId)) {
                super.consoleProxy.printLine("No WPS found for deletetation.");
            }
        } else {
            throw new WpsProcessMissingException();
        }
    }
    
    private Boolean deleteTrigger(final Long wpsId, final String identifier, final Integer id) {
        List<TriggerConfig> triggers = monitorControl.getTriggers(wpsId, identifier);
        
        for(int i = 0; i < triggers.size(); i++) {
            if(id == i) {
                monitorControl.deleteTrigger(triggers.get(i));
                
                return true;
            }
        }
        
        return false;
    }
}
