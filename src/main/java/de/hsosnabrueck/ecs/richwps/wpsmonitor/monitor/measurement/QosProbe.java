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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.measurement;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.client.WpsResponse;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;

/**
 * QosProbes are designed to be executed by a measurejob. A QosProbe
 * implementation ca do calculations or other measurement operationes. The
 * result should be saved in a entity which extends the AbstractQosEntity-class.
 * 
 * The Job-instance extract and persist the measured datas.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public abstract class QosProbe {

    private AbstractQosEntity measuredData;

    public AbstractQosEntity getMeasuredData() {
        return measuredData;
    }

    public void setMeasuredData(AbstractQosEntity measuredData) {
        this.measuredData = measuredData;
    }

    public abstract void execute(WpsRequest request, WpsResponse response);
}
