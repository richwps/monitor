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

import org.quartz.DateBuilder;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class IntervalComboBoxItem {
    private String label;
    private DateBuilder.IntervalUnit dateKey;

    public IntervalComboBoxItem(String label, DateBuilder.IntervalUnit dateKey) {
        this.label = label;
        this.dateKey = dateKey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DateBuilder.IntervalUnit getFormatKey() {
        return dateKey;
    }

    public void setDateKey(DateBuilder.IntervalUnit dateKey) {
        this.dateKey = dateKey;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
