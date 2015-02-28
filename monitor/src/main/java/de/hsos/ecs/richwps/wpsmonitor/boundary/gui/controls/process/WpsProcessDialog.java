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
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * Dialog to show and allow operations on WpsProcessEntities and
 * {@link WpsProcessEntity} Jobs.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessDialog extends JDialog {

    private final WpsMonitorAdminGui monitorMainFrame;
    private final WpsEntity wps;

    /**
     * Creates a new WpsProcessDialog instance.
     *
     * @param monitorMainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param wps {@link WpsEntity} to request the right data from the monitor
     */
    public WpsProcessDialog(final WpsMonitorAdminGui monitorMainFrame, final WpsEntity wps) {
        super(monitorMainFrame, true);
        this.wps = wps;
        this.monitorMainFrame = Validate.notNull(monitorMainFrame, "mainFrame");

        initComponents();
        init();
    }

    private void init() {
        setName(wps.getEndpoint().toString() + " ProcessDialog");
        setTitle(getTitle() + " of " + wps.getEndpoint().toString());
        List<WpsProcessEntity> processesOfWps = monitorMainFrame.getMonitorReference()
                .ServicegetMonitorControl()
                .getProcesses(wps);

        for (WpsProcessEntity processEntity : processesOfWps) {
            WpsProcessPanel processPane = createSavedProcessPanel(processEntity);
            addProcessPane.add(processPane);
        }

        addProcessPane.revalidate();
    }

    /**
     * reinit the dialog. Re-request the monitor for the necessary data.
     */
    public void reInit() {
        addProcessPane.removeAll();
        init();
        revalidate();
        repaint();
    }

    /**
     * Adds a new Process.
     *
     * @param processName
     */
    public void addProcess(final String processName) {
        WpsProcessEntity p = new WpsProcessEntity(processName, wps);

        WpsProcessPanel pPanel = createAndAddProcessPanel(p);
        pPanel.saveProcess();
    }

    private Boolean isNotEmptyProcessName() {
        return !"".equals(processIdentifierInput.getText().trim());
    }

    private WpsProcessPanel createProcessPanel(WpsProcessEntity processEntity) {
        return new WpsProcessPanel(monitorMainFrame, addProcessPane, processEntity);
    }

    private WpsProcessPanel createSavedProcessPanel(WpsProcessEntity processEntity) {
        return new WpsProcessPanel(monitorMainFrame, addProcessPane, processEntity, true);
    }

    private void addProcessPanel(WpsProcessPanel panel) {
        addProcessPane.add(panel, BorderLayout.SOUTH);
        addProcessPane.revalidate();
    }

    private WpsProcessPanel createAndAddProcessPanel(WpsProcessEntity processEntity) {
        WpsProcessPanel panel = createProcessPanel(processEntity);
        addProcessPanel(panel);

        return panel;
    }

    @Override
    public void setVisible(boolean b) {
        setLocationRelativeTo(monitorMainFrame);

        super.setVisible(b);
    }

    private Boolean processAlreadyRegistred(final String processName) {
        return monitorMainFrame.getMonitorReference()
                .ServicegetMonitorControl()
                .isProcessExists(wps.getEndpoint(), processName);
    }

    private Boolean isNotSavedWithSameNameExists(final String processName) {
        Component[] components = addProcessPane.getComponents();

        for (int i = 0; i < addProcessPane.getComponentCount(); i++) {
            if (components[i] instanceof WpsProcessPanel) {
                WpsProcessPanel p = (WpsProcessPanel) components[i];
                
                return p.getWpsProcess().getIdentifier().equals(processName);
            }
        }

        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPanel decoPanel = new JPanel();
        JLabel processIdentifierDecoText = new JLabel();
        processIdentifierInput = new JTextField();
        addProcessButton = new JButton();
        JPanel jPanel1 = new JPanel();
        wpsProcessScrollPane = new JScrollPane();
        addProcessPane = new JPanel();
        closeButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manage Processes");
        setIconImage(new ImageIcon(getClass().getResource("/icons/manage.png")).getImage());
        setResizable(false);

        decoPanel.setBorder(BorderFactory.createTitledBorder("Add Process to WPS"));

        processIdentifierDecoText.setText("Process-Identifier");

        processIdentifierInput.setName("processIdentifierInput"); // NOI18N
        processIdentifierInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                processIdentifierInputActionPerformed(evt);
            }
        });

        addProcessButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addProcessButton.setText("Add Process");
        addProcessButton.setName("addProcessButton"); // NOI18N
        addProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addProcessButtonActionPerformed(evt);
            }
        });

        GroupLayout decoPanelLayout = new GroupLayout(decoPanel);
        decoPanel.setLayout(decoPanelLayout);
        decoPanelLayout.setHorizontalGroup(decoPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processIdentifierDecoText)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(processIdentifierInput, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addProcessButton)
                .addContainerGap())
        );
        decoPanelLayout.setVerticalGroup(decoPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decoPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addProcessButton)
                    .addComponent(processIdentifierDecoText)
                    .addComponent(processIdentifierInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(BorderFactory.createTitledBorder("Registred Processes"));

        wpsProcessScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wpsProcessScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        addProcessPane.setBackground(new Color(255, 255, 255));
        addProcessPane.setName("addProcessPane"); // NOI18N
        addProcessPane.setLayout(new BoxLayout(addProcessPane, BoxLayout.PAGE_AXIS));
        wpsProcessScrollPane.setViewportView(addProcessPane);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsProcessScrollPane)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsProcessScrollPane, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );

        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decoPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(decoPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addProcessButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addProcessButtonActionPerformed

        if (isNotEmptyProcessName()) {

            String wpsProcessIdentifier = processIdentifierInput.getText();
            try {
                if (!isNotSavedWithSameNameExists(wpsProcessIdentifier) && !processAlreadyRegistred(wpsProcessIdentifier)) {
                    processIdentifierInput.setText("");
                    WpsProcessEntity wpsProcessEntity = new WpsProcessEntity(wpsProcessIdentifier, wps);

                    createAndAddProcessPanel(wpsProcessEntity);
                } else {
                    MessageDialogs.showError(this,
                            "Already registered",
                            "The process is already registered in the monitor. Please choose another process identifier."
                    );
                }
            } catch (IllegalArgumentException ex) {
                MessageDialogs.showError(this,
                        "The given Processname is not valid!",
                        ex.getMessage()
                );
            }
        }
    }//GEN-LAST:event_addProcessButtonActionPerformed

    private void processIdentifierInputActionPerformed(ActionEvent evt) {//GEN-FIRST:event_processIdentifierInputActionPerformed
        addProcessButtonActionPerformed(evt);
    }//GEN-LAST:event_processIdentifierInputActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addProcessButton;
    private JPanel addProcessPane;
    private JButton closeButton;
    private JTextField processIdentifierInput;
    private JScrollPane wpsProcessScrollPane;
    // End of variables declaration//GEN-END:variables
}
