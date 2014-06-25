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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.net.URI;
import java.util.List;
import org.quartz.TriggerKey;

/**
 * Facade to interact with the Monitor
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface MonitorControl {
    public Boolean createWps(final String wpsIdentifier, final URI uri);
    public Boolean createProcess(final String wpsIdentifier, final String processIdentifier);
    public TriggerKey saveTrigger(final String wpsIdentifier, final String processIdentifier, final TriggerConfig config);
    public Boolean setTestRequest(final String wpsIdentifier, final String processIdentifier, final String testRequest);
    public WpsEntity updateWps(final String oldWpsIdentifier, final String newWpsIdentifier, final URI newUri);
    public Boolean deleteWps(final String wpsIdentifier);
    public Boolean deleteProcess(final String wpsIdentifier, final String processIdentifier);
    public Boolean deleteTrigger(final TriggerKey triggerKey);
    public Boolean isPausedMonitoring(final String wpsIdentifier, final String processIdentifier);
    public void resumeMonitoring(final String wpsIdentifier, final String processIdentifier);
    public List<WpsEntity> getWpsList();
    public List<WpsProcessEntity> getProcessesOfWps(final String identifier);
    public List<TriggerConfig> getTriggers(final String wpsIdentifier, final String processIdentifier);
    public List<MeasuredDataEntity> getMeasuredData(final String wpsIdentifier, final String processIdentifier);
    public List<MeasuredDataEntity> getMeasuredData(String wpsIdentifier, String processIdentifier, Range range);
}
