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
package de.hsos.ecs.richwps.wpsmonitor.monitor.control;

import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorControl;
import de.hsos.ecs.richwps.wpsmonitor.control.builder.MonitorBuilder;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseFactory;
import de.hsos.ecs.richwps.wpsmonitor.util.BuilderException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Ignore
public class MonitorControlTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private MonitorControl mControl;
    private Monitor monitor;
    private WpsProcessDataAccess wpsProcessDao;
    private WpsDataAccess wpsDao;
    private QosDataAccess qosDao;

    public MonitorControlTest() {
    }

    @Before
    public void setUp() {
        try {
            monitor = new MonitorBuilder()
                    .withPersistenceUnit("de.hsosnabrueck.ecs.richwps_WPSMonitorTEST_pu")
                    .setupDefault()
                    .build();

            monitor.getProbeService()
                    .addProbe(new ResponseFactory());

            mControl = monitor.getMonitorControl();

            wpsProcessDao = monitor.getBuilderInstance()
                    .getWpsProcessDaoFactory().create();

            wpsDao = monitor.getBuilderInstance()
                    .getWpsDaoFactory().create();

            qosDao = monitor.getBuilderInstance()
                    .getQosDaoFactory().create();

            getStoredEntity();
        } catch (BuilderException ex) {
            fail("Can't build Monitor! " + ex.toString());
        } catch (CreateException ex) {
            fail(ex.toString());
        }
    }

    private WpsProcessEntity getStoredEntity() {
        WpsProcessEntity process = getUnstoredProcessEntity();

        wpsDao.persist(process.getWps());
        wpsProcessDao.persist(process);

        return process;
    }

    private WpsProcessEntity getUnstoredProcessEntity() {
        WpsProcessEntity process = null;
        try {
            WpsEntity wps = new WpsEntity(UUID.randomUUID().toString(), "http://example.com");
            process = new WpsProcessEntity(UUID.randomUUID().toString(), wps);
        } catch (MalformedURLException | URISyntaxException ex) {
            fail(ex.toString());
        }

        return process;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createWps method, of class MonitorControl.
     */
    @Test
    public void testCreateWps_WpsEntity() {
        WpsEntity wpsEntity = getUnstoredProcessEntity().getWps();
        Boolean createWps = mControl.createWps(wpsEntity);
        WpsEntity find = wpsDao.find(wpsEntity.getIdentifier());
        Assert.assertTrue(createWps && find != null && find.getIdentifier().equals(wpsEntity.getIdentifier()));
    }

    /**
     * Test of createWps method, of class MonitorControl.
     */
    @Test
    public void testCreateWps_String_URI() {
        WpsEntity wpsEntity = getUnstoredProcessEntity().getWps();
        Boolean createWps = mControl.createWps(wpsEntity.getIdentifier(), wpsEntity.getUri());
        WpsEntity find = wpsDao.find(wpsEntity.getIdentifier());
        Assert.assertTrue(createWps && find != null && find.getIdentifier().equals(wpsEntity.getIdentifier()));
    }

    @Test
    public void testItsFailIfNullCreateWps() {
        WpsEntity wpsEntity = getUnstoredProcessEntity().getWps();
        Boolean valid = true;

        try {
            mControl.createWps(null, wpsEntity.getUri());
            valid = false;
        } catch (IllegalArgumentException ex) {

        }

        try {
            mControl.createWps(wpsEntity.getIdentifier(), null);
            valid = false;
        } catch (IllegalArgumentException exx) {

        }

        try {
            mControl.createWps(null, null);
            valid = false;
        } catch (IllegalArgumentException exxx) {
        }

        Assert.assertTrue(valid);
    }

    /**
     * Test of createAndScheduleProcess method, of class MonitorControl.
     */
    @Test
    public void testCreateAndScheduleProcess_String_String() {
        WpsProcessEntity unstoredProcessEntity = getUnstoredProcessEntity();
        wpsDao.persist(unstoredProcessEntity.getWps());
        String wpsIdentifier = unstoredProcessEntity.getWps().getIdentifier();
        String processIdentifier = unstoredProcessEntity.getIdentifier();
        Boolean registred = mControl.createAndScheduleProcess(wpsIdentifier, processIdentifier);
        Boolean checkIsCreateAndScheduledProcess = checkIsCreateAndScheduledProcess(unstoredProcessEntity);
        Assert.assertTrue(registred && checkIsCreateAndScheduledProcess);
    }

    /**
     * Test of createAndScheduleProcess method, of class MonitorControl.
     */
    @Test
    public void testCreateAndScheduleProcess_WpsProcessEntity() {
        WpsProcessEntity unstoredProcessEntity = getUnstoredProcessEntity();
        wpsDao.persist(unstoredProcessEntity.getWps());
        Boolean registred = mControl.createAndScheduleProcess(unstoredProcessEntity);
        Boolean checkIsCreateAndScheduledProcess = checkIsCreateAndScheduledProcess(unstoredProcessEntity);
        Assert.assertTrue(registred && checkIsCreateAndScheduledProcess);
    }

    /**
     * Test of createAndScheduleProcess method, of class MonitorControl.
     */
    @Test
    public void testCreateAndScheduleProcessWithAlreadyExistedProcess() {
        WpsProcessEntity storedEntity = getStoredEntity();
        Boolean registred = mControl.createAndScheduleProcess(storedEntity);
        Boolean checkIsCreateAndScheduledProcess = checkIsCreateAndScheduledProcess(storedEntity);
        Assert.assertTrue(!registred && !checkIsCreateAndScheduledProcess);
    }

    @Test
    public void testCreateAndScheduleProcessIsNotAcceptedNullValues() {
        WpsProcessEntity unstoredProcessEntity = getUnstoredProcessEntity();
        String wpsIdentifier = unstoredProcessEntity.getWps().getIdentifier();
        String processIdentifier = unstoredProcessEntity.getIdentifier();

        Boolean check = true;
        try {
            mControl.createAndScheduleProcess(null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }

        try {
            mControl.createAndScheduleProcess(null, processIdentifier);
            check = false;
        } catch (IllegalArgumentException ex) {

        }

        try {
            mControl.createAndScheduleProcess(wpsIdentifier, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }

        try {
            mControl.createAndScheduleProcess(null, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }

        Assert.assertTrue(check);
    }

    private Boolean checkIsCreateAndScheduledProcess(WpsProcessEntity processEntity) {
        String wpsIdentifier = processEntity.getWps().getIdentifier();
        String processIdentifier = processEntity.getIdentifier();

        JobKey jobKey = new JobKey(processIdentifier, wpsIdentifier);

        Boolean isRegistred = false;
        try {
            isRegistred = monitor.getSchedulerControl()
                    .isJobRegistred(jobKey);
        } catch (SchedulerException ex) {

        }

        WpsProcessEntity find = wpsProcessDao.find(wpsIdentifier, processIdentifier);

        return find != null
                && find.getIdentifier().equals(processIdentifier)
                && find.getWps().getIdentifier().equals(wpsIdentifier)
                && isRegistred;
    }

    private TriggerConfig getTriggerConfigAndCreateJob(WpsProcessEntity p) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);

        if (!mControl.createAndScheduleProcess(p)) {
            fail("Can't create and schedule process.");
        }

        return new TriggerConfig(new Date(), c.getTime(), 30, TriggerConfig.IntervalUnit.MINUTE);
    }

    private Boolean checkIfSaved(TriggerKey tKey) {
        try {
            return monitor.getSchedulerControl()
                    .isTriggerRegistred(tKey);
        } catch (SchedulerException ex) {
            return false;
        }
    }

    /**
     * Test of saveTrigger method, of class MonitorControl.
     */
    @Test
    public void testSaveTrigger_WpsProcessEntity_TriggerConfig() {
        WpsProcessEntity pE = getUnstoredProcessEntity();
        wpsDao.persist(pE.getWps());
        TriggerConfig t = getTriggerConfigAndCreateJob(pE);
        TriggerConfig saveTrigger = mControl.saveTrigger(pE, t);
        TriggerKey key = new TriggerKey(saveTrigger.getTriggerName(), saveTrigger.getTriggerGroup());
        Boolean checkIfSaved = checkIfSaved(key);
        Assert.assertTrue(checkIfSaved);
    }

    /**
     * Test of setTestRequest method, of class MonitorControl.
     */
    @Test
    public void testSetTestRequest_3args() {
        WpsProcessEntity storedEntity = getStoredEntity();
        mControl.setTestRequest(storedEntity, "hello world");
        wpsProcessDao.update(storedEntity);
        WpsProcessEntity find = wpsProcessDao.find(storedEntity.getWps().getIdentifier(), storedEntity.getIdentifier());
        Assert.assertTrue(find != null && find.getRawRequest().equals("hello world"));
    }

    /**
     * Test of updateWps method, of class MonitorControl.
     */
    @Test
    public void testUpdateWps_3args() {
        WpsProcessEntity wpsProcess = getUnstoredProcessEntity();
        wpsDao.persist(wpsProcess.getWps());
        String oldWpsIdentifier = wpsProcess.getWps().getIdentifier();
        Boolean createAndScheduleProcess = mControl.createAndScheduleProcess(wpsProcess);
        if (!createAndScheduleProcess) {
            fail("Can't create and schedule process");
        }
        WpsEntity newWps = getUnstoredProcessEntity().getWps();
        mControl.updateWps(oldWpsIdentifier, newWps);
        JobKey k = new JobKey(wpsProcess.getIdentifier(), newWps.getIdentifier());
        try {
            if (!monitor.getSchedulerControl().isJobRegistred(k)) {
                fail("Job was not modified.");
            }
        } catch (SchedulerException ex) {
            fail(ex.toString());
        }
    }

    /**
     * Test of updateWps method, of class MonitorControl.
     */
    @Test
    public void testUpdateWpsAcceptedNotNullValues() {
        WpsProcessEntity unstoredProcessEntity = getUnstoredProcessEntity();
        String wpsIdentifier = unstoredProcessEntity.getIdentifier();
        URI uri = unstoredProcessEntity.getWps().getUri();
        WpsEntity wps = getUnstoredProcessEntity().getWps();
        Boolean check = true;
        try {
            mControl.updateWps(null, wps);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(wpsIdentifier, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(null, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(null, null, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(wpsIdentifier, null, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(null, wpsIdentifier, null);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            mControl.updateWps(null, null, uri);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        Assert.assertTrue(check);
    }

   

    /**
     * Test of deleteWps method, of class MonitorControl.
     */
    @Test
    public void testDeleteWpsNotAcceptedNullValues() {
        Boolean check = true;
        try {
            WpsEntity en = null;
            mControl.deleteWps(en);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        try {
            String identifier = null;
            mControl.deleteWps(identifier);
            check = false;
        } catch (IllegalArgumentException ex) {

        }
        Assert.assertTrue(check);
    }

  
    /**
     * Test of isPausedMonitoring method, of class MonitorControl.
     */
    @Test
    public void testPauseAndIsPausedMonitoring() {
        WpsProcessEntity storedEntity = getStoredEntity();
        mControl.createAndScheduleProcess(storedEntity);
        mControl.pauseMonitoring(storedEntity);
        
        Assert.assertTrue(mControl.isPausedMonitoring(storedEntity));
    }

    /**
     * Test of isPausedMonitoring method, of class MonitorControl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoring_NullOneArgLeft() {
        mControl.isPausedMonitoring(null, "hello world");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoring_NullOneArgRight() {
        mControl.isPausedMonitoring("Hello World", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoring_NullAllArgs() {
        mControl.isPausedMonitoring("Hello World", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoring_Overloaded_NullAllArgs() {
        mControl.isPausedMonitoring(null);
    }

    /**
     * Test of resumeMonitoring method, of class MonitorControl.
     */
    @Test
    public void testResumeMonitoring() {
        WpsProcessEntity storedEntity = getStoredEntity();
        mControl.createAndScheduleProcess(storedEntity);
        mControl.pauseMonitoring(storedEntity);
       
        mControl.resumeMonitoring(storedEntity);
        
        Assert.assertTrue(!mControl.isPausedMonitoring(storedEntity));
    }

    /**
     * Test of resumeMonitoring method, of class MonitorControl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoring_NullAllArgs() {
        mControl.resumeMonitoring(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoring_NullOneLeftArgs() {
        mControl.resumeMonitoring(null, "dad");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoring_NullOneRightArgs() {
        mControl.resumeMonitoring("dad", null);
    }
    
     @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoring_Overloaded_NullAllArgs() {
        mControl.resumeMonitoring(null);
    }

    /**
     * Test of pauseMonitoring method, of class MonitorControl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoring_Null_AllArgs() {
        mControl.pauseMonitoring(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoring_NullOneArgRight() {
        mControl.pauseMonitoring("kdwlod", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoring_OneArgLeft() {
        mControl.pauseMonitoring(null, "kdwlod");
    }
}
