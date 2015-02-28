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
package de.hsos.ecs.richwps.wpsmonitor.boundary.restful.metric;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.PresentateStrategy;
import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategies.JsonPresentateStrategy;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the metrics.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MetricTest {

    private static DispatcherFactory dispatchFactory;

    @BeforeClass
    public static void setUpClass() {
        MetricFactoryMap converterMap = new MetricFactoryMap();
        converterMap.add("exampleMeasurement", new MyMetricFactory());

        dispatchFactory = new DispatcherFactory(converterMap);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public static void main(String[] args) {
        MetricTest.setUpClass();
        MetricTest t = new MetricTest();

        t.setUp();
        PresentateStrategy json = new JsonPresentateStrategy();
        Map<String, Object> dispatchToMetric = t.getDispatch().dispatchToMetric(t.getData());

        System.out.println(json.presentate(dispatchToMetric));
    }

    private EntityDispatcher dispatch;
    private List<MeasuredDataEntity> data;

    public MetricTest() {
    }

    @Before
    public void setUp() {

        dispatch = dispatchFactory.create();
        data = new ArrayList<>();

        List<AbstractQosEntity> qosEntities = new ArrayList<>();

        qosEntities.add(new ExampleQos("muh", 22));
        qosEntities.add(new ExampleQos("buh", 11));
        qosEntities.add(new ExampleQos("valuhu", 9));
        data.add(new MeasuredDataEntity(qosEntities));

        qosEntities = new ArrayList<>();

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
                        + "    \"presentate\": {\n"
                        + "      \"value\": 84,\n"
                        + "      \"measureUnit\": \"BYTE\"\n"
                        + "    }\n"
                        + "  }\n"
                        + "}")
        );
    }

    public static DispatcherFactory getDispatchFactory() {
        return dispatchFactory;
    }

    public static void setDispatchFactory(DispatcherFactory dispatchFactory) {
        MetricTest.dispatchFactory = dispatchFactory;
    }

    public EntityDispatcher getDispatch() {
        return dispatch;
    }

    public void setDispatch(EntityDispatcher dispatch) {
        this.dispatch = dispatch;
    }

    public List<MeasuredDataEntity> getData() {
        return data;
    }

    public void setData(List<MeasuredDataEntity> data) {
        this.data = data;
    }

}
