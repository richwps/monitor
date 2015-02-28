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
package de.hsos.ecs.richwps.wpsmonitor.qos.response;

import de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseMetric;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class ResponseMetricTest {

    private ResponseMetric instance;

    public ResponseMetricTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new ResponseMetric();
    }

    @After
    public void tearDown() {
    }
    
    /**
     * Test of calculate method, of class ResponseMetric.
     */
    @Test
    public void testMedianCalculationEvenCase() {
        Double result, expected;
        result = computeMedian(new Integer[]{7, 100, 9, 1});
        expected = 8.;

        Assert.assertTrue(result.equals(expected));
    }
    
    @Test
    public void testMedianCalculationUnevenCase() {
        Double result, expected;
        result = computeMedian(new Integer[]{3, 7, 1, 9, 100});
        expected = 7.;

        Assert.assertTrue(result.equals(expected));
    }

    private Double computeMedian(Integer[] values) {
        Double result = null;

        try {
            Method method = instance.getClass().getDeclaredMethod("computeMedian", Integer[].class);
            method.setAccessible(true);

            Object invoke = method.invoke(instance, (Object) values);

            result = (Double) invoke;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            fail(ex.getMessage());
        }

        return result;
    }

}
