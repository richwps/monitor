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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.Command;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import java.util.List;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class HelpCommand extends Command {
    private final List<Command> commands;

    public HelpCommand(final List<Command> commands) {
        super("help", "Prints all Commands with their descriptions and options.");
        
        this.commands = commands;
    }

    @Override
    public void execute() throws CommandException {
        StringBuilder strBuilder = new StringBuilder(LINE_SEPERATOR);
        
        for(final Command command : commands) {
            strBuilder.append(command.toString())
                    .append("\n");
            
        }
        
        consoleProxy.printLine(strBuilder.toString());
    }
    
}
