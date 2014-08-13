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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import junit.framework.Assert;
import org.assertj.swing.core.Robot;
import static org.assertj.swing.core.matcher.JButtonMatcher.withText;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorAdminGuiTest extends AssertJSwingJUnitTestCase {

    private static Monitor monitor;
    private FrameFixture gui;
    
    /**
     * private filds which will be incremeted after every tests
     */
    private String wpsIdentifier;
    private String wpsUri;
    private String wpsProcessIdentifier;

    private static Integer i = 0;
    
    public WpsMonitorAdminGuiTest() {
    }

    @Override
    protected void onSetUp() {
        WpsMonitorAdminGui mg = GuiActionRunner.execute(new GuiQuery<WpsMonitorAdminGui>() {
            @Override
            protected WpsMonitorAdminGui executeInEDT() {
                return new WpsMonitorAdminGui(monitor, Log4j2Utils.getFileNameIfExists());
            }
        });

        robot().settings().delayBetweenEvents(2);
        gui = new FrameFixture(robot(), mg);
        gui.show();
        
        wpsIdentifier = generateWpsName();
        wpsUri = "http://example.com";
        wpsProcessIdentifier = generateProcessName();
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

    private void addWps(String identifier, String uri) {
        gui.textBox("wpsToAddField").enterText(identifier);
        gui.textBox("wpsToAddUriField").enterText(uri);

        gui.button("addWpsButton").click();
    }

    private void addStoredProcess(String wpsIdentifier, String wpsUri, String processIdentifier) {
        addProcess(wpsIdentifier, wpsUri, processIdentifier);

        gui.panel(processIdentifier)
                .button("saveProcessButton")
                .click();

        JOptionPaneFixture optionpane = JOptionPaneFinder.findOptionPane()
                .withTimeout(monitor.getConfig().getWpsClientTimeout())
                .using(robot());

        // must be german .. dunno why
        optionpane.button(withText("Ja"))
                .click();
    }

    private void addProcess(String wpsIdentifier, String wpsUri, String processIdentifier) {
        addWps(wpsIdentifier, wpsUri);

        gui.panel(wpsIdentifier)
                .button("manageProcessesButton").click();

        String dialogName = wpsIdentifier + "ProcessDialog";

        gui.dialog(dialogName)
                .textBox("processIdentifierInput").enterText(processIdentifier);

        gui.dialog(dialogName)
                .button("addProcessButton")
                .click();
    }
    
    private String generateProcessName() {
        return "ExampleProcess" + i++;
    }
    
    private String generateWpsName() {
        return "ExampleWps" + i++;
    }

    @Test
    public void testAddWps() {
        addWps(wpsIdentifier, wpsUri);

        boolean identifierLabelEqualsIdentifier = gui.panel(wpsIdentifier)
                .label("wpsNameLabel")
                .text()
                .equals(wpsIdentifier);

        boolean uriLabelEqualsUri = gui.panel(wpsIdentifier)
                .label("wpsUriLabel")
                .text()
                .equals(wpsUri);

        Assert.assertTrue(identifierLabelEqualsIdentifier && uriLabelEqualsUri);
    }

    @Test(expected = ComponentLookupException.class)
    public void removeWps() {

        addWps(wpsIdentifier, wpsUri);

        gui.panel(wpsIdentifier).
                button("deleteWpsButton")
                .click();

        JOptionPaneFixture optionpane = JOptionPaneFinder.findOptionPane()
                .using(robot());

        optionpane.button(withText("Ja"))
                .click();

        gui.panel(wpsIdentifier);
    }

    @Test
    public void testAddProcess() {
        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);

        boolean processIdentifierEqualsIdentifier = gui.panel(wpsProcessIdentifier)
                .label("processNameLabel")
                .text()
                .equals(wpsProcessIdentifier);

        Assert.assertTrue(processIdentifierEqualsIdentifier);
    }

    @Test(expected = ComponentLookupException.class)
    public void testRemoveProcess() {

        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);

        gui.panel(wpsProcessIdentifier)
                .button("removeProcessButton")
                .click();

        JOptionPaneFixture optionpane = JOptionPaneFinder.findOptionPane()
                .using(robot());

        optionpane.button(withText("Ja"))
                .click();

        gui.panel(wpsProcessIdentifier);
    }
    
    private void addJob() {
        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);

        gui.panel(wpsProcessIdentifier)
                .button("manageJobsButton")
                .click();

        gui.dialog("jobDialog")
                .button("addJobButton")
                .click();
    }

    @Test
    public void testAddJob() {    
        addJob();
    }
    
    private String getDateInRightFormat(Date d) {
        return new SimpleDateFormat("dd.MM.yyyy").format(d);
    }
    
    private Date addDaysToDate(Date d, Integer days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, days);
        
        return cal.getTime();
    }
/*    
    @Test 
    public void testAddJobAndSave() {
        addJob();
        JPanelFixture panel = gui.panel("JobEntry1");
        
        
        Date d = new Date();
        panel.textBox("startDate")
                .enterText(getDateInRightFormat(d));
        panel.textBox("endDate")
                .enterText(getDateInRightFormat(addDaysToDate(d, 10)));
        panel.textBox("intervalField")
                .enterText(10 + "");
        panel.comboBox("intervalTypeComboBox")
                .selectItem("Years");
        
        panel.button("saveJobButton");
        
        gui.dialog("jobDialog").requireFocused();
    }
*/
    @Test
    public void testEditWpsFieldsEquals() {

        addWps(wpsIdentifier, wpsUri);

        String oldWpsUri = gui.panel(wpsIdentifier)
                .label("wpsUriLabel")
                .text();

        String oldWpsIdentifier = gui.panel(wpsIdentifier)
                .label("wpsNameLabel")
                .text();

        gui.panel(wpsIdentifier)
                .button("editWpsButton")
                .click();
        DialogFixture editDialog = gui.dialog("editDialog");
        String newWpsIdentifier = editDialog.textBox("newIdentifierTextField").text();
        String newWpsUri = editDialog.textBox("newUriTextField").text();

        Assert.assertTrue(newWpsIdentifier.equals(oldWpsIdentifier) && newWpsUri.equals(oldWpsUri));
    }
    
    @Test
    public void testEditWpsFields() {
        addWps(wpsIdentifier, wpsUri);
        
        gui.panel(wpsIdentifier)
                .button("editWpsButton")
                .click();
        
        String newWpsIdentifier = "editedWps" + i++;
        String newUriIdentifier = "http://example.com/edited/wps" + i++;
        
        DialogFixture editDialog = gui.dialog("editDialog");
        
        editDialog.textBox("newIdentifierTextField")
                .deleteText();
        editDialog.textBox("newUriTextField")
                .deleteText();
        
        editDialog.textBox("newIdentifierTextField")
                .enterText(newWpsIdentifier);
        editDialog.textBox("newUriTextField")
                .enterText(newUriIdentifier);
        
        editDialog.button("saveButton")
                .click();
        
        boolean newNameSet = gui.panel(newWpsIdentifier)                
                .label("wpsNameLabel")
                .text()
                .equals(newWpsIdentifier);
        
        boolean newUriSet = gui.panel(newWpsIdentifier)
                .label("wpsUriLabel")
                .text()
                .equals(newUriIdentifier);
        
        Assert.assertTrue(newNameSet && newUriSet);
    }

    @Test
    public void testButtonsAreDisabledWhileNotSaved() {
         
        addProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);
        
        String[] buttonsToCheck = new String[]{
            "manageJobsButton",
            "showMeasuredDataButton",
            "stopMonitoringButton",
            "resheduleButton"
        };
        
        for(String buttonIdentifier : buttonsToCheck) {
            boolean enabled = gui.panel(wpsProcessIdentifier)
                    .button(buttonIdentifier)
                    .isEnabled();
            
            Assert.assertTrue(!enabled);
        }
    }
    
    @Test
    public void testCloseButtonOfProcessDialog() {
        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);
        
        DialogFixture processDialog = gui.dialog();
        processDialog.button("closeButton")
                .click();
        
        processDialog.requireNotVisible();
    }
    
    @Test
    public void testCloseButtonOfManageJobsDialog() {
        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);
        
        gui.panel(wpsProcessIdentifier)
                .button("manageJobsButton")
                .click();
        
        DialogFixture dialog = gui.dialog("jobDialog");
        
        dialog.button("closeButton")
                .click();
        
        dialog.requireNotVisible();
    }
    
    @Test
    public void testCloseButtonOfShowMeasuredDataDialog() {
        addStoredProcess(wpsIdentifier, wpsUri, wpsProcessIdentifier);
        
        gui.panel(wpsProcessIdentifier)
                .button("showMeasuredDataButton")
                .click();
        
        DialogFixture dialog = gui.dialog("measuredDataDialog");
        
        dialog.button("closeButton")
                .click();
        
        dialog.requireNotVisible();
    }
}
