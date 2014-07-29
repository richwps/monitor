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
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Only one {@link JobFactory} can be registred in Quartz at the same time.
 * However the WpsMonitor uses factories to inject all necessary dependencies
 * into the new objects. This approach is also used at new Job instances.
 *
 * So the JobFactoryService, which implements the JobFactory interface, stores
 * other JobFactory instances and delegates the newJob-calls to the right
 * registred JobFactory instance. The right {@link JobFactory} instance is
 * identified by the Job.class Class.
 *
 * If no JobFactory is registred for a specific job, then
 * {@link SimpleJobFactory} is used. {@link SimpleJobFactory} is the default
 * behavior of Quartz.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class JobFactoryService implements JobFactory {

    private final Map<Class<? extends Job>, JobFactory> classFactoryMap;

    public JobFactoryService() {
        this.classFactoryMap = new HashMap<>();
    }

    /**
     * Checks if a {@link JobFactory} instance is already registered.
     *
     * @param o Class instance
     * @return true if already registered, otherwise false
     */
    public boolean containsKey(final Class<? extends Job> o) {
        return classFactoryMap.containsKey(o);
    }

    /**
     * Registers a new {@link JobFactory} instance for the given Job Class.
     *
     * @param jobClass Class instance
     * @param jobFactory JobFactory instance
     * @return The registred JobFactory
     */
    public JobFactory register(Class<? extends Job> jobClass, JobFactory jobFactory) {
        return classFactoryMap.put(jobClass, jobFactory);
    }

    /**
     * Get a JobFactory.
     *
     * @param o Class instance
     * @return JobFactory instance if registered, otherwise null
     */
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
