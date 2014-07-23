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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.wps;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process.WpsProcessDialog;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.EventNotFound;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEvent;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.event.MonitorEventListener;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorAdminGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

/**
 * Representation of a WPS entry.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsPanel extends javax.swing.JPanel {

    private final JPanel addPanelParent;
    private final WpsMonitorAdminGui monitorMainFrame;
    private WpsProcessDialog wpsProcessDialog;
    private WpsEntity wps;

    /**
     * Constructor.
     *
     * @param monitorMainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param addPanelParent Parent panel; is needed for delete operation
     * @param wps {@link WpsEntity} to request the right data from the monitor
     */
    public WpsPanel(WpsMonitorAdminGui monitorMainFrame, JPanel addPanelParent, final WpsEntity wps) {
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
     * reinit the form - all data will be re-requested.
     */
    public void reInit() {
        init();
        revalidate();
        repaint();
    }

    /**
     * Listener - if a WpsProcess's job is paused.
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
            Logger.getLogger(WpsMonitorAdminGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Getter for the edit dialog.
     *
     * @return {@link WpsEntity} instance
     */
    public WpsEntity getWps() {
        return wps;
    }

    /**
     * Updates a wps.
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

        JPanel jPanel1 = new JPanel();
        wpsNameLabel = new JLabel();
        wpsUriLabel = new JLabel();
        errorIcon = new JLabel();
        editWpsButton = new JButton();
        addProcessToWpsButton = new JButton();
        deleteWpsButton = new JButton();

        setBackground(new Color(255, 255, 255));

        wpsNameLabel.setFont(new Font("Tahoma", 0, 24)); // NOI18N
        wpsNameLabel.setText("<Wps Name>");

        wpsUriLabel.setText("<URI>");

        errorIcon.setIcon(new ImageIcon(getClass().getResource("/icons/cancel-round.png"))); // NOI18N
        errorIcon.setToolTipText("One or more Processes encountered a Wps-Process Error");
        errorIcon.setEnabled(false);

        editWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        editWpsButton.setText("Edit Wps");
        editWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editWpsButtonActionPerformed(evt);
            }
        });

        addProcessToWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addProcessToWpsButton.setText("Add or Edit Processes");
        addProcessToWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addProcessToWpsButtonActionPerformed(evt);
            }
        });

        deleteWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteWpsButton.setText("Delete WPS");
        deleteWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteWpsButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(wpsNameLabel)
                    .addComponent(wpsUriLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                .addComponent(errorIcon)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editWpsButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteWpsButton))
                    .addComponent(addProcessToWpsButton, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(errorIcon, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteWpsButton)
                                .addComponent(editWpsButton)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addProcessToWpsButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(wpsNameLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wpsUriLabel)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void deleteWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteWpsButtonActionPerformed
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

    private void addProcessToWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addProcessToWpsButtonActionPerformed
        wpsProcessDialog.setVisible(true);
        hideErrorIndicator();
    }//GEN-LAST:event_addProcessToWpsButtonActionPerformed

    private void editWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editWpsButtonActionPerformed
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
    private JButton addProcessToWpsButton;
    private JButton deleteWpsButton;
    private JButton editWpsButton;
    private JLabel errorIcon;
    private JLabel wpsNameLabel;
    private JLabel wpsUriLabel;
    // End of variables declaration//GEN-END:variables
}
