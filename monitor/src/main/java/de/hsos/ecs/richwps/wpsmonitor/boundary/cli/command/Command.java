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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.CliProxy;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * An abstract class for command implementations.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class Command {

    protected String commandName;
    protected String description;
    protected Options options;
    protected CliProxy consoleProxy;

    protected static final String LINE_SEPERATOR = System.getProperty("line.separator");

    public Command(final String commandName, final String description) {
        this.commandName = commandName;
        this.description = description;
        this.options = new Options();
    }

    public final void setConsoleProxy(final CliProxy consoleProxy) {
        this.consoleProxy = consoleProxy;
    }

    public Options getOptions() {
        return options;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public void addOption(final Option opt) {
        options.addOption(opt);
    }

    /**
     * Representates the command object as text with all options. The returned
     * string is formatted for console output.
     *
     * @return Command representation.
     */
    @Override
    public String toString() {
        return new CommandFormatter(this).toString();
    }

    public abstract void execute() throws CommandException;
}
