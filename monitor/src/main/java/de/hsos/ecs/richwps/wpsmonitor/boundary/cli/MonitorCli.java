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

import de.hsos.ecs.richwps.wpsmonitor.ApplicationInfo;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.Command;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandAnnotationProcessor;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation.CommandAnnotationProcessorException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsole;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorCli extends CliProxy {

    private final Map<String, Command> commandMap;
    private final CommandAnnotationProcessor annotationProcessor;

    public MonitorCli(final CommandAnnotationProcessor annotationProcessor,
            final MonitorConsole console) {
        super(console);

        this.commandMap = new HashMap<>();
        this.shutdownRequested = false;
        this.annotationProcessor = annotationProcessor;
    }
    
    public void addCommand(final Command cmd) throws CommandException {
        try {
            annotationProcessor.initCommand(cmd);
            cmd.setConsoleProxy(this);
            commandMap.put(cmd.getCommandName(), cmd);
        } catch (CommandAnnotationProcessorException ex) {
            throw new CommandException("Can't init Command.", ex);
        }
    }

    public void addCommand(final List<Command> cmds) throws CommandException {
        for(final Command cmd : cmds) {
            addCommand(cmd);
        }
    }

    @Override
    public void run() {
        printLine(buildWelcomeMessage());

        while (!shutdownRequested) {
            try {
                fetchCommand(readFromPrompt()).execute();
            } catch (CommandException ex) {
                printLine(getExceptionMessages(ex));
            } catch (Exception ex) {
                printLine(ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    private String buildWelcomeMessage() {
        return new StringBuilder("WPS Monitor ")
                .append(ApplicationInfo.VERSION)
                .append(" - A system to monitor Web Processing Services (WPS)")
                .append("\n")
                .append("For more informations enter \"help\" or visit the project site at ")
                .append(ApplicationInfo.PROJECT_SITE)
                .append("\n\n")
                .append("Licensed under the Apache License, Version 2.0.")
                .append("\n\n")
                .toString();
    }

    private Command fetchCommand(final String in) throws CommandException {
        String commandName = CommandStringParser.getCommandName(in);

        if (!commandMap.containsKey(commandName)) {
            throw new CommandException("Command not found.");
        }

        Command cmd = commandMap.get(commandName);
        String[] args = CommandStringParser.getCommandOptions(in);
        
        try {
            this.annotationProcessor.injectOptions(cmd, args);
        } catch (CommandAnnotationProcessorException ex) {
            throw new CommandException("Can't pass Options.", ex);
        }

        return cmd;
    }
    
    private String getExceptionMessages(final Exception ex) {
        final StringBuilder strBuilder = new StringBuilder();
        
        appendExceptionMessages(strBuilder, ex);
        
        return strBuilder.toString();
    }
    
    private void appendExceptionMessages(final StringBuilder strBuilder, final Throwable ex) {
        strBuilder.append(ex.getMessage());
        
        if(ex.getCause() != null) {
            strBuilder.append(" ");
            appendExceptionMessages(strBuilder, ex.getCause());
        }
    }
}
