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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.annotation;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.Command;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.ConverterException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.StringConverter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CommandAnnotationProcessor {

    private final CommandLineParser parser;
    private final Map<Class, StringConverter> converterMap;

    public CommandAnnotationProcessor(final CommandLineParser parser, final Map<Class, StringConverter> converterMap) {
        this.parser = parser;
        this.converterMap = converterMap;
    }

    public void initCommand(final Command cmd) throws CommandAnnotationProcessorException {
        addOptions(cmd);
    }

    private void addOptions(final Command cmd) throws CommandAnnotationProcessorException {
        final List<Field> fields = getFields(cmd.getClass());

        for (final Field field : fields) {
            final CommandOption annotation = field.getAnnotation(CommandOption.class);

            if (annotation != null) {
                final String longOpt = annotation.longOptionName().equals("") ? null : annotation.longOptionName();

                Option option = new Option(
                        annotation.shortOptionName(),
                        longOpt,
                        annotation.hasArgument(),
                        annotation.description()
                );

                if (annotation.hasArgument()) {
                    option.setArgName(annotation.argumentName());
                } else {
                    if (!field.getType().equals(Boolean.class)) {
                        throw new CommandAnnotationProcessorException("If the Option has no argument, the field must be of type boolean.");
                    }
                }

                option.setOptionalArg(annotation.optionalArgument());

                cmd.addOption(option);
            }
        }
    }

    public void injectOptions(final Command cmd, final String[] args) throws CommandAnnotationProcessorException {
        try {
            final Options opt = cmd.getOptions();
            final CommandLine cmdLine = parser.parse(opt, args);

            final List<Field> fields = getFields(cmd.getClass());

            for (final Field f : fields) {
                final CommandOption annotation = f.getAnnotation(CommandOption.class);

                if (annotation != null) {
                    final String optionValue = cmdLine.getOptionValue(annotation.shortOptionName());

                    if (annotation.required() && optionValue == null) {
                        throw new CommandAnnotationProcessorException("Option "
                                + annotation.longOptionName()
                                + " is required."
                        );
                    }

                    Object toInject = null;

                    if (f.getType() == Boolean.class) {
                        toInject = cmdLine.hasOption(annotation.shortOptionName());
                    } else if (f.getType() != String.class) {
                        if (optionValue != null && !optionValue.isEmpty()) {
                            StringConverter converter = converterMap.get(f.getType());

                            if (converter == null) {
                                throw new CommandAnnotationProcessorException("Can't convert the given string to "
                                        + f.getType().toString()
                                );
                            }
                            
                            toInject = converter.convert(optionValue);
                        }
                    } else {
                        toInject = optionValue;
                    }

                    f.setAccessible(true);
                    f.set(cmd, toInject);
                }
            }
        } catch (ParseException | ConverterException | IllegalArgumentException | IllegalAccessException ex) {
            throw new CommandAnnotationProcessorException("Parse Exception.", ex);
        }
    }

    private List<Field> getFields(final Class<?> type) {
        List<Field> fields = new ArrayList<>();

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields.addAll(getFields(type.getSuperclass()));
        }

        return fields;
    }
}
