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
import java.util.ArrayList;
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
                insertedIds[i - 1] = generatedData.getId();
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

        en.setCreateTime(cal.getTime());
        en.setProcess(process);

        TestQosEntity te = new TestQosEntity();
        te.setSomeValue(100000);

        en.add(te);

        return en;
    }

    private WpsProcessEntity genProcess(WpsEntity wpsEnt) {
        return new WpsProcessEntity(WPS_PROCESS_NAME, wpsEnt);
    }

    private WpsEntity genWps() {
        try {
            return new WpsEntity(WPS_NAME, "http://localhost");
        } catch (MalformedURLException | URISyntaxException ex) {
            fail(ex.toString());
        }

        return null;
    }

    /**
     * Test of find method, of class QosDao.
     */
    @Test
    public void testFind() {
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

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByProcess_NullValue_firstParam() {
        Range r = new Range(null, GENERATE_COUNT);
        qosDao.getByProcess(null, WPS_PROCESS_NAME, r);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByProcess_NullValue_secondParam() {
        Range r = new Range(null, GENERATE_COUNT);
        qosDao.getByProcess(WPS_NAME, null, r);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByProcess_NullValue_allParams() {
        Range r = new Range(null, GENERATE_COUNT);
        qosDao.getByProcess(null, null, r);
    }

    private List<Long> getListOfAbstractQosEntitiesId(List<MeasuredDataEntity> dataList) {
        List<Long> result = new ArrayList<>();

        for (MeasuredDataEntity m : dataList) {
            for (AbstractQosEntity q : m.getData()) {
                result.add(q.getId());
            }
        }

        return result;
    }

    private Boolean allQosDeleted(List<MeasuredDataEntity> data) {
        Boolean result = true;

        List<Long> listOfAbstractQosEntitiesId = getListOfAbstractQosEntitiesId(data);

        for (Long id : listOfAbstractQosEntitiesId) {
            if (qosDao.findAbstractQosEntityByid(id) != null) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Test of deleteByProcess method, of class QosDao.
     */
    @Test
    public void testDeleteByProcess() {
        List<MeasuredDataEntity> byProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);

        if (byProcess.isEmpty()) {
            fail("No MeasuredDataEntity");
        }

        Integer deleteByProcess = qosDao.deleteByProcess(WPS_NAME, WPS_PROCESS_NAME);
        List<MeasuredDataEntity> evaluateByProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);

        Boolean allQosDeleted = allQosDeleted(byProcess);
        Assert.assertTrue(allQosDeleted && deleteByProcess.equals(GENERATE_COUNT) && evaluateByProcess.isEmpty());
    }

    /**
     * Test of deleteByProcess method, of class QosDao.
     */
    @Test
    public void testDeleteByProcess_olderAs() {
        Integer substract = GENERATE_COUNT / 2;
        Date date = getDate(-substract);

        Integer deleteAllOlderAs = qosDao.deleteByProcess(WPS_NAME, WPS_PROCESS_NAME, date);
        List<MeasuredDataEntity> evaluateByProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);

        Integer expectedCount = (GENERATE_COUNT - substract);

        Assert.assertTrue(deleteAllOlderAs.equals(expectedCount) && evaluateByProcess.size() == (GENERATE_COUNT - expectedCount));
    }

    /**
     * Test of deleteAllOlderAs method, of class QosDao.
     */
    @Test
    public void testDeleteAllOlderAs() {
        Integer substract = GENERATE_COUNT / 2;

        Date date = getDate(-substract);
        Integer deleteAllOlderAs = qosDao.deleteAllOlderAs(date);
        List<MeasuredDataEntity> evaluateByProcess = qosDao.getByProcess(WPS_NAME, WPS_PROCESS_NAME);

        Integer expectedCount = (GENERATE_COUNT - substract);
        Assert.assertTrue(deleteAllOlderAs.equals(expectedCount) && evaluateByProcess.size() == (GENERATE_COUNT - expectedCount));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteAllOlderAs_NullValue() {
        qosDao.deleteAllOlderAs(null);
    }

    private Date getDate(Integer substract) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, substract);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
