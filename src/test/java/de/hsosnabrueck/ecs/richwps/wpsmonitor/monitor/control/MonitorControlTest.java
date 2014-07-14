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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.QosDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsProcessDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.InitJpa;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl.JpaPuConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.factory.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.measurement.qos.response.ResponseFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.MonitorBuilder;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter.ExampleQos;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.BuilderException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.DateBuilder;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Ignore
public class MonitorControlTest {

    private MonitorControl mControl;
    private Monitor monitor;
    private WpsProcessDataAccess wpsProcessDao;
    private WpsDataAccess wpsDao;
    private QosDataAccess qosDao;

    public MonitorControlTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        JpaPuConfig.setPersistenceUnitName("de.hsosnabrueck.ecs.richwps_WPSMonitorTEST_pu");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        try {
            monitor = new MonitorBuilder()
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
        } catch (MalformedURLException ex) {
            fail(ex.toString());
        } catch (URISyntaxException ex) {
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
        System.out.println("createWps");
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
        System.out.println("createWps");
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
        System.out.println("createAndScheduleProcess");
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
        System.out.println("createAndScheduleProcess");

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
        System.out.println("createAndScheduleProcess");

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
        
        if(!mControl.createAndScheduleProcess(p)) {
            fail("Can't create and schedule process.");
        }
        
        return new TriggerConfig(new Date(), c.getTime(), 30, DateBuilder.IntervalUnit.MINUTE);
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
        System.out.println("saveTrigger");
        
        WpsProcessEntity pE = getUnstoredProcessEntity();
        wpsDao.persist(pE.getWps());
        
        TriggerConfig t = getTriggerConfigAndCreateJob(pE);
        
        TriggerKey saveTrigger = mControl.saveTrigger(pE, t);
        Boolean checkIfSaved = checkIfSaved(saveTrigger);
        
        Assert.assertTrue(checkIfSaved);
    }
    
    

    /**
     * Test of setTestRequest method, of class MonitorControl.
     */
    @Test
    public void testSetTestRequest_3args() {
        System.out.println("setTestRequest");
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
        System.out.println("updateWps");
        WpsProcessEntity wpsProcess = getUnstoredProcessEntity();
        wpsDao.persist(wpsProcess.getWps());
        String oldWpsIdentifier = wpsProcess.getWps().getIdentifier();
        
        Boolean createAndScheduleProcess = mControl.createAndScheduleProcess(wpsProcess);
        
        if(!createAndScheduleProcess) {
            fail("Can't create and schedule process");
        }
        
        WpsEntity newWps = getUnstoredProcessEntity().getWps();
        mControl.updateWps(oldWpsIdentifier, newWps);
        
        JobKey k = new JobKey(wpsProcess.getIdentifier(), newWps.getIdentifier());
        
        try {
            if(!monitor.getSchedulerControl().isJobRegistred(k)) {
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
        System.out.println("updateWps");
        WpsProcessEntity unstoredProcessEntity = getUnstoredProcessEntity();
        String wpsIdentifier = unstoredProcessEntity.getIdentifier();
        URI uri = unstoredProcessEntity.getWps().getUri();
        WpsEntity wps = getUnstoredProcessEntity().getWps();
        
        Boolean check = true;
        try {
            mControl.updateWps(null, wps);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(wpsIdentifier, null);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(null, null);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(null, null, null);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(wpsIdentifier, null, null);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(null, wpsIdentifier, null);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            mControl.updateWps(null, null, uri);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        Assert.assertTrue(check);
    }

    /**
     * Test of deleteWps method, of class MonitorControl.
     */
    @Test
    public void testDeleteWps_String() {
        try { //todo ggf. noch testen ob die measured data ebenfalls geloescht wurden
            System.out.println("deleteWps");
            WpsProcessEntity wpsProcess = getUnstoredProcessEntity();
            Boolean createWps = mControl.createWps(wpsProcess.getWps());
            WpsEntity find = wpsDao.find(wpsProcess.getWps().getIdentifier());
            
            if(!createWps) {
                fail("Can't create WPS");
            }

            
            TriggerConfig triggerConfig = getTriggerConfigAndCreateJob(wpsProcess);
            TriggerKey saveTrigger = mControl.saveTrigger(wpsProcess, triggerConfig);
            
            JobKey jobKey = new JobKey(wpsProcess.getIdentifier(), wpsProcess.getWps().getIdentifier());
            
            if(saveTrigger == null) {
                fail("Can't save trigger");
            }
            
            SchedulerControl schedulerControl = monitor.getSchedulerControl();
            if(!schedulerControl.isJobRegistred(jobKey)) {
                fail("Job key wasn't register at createAndScheduleJob call");
            }
            
            if(!schedulerControl.isTriggerRegistred(saveTrigger)) {
                fail("Trigger wasn't register at saveTrigger call");
            }
            
            WpsProcessEntity dbWpsProcess = wpsProcessDao.find(wpsProcess.getWps().getIdentifier(), wpsProcess.getIdentifier());
            ResponseEntity qos = new ResponseEntity();
            qos.setResponseTime(10000);
            MeasuredDataEntity mData = new MeasuredDataEntity();
            mData.add(qos);
            mData.setCreateTime(new Date());
            mData.setProcess(dbWpsProcess);
            
            qosDao.persist(mData);
            mData = qosDao.find(mData.getId());
            Long qosId = mData.getData().get(0).getId();
            AbstractQosEntity findAbstractQosEntity = qosDao.findAbstractQosEntityByid(qosId);
            
            if(findAbstractQosEntity == null) {
                fail("AbstractQosEntity wasn't stored in the database");
            }
            
            
            
            
            mControl.deleteWps(wpsProcess.getWps());
            findAbstractQosEntity = qosDao.findAbstractQosEntityByid(qosId);
            
            if(findAbstractQosEntity != null) {
                fail("Qos Entity are not deleted too");
            }
            
            if(schedulerControl.isJobRegistred(jobKey)) {
                fail("Job key already registred after deleteWps!");
            }
            
            if(schedulerControl.isTriggerRegistred(saveTrigger)) {
                fail("TriggerKey already registred after deleteWps!");
            }
            
            WpsProcessEntity processFind = wpsProcessDao.find(wpsProcess.getWps().getIdentifier(), wpsProcess.getIdentifier());
            WpsEntity wpsFind = wpsDao.find(wpsProcess.getWps().getIdentifier());
            
            if(processFind != null) {
                fail("Wps Process wasn't deleted");
            }
            
            if(wpsFind != null) {
                fail("Wps wasn't deleted");
            }
        } catch (SchedulerException ex) {
            fail(ex.toString());
        }
    }

    /**
     * Test of deleteWps method, of class MonitorControl.
     */
    @Test
    public void testDeleteWpsNotAcceptedNullValues() {
        System.out.println("deleteWps");
        Boolean check = true;
        
        try {
            WpsEntity en = null;
            mControl.deleteWps(en);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        try {
            String identifier = null;
            mControl.deleteWps(identifier);
            check = false;
        } catch(IllegalArgumentException ex) {
            
        }
        
        Assert.assertTrue(check);
    }

    /**
     * Test of deleteProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteProcess_String_String() {
        System.out.println("deleteProcess");
        
    }

    /**
     * Test of deleteProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteProcess_WpsProcessEntity() {
        System.out.println("deleteProcess");

    }

    /**
     * Test of isPausedMonitoring method, of class MonitorControl.
     */
    @Test
    public void testIsPausedMonitoring_String_String() {
        System.out.println("isPausedMonitoring");

    }

    /**
     * Test of isPausedMonitoring method, of class MonitorControl.
     */
    @Test
    public void testIsPausedMonitoring_WpsProcessEntity() {
        System.out.println("isPausedMonitoring");

    }

    /**
     * Test of resumeMonitoring method, of class MonitorControl.
     */
    @Test
    public void testResumeMonitoring_String_String() {
        System.out.println("resumeMonitoring");

    }

    /**
     * Test of resumeMonitoring method, of class MonitorControl.
     */
    @Test
    public void testResumeMonitoring_WpsProcessEntity() {
        System.out.println("resumeMonitoring");

    }

    /**
     * Test of pauseMonitoring method, of class MonitorControl.
     */
    @Test
    public void testPauseMonitoring_String_String() {
        System.out.println("pauseMonitoring");

    }

    /**
     * Test of pauseMonitoring method, of class MonitorControl.
     */
    @Test
    public void testPauseMonitoring_WpsProcessEntity() {
        System.out.println("pauseMonitoring");

    }

    /**
     * Test of getProcessesOfWps method, of class MonitorControl.
     */
    @Test
    public void testGetProcessesOfWps_String() {
        System.out.println("getProcessesOfWps");

    }

    /**
     * Test of getProcessesOfWps method, of class MonitorControl.
     */
    @Test
    public void testGetProcessesOfWps_WpsEntity() {
        System.out.println("getProcessesOfWps");

    }

    /**
     * Test of getTriggers method, of class MonitorControl.
     */
    @Test
    public void testGetTriggers_String_String() {
        System.out.println("getTriggers");

    }

    /**
     * Test of getTriggers method, of class MonitorControl.
     */
    @Test
    public void testGetTriggers_WpsProcessEntity() {
        System.out.println("getTriggers");

    }

    /**
     * Test of getMeasuredData method, of class MonitorControl.
     */
    @Test
    public void testGetMeasuredData_String_String() {
        System.out.println("getMeasuredData");

    }

    /**
     * Test of getMeasuredData method, of class MonitorControl.
     */
    @Test
    public void testGetMeasuredData_WpsProcessEntity() {
        System.out.println("getMeasuredData");

    }

    /**
     * Test of getMeasuredData method, of class MonitorControl.
     */
    @Test
    public void testGetMeasuredData_3args() {
        System.out.println("getMeasuredData");

    }

    /**
     * Test of getMeasuredData method, of class MonitorControl.
     */
    @Test
    public void testGetMeasuredData_WpsProcessEntity_Range() {
        System.out.println("getMeasuredData");

    }

    /**
     * Test of deleteMeasuredDataOfProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteMeasuredDataOfProcess_String_String() {
        System.out.println("deleteMeasuredDataOfProcess");

    }

    /**
     * Test of deleteMeasuredDataOfProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteMeasuredDataOfProcess_WpsProcessEntity() {
        System.out.println("deleteMeasuredDataOfProcess");

    }

    /**
     * Test of deleteMeasuredDataOfProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteMeasuredDataOfProcess_3args() {
        System.out.println("deleteMeasuredDataOfProcess");

    }

    /**
     * Test of deleteMeasuredDataOfProcess method, of class MonitorControl.
     */
    @Test
    public void testDeleteMeasuredDataOfProcess_WpsProcessEntity_Date() {
        System.out.println("deleteMeasuredDataOfProcess");

    }

    /**
     * Test of deleteTrigger method, of class MonitorControl.
     */
    @Test
    public void testDeleteTrigger() {
        System.out.println("deleteTrigger");

    }

    /**
     * Test of getWpsList method, of class MonitorControl.
     */
    @Test
    public void testGetWpsList() {
        System.out.println("getWpsList");

    }

    /**
     * Test of deleteMeasuredData method, of class MonitorControl.
     */
    @Test
    public void testDeleteMeasuredData() {
        System.out.println("deleteMeasuredData");

    }

}
