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
package de.hsos.ecs.richwps.wpsmonitor.control.scheduler;

import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.List;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Creates a new Quartz {@link Scheduler} instance.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class SchedulerFactory implements Factory<Scheduler> {

    private final List<JobListener> jobListeners;
    private final JobFactoryService jobFactoryService;

    /**
     * Constructor.
     *
     * @param jobFactoryService {@link JobFactoryService} instance
     * @param jobListeners List of {@link JobListener}s
     */
    public SchedulerFactory(final JobFactoryService jobFactoryService, final List<JobListener> jobListeners) {
        this.jobListeners = jobListeners;
        this.jobFactoryService = Validate.notNull(jobFactoryService, "jobFactoryService");
    }

    @Override
    public Scheduler create() throws CreateException {
        try {
            Scheduler result = StdSchedulerFactory.getDefaultScheduler();

            result.setJobFactory(jobFactoryService);

            for (JobListener listener : jobListeners) {
                result.getListenerManager()
                        .addJobListener(listener);
            }

            return result;
        } catch (SchedulerException ex) {
            throw new CreateException(ex);
        }
    }
}
