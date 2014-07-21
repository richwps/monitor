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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process.WpsProcessJobDialog;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process.ShowMeasuredDataDialog;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.EventNotFound;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventHandler;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a WpsProcessEntity instance and all monitoring and job
 * operations..
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessPanel extends javax.swing.JPanel {

    private WpsMonitorGui mainFrame;
    private WpsProcessJobDialog wpsProcessJobDialog;
    private JPanel parent;
    private ShowMeasuredDataDialog measuredDataDialog;

    private WpsProcessEntity wpsProcess;
    private Boolean saved;

    private static Logger log = LogManager.getLogger();

    /**
     *
     * @param mainFrame Reference to the WpsMonitorGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     */
    public WpsProcessPanel(WpsMonitorGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess) {
        this(mainFrame, parent, wpsProcess, false);
    }

    /**
     *
     * @param mainFrame Reference to the WpsMonitorGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     * @param restored true if the form is only restored by its parent (e.g. the
     * monitor is restarted)
     */
    public WpsProcessPanel(WpsMonitorGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess, Boolean restored) {
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
        this.wpsProcessJobDialog = new WpsProcessJobDialog(mainFrame, wpsProcess, true);
        this.measuredDataDialog = new ShowMeasuredDataDialog(mainFrame, wpsProcess, true);

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
                                WpsProcessEntity wpsProcess = (WpsProcessEntity) event.getMsg();
                                processRequestException(wpsProcess);
                            }
                        }

                    });

            eventHandler
                    .registerListener("monitorcontrol.pauseMonitoring", new MonitorEventListener() {

                        @Override
                        public void execute(MonitorEvent event) {
                            if (event.getMsg() instanceof WpsProcessEntity) {
                                WpsProcessEntity wpsProcess = (WpsProcessEntity) event.getMsg();
                                monitoringPauseEvent(wpsProcess);
                            }
                        }
                    });
        } catch (EventNotFound ex) {
            log.warn(ex);
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

            return response;
        } catch (CreateException ex) {
            log.error(ex);
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
        log.debug("Exception event triggered by {}", process);

        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            indicateError();
        }
    }

    public void monitoringPauseEvent(WpsProcessEntity process) {
        log.debug("Pause event triggered by {}", process);

        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            rescheduleButton.setEnabled(true);
        }
    }

    public final void indicateError() {
        this.setBackground(new Color(255, 102, 102));
    }

    public final void clearError() {
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

        jPanel1 = new javax.swing.JPanel();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        showJobsButton = new javax.swing.JButton();
        showMeasuredDataButton = new javax.swing.JButton();
        stopMonitoringButton = new javax.swing.JButton();
        rescheduleButton = new javax.swing.JButton();
        deleteProcessButton = new javax.swing.JButton();
        saveProcessButton = new javax.swing.JButton();
        processNameText = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        testRequestTextArea = new javax.swing.JTextArea();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);

        showJobsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clock.png"))); // NOI18N
        showJobsButton.setText("Show Jobs");
        showJobsButton.setEnabled(false);
        showJobsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showJobsButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showJobsButton);

        showMeasuredDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/measure.png"))); // NOI18N
        showMeasuredDataButton.setText("Show Measuredata");
        showMeasuredDataButton.setEnabled(false);
        showMeasuredDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMeasuredDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showMeasuredDataButton);

        stopMonitoringButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stop.png"))); // NOI18N
        stopMonitoringButton.setText("Stop Monitoring");
        stopMonitoringButton.setEnabled(false);
        stopMonitoringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopMonitoringButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stopMonitoringButton);

        rescheduleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        rescheduleButton.setText("Re-schedule");
        rescheduleButton.setEnabled(false);
        rescheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rescheduleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(rescheduleButton);

        deleteProcessButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteProcessButton.setText("Delete");
        deleteProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteProcessButton);

        saveProcessButton.setBackground(new java.awt.Color(255, 51, 0));
        saveProcessButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveProcessButton.setText("Save");
        saveProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveProcessButton);

        processNameText.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        processNameText.setText("jLabel3");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Request for testing"));

        testRequestTextArea.setColumns(20);
        testRequestTextArea.setRows(5);
        testRequestTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                testRequestTextAreaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(testRequestTextArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 156, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(processNameText)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showJobsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showJobsButtonActionPerformed
        wpsProcessJobDialog.setVisible(true);
    }//GEN-LAST:event_showJobsButtonActionPerformed

    private void saveProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProcessButtonActionPerformed

        String testRequest = testRequestTextArea.getText();

        if (evaluateTestRequest(testRequest)) {
            Boolean inserted = true;
            clearError();

            if (!saved) {
                inserted = mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .createAndScheduleProcess(wpsProcess);
            }

            if (inserted) {
                inserted = mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .setTestRequest(wpsProcess, testRequest);
            }

            if (wpsProcess.isWpsException()) {
                mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .resumeMonitoring(wpsProcess);
            }

            if (inserted) {
                triggerSaveState();
            } else {
                MessageDialogs.showError(mainFrame,
                        "Error",
                        "Can't register Process to this WPS. Maybe the Process is already registred.");
            }
        }
    }//GEN-LAST:event_saveProcessButtonActionPerformed

    private void deleteProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProcessButtonActionPerformed
        if (saved) {
            mainFrame.getMonitorReference()
                    .getMonitorControl()
                    .deleteProcess(wpsProcess);
        }

        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_deleteProcessButtonActionPerformed

    private void testRequestTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_testRequestTextAreaKeyReleased
        this.saveProcessButton.setEnabled(true);
    }//GEN-LAST:event_testRequestTextAreaKeyReleased

    private void showMeasuredDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showMeasuredDataButtonActionPerformed
        measuredDataDialog.recaptureData();

        if (measuredDataDialog.isVisible()) {
            measuredDataDialog.revalidate();
        } else {
            measuredDataDialog.setVisible(true);
        }
    }//GEN-LAST:event_showMeasuredDataButtonActionPerformed

    private void rescheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rescheduleButtonActionPerformed
        mainFrame.getMonitorReference()
                .getMonitorControl()
                .resumeMonitoring(wpsProcess);

        triggerMonitoringCase();
    }//GEN-LAST:event_rescheduleButtonActionPerformed

    private void stopMonitoringButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopMonitoringButtonActionPerformed

        mainFrame.getMonitorReference()
                .getMonitorControl()
                .pauseMonitoring(wpsProcess);

        triggerPauseCase();
    }//GEN-LAST:event_stopMonitoringButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteProcessButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel processNameText;
    private javax.swing.JButton rescheduleButton;
    private javax.swing.JButton saveProcessButton;
    private javax.swing.JButton showJobsButton;
    private javax.swing.JButton showMeasuredDataButton;
    private javax.swing.JButton stopMonitoringButton;
    private javax.swing.JTextArea testRequestTextArea;
    // End of variables declaration//GEN-END:variables
}