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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
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
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class QosDaoTest {

    private QosDataAccess qosDao;
    private WpsDataAccess wpsDao;
    private WpsProcessDataAccess wpsProcessDao;

    private Long[] insertedIds;

    private static QosDaoFactory qosFactory;
    private static WpsDaoFactory wpsFactory;
    private static WpsProcessDaoFactory wpsProcessFactory;

    private static final Integer GENERATE_COUNT = 5;
    private static final String WPS_PROCESS_NAME = "testCaseScenario_SimpleBuffer";
    private static final String WPS_NAME = "testCaseScenario_localWps";
    
    private static Jpa jpa;

    public QosDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        jpa = new Jpa("de.hsosnabrueck.ecs.richwps_WPSMonitorTEST_pu");
        jpa.open();

        qosFactory = new QosDaoFactory(new QosDaoDefaultFactory(jpa));
        wpsFactory = new WpsDaoFactory(new WpsDaoDefaultFactory(jpa));
        wpsProcessFactory = new WpsProcessDaoFactory(new WpsProcessDaoDefaultFactory(jpa));
    }

    @AfterClass
    public static void tearDownClass() {
        jpa.close();
    }

    @Before
    public void setUp() {
        try {
            qosDao = qosFactory.create();
            qosDao.setAutoCommit(false);

            wpsDao = wpsFactory.create();
            wpsDao.setAutoCommit(false);

            wpsProcessDao = wpsProcessFactory.create();
            wpsProcessDao.setAutoCommit(false);

            insertedIds = new Long[GENERATE_COUNT];

            WpsEntity wps = genWps();
            WpsProcessEntity process = genProcess(wps);

            wpsDao.persist(wps);
            wpsProcessDao.persist(process);

            for (int i = 1; i <= GENERATE_COUNT; i++) {
                MeasuredDataEntity generatedData = genDataEn(process, -i);
                qosDao.persist(generatedData);
                insertedIds[i] = generatedData.getId();
            }
        } catch (CreateException ex) {
            fail("Can't create DAO!");
        }
    }

    @After
    public void tearDown() {
        qosDao.rollback();
        wpsDao.rollback();
        wpsProcessDao.rollback();
    }

    private MeasuredDataEntity genDataEn(WpsProcessEntity process, Integer addDay) {
        MeasuredDataEntity en = new MeasuredDataEntity();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, addDay);
        en.setCreateTime(new Date());
        en.setProcess(process);

        return en;
    }

    private WpsProcessEntity genProcess(WpsEntity wpsEnt) {
        return new WpsProcessEntity(WPS_PROCESS_NAME, wpsEnt);
    }

    private WpsEntity genWps() {
        try {
            return new WpsEntity(WPS_NAME, "http://localhost");
        } catch (MalformedURLException ex) {
            fail(ex.toString());
        } catch (URISyntaxException ex) {
            fail(ex.toString());
        }

        return null;
    }

    /**
     * Test of find method, of class QosDao.
     */
    @Test
    public void testFind() {
        if (insertedIds == null || insertedIds[0] == null) {
            fail("Inserted ID is null!");
        }

        MeasuredDataEntity find = qosDao.find(insertedIds[0]);

        Assert.assertTrue(find != null);
    }

    /**
     * Test of getByWps method, of class QosDao.
     */
    @Test
    public void testGetByWps() {
        System.out.println("getByWps");
        Range r = new Range(null, GENERATE_COUNT);

        List<MeasuredDataEntity> byWps = qosDao.getByWps(WPS_NAME, r);

        if (byWps == null) {
            fail("qosDao.getByWps returns null value");
        }

        Boolean assertWpsIdentical = true;
        for (MeasuredDataEntity e : byWps) {
            assertWpsIdentical = assertWpsIdentical && e.getProcess()
                    .getWps()
                    .getIdentifier()
                    .equals(WPS_NAME);
        }

        Assert.assertTrue(assertWpsIdentical && byWps.size() == GENERATE_COUNT);
    }

    /**
     * Test of getByProcess method, of class QosDao.
     */
    @Test
    public void testGetByProcess() {
        Range r = new Range(null, GENERATE_COUNT);
        List<MeasuredDataEntity> byProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME, r);

        Boolean assertWpsIdentical = true;
        for (MeasuredDataEntity e : byProcess) {
            assertWpsIdentical = assertWpsIdentical && e.getProcess()
                    .getIdentifier()
                    .equals(WPS_PROCESS_NAME);
        }

        Assert.assertTrue(assertWpsIdentical && byProcess.size() == GENERATE_COUNT);
    }

    /**
     * Test of getByProcess method, of class QosDao.
     */
    @Test
    public void testGetByProcess_NullValue() {
        Range r = new Range(null, GENERATE_COUNT);
        
        try {
            qosDao.getByProcess(null, WPS_PROCESS_NAME, r);
            fail("Exception was not thrown by wpsidentifier as null value");
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            qosDao.getByProcess(WPS_NAME, null, r);
            fail("Exception was not thrown by processidentifier as null value");
        } catch(IllegalArgumentException ex) {
            
        }
    }

    /**
     * Test of deleteByProcess method, of class QosDao.
     */
    @Test
    public void testDeleteByProcess() {
        List<MeasuredDataEntity> byProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);
        
        if(byProcess.isEmpty()) {
            fail("No MeasuredDataEntity");
        }
        
        Integer deleteByProcess = qosDao.deleteByProcess(WPS_NAME, WPS_PROCESS_NAME);
        byProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);
        
        Assert.assertTrue(deleteByProcess.equals(GENERATE_COUNT) && byProcess.isEmpty());
    }

    /**
     * Test of deleteByProcess method, of class QosDao.
     */
    @Test
    public void testDeleteByProcess_olderAs() {
        System.out.println("deleteByProcess");
        String wpsIdentifier = "";
        String processIdentifier = "";
        Date olderDate = null;
        QosDao instance = null;
        Integer expResult = null;
        Integer result = instance.deleteByProcess(wpsIdentifier, processIdentifier, olderDate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteAllOlderAs method, of class QosDao.
     */
    @Test
    public void testDeleteAllOlderAs() {
        System.out.println("deleteAllOlderAs");
        Date date = null;
        QosDao instance = null;
        Integer expResult = null;
        Integer result = instance.deleteAllOlderAs(date);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
