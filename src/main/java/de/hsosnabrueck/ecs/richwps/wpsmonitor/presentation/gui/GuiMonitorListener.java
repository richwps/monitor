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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsMonitorGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class GuiMonitorListener implements MonitorEventListener {
    private WpsMonitorGui gui;
    
    public GuiMonitorListener(final WpsMonitorGui gui) {
        this.gui = Param.notNull(gui, "gui");
    }

    @Override
    public void execute(Object eventData) {
        
    }
    
}
