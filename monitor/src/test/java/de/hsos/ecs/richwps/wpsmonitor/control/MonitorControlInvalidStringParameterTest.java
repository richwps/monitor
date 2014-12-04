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
 * This test check extrem values of methods from MonitorControl implementation.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@RunWith(Parameterized.class)
public class MonitorControlInvalidStringParameterTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            // 256 characters
            {"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"},
            {"cc"},
        });
    }

    private final String param1;
    private final URI uriParam;

    public MonitorControlInvalidStringParameterTest(String param1) {
        this.param1 = param1;

        URI uri = null;
        try {
            uri = new URI("http://example.com");
        } catch (URISyntaxException ex) {
            fail(ex.getMessage());
        }

        this.uriParam = uri;
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
        WpsProcessEntity process = new WpsProcessEntity(param1, getPreparedWpsEntity());

        return process;
    }

    private WpsEntity getPreparedWpsEntity() {
        WpsEntity wps = new WpsEntity();
        wps.setIdentifier(param1);
        wps.setUri(uriParam);

        return wps;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testcreateWpsNotAllowedNull() {
        mControl.createWps(param1, uriParam);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndScheduleProcessNotAllowedNull() {
        mControl.createAndScheduleProcess(param1, param1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWpsProcessNotAllowedNull() {
        mControl.updateWps(param1, param1, getPreparedWpsEntity().getUri());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWpsProcessNotAllowedNullThirdParameter() {
        mControl.updateWps(param1, param1, getPreparedWpsEntity().getUri());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetTriggersNotAllowedNull() {
        mControl.getTriggers(param1, param1);
    }
}
