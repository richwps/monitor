/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
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
package de.hsos.ecs.richwps.wpsmonitor.util;

import java.nio.file.Paths;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Utils to get some Log4J things.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class Log4j2Utils {

    public static String getFileNameIfExists() {
        String match = null;
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        for (Map.Entry e : ctx.getConfiguration().getLoggers().entrySet()) {
            LoggerConfig c = (LoggerConfig) e.getValue();

            for (Map.Entry f : c.getAppenders().entrySet()) {
                Appender a = (Appender) f.getValue();

                if (a instanceof RollingFileAppender) {
                    match = ((RollingFileAppender) a).getFileName();
                }

                if (a instanceof FileAppender) {
                    match = ((FileAppender) a).getFileName();
                }
            }
        }

        if (match != null) {
            match = Paths.get(match).getParent().toString();
            //match = Log4j2Utils.class.getClassLoader().getResource("").getPath().concat(match);
        }

        return match;
    }
    
    public static void setLogLevel(final Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig lConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        lConfig.setLevel(level);
        
        ctx.updateLoggers();
    }

    private Log4j2Utils() {

    }
}
