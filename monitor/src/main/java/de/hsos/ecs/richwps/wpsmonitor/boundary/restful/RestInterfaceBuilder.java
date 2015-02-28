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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.PresentateStrategy;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.DispatcherFactory;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.MetricFactoryMap;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric.QosMetric;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControlService;
import de.hsos.ecs.richwps.wpsmonitor.creation.Factory;
import de.hsos.ecs.richwps.wpsmonitor.creation.BuilderException;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;

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
     * MonitorControlService instance.
     */
    private MonitorControlService monitorControl;

    /**
     * Metric factory Map.
     */
    private MetricFactoryMap metricFactoryMap;

    /**
     * Port for Jetty
     */
    private Integer port;

    /**
     * Creates a new RestInterfaceBuilder instance.
     */
    public RestInterfaceBuilder() {
        this.metricFactoryMap = new MetricFactoryMap();
        this.port = 4567;
    }

    /**
     * Sets the converterMap instance.
     *
     * @param converterMap Map instance
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder withConverterMap(MetricFactoryMap converterMap) {
        this.metricFactoryMap = Validate.notNull(converterMap, "converterMap");

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
     * Sets the MonitorControlService instance.
     *
     * @param monitorControl MonitorControlService instance
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder withMonitorControl(final MonitorControlService monitorControl) {
        this.monitorControl = Validate.notNull(monitorControl, "monitorControl");

        return this;
    }

    /**
     * Adds a Metric factory instance to an AbstractQosEntity instance.
     *
     * @param abstractQosEntityName Name of an AbstractQosEntity-Instance
     * @param metricFactory A factory instance of a concrete metric
     * @return RestInterfaceBuilder instance
     */
    public RestInterfaceBuilder addMetric(final String abstractQosEntityName, final Factory<QosMetric> metricFactory) {
        Validate.notNull(abstractQosEntityName, "abstractQosEntityName");
        Validate.notNull(metricFactory, "converterFactory");

        metricFactoryMap.add(abstractQosEntityName, metricFactory);

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

        DispatcherFactory dispatchFactory = new DispatcherFactory(metricFactoryMap);
        RestInterface rest = new RestInterface(strategy, monitorControl, dispatchFactory);

        rest.setPort(port);

        return rest;
    }
}
