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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.EventNotFound;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Representation of a WPS entry.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsPanel extends javax.swing.JPanel {

    private final JPanel addPanelParent;
    private final WpsMonitorGui monitorMainFrame;
    private WpsProcessDialog wpsProcessDialog;
    private WpsEntity wps;

    /**
     * Constructor.
     * 
     * @param monitorMainFrame Reference to the WpsMonitorGui of this gui
     * @param addPanelParent Parent panel; is needed for delete operation
     * @param wps {@link WpsEntity} to request the right data from the monitor
     */
    public WpsPanel(WpsMonitorGui monitorMainFrame, JPanel addPanelParent, final WpsEntity wps) {
        this.wps = Validate.notNull(wps, "wps");
        this.addPanelParent = Validate.notNull(addPanelParent, "parent");
        this.monitorMainFrame = Validate.notNull(monitorMainFrame, "mainFrame");

        initComponents();
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));
        
        init();
    }
    
    private void init() {
        this.wpsProcessDialog = new WpsProcessDialog(monitorMainFrame, wps, true);
        setWpsTextLabels(wps);

        registerMonitoringPausedEvent();
    }
    
    /**
     * reinit the form - all data will be rerequestet 
     */
    public void reInit() {
        init();
        revalidate();
        repaint();
    }

    /**
     * Listener - if a WpsProcess's job is paused
     */
    private void registerMonitoringPausedEvent() {
        try {
            monitorMainFrame
                    .getMonitorReference()
                    .getEventHandler()
                    .registerListener("measurement.wpsjob.wpsexception", new MonitorEventListener() {

                        @Override
                        public void execute(MonitorEvent event) {

                            if (event.getMsg() instanceof WpsProcessEntity) {
                                WpsProcessEntity wpsProcess = (WpsProcessEntity) event.getMsg();
                                processMonitoringPaused(wpsProcess);
                            }
                        }

                    });
        } catch (EventNotFound ex) {
            Logger.getLogger(WpsMonitorGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Getter for the edit dialog
     * 
     * @return {@link WpsEntity} instance
     */
    public WpsEntity getWps() {
        return wps;
    }

    /**
     * Updates a wps 
     * 
     * @param wps WpsEntity instance
     */
    public void updateWps(WpsEntity wps) {
        String oldIdentifier = this.wps.getIdentifier();

        this.wps = this.monitorMainFrame
                .getMonitorReference()
                .getMonitorControl()
                .updateWps(oldIdentifier, wps);
        
        setWpsTextLabels(wps);
        revalidate();
    }
    
    private void setWpsTextLabels(final WpsEntity wps) {
        this.wpsNameLabel.setText(wps.getIdentifier());
        this.wpsUriLabel.setText(wps.getUri().toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        wpsNameLabel = new javax.swing.JLabel();
        wpsUriLabel = new javax.swing.JLabel();
        errorIcon = new javax.swing.JLabel();
        editWpsButton = new javax.swing.JButton();
        addProcessToWpsButton = new javax.swing.JButton();
        deleteWpsButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        wpsNameLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        wpsNameLabel.setText("<Wps Name>");

        wpsUriLabel.setText("<URI>");

        errorIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancel-round.png"))); // NOI18N
        errorIcon.setToolTipText("One or more Processes encountered a Wps-Process Error");
        errorIcon.setEnabled(false);

        editWpsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        editWpsButton.setText("Edit Wps");
        editWpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editWpsButtonActionPerformed(evt);
            }
        });

        addProcessToWpsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addProcessToWpsButton.setText("Add or Edit Processes");
        addProcessToWpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProcessToWpsButtonActionPerformed(evt);
            }
        });

        deleteWpsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteWpsButton.setText("Delete WPS");
        deleteWpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteWpsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wpsNameLabel)
                    .addComponent(wpsUriLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                .addComponent(errorIcon)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editWpsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteWpsButton))
                    .addComponent(addProcessToWpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(errorIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteWpsButton)
                                .addComponent(editWpsButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addProcessToWpsButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(wpsNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wpsUriLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void deleteWpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteWpsButtonActionPerformed
        Boolean sure = MessageDialogs.showQuestionDialog(this,
                "Delete WPS",
                "Are you sure you want to permanently delete this WPS out of the Monitor?"
        );

        if (sure) {
            monitorMainFrame.getMonitorReference()
                    .getMonitorControl()
                    .deleteWps(wps);

            addPanelParent.remove(this);
            addPanelParent.revalidate();
            addPanelParent.repaint(); // repaint required, otherwise the last element will not disappear
        }
    }//GEN-LAST:event_deleteWpsButtonActionPerformed

    private void addProcessToWpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProcessToWpsButtonActionPerformed
        wpsProcessDialog.setVisible(true);
        hideErrorIndicator();
    }//GEN-LAST:event_addProcessToWpsButtonActionPerformed

    private void editWpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editWpsButtonActionPerformed
        new WpsEditDialog(monitorMainFrame, this, true).setVisible(true);
    }//GEN-LAST:event_editWpsButtonActionPerformed

    public WpsProcessDialog getWpsProcessDialog() {
        return wpsProcessDialog;
    }

    public void setWpsProcessDialog(WpsProcessDialog wpsProcessDialog) {
        this.wpsProcessDialog = wpsProcessDialog;
    }

    /**
     * Listener method for a MonitorEvent
     * 
     * @param process WpsProcessEntity instance
     */
    public void processMonitoringPaused(WpsProcessEntity process) {
        if (process.getWps().getIdentifier().equals(wps.getIdentifier())) {
            showErrorIndicator();
        }
    }

    /**
     * hides the error indicator
     */
    public void hideErrorIndicator() {
        errorIcon.setEnabled(false);
    }

    /**
     * displays the error indicator
     */
    public void showErrorIndicator() {
        errorIcon.setEnabled(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProcessToWpsButton;
    private javax.swing.JButton deleteWpsButton;
    private javax.swing.JButton editWpsButton;
    private javax.swing.JLabel errorIcon;
    private javax.swing.JLabel wpsNameLabel;
    private javax.swing.JLabel wpsUriLabel;
    // End of variables declaration//GEN-END:variables
}
