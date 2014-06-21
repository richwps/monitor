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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class PairTest {
    private Pair<String, Integer> instance;
    
    public PairTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new Pair<String, Integer>("Zahl", 5);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getLeft method, of class Pair.
     */
    @Test
    public void testGetLeft() {
        System.out.println("getLeft");

        Object expResult = "Zahl";
        Object result = instance.getLeft();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getRight method, of class Pair.
     */
    @Test
    public void testGetRight() {
        System.out.println("getRight");

        Object expResult = 5;
        Object result = instance.getRight();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Pair.
     */
    @Test
    public void testToString() {
        System.out.println("toString");

        String expResult = "Zahl.5";
        String result = instance.toString();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Pair.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        assertEquals(new Pair<String, Integer>("Zahl", 5), instance);
    }
    
}