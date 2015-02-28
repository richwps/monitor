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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils;

import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.EnumMap;
import java.util.Map;

/**
 * A ComboBox item for the interval type selection.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class IntervalComboBoxItem {

    private static final Map<TriggerConfig.IntervalUnit, String> INTERVAL_TYPE;

    static {
        INTERVAL_TYPE = new EnumMap<>(TriggerConfig.IntervalUnit.class);

        String[] fill = new String[]{
            "Millisecond",
            "Second",
            "Minute",
            "Hour",
            "Day",
            "Week",
            "Month",
            "Year"
        };

        TriggerConfig.IntervalUnit[] dateValues = TriggerConfig.IntervalUnit.values();

        for (int i = 0; i < fill.length && i < dateValues.length; i++) {
            INTERVAL_TYPE.put(dateValues[i], fill[i]);
        }
    }
    private final TriggerConfig.IntervalUnit dateKey;

    /**
     * Creates an interval type item that matches the given dataKey.
     *
     * @param dateKey TriggerConfig.IntervalUnit instance
     */
    public IntervalComboBoxItem(TriggerConfig.IntervalUnit dateKey) {
        this.dateKey = Validate.notNull(dateKey, "dateKey");
    }

    public String getLabel() {
        return INTERVAL_TYPE.get(dateKey);
    }

    public TriggerConfig.IntervalUnit getFormatKey() {
        return dateKey;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
