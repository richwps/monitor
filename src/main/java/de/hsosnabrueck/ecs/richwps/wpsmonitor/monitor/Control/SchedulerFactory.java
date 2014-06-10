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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SchedulerFactory {
    private ProbeService probeService;
    
    public SchedulerFactory(final ProbeService probeService) {
        this.probeService = Param.notNull(probeService, "probeService");
    }

    public Scheduler getConfiguredScheduler() throws SchedulerException {    
        Scheduler result = StdSchedulerFactory.getDefaultScheduler();
        
        WpsProcessDataAccess wpsProcessDao = WpsProcessDaoFactory.create();

        JobFactory jobFactory = new MeasureJobFactory(probeService, wpsProcessDao);
        JobListener jobListener = new MeasureJobListener(wpsProcessDao);

        result.setJobFactory(jobFactory);
        result.getListenerManager()
                .addJobListener(jobListener);

        return result;
    }
}
