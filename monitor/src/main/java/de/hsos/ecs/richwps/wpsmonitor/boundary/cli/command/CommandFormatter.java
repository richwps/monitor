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

import org.apache.commons.cli.Option;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CommandFormatter {
    private final Command cmd;

    public CommandFormatter(final Command cmd) {
        this.cmd = cmd;
    }
    
    
    /**
     * Representates the command object as text with all options. The returned
     * string is formatted for console output.
     *
     * @return Command representation.
     */
    @Override
    public String toString() {
        final String desc_sep = "\t\t";
        final String desc_sep_break = "\n" + desc_sep;
        final String desc_opt_sep_break = desc_sep_break + "\t\t\t";

        final Integer maxStrL = 100;

        final String cmdDescription = trimByWhitespace(cmd.getDescription(), desc_sep_break, maxStrL);

        StringBuilder strBuilder = new StringBuilder(cmd.getCommandName())
                .append(desc_sep)
                .append(cmdDescription);

        if (cmd.getOptions().getOptions().size() > 0) {
            strBuilder.append("\n\n")
                    .append(desc_sep)
                    .append("Options:")
                    .append(desc_sep);
        }

        for (final Object o : cmd.getOptions().getOptions()) {
            if (o instanceof Option) {
                final Option opt = (Option) o;
                final String optName = opt.getOpt();

                strBuilder.append("\n")
                        .append(desc_sep)
                        .append(optList(opt));

                if (opt.hasArg()) {
                    strBuilder.append(decorateArg(opt));
                }

                final String optDescription = trimByWhitespace(
                        opt.getDescription(),
                        desc_opt_sep_break,
                        maxStrL - optName.length() - 5
                );

                strBuilder.append(" : ")
                        .append(optDescription);
            }
        }

        return strBuilder.append("\n").toString();
    }

    private String optList(final Option opt) {
        final String lOptName = opt.getLongOpt();
        final StringBuilder strBuilder = new StringBuilder();
        
        strBuilder.append(decorateOptionName(opt.getOpt()));
        
        if(lOptName != null) {
            strBuilder.append(", ")
                    .append(decorateOptionName(opt.getLongOpt()));
        }
        
        return strBuilder.toString();
    }

    private String decorateOptionName(final String optName) {
        return "--" + optName;
    }

    private String decorateArg(final Option opt) {
        final StringBuilder strBuilder = new StringBuilder();
        
        if (opt.hasArg()) {
            if (opt.hasOptionalArg()) {
                strBuilder.append("[");
            }

            strBuilder.append("=")
                    .append("<")
                    .append(opt.getArgName())
                    .append(">");

            if (opt.hasOptionalArg()) {
                strBuilder.append("]");
            }
        }
        
        return strBuilder.toString();
    }

    private String trimByWhitespace(final String toCut, final String sep, final Integer maxStringLength) {
        if (toCut.length() > maxStringLength) {
            int lastIndexOf = toCut.lastIndexOf(" ", maxStringLength);

            if (lastIndexOf > 0) {
                String partOne = toCut.substring(0, lastIndexOf);
                String partTwo = toCut.substring(lastIndexOf + 1, toCut.length());

                partTwo = trimByWhitespace(partTwo, sep, maxStringLength);

                return partOne + sep + partTwo;
            }
        }

        return toCut;
    }
}
