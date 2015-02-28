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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.Command;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandAnnotationProcessor;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.StringConverter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters.StringToDateConverter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters.StringToIntegerConverter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters.StringToLongConverter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters.StringToUrlConverter;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.commands.HelpCommand;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsole;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.GnuParser;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CliBuilder {
    private final List<Command> commandMap;
    private final Map<Class, StringConverter> converterMap;
    private final MonitorConsole console;
    
    public CliBuilder(final MonitorConsole console) {
        this.commandMap = new ArrayList<>();
        this.converterMap = new HashMap<>();
        this.console = console;
    }
    
    public CliBuilder addCommand(final Command cmd) {
        commandMap.add(cmd);
        
        return this;
    }
    
    public CliBuilder withDefaultCommands() {
        return addCommand(new HelpCommand(commandMap));
    }
    
    public CliBuilder addCommandOptionConverter(final Class forType, final StringConverter strC) {
        converterMap.put(forType, strC);
        
        return this;
    }
    
    public CommandAnnotationProcessor buildAnnotationProcessor() {
        converterMap.put(Date.class, new StringToDateConverter());
        converterMap.put(URL.class, new StringToUrlConverter());
        converterMap.put(Integer.class, new StringToIntegerConverter());
        converterMap.put(Long.class, new StringToLongConverter());
        
        return new CommandAnnotationProcessor(new GnuParser(), converterMap);
    }

    public MonitorCli build() throws CommandException  {
        MonitorCli commandLineInterface = new MonitorCli(buildAnnotationProcessor(), console);
        commandLineInterface.addCommand(commandMap);
        
        return commandLineInterface;
    }
    
}
