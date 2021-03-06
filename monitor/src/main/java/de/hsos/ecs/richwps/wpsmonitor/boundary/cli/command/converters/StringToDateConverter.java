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
package de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters;

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.ConverterException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.StringConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class StringToDateConverter extends StringConverter {

    @Override
    public Date convert(final String toConvert) throws ConverterException {
        Date result = null;
        
        try {
            result = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(toConvert);
        } catch (ParseException ex) {
            
        }
        try {
            result = new SimpleDateFormat("dd.MM.yyyy").parse(toConvert);
        } catch (ParseException ex) {
            
        }

        if(result == null) {
            throw new ConverterException("Date");
        }
        
        return result;
    }
    
}
