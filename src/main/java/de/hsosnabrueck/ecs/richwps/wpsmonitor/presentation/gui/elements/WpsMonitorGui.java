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
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.JTextField;

/**
 * Main frame of the monitor GUI. The monitor gets the Monitor instance as
 * constructor dependencie. The monitor GUI allows to add, remove or edit WPS,
 * WPSProcesses and Triggers. You can also show and remove the measured data by
 * wps process. You can stop and resume the monitoring of wps processes.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorGui extends javax.swing.JFrame {

    private final Monitor monitor;

    /**
     * Creates new form WpsMonitorGui instance
     *
     * @param monitor {@link Monitor} reference
     */
    public WpsMonitorGui(final Monitor monitor) {
        this.monitor = Param.notNull(monitor, "monitor");

        initComponents();
        init();

        setLocationRelativeTo(null);
    }

    /**
     * Load data
     */
    private void init() {
        List<WpsEntity> wpsList = monitor.getMonitorControl()
                .getWpsList();

        for (WpsEntity wps : wpsList) {
            createAndAddWpsPanel(wps);
        }
    }

    private Boolean isCreateFieldsNotEmpty() {
        return !(wpsToAddField.getText().trim().equalsIgnoreCase("")
                || wpsToAddUriField.getText().trim().equalsIgnoreCase(""));
    }

    public JTextField getWpsToAddField() {
        return wpsToAddField;
    }

    public void setWpsToAddField(JTextField wpsToAddField) {
        this.wpsToAddField = wpsToAddField;
    }

    public JTextField getWpsToAddUriField() {
        return wpsToAddUriField;
    }

    public void setWpsToAddUriField(JTextField wpsToAddUriField) {
        this.wpsToAddUriField = wpsToAddUriField;
    }

    public Monitor getMonitorReference() {
        return monitor;
    }

    private void resetAddWpsFields() {
        getWpsToAddField()
                .setText("");
        getWpsToAddUriField()
                .setText("");

        getWpsToAddField()
                .requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel wpsIdentifierDecoText = new javax.swing.JLabel();
        wpsToAddField = new javax.swing.JTextField();
        javax.swing.JLabel wpsUrlDecoText = new javax.swing.JLabel();
        wpsToAddUriField = new javax.swing.JTextField();
        addWpsButton = new javax.swing.JButton();
        javax.swing.JPanel decoPanelWpsScroll = new javax.swing.JPanel();
        wpsScrollPane = new javax.swing.JScrollPane();
        wpsAddPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu monitorMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem settingsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu aboutMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WPS-Monitor Control Interface");
        setBackground(new java.awt.Color(255, 255, 255));
        setName("wpsGui"); // NOI18N
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Register a WPS Server in the Monitor"));

        wpsIdentifierDecoText.setText("WPS Identifier");

        wpsToAddField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpsToAddFieldActionPerformed(evt);
            }
        });

        wpsUrlDecoText.setText("WPS URL");

        wpsToAddUriField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpsToAddUriFieldActionPerformed(evt);
            }
        });

        addWpsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addWpsButton.setText("Add WPS");
        addWpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWpsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsIdentifierDecoText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wpsToAddField, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(wpsUrlDecoText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wpsToAddUriField, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addWpsButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wpsIdentifierDecoText)
                    .addComponent(wpsToAddField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wpsUrlDecoText)
                    .addComponent(wpsToAddUriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addWpsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        decoPanelWpsScroll.setBorder(javax.swing.BorderFactory.createTitledBorder("Registred WPS Server"));

        wpsScrollPane.setBorder(null);
        wpsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wpsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        wpsAddPanel.setBackground(new java.awt.Color(255, 255, 255));
        wpsAddPanel.setLayout(new javax.swing.BoxLayout(wpsAddPanel, javax.swing.BoxLayout.PAGE_AXIS));
        wpsScrollPane.setViewportView(wpsAddPanel);

        javax.swing.GroupLayout decoPanelWpsScrollLayout = new javax.swing.GroupLayout(decoPanelWpsScroll);
        decoPanelWpsScroll.setLayout(decoPanelWpsScrollLayout);
        decoPanelWpsScrollLayout.setHorizontalGroup(
            decoPanelWpsScrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelWpsScrollLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsScrollPane)
                .addContainerGap())
        );
        decoPanelWpsScrollLayout.setVerticalGroup(
            decoPanelWpsScrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelWpsScrollLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(decoPanelWpsScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(decoPanelWpsScroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        monitorMenu.setText("Monitor");

        settingsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(settingsMenuItem);
        monitorMenu.add(jSeparator1);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(exitMenuItem);

        menuBar.add(monitorMenu);

        aboutMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        aboutMenu.add(aboutMenuItem);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 100, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addWpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWpsButtonActionPerformed
        if (isCreateFieldsNotEmpty()) {
            try {
                WpsEntity wps = new WpsEntity(getWpsToAddField().getText(),
                        getWpsToAddUriField().getText()
                );

                Boolean isWpsCreated = monitor
                        .getMonitorControl()
                        .createWps(wps);

                if (isWpsCreated) {
                    resetAddWpsFields();
                    createAndAddWpsPanel(wps);
                } else {
                    MessageDialogs.showError(this,
                            "Error",
                            "Can't register Wps. Maybe the Wps is already registred."
                    );
                }
            } catch (MalformedURLException ex) {
                showUriConvertError();
            } catch (URISyntaxException ex) {
                showUriConvertError();
            }
        } else {
            MessageDialogs.showError(this,
                    "Error",
                    "One of the fields is empty!"
            );
        }
    }//GEN-LAST:event_addWpsButtonActionPerformed

    private void createAndAddWpsPanel(WpsEntity wps) {
        WpsPanel panel = createWpsPanel(wps);
        addWpsPanel(panel);
    }

    private WpsPanel createWpsPanel(WpsEntity wps) {
        return new WpsPanel(this, wpsAddPanel, wps);
    }

    private void addWpsPanel(WpsPanel panel) {
        wpsAddPanel.add(panel, BorderLayout.PAGE_START);
        wpsAddPanel.revalidate();
    }

    private void showUriConvertError() {
        MessageDialogs.showError(this,
                "Error",
                "The entered URI is not valid!"
        );
    }

    /**
     * reInit the monitorgui
     */
    public void reInit() {
        init();
        revalidate();
        repaint();
    }

    private void wpsToAddFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpsToAddFieldActionPerformed
        addWpsButtonActionPerformed(evt);
    }//GEN-LAST:event_wpsToAddFieldActionPerformed

    private void wpsToAddUriFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpsToAddUriFieldActionPerformed
        addWpsButtonActionPerformed(evt);
    }//GEN-LAST:event_wpsToAddUriFieldActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        new MonitorProperties(this, true).setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        Boolean yes = MessageDialogs
                .showQuestionDialog(this, "Close Monitor?", "Are you sure to close this Application? The Monitor will be stoped.");

        if (yes) {
            System.exit(0);
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new About(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addWpsButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel wpsAddPanel;
    private javax.swing.JScrollPane wpsScrollPane;
    private javax.swing.JTextField wpsToAddField;
    private javax.swing.JTextField wpsToAddUriField;
    // End of variables declaration//GEN-END:variables
}
