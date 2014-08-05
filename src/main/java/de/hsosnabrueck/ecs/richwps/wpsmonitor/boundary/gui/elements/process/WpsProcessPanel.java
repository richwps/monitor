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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorAdminGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.event.EventNotFound;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.control.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a WpsProcessEntity instance and all monitoring and job
 * operations..
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessPanel extends javax.swing.JPanel {

    private WpsMonitorAdminGui mainFrame;
    private WpsProcessJobDialog wpsProcessJobDialog;
    private JPanel parent;
    private ShowMeasuredDataDialog measuredDataDialog;

    private WpsProcessEntity wpsProcess;
    private Boolean saved;

    private static final Logger LOG = LogManager.getLogger();

    /**
     *
     * @param mainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     */
    public WpsProcessPanel(WpsMonitorAdminGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess) {
        this(mainFrame, parent, wpsProcess, false);
    }

    /**
     *
     * @param mainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     * @param restored true if the form is only restored by its parent (e.g. the
     * monitor is restarted)
     */
    public WpsProcessPanel(WpsMonitorAdminGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess, Boolean restored) {
        this.mainFrame = mainFrame;
        this.parent = parent;
        this.wpsProcess = Validate.notNull(wpsProcess, "wpsProcess");

        initComponents();

        if (restored) {
            triggerSaveState();
        } else {
            this.saved = false;
        }

        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));
        init();
    }

    private void init() {
        this.wpsProcessJobDialog = new WpsProcessJobDialog(mainFrame, wpsProcess);
        this.measuredDataDialog = new ShowMeasuredDataDialog(mainFrame, wpsProcess);

        processNameText.setText(wpsProcess.getIdentifier());
        testRequestTextArea.setText(wpsProcess.getRawRequest());

        if (wpsProcess.isWpsException()) {
            indicateError();
        }

        Boolean paused = mainFrame.getMonitorReference()
                .getMonitorControl()
                .isPausedMonitoring(wpsProcess.getWps().getIdentifier(), wpsProcess.getIdentifier());

        if (paused) {
            triggerPauseCase();
        } else {
            triggerMonitoringCase();
        }

        registerMonitoringEvents();
    }

    private void triggerPauseCase() {
        stopMonitoringButton.setEnabled(false);
        rescheduleButton.setEnabled(true);
    }

    private void triggerMonitoringCase() {
        stopMonitoringButton.setEnabled(true);
        rescheduleButton.setEnabled(false);
    }

    private void registerMonitoringEvents() {
        try {
            MonitorEventHandler eventHandler = mainFrame
                    .getMonitorReference()
                    .getEventHandler();

            eventHandler
                    .registerListener("measurement.wpsjob.wpsexception", new MonitorEventListener() {

                        @Override
                        public void execute(MonitorEvent event) {

                            if (event.getMsg() instanceof WpsProcessEntity) {
                                WpsProcessEntity process = (WpsProcessEntity) event.getMsg();
                                processRequestException(process);
                            }
                        }

                    });

            eventHandler
                    .registerListener("monitorcontrol.pauseMonitoring", new MonitorEventListener() {

                        @Override
                        public void execute(MonitorEvent event) {
                            if (event.getMsg() instanceof WpsProcessEntity) {
                                WpsProcessEntity process = (WpsProcessEntity) event.getMsg();
                                monitoringPauseEvent(process);
                            }
                        }
                    });
        } catch (EventNotFound ex) {
            LOG.warn("Can't register WpsProcessPanel Listener at EventHandler.", ex);
        }
    }

    private WpsResponse doTestRequest(String testRequest) {
        WpsResponse response = null;

        try {
            WpsClient wpsClient = mainFrame.getMonitorReference()
                    .getBuilderInstance()
                    .getWpsClientFactory()
                    .create();

            WpsProcessInfo info = new WpsProcessInfo(wpsProcess.getWps().getUri(), wpsProcess.getIdentifier());
            WpsRequest request = new WpsRequest(testRequest, info);

            response = wpsClient.execute(request);
        } catch (CreateException ex) {
            LOG.error("Can't create WpsClient instance in doTestRequest-method.", ex);
        }

        return response;
    }

    private Boolean evaluateTestRequest(String testRequest) {
        WpsResponse response = doTestRequest(testRequest);
        Boolean result = true;

        if (response != null) {

            if (response.isConnectionException()) {
                result = MessageDialogs.showQuestionDialog(mainFrame,
                        "Not reachable",
                        "The specified WPS is not reachable; do you want to proceed?");
            }

            if (response.isWpsException()) {
                MessageDialogs.showError(mainFrame,
                        "WPS exception",
                        "The testrequest produced an exception! "
                        + "I will use the previous version of the Testrequest."
                        + "The old request is shown after a restart. This gives you a chance to edit the errornous request. :)");

                result = false;
            }
        }

        return result;
    }

    private void triggerSaveState() {
        this.saved = true;

        saveProcessButton.setBackground(new Color(153, 255, 153));
        showJobsButton.setEnabled(true);
        stopMonitoringButton.setEnabled(true);
        showMeasuredDataButton.setEnabled(true);
    }

    public void processRequestException(WpsProcessEntity process) {
        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            indicateError();
        }
    }

    public void monitoringPauseEvent(WpsProcessEntity process) {
        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            rescheduleButton.setEnabled(true);
        }
    }

    public void indicateError() {
        this.setBackground(new Color(255, 102, 102));
    }

    public void clearError() {
        this.setBackground(new Color(240, 240, 240));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPanel jPanel1 = new JPanel();
        JToolBar jToolBar1 = new JToolBar();
        showJobsButton = new JButton();
        showMeasuredDataButton = new JButton();
        stopMonitoringButton = new JButton();
        rescheduleButton = new JButton();
        deleteProcessButton = new JButton();
        saveProcessButton = new JButton();
        processNameText = new JLabel();
        JPanel jPanel2 = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        testRequestTextArea = new JTextArea();

        jPanel1.setBorder(BorderFactory.createTitledBorder(""));

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);

        showJobsButton.setIcon(new ImageIcon(getClass().getResource("/icons/clock.png"))); // NOI18N
        showJobsButton.setText("Show Jobs");
        showJobsButton.setEnabled(false);
        showJobsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showJobsButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showJobsButton);

        showMeasuredDataButton.setIcon(new ImageIcon(getClass().getResource("/icons/measure.png"))); // NOI18N
        showMeasuredDataButton.setText("Show Measuredata");
        showMeasuredDataButton.setEnabled(false);
        showMeasuredDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showMeasuredDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showMeasuredDataButton);

        stopMonitoringButton.setIcon(new ImageIcon(getClass().getResource("/icons/stop.png"))); // NOI18N
        stopMonitoringButton.setText("Stop Monitoring");
        stopMonitoringButton.setEnabled(false);
        stopMonitoringButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopMonitoringButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stopMonitoringButton);

        rescheduleButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        rescheduleButton.setText("Re-schedule");
        rescheduleButton.setEnabled(false);
        rescheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rescheduleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(rescheduleButton);

        deleteProcessButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteProcessButton.setText("Delete");
        deleteProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteProcessButton);

        saveProcessButton.setBackground(new Color(255, 51, 0));
        saveProcessButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveProcessButton.setText("Save");
        saveProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveProcessButton);

        processNameText.setFont(new Font("Tahoma", 0, 24)); // NOI18N
        processNameText.setText("jLabel3");

        jPanel2.setBorder(BorderFactory.createTitledBorder("Raw Request for testing"));

        testRequestTextArea.setColumns(20);
        testRequestTextArea.setRows(5);
        testRequestTextArea.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                testRequestTextAreaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(testRequestTextArea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 156, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(processNameText)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processNameText, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showJobsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showJobsButtonActionPerformed
        wpsProcessJobDialog.setVisible(true);
    }//GEN-LAST:event_showJobsButtonActionPerformed

    private void saveProcessButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveProcessButtonActionPerformed

        String testRequest = testRequestTextArea.getText();

        if (evaluateTestRequest(testRequest)) {
            Boolean inserted = true;
            clearError();

            if (!saved) {
                inserted = saveProcess();
            }

            if (inserted) {
                mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .setTestRequest(wpsProcess, testRequest);
            }

            if (wpsProcess.isWpsException()) {
                mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .resumeMonitoring(wpsProcess);
            }
        }
    }//GEN-LAST:event_saveProcessButtonActionPerformed

    public Boolean saveProcess() {
        Boolean inserted = mainFrame.getMonitorReference()
                .getMonitorControl()
                .createAndScheduleProcess(wpsProcess);

        if (inserted) {
            triggerSaveState();
        } else {
            MessageDialogs.showError(mainFrame,
                    "Error",
                    "Can't register Process to this WPS. Maybe the Process is already registred.");
        }

        return inserted;
    }

    private void deleteProcessButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteProcessButtonActionPerformed
        if (saved) {
            mainFrame.getMonitorReference()
                    .getMonitorControl()
                    .deleteProcess(wpsProcess);
        }

        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_deleteProcessButtonActionPerformed

    private void testRequestTextAreaKeyReleased(KeyEvent evt) {//GEN-FIRST:event_testRequestTextAreaKeyReleased
        this.saveProcessButton.setEnabled(true);
    }//GEN-LAST:event_testRequestTextAreaKeyReleased

    private void showMeasuredDataButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showMeasuredDataButtonActionPerformed
        measuredDataDialog.recaptureData();

        if (measuredDataDialog.isVisible()) {
            measuredDataDialog.revalidate();
        } else {
            measuredDataDialog.setVisible(true);
        }
    }//GEN-LAST:event_showMeasuredDataButtonActionPerformed

    private void rescheduleButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_rescheduleButtonActionPerformed
        mainFrame.getMonitorReference()
                .getMonitorControl()
                .resumeMonitoring(wpsProcess);

        triggerMonitoringCase();
    }//GEN-LAST:event_rescheduleButtonActionPerformed

    private void stopMonitoringButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_stopMonitoringButtonActionPerformed

        mainFrame.getMonitorReference()
                .getMonitorControl()
                .pauseMonitoring(wpsProcess);

        triggerPauseCase();
    }//GEN-LAST:event_stopMonitoringButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteProcessButton;
    private JLabel processNameText;
    private JButton rescheduleButton;
    private JButton saveProcessButton;
    private JButton showJobsButton;
    private JButton showMeasuredDataButton;
    private JButton stopMonitoringButton;
    private JTextArea testRequestTextArea;
    // End of variables declaration//GEN-END:variables
}
