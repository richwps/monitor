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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CommandStringParser {

    public static String getCommandName(final String stdin) {
        return stdin.substring(0, getIndexOfCommandEnd(stdin));
    }

    public static String[] getCommandOptions(final String stdin) {
        String argsString = stdin.substring(getIndexOfCommandEnd(stdin)).trim();

        StringBuilder argBuilder = new StringBuilder();
        Boolean trim = true;
        char ignoreTrimChar = '#';

        List<String> args = new ArrayList<>();
        for (Integer i = 0; i < argsString.length(); i++) {
            char c = argsString.charAt(i);

            if ((c == '"' || c == '\'') && c != ignoreTrimChar) {
                ignoreTrimChar = c == '"' ? '\'' : '"';
                
                // toggle trim functionallity
                trim = !trim;
                
                // reset ignore tim char marker if set trim to true
                if(trim) {
                    ignoreTrimChar = '#';
                }
            } else {
                if (c == ' ' && trim) {
                    args.add(argBuilder.toString());
                    argBuilder = new StringBuilder();
                } else {
                    argBuilder.append(c);
                }
            }
        }
        
        args.add(argBuilder.toString());
        
        return args.toArray(new String[args.size()]);
    }

    private static Integer getIndexOfCommandEnd(final String stdin) {
        Integer pos = stdin.indexOf(" ");

        if (pos < 0) {
            pos = stdin.length();
        }

        return pos;
    }

    private CommandStringParser() {

    }
}
