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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsClientFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.MeasureJobListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement.ProbeService;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SchedulerFactory implements Factory<Scheduler> {

    private ProbeService probeService;
    private WpsProcessDaoFactory wpsProcessDaoFactory;
    private QosDaoFactory qosDaoFactory;
    private WpsClientFactory wpsClientFactory;
    private MonitorEventHandler eventHandler;

    public SchedulerFactory(ProbeService probeService, WpsProcessDaoFactory wpsProcessDaoFactory,
            QosDaoFactory qosDaoFactory, WpsClientFactory wpsClientFactory, MonitorEventHandler eventHandler) {
        this.probeService = Param.notNull(probeService, "probeService");
        this.wpsProcessDaoFactory = Param.notNull(wpsProcessDaoFactory, "wpsProcessDaoFactory");
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");
        this.wpsClientFactory = Param.notNull(wpsClientFactory, "wpsClientFactory");
        this.eventHandler = Param.notNull(eventHandler, "eventHandler");
    }

    @Override
    public Scheduler create() throws CreateException {
        try {
            Scheduler result = StdSchedulerFactory.getDefaultScheduler();

            WpsProcessDataAccess wpsProcessDao = wpsProcessDaoFactory.create();

            JobFactory jobFactory = new MeasureJobFactory(probeService, wpsProcessDao, qosDaoFactory, wpsClientFactory);
            JobListener jobListener = new MeasureJobListener(wpsProcessDaoFactory, eventHandler);

            result.setJobFactory(jobFactory);
            result.getListenerManager()
                    .addJobListener(jobListener);

            return result;
        } catch (SchedulerException ex) {
            throw new CreateException(ex);
        }
    }
}
