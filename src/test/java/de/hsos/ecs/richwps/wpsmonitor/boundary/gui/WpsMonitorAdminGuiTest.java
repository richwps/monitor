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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui;

import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.builder.MonitorBuilder;
import de.hsos.ecs.richwps.wpsmonitor.util.BuilderException;
import de.hsos.ecs.richwps.wpsmonitor.util.Log4j2Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorAdminGuiTest {

    private static Monitor monitor;
    private FrameFixture gui;

    public WpsMonitorAdminGuiTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        try {
            monitor = new MonitorBuilder()
                    .withPersistenceUnit("de.hsosnabrueck.ecs.richwps_WPSMonitorTEST_pu")
                    .setupDefault()
                    .build();
        } catch (BuilderException ex) {
            fail(ex.toString());
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        WpsMonitorAdminGui mg = GuiActionRunner.execute(new GuiQuery<WpsMonitorAdminGui>() {
            @Override
            protected WpsMonitorAdminGui executeInEDT() {
                return new WpsMonitorAdminGui(monitor, Log4j2Utils.getFileNameIfExists());
            }
        });
        
        gui = new FrameFixture(mg);
        gui.show();
    }

    @After
    public void tearDown() {
    }

    private void addWps(String identifier, String uri) {
        gui.textBox("wpsToAddField").enterText(identifier);
        gui.textBox("wpsToAddUriField").enterText(uri);

        gui.button("addWpsButton").click();
    }

    @Test
    public void testAddWps() {
        String identifier = "example.com";
        String uri = "http://example.com";

        addWps(identifier, uri);
        
        boolean identifierLabelEqualsIdentifier = gui.panel(identifier)
                .label("wpsNameLabel")
                .text()
                .equals(identifier);
        
        boolean uriLabelEqualsUri = gui.panel(identifier)
                .label("wpsUriLabel")
                .text()
                .equals(uri);

        
        Assert.assertTrue(identifierLabelEqualsIdentifier && uriLabelEqualsUri);
    }

}
