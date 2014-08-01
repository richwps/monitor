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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.DispatcherFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.MetricFactoryMap;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.QosMetric;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;

/**
 * Builder to create a RestInterface instance with all necessary dependencies.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class RestInterfaceBuilder {

    /**
     * PresentateStrategy instance.
     */
    private PresentateStrategy strategy;

    /**
     * MonitorControl instance.
     */
    private MonitorControl monitorControl;

    /**
     * Converter Map.
     */
    private MetricFactoryMap converterMap;

    /**
     * Port for Jetty
     */
    private Integer port;

    /**
     * Creates a new RestInterfaceBuilder instance.
     */
    public RestInterfaceBuilder() {
        this.converterMap = new MetricFactoryMap();
        this.port = 4567;
    }

    /**
     * Sets the converterMap instance.
     *
     * @param converterMap Map instance
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder withConverterMap(MetricFactoryMap converterMap) {
        this.converterMap = Validate.notNull(converterMap, "converterMap");

        return this;
    }

    public RestInterfaceBuilder withPort(Integer port) {
        if (port != null && port > 1000) {
            this.port = port;
        }

        return this;
    }

    /**
     * Sets the PresentateStrategy instance.
     *
     * @param strategy PresentateStrategy instance.
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder withStrategy(final PresentateStrategy strategy) {
        this.strategy = Validate.notNull(strategy, "strategy");

        return this;
    }

    /**
     * Sets the MonitorControl instance.
     *
     * @param monitorControl MonitorControl instance
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder withMonitorControl(final MonitorControl monitorControl) {
        this.monitorControl = Validate.notNull(monitorControl, "monitorControl");

        return this;
    }

    /**
     * Adds a Metric factory instance.
     *
     * @param abstractQosEntityName
     * @param metricFactory
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder addMetric(final String abstractQosEntityName, final Factory<QosMetric> metricFactory) {
        Validate.notNull(abstractQosEntityName, "abstractQosEntityName");
        Validate.notNull(metricFactory, "converterFactory");

        converterMap.add(abstractQosEntityName, metricFactory);

        return this;
    }

    /**
     * Build the {@link RestInterface}.
     *
     * @return {@link RestInterface} instance
     * @throws BuilderException
     */
    public RestInterface build() throws BuilderException {
        if (strategy == null || monitorControl == null) {
            throw new BuilderException("Strategy and MonitorControl instances must be set.");
        }

        DispatcherFactory dispatchFactory = new DispatcherFactory(converterMap);
        RestInterface rest = new RestInterface(strategy, monitorControl, dispatchFactory);

        rest.setPort(port);

        return rest;
    }
}
