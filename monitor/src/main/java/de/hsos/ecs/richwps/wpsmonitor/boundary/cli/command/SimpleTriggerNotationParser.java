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

import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converter.ConverterException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.cli.command.converters.StringToDateConverter;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SimpleTriggerNotationParser {
    private final static Map<String, TriggerConfig.IntervalUnit> unitMap ;

    static {
        unitMap = new HashMap<>();
        
        unitMap.put("@second", TriggerConfig.IntervalUnit.SECOND);
        unitMap.put("@minute", TriggerConfig.IntervalUnit.MINUTE);
        unitMap.put("@hour", TriggerConfig.IntervalUnit.HOUR);
        unitMap.put("@day", TriggerConfig.IntervalUnit.DAY);
        unitMap.put("@month", TriggerConfig.IntervalUnit.MONTH);
        unitMap.put("@week", TriggerConfig.IntervalUnit.WEEK);
        unitMap.put("@year", TriggerConfig.IntervalUnit.YEAR);
    }
    
    public SimpleTriggerNotationParser() {
    }
    
    public TriggerConfig parse(final String notation) throws SimpleTriggerNotationException {
        final String[] splitted = notation.split(",");
        
        if(splitted == null || splitted.length != 3) {
            throw new SimpleTriggerNotationException("Missing Arguments.");
        }

        TriggerConfig.IntervalUnit intervalUnit = getIntervalUnit(splitted[0].trim());
        Integer interval = getInterval(splitted[0].trim());
        Date start = getDate(splitted[1].trim());
        Date end = getDate(splitted[2].trim());
        
        if(intervalUnit == null) {
            throw new SimpleTriggerNotationException("Uknow interval unit. Must be @second|minute|hour|day|month|week|year");
        }
        
        return new TriggerConfig(start, end, interval, intervalUnit);
    }
    
    private TriggerConfig.IntervalUnit getIntervalUnit(final String check) throws SimpleTriggerNotationException {
        final Integer pos = check.indexOf("(");
        
        if(pos < 0 ) {
            throw new SimpleTriggerNotationException();
        }
        
        final String unit = check.substring(0, pos);
        
        if(!unit.startsWith("@")) {
            throw new SimpleTriggerNotationException("Must start with @.");
        }

        return unitMap.get(unit.toLowerCase());
    }
    
    private Integer getInterval(final String str) throws SimpleTriggerNotationException {
        final Integer startPos = str.indexOf("(");
        final Integer endPos = str.indexOf(")");
        
        if(startPos < 0 || endPos < 0) {
            throw new SimpleTriggerNotationException();
        }
        
        final String iStr = str.substring(str.indexOf("(") + 1, endPos);
        
        try {
            return Integer.parseInt(iStr);
        } catch (NumberFormatException ex) {
            throw new SimpleTriggerNotationException(ex);
        }
    }
    
    private Date getDate(final String str) throws SimpleTriggerNotationException {
        if(str.equals("now")) {
            return new Date();
        }
        
        try {
            return new StringToDateConverter().convert(str);
        } catch (ConverterException ex) {
            throw new SimpleTriggerNotationException(ex);
        }
    }
    
}
