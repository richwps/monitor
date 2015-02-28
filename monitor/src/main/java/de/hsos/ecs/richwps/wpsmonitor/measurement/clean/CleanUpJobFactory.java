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
package de.hsos.ecs.richwps.wpsmonitor.measurement.clean;

import de.hsos.ecs.richwps.wpsmonitor.Application;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Calendar;
import java.util.Date;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Factory for the CleanUp-Job.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public final class CleanUpJobFactory implements JobFactory {

    private final QosDaoFactory qosDaoFactory;
    private Integer olderAs;
    private Date olderAsDate;

    public CleanUpJobFactory(final QosDaoFactory qosDaoFactory, final Integer olderAs) {
        this.qosDaoFactory = Validate.notNull(qosDaoFactory, "qosDaoFactory");
        setOlderAs(olderAs);
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Job newJobInstance = null;

        try {
            newJobInstance = new CleanUpJob(qosDaoFactory.create(), olderAsDate);
        } catch (CreateException ex) {
            Application.exitApplicationImmediately(
                    new AssertionError("Can't create the qosDao-dependencie. "
                            + "This is necessary for the CleanUpJob. Execution aborted.", ex)
            );
        }

        return newJobInstance;
    }
    
    private Date computeOlderAsDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -olderAs);
        
        return cal.getTime();
    }

    public void setOlderAs(Integer olderAs) {
        if (olderAs != null && olderAs > 0) {
            this.olderAs = olderAs;
            
            this.olderAsDate = computeOlderAsDate();
        }
    }
}
