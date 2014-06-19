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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.structures;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.EnumMap;
import org.quartz.DateBuilder;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class IntervalComboBoxItem {
    private DateBuilder.IntervalUnit dateKey;
    private static EnumMap<DateBuilder.IntervalUnit, String> enumStringMap;

    public IntervalComboBoxItem(DateBuilder.IntervalUnit dateKey) {
        this.dateKey = Param.notNull(dateKey, "dateKey");
        
        if(enumStringMap == null) {
            initMap();
        }
    }
    
    private void initMap() {
        enumStringMap = new EnumMap<DateBuilder.IntervalUnit, String>(DateBuilder.IntervalUnit.class);
        
        String[] fill = new String[] {
            "Millisecond",
            "Second",
            "Minute",
            "Hour",
            "Day",
            "Week",
            "Month",
            "Year"
        };
        
        DateBuilder.IntervalUnit[] dateValues = DateBuilder.IntervalUnit.values();
        
        // starting at 1 to skip millisecond entry!
        for(int i = 1; i < fill.length && i < dateValues.length; i++) {
            enumStringMap.put(dateValues[i], fill[i]);
        }
        
    }

    public String getLabel() {
        return enumStringMap.get(dateKey);
    }

    public DateBuilder.IntervalUnit getFormatKey() {
        return dateKey;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
