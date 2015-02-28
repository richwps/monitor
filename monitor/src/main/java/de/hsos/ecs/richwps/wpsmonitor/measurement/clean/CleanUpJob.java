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

import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A Job implementation which deletes the old measured data.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CleanUpJob implements Job {

    private static final Logger LOG = LogManager.getLogger();

    private final QosDataAccess qosDao;
    private final Date olderAs;

    /**
     * Creates a CleanUpJob instance which will call the deleteAllOlderAss method
     * of the monitor which are older as oderAs.
     * 
     * @param qosDao QosDataAccess instance
     * @param olderAs Date instance
     */
    public CleanUpJob(final QosDataAccess qosDao, final Date olderAs) {
        this.qosDao = Validate.notNull(qosDao, "qosDao");
        this.olderAs = Validate.notNull(olderAs, "olderAs");
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            LOG.info("Cleanup Job: deleteAllOlderAs {}", olderAs);
            qosDao.deleteAllOlderAs(olderAs);
        } finally {
            qosDao.close();
        }
    }

}
