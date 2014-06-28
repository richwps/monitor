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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JobFactoryService implements JobFactory {

    private final Map<Class<? extends Job>, JobFactory> classFactoryMap;

    private final static Logger log = LogManager.getLogger();

    public JobFactoryService() {
        this.classFactoryMap = new HashMap<Class<? extends Job>, JobFactory>();
    }

    public boolean containsKey(final Class<? extends Job> o) {
        return classFactoryMap.containsKey(o);
    }

    public JobFactory put(Class<? extends Job> k, JobFactory v) {
        return classFactoryMap.put(k, v);
    }

    public JobFactory get(Class<? extends Job> o) {
        return classFactoryMap.get(o);
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Class<? extends Job> jobClass = bundle
                .getJobDetail()
                .getJobClass();

        Job newJobInstance;

        if (classFactoryMap.containsKey(jobClass)) {
            newJobInstance = classFactoryMap.get(jobClass)
                    .newJob(bundle, scheduler);
        } else {
            newJobInstance = new SimpleJobFactory()
                    .newJob(bundle, scheduler);
        }

        return newJobInstance;
    }

}
