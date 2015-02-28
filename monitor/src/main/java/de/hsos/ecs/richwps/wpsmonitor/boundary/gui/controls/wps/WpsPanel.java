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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.wps;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.process.WpsProcessDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.control.event.EventNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventListener;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

    private final MonitorEventListener wpsExceptionListener;

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

        wpsExceptionListener = new MonitorEventListener() {
            @Override
            public void execute(MonitorEvent event) {
                processMonitoringPaused(event.getMsg());
            }
        };
        
        init();
    }

    private void init() {
        this.setName(wps.getEndpoint().toString());
        this.wpsProcessDialog = new WpsProcessDialog(monitorMainFrame, wps);
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
                    .registerListener("measurement.wpsjob.wpsexception", wpsExceptionListener);
        } catch (EventNotFoundException ex) {
            Logger.getLogger(WpsMonitorAdminGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void removeMonitoringPausedEvent() {
        try {
            monitorMainFrame
                    .getMonitorReference()
                    .getEventHandler()
                    .removeListener("measurement.wpsjob.wpsexception", wpsExceptionListener);
        } catch (EventNotFoundException ex) {
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
        URL oldEndpoint = this.wps.getEndpoint();

        this.wps = this.monitorMainFrame
                .getMonitorReference()
                .ServicegetMonitorControl()
                .updateWps(oldEndpoint, wps.getEndpoint());

        setWpsTextLabels(wps);
        revalidate();
    }

    private void setWpsTextLabels(final WpsEntity wps) {
        this.wpsUriLabel.setText(wps.getEndpoint().toString());
    }

    /**
     * Gets the wpsProcessDialog instance.
     *
     * @return WpsProcessDialog instance
     */
    public WpsProcessDialog getWpsProcessDialog() {
        return wpsProcessDialog;
    }

    /**
     * Listener method for a MonitorEvent
     *
     * @param process WpsProcessEntity instance
     */
    public void processMonitoringPaused(final WpsProcessEntity process) {
        if (process.getWps().getEndpoint().equals(wps.getEndpoint())) {
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

    private void processMonitoringPaused(final Object msg) {
        if (msg instanceof WpsProcessEntity) {
            processMonitoringPaused((WpsProcessEntity) msg);
        }
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
        errorIcon = new JLabel();
        editWpsButton = new JButton();
        manageProcessesButton = new JButton();
        deleteWpsButton = new JButton();
        JScrollPane jScrollPane1 = new JScrollPane();
        JPanel jPanel2 = new JPanel();
        wpsUriLabel = new JLabel();

        setBackground(new Color(255, 255, 255));

        jPanel1.setBorder(BorderFactory.createTitledBorder(""));

        errorIcon.setIcon(new ImageIcon(getClass().getResource("/icons/cancel-round.png"))); // NOI18N
        errorIcon.setToolTipText("One or more Processes encountered a Wps-Process Error");
        errorIcon.setEnabled(false);

        editWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        editWpsButton.setText("Edit WPS");
        editWpsButton.setName("editWpsButton"); // NOI18N
        editWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editWpsButtonActionPerformed(evt);
            }
        });

        manageProcessesButton.setIcon(new ImageIcon(getClass().getResource("/icons/manage.png"))); // NOI18N
        manageProcessesButton.setText("Manage Processes");
        manageProcessesButton.setName("manageProcessesButton"); // NOI18N
        manageProcessesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manageProcessesButtonActionPerformed(evt);
            }
        });

        deleteWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteWpsButton.setText("Delete WPS");
        deleteWpsButton.setName("deleteWpsButton"); // NOI18N
        deleteWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteWpsButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(null);

        wpsUriLabel.setText("<WPS Endpoint>");
        wpsUriLabel.setName("wpsEndpointLabel"); // NOI18N

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(wpsUriLabel)
                .addContainerGap(245, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(wpsUriLabel)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(errorIcon)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editWpsButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteWpsButton, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
                    .addComponent(manageProcessesButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(errorIcon, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteWpsButton)
                                .addComponent(editWpsButton)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(manageProcessesButton)
                        .addContainerGap())
                    .addComponent(jScrollPane1)))
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

    private void deleteWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteWpsButtonActionPerformed
        Boolean sure = MessageDialogs.showQuestionDialog(this,
                "Delete WPS Entry",
                "Are you sure you want to permanently delete this WPS-Entry from the Monitor?"
        );

        if (sure) {
            monitorMainFrame.getMonitorReference()
                    .ServicegetMonitorControl()
                    .deleteWps(wps);

            removeMonitoringPausedEvent();
            
            addPanelParent.remove(this);
            addPanelParent.revalidate();

            // repaint required, otherwise the last element will not disappear
            addPanelParent.repaint();
        }
    }//GEN-LAST:event_deleteWpsButtonActionPerformed

    private void manageProcessesButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_manageProcessesButtonActionPerformed
        wpsProcessDialog.setVisible(true);
        hideErrorIndicator();
    }//GEN-LAST:event_manageProcessesButtonActionPerformed

    private void editWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editWpsButtonActionPerformed
        new WpsEditDialog(monitorMainFrame, this).setVisible(true);
    }//GEN-LAST:event_editWpsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteWpsButton;
    private JButton editWpsButton;
    private JLabel errorIcon;
    private JButton manageProcessesButton;
    private JLabel wpsUriLabel;
    // End of variables declaration//GEN-END:variables
}
