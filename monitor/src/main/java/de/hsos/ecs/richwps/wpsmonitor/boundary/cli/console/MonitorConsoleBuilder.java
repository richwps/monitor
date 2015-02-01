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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorConsoleBuilder {
    private PrintStream output;
    private InputStream input;
    private boolean silenceMode;

    public MonitorConsoleBuilder() {
        this.silenceMode = false;
    }
    
    public MonitorConsoleBuilder withStdInAndOut() {
        output = System.out;
        input = System.in;
        
        return this;
    }
    
    public MonitorConsoleBuilder withIn(final InputStream inputStream) {
        input = inputStream;
        
        return this;
    }
    
    public MonitorConsoleBuilder withOut(final PrintStream printStream) {
        output = printStream;
        
        return this;
    }
    
    public MonitorConsoleBuilder silenceMode(final boolean state) {
        this.silenceMode = state;
        
        return this;
    }
    
    public MonitorConsole build() {
        if(input == System.in && silenceMode) {
            activateSilentMode();
        }
        
        return new MonitorConsole(output, input);
    }
    
    private void activateSilentMode() {
        System.setOut(new PrintStream(new OutputStream() {

                @Override
                public void write(int i) throws IOException {
                    // do nothing
                }
            }));
    }
}
