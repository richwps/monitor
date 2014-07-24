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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ProbeService can store multiple Factory&lt;QosProbe> factories which can
 * create various types of objects which extend the {@link QosProbe} interface.
 * The idea behind this service is, that everyone can register its own QosProbe
 * instance which is executed at each measurement process.
 *
 * However, jobs can be threads. To minimize side effects, the probeservice uses
 * the factories to create a list of new QosProbe instances.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ProbeService {

    private final List<Factory<QosProbe>> probeFactories;
    private static final Logger LOG = LogManager.getLogger();

    public ProbeService() {
        probeFactories = new ArrayList<Factory<QosProbe>>();
    }

    /**
     * Adds a probe factory.
     *
     * @param probeFactory Factory&lt;QosProbe> instance
     * @return this for method chaining
     */
    public ProbeService addProbe(final Factory<QosProbe> probeFactory) {
        probeFactories.add(Validate.notNull(probeFactory, "probeFactory"));

        return this;
    }

    /**
     * Removes a probe factory.
     *
     * @param probeFactory Probe factory to rmove
     * @return True if sucessfully removed
     */
    public boolean removeProbeClass(final Factory<QosProbe> probeFactory) {
        return probeFactories.remove(probeFactory);
    }

    /**
     * Creates a new list of {@link QosProbes}.
     *
     * @return list of QosProbes
     */
    public List<QosProbe> buildProbes() {
        List<QosProbe> factoredObjects = new ArrayList<QosProbe>();

        for (Factory<QosProbe> probeFactory : probeFactories) {
            try {
                factoredObjects.add(probeFactory.create());
            } catch (CreateException ex) {
                LOG.warn("Can't create QosProb-Instancee.", ex);
            }
        }

        return factoredObjects;
    }
}
