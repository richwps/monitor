/*
 * Copyright 2014 FloH.
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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.MetricFactoryMap;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.EntityDispatcher;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.metric.DispatcherFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.strategies.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.restful.PresentateStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author FloH
 */
public class ConverterTest {

    private EntityDispatcher dispatch;
    private List<MeasuredDataEntity> data;

    private static DispatcherFactory dispatchFactory;

    public ConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        MetricFactoryMap converterMap = new MetricFactoryMap();
        converterMap.add("exampleMeasurement", new MyConverterFactory());

        dispatchFactory = new DispatcherFactory(converterMap);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        dispatch = dispatchFactory.create();
        data = new ArrayList<MeasuredDataEntity>();

        List<AbstractQosEntity> qosEntities = new ArrayList<AbstractQosEntity>();

        qosEntities.add(new ExampleQos("muh", 22));
        qosEntities.add(new ExampleQos("buh", 11));
        qosEntities.add(new ExampleQos("valuhu", 9));
        data.add(new MeasuredDataEntity(qosEntities));

        qosEntities = new ArrayList<AbstractQosEntity>();

        qosEntities.add(new ExampleQos("muh", 22));
        qosEntities.add(new ExampleQos("buh", 11));
        qosEntities.add(new ExampleQos("valuhu", 9));

        data.add(new MeasuredDataEntity(qosEntities));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDispatchResultIsNotNull() {
        Assert.assertTrue("check if the dispatch result is not null", dispatch.dispatchToMetric(data) != null);
    }

    @Test
    public void testDispatchResult() {
        Assert.assertTrue("check if the dispatch result is a List and the List is not empty",
                dispatch.dispatchToMetric(data).getClass().equals(HashMap.class)
                && dispatch.dispatchToMetric(data).size() > 0);
    }

    @Test
    public void testJsonStrategyNoException() {

        Boolean failed = false;
        String message = null;

        try {
            PresentateStrategy json = new JsonPresentateStrategy();
            json.presentate(dispatch.dispatchToMetric(data));
        } catch (Exception e) {
            failed = true;
            message = e.getMessage();
        }

        Assert.assertTrue(message, !failed);
    }

    @Test
    public void testJsonStrategyOutput() {
        Assert.assertTrue(
                new JsonPresentateStrategy().presentate(dispatch.dispatchToMetric(data))
                .equals("{\n"
                        + "  \"MyTestConverter\": {\n"
                        + "    \"sum\": 84\n"
                        + "  }\n"
                        + "}")
        );
    }

    public static void main(String[] args) {
        ConverterTest.setUpClass();
        ConverterTest t = new ConverterTest();

        t.setUp();
        PresentateStrategy json = new JsonPresentateStrategy();
        System.out.println(json.presentate(t.dispatch.dispatchToMetric(t.data)));
    }
}
