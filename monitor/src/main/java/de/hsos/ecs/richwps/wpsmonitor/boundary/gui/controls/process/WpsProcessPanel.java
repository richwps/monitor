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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.process;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.control.event.EventNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventHandler;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventListener;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import org.apache.batik.util.gui.xmleditor.XMLEditorKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a WpsProcessEntity instance and all monitoring and job
 * operations..
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessPanel extends JPanel {

    private static final Logger LOG = LogManager.getLogger();

    private final WpsMonitorAdminGui mainFrame;
    private WpsProcessJobDialog wpsProcessJobDialog;
    private final JPanel parent;
    private MeasuredDataDialog measuredDataDialog;

    private WpsProcessEntity wpsProcess;
    private Boolean saved;

    private final MonitorEventListener exceptionListener;
    private final MonitorEventListener pauseMonitoringListener;

    private final String manageJobButtonText;

    /**
     *
     * @param mainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     */
    public WpsProcessPanel(final WpsMonitorAdminGui mainFrame, final JPanel parent,
            final WpsProcessEntity wpsProcess) {

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
    public WpsProcessPanel(final WpsMonitorAdminGui mainFrame, final JPanel parent, WpsProcessEntity wpsProcess,
            final Boolean restored) {

        this.mainFrame = mainFrame;
        this.parent = parent;
        this.wpsProcess = Validate.notNull(wpsProcess, "wpsProcess");

        // set up listeners. the fields are final to be save for reference changes
        exceptionListener = new MonitorEventListener() {
            @Override
            public void execute(MonitorEvent event) {
                processRequestException(event.getMsg());
            }
        };

        pauseMonitoringListener = new MonitorEventListener() {
            @Override
            public void execute(MonitorEvent event) {
                monitoringPauseEvent(event.getMsg());
            }
        };

        initComponents();
        // fix for box layout
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));

        if (restored) {
            triggerSaveState();
        } else {
            this.saved = false;
        }

        this.manageJobButtonText = manageJobsButton.getText();
        init();
    }

    /**
     * Callback for WpsProcessJobDialog
     *
     * @param newCount New Count of Jobs
     */
    public void jobCountChanged(final Integer newCount) {
        evaluateJobState(newCount);
        changeManageJobText(newCount);
    }

    private void changeManageJobText(final Integer jobCount) {
        String text = manageJobButtonText + '(' + jobCount + ')';
        manageJobsButton.setText(text);
    }

    private void init() {
        setName(wpsProcess.getIdentifier());
        testRequestTextArea.setEditorKit(new XMLEditorKit());
        
        this.wpsProcessJobDialog = new WpsProcessJobDialog(mainFrame, this, wpsProcess);
        this.measuredDataDialog = new MeasuredDataDialog(mainFrame, wpsProcess);

        processNameLabel.setText(wpsProcess.getIdentifier());
        setEnteredTestRequest(wpsProcess.getRawRequest());

        if (wpsProcess.isWpsException()) {
            indicateError();
        }

        registerMonitoringEvents();
        jobCountChanged(wpsProcessJobDialog.getCountOfJobs());
    }

    private void evaluateJobState(final Integer jobCount) {
        if (jobCount > 0) {
            if (isMonitoringActive()) {
                triggerMonitoringCase();
            } else {
                triggerPauseCase();
            }
        } else {
            triggerNoJobsCase();
        }
    }

    private Boolean isMonitoringActive() {
        return !mainFrame.getMonitorReference()
                .ServicegetMonitorControl()
                .isMonitoringPaused(wpsProcess.getWps().getEndpoint(), wpsProcess.getIdentifier());
    }

    private void triggerNoJobsCase() {
        stopMonitoringButton.setEnabled(false);
        rescheduleButton.setEnabled(false);
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

            eventHandler.registerListener("measurement.wpsjob.wpsexception", exceptionListener);
            eventHandler.registerListener("monitorcontrol.pauseMonitoring", pauseMonitoringListener);
        } catch (EventNotFoundException ex) {
            LOG.warn("Can't register WpsProcessPanel Listener at EventHandler.", ex);
        }
    }

    private void removeMonitoringEvents() {
        try {
            MonitorEventHandler eventHandler = mainFrame
                    .getMonitorReference()
                    .getEventHandler();

            eventHandler.removeListener("measurement.wpsjob.wpsexception", exceptionListener);
            eventHandler.removeListener("monitorcontrol.pauseMonitoring", pauseMonitoringListener);
        } catch (EventNotFoundException ex) {
            LOG.warn("Can't register WpsProcessPanel Listener at EventHandler.", ex);
        }
    }

    private void triggerSaveState() {
        this.saved = true;

        saveProcessButton.setBackground(new Color(153, 255, 153));
        manageJobsButton.setEnabled(true);
        stopMonitoringButton.setEnabled(true);
        showMeasuredDataButton.setEnabled(true);
    }

    private void processRequestException(final Object msg) {
        if (msg instanceof WpsProcessEntity) {
            WpsProcessEntity process = (WpsProcessEntity) msg;
            processRequestException(process);
        }
    }

    private void processRequestException(final WpsProcessEntity process) {
        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            indicateError();
        }
    }

    private void monitoringPauseEvent(final Object msg) {
        if (msg instanceof WpsProcessEntity) {
            WpsProcessEntity process = (WpsProcessEntity) msg;
            monitoringPauseEvent(process);
        }
    }

    private void monitoringPauseEvent(final WpsProcessEntity process) {
        if (wpsProcess.getIdentifier().equals(process.getIdentifier())) {
            rescheduleButton.setEnabled(true);
        }
    }

    private void indicateError() {
        this.setBackground(new Color(255, 102, 102));
    }

    private void clearError() {
        this.setBackground(new Color(240, 240, 240));
    }

    /**
     * Gets the process entity of this wpspanel
     *
     * @return WpsProcessEntity instance
     */
    public WpsProcessEntity getWpsProcess() {
        return wpsProcess;
    }

    public String getEnteredRequest() {
        return testRequestTextArea.getText();
    }

    public void setEnteredTestRequest(final String testRequest) {
        testRequestTextArea.setText(testRequest);
        testRequestTextArea.setCaretPosition(0);
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
        manageJobsButton = new JButton();
        showMeasuredDataButton = new JButton();
        stopMonitoringButton = new JButton();
        rescheduleButton = new JButton();
        jButton1 = new JButton();
        deleteProcessButton = new JButton();
        saveProcessButton = new JButton();
        JPanel jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        testRequestTextArea = new JEditorPane();
        JScrollPane jScrollPane2 = new JScrollPane();
        JPanel jPanel3 = new JPanel();
        processNameLabel = new JLabel();

        setBackground(new Color(255, 255, 255));

        jPanel1.setBorder(BorderFactory.createTitledBorder(""));

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);

        manageJobsButton.setIcon(new ImageIcon(getClass().getResource("/icons/clock.png"))); // NOI18N
        manageJobsButton.setText("Manage Jobs");
        manageJobsButton.setEnabled(false);
        manageJobsButton.setName("manageJobsButton"); // NOI18N
        manageJobsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manageJobsButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(manageJobsButton);

        showMeasuredDataButton.setIcon(new ImageIcon(getClass().getResource("/icons/measure.png"))); // NOI18N
        showMeasuredDataButton.setText("Show Measured Data");
        showMeasuredDataButton.setEnabled(false);
        showMeasuredDataButton.setName("showMeasuredDataButton"); // NOI18N
        showMeasuredDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showMeasuredDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showMeasuredDataButton);

        stopMonitoringButton.setIcon(new ImageIcon(getClass().getResource("/icons/stop.png"))); // NOI18N
        stopMonitoringButton.setText("Stop Monitoring");
        stopMonitoringButton.setEnabled(false);
        stopMonitoringButton.setName("stopMonitoringButton"); // NOI18N
        stopMonitoringButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopMonitoringButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stopMonitoringButton);

        rescheduleButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        rescheduleButton.setText("Start Monitoring");
        rescheduleButton.setEnabled(false);
        rescheduleButton.setName("resheduleButton"); // NOI18N
        rescheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rescheduleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(rescheduleButton);

        jButton1.setIcon(new ImageIcon(getClass().getResource("/icons/testProcess.png"))); // NOI18N
        jButton1.setText("Test Request");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        deleteProcessButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteProcessButton.setText("Delete Process");
        deleteProcessButton.setName("deleteProcessButton"); // NOI18N
        deleteProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteProcessButton);

        saveProcessButton.setBackground(new Color(255, 51, 0));
        saveProcessButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveProcessButton.setText("Save Process");
        saveProcessButton.setName("saveProcessButton"); // NOI18N
        saveProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveProcessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveProcessButton);

        jPanel2.setBorder(BorderFactory.createTitledBorder("Test-Request"));

        testRequestTextArea.setMaximumSize(null);
        testRequestTextArea.setPreferredSize(null);
        jScrollPane1.setViewportView(testRequestTextArea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane2.setBorder(null);
        jScrollPane2.setMaximumSize(new Dimension(610, 32767));

        jPanel3.setMaximumSize(null);

        processNameLabel.setFont(new Font("Tahoma", 0, 24)); // NOI18N
        processNameLabel.setText("jLabel3");
        processNameLabel.setName("processNameLabel"); // NOI18N

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(processNameLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(processNameLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel3);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 601, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void manageJobsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_manageJobsButtonActionPerformed
        wpsProcessJobDialog.setVisible(true);
    }//GEN-LAST:event_manageJobsButtonActionPerformed

    private void saveProcessButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveProcessButtonActionPerformed
        String testRequest = testRequestTextArea.getText();
        Boolean inserted = true;
        clearError();

        if (!saved) {
            inserted = saveProcess();
        }

        if (inserted) {
            mainFrame.getMonitorReference()
                    .ServicegetMonitorControl()
                    .setTestRequest(wpsProcess, testRequest);
        }

        if (wpsProcess.isWpsException()) {
            mainFrame.getMonitorReference()
                    .ServicegetMonitorControl()
                    .resumeMonitoring(wpsProcess);
        }
    }//GEN-LAST:event_saveProcessButtonActionPerformed

    public Boolean saveProcess() {
        Boolean inserted = mainFrame.getMonitorReference()
                .ServicegetMonitorControl()
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
        Boolean yes = MessageDialogs.showQuestionDialog(this,
                "Delete WPS Process Entry",
                "Are you sure you want to permanently delete this WPS-Process-Entry from the Monitor?");

        if (yes) {
            if (saved) {
                mainFrame.getMonitorReference()
                        .ServicegetMonitorControl()
                        .deleteProcess(wpsProcess);
            }

            removeMonitoringEvents();

            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }//GEN-LAST:event_deleteProcessButtonActionPerformed

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
                .ServicegetMonitorControl()
                .resumeMonitoring(wpsProcess);

        triggerMonitoringCase();
    }//GEN-LAST:event_rescheduleButtonActionPerformed

    private void stopMonitoringButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_stopMonitoringButtonActionPerformed

        mainFrame.getMonitorReference()
                .ServicegetMonitorControl()
                .pauseMonitoring(wpsProcess);

        triggerPauseCase();
    }//GEN-LAST:event_stopMonitoringButtonActionPerformed

    private void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        WpsRequestTesterDialog wpsProcessTest = new WpsRequestTesterDialog(mainFrame, this);
        wpsProcessTest.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteProcessButton;
    private JButton jButton1;
    private JScrollPane jScrollPane1;
    private JButton manageJobsButton;
    private JLabel processNameLabel;
    private JButton rescheduleButton;
    private JButton saveProcessButton;
    private JButton showMeasuredDataButton;
    private JButton stopMonitoringButton;
    private JEditorPane testRequestTextArea;
    // End of variables declaration//GEN-END:variables
}
