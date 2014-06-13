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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.JsonPresentateStrategy;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.restful.PresentateStrategy;
import java.util.ArrayList;
import java.util.List;
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
    private Dispatcher dispatch;
    private List<AbstractQosEntity> data;
    
    public ConverterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        EntityDisassembler entityDisassembler = new EntityDisassembler();

        entityDisassembler.addConverter(new MyConverterFactory(), "exampleMeasurement");
        
        dispatch = new Dispatcher(entityDisassembler);
        data = new ArrayList<AbstractQosEntity>();
        
        data.add(new ExampleQos("muh", 22));
        data.add(new ExampleQos("buh", 11));
        data.add(new ExampleQos("valuhu", 9));
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testDispatchResultIsNotNull() {
        Assert.assertTrue("check if the dispatch result is not null", dispatch.dispatch(data) != null);
    }
    
    @Test
    public void testDispatchResult() {
        Assert.assertTrue("check if the dispatch result is a List and the List is not empty", 
                dispatch.dispatch(data).getClass().equals(ArrayList.class) &&
                        dispatch.dispatch(data).size() > 0);
    }
    
    @Test
    public void testResultIsValid() {
        Assert.assertTrue(((MyPresentate)dispatch.dispatch(data).get(0).get(0)).getSum() == 42);
    }
    
    @Test
    public void testJsonStrategy() {
        
        Boolean failed = false;
        String message = null;
        
        try {
            PresentateStrategy json = new JsonPresentateStrategy(new Gson());
            json.toPresentate(dispatch.dispatch(data));
        } catch(Exception e) {
            failed = true;
            message = e.getMessage();
        }
        
        Assert.assertTrue(message, !failed);
    }
}
