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
package de.hsos.ecs.richwps.wpsmonitor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ApplicationStartOptions {
    private final CommandLine cmd;
    private final static Options opt;
    
    public final static Level DEFAULT_LOG_LEVEL = Level.INFO;
    
    public enum UiType {
        GUI, CLI, NONE
    };
    
    static {
        opt = new Options();
        opt.addOption("ut", "ui-type", true, "Type of ui. Possibilities: gui, cli");
        opt.addOption("ll", "log-level", true, "Log Level. Possibilities: debug, none");
    }
    
    private Level logLevel;
    private UiType ui;

    public ApplicationStartOptions(final String[] args) throws ParseException {
        this.cmd = new GnuParser().parse(opt, args);
        this.ui = UiType.GUI;
        this.logLevel = DEFAULT_LOG_LEVEL;
        
        init();
    }
    
    private void init() {
        if(cmd.hasOption("ui-type") && cmd.getOptionValue("ui-type").equals("cli")) {
            ui = UiType.CLI;
        }
        
        if(cmd.hasOption("log-level")) {
            String strLogLevel = cmd.getOptionValue("log-level");
            
            if(strLogLevel.equals("debug")) {
                logLevel = Level.DEBUG;
            }
        }
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public UiType getUi() {
        return ui;
    }

    public static Options getOpt() {
        return opt;
    }
}
