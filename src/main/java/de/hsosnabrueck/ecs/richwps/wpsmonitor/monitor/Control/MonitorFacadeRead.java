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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Control;

import java.util.List;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface MonitorFacadeRead {
    public List<String> getWpsList();
    public List<String> getProcessesOfWps(final String identifier);
    public List<TriggerKey> getTriggers(final String wpsIdentifier, final String processIdentifier);
    public String getRequestString(final String wpsIdentifier, final String processIdentifier);
    public List<String> getMeasuredData(final String wpsIdentifier, final String processIdentifier);
    public TriggerConfig getTriggerConfig(final TriggerKey triggerKey);
}
