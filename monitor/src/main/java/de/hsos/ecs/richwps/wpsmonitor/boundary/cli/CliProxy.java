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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.CommandException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsole;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console.MonitorConsoleException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class CliProxy {

    protected final MonitorConsole console;
    private static final String LINE_SEPERATOR = System.getProperty("line.separator");
    private static final String PROMPT_PREFIX = "WPSMonitor> ";
    private static final String SUB_COMMAND_PREFIX = "";
    protected Boolean shutdownRequested;

    public CliProxy(final MonitorConsole console) {
        this.console = console;
    }

    public static String getSystemLineSeperator() {
        return LINE_SEPERATOR;
    }

    public static String getPromptPrefix() {
        return PROMPT_PREFIX;
    }
    
    protected void printToPrompt(final String msg) {
        printLine(PROMPT_PREFIX, msg);
    }

    protected void printLine(final String prefix, final String msg) {
        if (prefix != null && !prefix.isEmpty()) {
            console.printf("%1$s %2$s", prefix, msg);
        } else {
            console.printf("%1$s", msg);
        }
        
        console.printf(LINE_SEPERATOR);
    }

    public void printLine(final String msg) {
        printLine(SUB_COMMAND_PREFIX, msg);
    }

    protected String readFromPrompt() throws CommandException {
        return _readLine(PROMPT_PREFIX);
    }
    
    protected String _readLine(final String prefix) throws CommandException {
        try {
            if(prefix == null) {
                return console.readLine();
            }
            
            return console.readLine(prefix);
        } catch (MonitorConsoleException ex) {
            throw new CommandException("Error:", ex);
        }
    }
    
    public String readLine(final String msg) throws CommandException {
        return _readLine(SUB_COMMAND_PREFIX + msg);
    }

    public String readLine() throws CommandException {
        return _readLine(null);
    }
    
    public void stop() {
        this.shutdownRequested = true;
    }
    
    public void requestStop() {
        stop();
    }
    
    public abstract void run();
}
