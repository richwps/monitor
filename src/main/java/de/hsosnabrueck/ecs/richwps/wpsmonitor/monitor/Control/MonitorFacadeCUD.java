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

import java.net.URI;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public interface MonitorFacadeCUD {
    public Boolean createWps(final String wpdIdentifier, final URI uri);
    public Boolean createProcess(final String wpsIdentifier, final String processIdentifier);
    public TriggerKey createTrigger(final String wpdIdentifier, final String processIdentifier, final TriggerConfig config);
    public void setTestRequest(final String wpdIdentifier, final String processIdentifier, final String testRequest);
    public Boolean updateWpsUri(final String wpdIdentifier, final URI newUri);
    public Boolean deleteWps(final String wpsIdentifier);
    public Boolean deleteProcess(final String wpdIdentifier, final String processIdentifier);
    public Boolean deleteTrigger(final TriggerKey triggerKey);
}
