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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.clean;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class CleanUpJobFactory implements JobFactory {

    private final QosDaoFactory qosDaoFactory;
    private Date olderAs;

    private final static Logger log = LogManager.getLogger();

    public CleanUpJobFactory(final QosDaoFactory qosDaoFactory, final Date olderAs) {
        this.qosDaoFactory = Param.notNull(qosDaoFactory, "qosDaoFactory");
        setOlderAs(olderAs);
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job newJobInstance = null;

        try {
            newJobInstance = new CleanUpJob(qosDaoFactory.create(), olderAs);
        } catch (CreateException ex) {
            log.fatal(ex);
        }

        return newJobInstance;
    }

    public Date getOlderAs() {
        return olderAs;
    }

    public void setOlderAs(Date olderAs) {
        if (Param.notNull(olderAs, "olderAs").getTime() < new Date().getTime()) {
            this.olderAs = olderAs;
        }
    }
}
