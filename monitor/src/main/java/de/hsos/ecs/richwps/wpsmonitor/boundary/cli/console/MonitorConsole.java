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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorConsole {
    private final PrintStream output;
    private final BufferedReader input;

    public MonitorConsole(final PrintStream printStream, final InputStream inputStream) {
        this.output = printStream;
        this.input = new BufferedReader(new InputStreamReader(inputStream)); 
    }

    public PrintStream printf(final String string, final Object... os) {
        return output.printf(string, os); 
    }

    public void print(final String string) {
        output.print(string);
    }

    public void println(final String string) {
        output.println(string);
    }

    public String readLine() throws MonitorConsoleException {
        try {
            return input.readLine();
        } catch (IOException ex) {
            throw new MonitorConsoleException("Can't read from the given InputStream.", ex);
        }
    }

    public String readLine(final String prefix) throws MonitorConsoleException {
        print(prefix);
        return readLine();
    }
}
