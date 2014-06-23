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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.util.List;
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
public class WpsProcessDaoTest {
    
    public WpsProcessDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of persist method, of class WpsProcessDao.
     */
    @Test
    public void testPersist() {
        System.out.println("persist");
        WpsProcessEntity e = null;
        WpsProcessDao instance = null;
        Boolean expResult = null;
        Boolean result = instance.persist(e);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class WpsProcessDao.
     */
    @Test
    public void testFind_Object() {
        System.out.println("find");
        Object primaryKey = null;
        WpsProcessDao instance = null;
        WpsProcessEntity expResult = null;
        WpsProcessEntity result = instance.find(primaryKey);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class WpsProcessDao.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Range range = null;
        WpsProcessDao instance = null;
        List<WpsProcessEntity> expResult = null;
        List<WpsProcessEntity> result = instance.get(range);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class WpsProcessDao.
     */
    @Test
    public void testFind_String_String() {
        System.out.println("find");
        String wpsIdentifier = "";
        String processIdentifier = "";
        WpsProcessDao instance = null;
        WpsProcessEntity expResult = null;
        WpsProcessEntity result = instance.find(wpsIdentifier, processIdentifier);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAll method, of class WpsProcessDao.
     */
    @Test
    public void testGetAll() {
        System.out.println("getAll");
        String wpsIdentifier = "";
        WpsProcessDao instance = null;
        List<WpsProcessEntity> expResult = null;
        List<WpsProcessEntity> result = instance.getAll(wpsIdentifier);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteProcessesFromWps method, of class WpsProcessDao.
     */
    @Test
    public void testDeleteProcessesFromWps() {
        System.out.println("deleteProcessesFromWps");
        String wpsIdentifier = "";
        WpsProcessDao instance = null;
        instance.deleteProcessesFromWps(wpsIdentifier);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
