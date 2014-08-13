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
package de.hsos.ecs.richwps.wpsmonitor.control;

import de.hsos.ecs.richwps.wpsmonitor.control.builder.MonitorBuilder;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.qos.response.ResponseFactory;
import de.hsos.ecs.richwps.wpsmonitor.util.BuilderException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@RunWith(Parameterized.class)
public class MonitorControlParameterNullTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {null, null}, {null, "http://value"}, {"value", null}
        });
    }

    private final String param1;
    private final String param2;

    public MonitorControlParameterNullTest(String param1, String param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private MonitorControl mControl;
    private Monitor monitor;

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
        } catch (BuilderException ex) {
            fail("Can't build Monitor! " + ex.toString());
        } 
    }


    private WpsProcessEntity getPreparedProcessEntity() {
        WpsProcessEntity process = null;

        try {
            process = new WpsProcessEntity(param1, new WpsEntity(param2, new URI("http://example.com")));
        } catch (URISyntaxException ex) {
            fail(ex.getMessage());
        }

        return process;
    }

    private WpsProcessEntity getPreparedProcessEntityWithNullWps() {
        WpsProcessEntity process = new WpsProcessEntity(param1, null);

        return process;
    }

    private WpsEntity getPreparedWpsEntity() {
        WpsEntity wps = null;

        try {
            wps = new WpsEntity();
            wps.setIdentifier(param1);

            if (param2 != null) {
                wps.setUri(param2);
            }
        } catch (URISyntaxException | MalformedURLException ex) {
            fail(ex.getMessage());
        }

        return wps;
    }

    /**
     * Test of isPausedMonitoring method, of class MonitorControl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoringNotAllowedNull() {
        mControl.isPausedMonitoring(param1, param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsPausedMonitoringOverloadedNotAllowedNull() {
        mControl.isPausedMonitoring(getPreparedProcessEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoringNotAllowedNull() {
        mControl.resumeMonitoring(param1, param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoringOverloadedNotAllowedNull() {
        mControl.resumeMonitoring(getPreparedProcessEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeMonitoringOverloadedNotAllowedNullWpsInnerInstance() {
        mControl.resumeMonitoring(getPreparedProcessEntityWithNullWps());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoringNotAllowedNull() {
        mControl.pauseMonitoring(param1, param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoringOverloadedNotAllowedNull() {
        mControl.pauseMonitoring(getPreparedProcessEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseMonitoringOverloadedNotAllowedNullWpsInnerInstance() {
        mControl.pauseMonitoring(getPreparedProcessEntityWithNullWps());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testcreateWpsNotAllowedNull() {
        URI uri = null;

        try {
            if (param2 != null) {
                uri = new URI(param2);
            }
        } catch (URISyntaxException ex) {
            fail(ex.getMessage());
        }

        mControl.createWps(param1, uri);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testcreateWpsOverloadedNotAllowedNull() {
        mControl.createWps(getPreparedWpsEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndScheduleProcessNotAllowedNull() {
        mControl.createAndScheduleProcess(param1, param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndScheduleProcessOverloadedNotAllowedNull() {
        mControl.createAndScheduleProcess(getPreparedProcessEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndScheduleProcessOverloadedNotAllowedNullWpsInnerInstance() {
        mControl.createAndScheduleProcess(getPreparedProcessEntityWithNullWps());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWpsProcessNotAllowedNull() {
        mControl.updateWps(param1, param2, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWpsProcessNotAllowedNullThirdParameter() {
        mControl.updateWps(param1, param2, getPreparedWpsEntity().getUri());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetTriggersNotAllowedNull() {
        mControl.getTriggers(param1, param2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetTriggersOverloadedNotAllowedNull() {
        mControl.getTriggers(getPreparedProcessEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetTriggersOverloadedNotAllowedNullWpsInnerInstance() {
        mControl.getTriggers(getPreparedProcessEntityWithNullWps());
    }
}
