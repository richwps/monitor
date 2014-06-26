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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class CleanUpJob implements Job {
    private final QosDataAccess qosDao;
    private final Date olderAs;
    
    public CleanUpJob(final QosDataAccess qosDao, final Date olderAs) {
        this.qosDao = Param.notNull(qosDao, "qosDao");
        this.olderAs = Param.notNull(olderAs, "olderAs");
    }
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        qosDao.deleteAllOlderAs(olderAs);
    }
    
}
