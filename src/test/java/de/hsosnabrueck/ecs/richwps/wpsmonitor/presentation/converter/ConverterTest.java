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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.Factory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.strategies.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.PresentateStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
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
        Map<String, Factory<EntityConverter>> converterMap = new HashMap<String, Factory<EntityConverter>>();
        converterMap.put("exampleMeasurement", new MyConverterFactory());

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
        Assert.assertTrue("check if the dispatch result is not null", dispatch.dispatchToConverter(data) != null);
    }

    @Test
    public void testDispatchResult() {
        Assert.assertTrue("check if the dispatch result is a List and the List is not empty",
                dispatch.dispatchToConverter(data).getClass().equals(HashMap.class)
                && dispatch.dispatchToConverter(data).size() > 0);
    }

    @Test
    public void testJsonStrategyNoException() {

        Boolean failed = false;
        String message = null;

        try {
            PresentateStrategy json = new JsonPresentateStrategy();
            json.presentate(dispatch.dispatchToConverter(data));
        } catch (Exception e) {
            failed = true;
            message = e.getMessage();
        }

        Assert.assertTrue(message, !failed);
    }

    @Test
    public void testJsonStrategyOutput() {
        Assert.assertTrue(
                new JsonPresentateStrategy().presentate(dispatch.dispatchToConverter(data))
                .equals("{\"exampleMeasurement\":{\"sum\":84}}")
        );
    }

    public static void main(String[] args) {
        ConverterTest t = new ConverterTest();
        t.setUp();
        PresentateStrategy json = new JsonPresentateStrategy();
        System.out.println(json.presentate(t.dispatch.dispatchToConverter(t.data)));
    }
}
