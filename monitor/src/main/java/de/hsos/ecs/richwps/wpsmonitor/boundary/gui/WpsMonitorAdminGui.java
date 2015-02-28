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

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.miscs.AboutDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.miscs.MonitorPropertiesDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.datasource.DataSourceDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.wps.WpsPanel;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.miscs.LogViewerDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import de.hsos.ecs.richwps.wpsmonitor.control.MonitorException;
import de.hsos.ecs.richwps.wpsmonitor.data.config.MonitorConfigException;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * Main frame of the monitor GUI. The monitor gets the Monitor instance as
 * constructor dependency. The monitor GUI allows to add, remove or edit WPS,
 * WPSProcesses and Triggers. You can also show and remove the measured data of
 * the wps process. You can stop and resume the monitoring of wps processes.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsMonitorAdminGui extends JFrame {

    private final Monitor monitor;
    private final DataSourceDialog dsDialog;
    private final LogViewerDialog lvDialog;

    /**
     * Creates new WpsMonitorGui JFrame instance without DataSources.
     *
     * @param monitor {@link Monitor} reference
     * @param logDirectory Directory of monitor logs
     */
    public WpsMonitorAdminGui(final Monitor monitor, final String logDirectory) {
        this(monitor, logDirectory, new HashSet<DataSourceCreator>());
    }

    /**
     * Creates new WpsMonitorGui JFrame instance.
     *
     * @param monitor {@link Monitor} reference
     * @param logDirectory Directory of monitor logs
     * @param dataSources List of possible DataSources
     */
    public WpsMonitorAdminGui(final Monitor monitor, final String logDirectory, final Set<DataSourceCreator> dataSources) {
        initComponents();
        setLocationRelativeTo(null);

        this.monitor = Validate.notNull(monitor, "monitor");
        this.dsDialog = new DataSourceDialog(this, dataSources);
        this.lvDialog = new LogViewerDialog(this, Paths.get(logDirectory));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                exitMenuItemActionPerformed(null);
            }
        }) ;
        
        init();
    }

    /**
     * Load data
     */
    private void init() {
        List<WpsEntity> wpsList = monitor.ServicegetMonitorControl()
                .getWpsList();

        wpsAddPanel.removeAll();
        for (WpsEntity wps : wpsList) {
            createAndAddWpsPanel(wps);
        }
    }

    /**
     * reInit the monitorgui
     */
    public void reInit() {
        init();
        revalidate();
        repaint();
    }

    /**
     * Adds and returns new WpsPanel instance to the Mainframe.
     *
     * @param endpoint WPS Identifier String
     * @return The created WpsPanel instance
     */
    public WpsPanel addWps(final URL endpoint) {
        return addWps(endpoint, null);
    }

    /**
     * Finds the WpsPanel instance for the given WPS-Identifier.
     *
     * @param endpoint WPS-Identifier String
     * @return null if not found
     */
    public WpsPanel getPanel(final URL endpoint) {
        WpsPanel result = null;

        for (Component c : wpsAddPanel.getComponents()) {
            if (c instanceof WpsPanel) {
                WpsPanel search = (WpsPanel) c;

                if (search.getWps().getEndpoint().equals(endpoint)) {
                    result = search;
                }
            }
        }

        return result;
    }

    /**
     * Adds and returns a new WpsPanel instance to the Mainframe.
     *
     * @param endpoint WPS Uri
     * @param callFrame The Frame which calls this method
     * @return The created WpsPanel instance
     */
    public WpsPanel addWps(final URL endpoint, final Frame callFrame) {
        WpsPanel panel = null;

        WpsEntity wps = new WpsEntity(endpoint);

        Boolean isWpsCreated = monitor
                .ServicegetMonitorControl()
                .createWps(wps);

        if (isWpsCreated) {
            resetAddWpsFields();
            panel = createAndAddWpsPanel(wps);
        } else {
            MessageDialogs.showError(callFrame,
                    "Error",
                    "Can't register WPS. Maybe the WPS is already registred."
            );
        }

        return panel;
    }

    public void setWpsToAddUriField(final JTextField wpsToAddUriField) {
        this.newWpsEndpointField = wpsToAddUriField;
    }

    public Monitor getMonitorReference() {
        return monitor;
    }

    private void resetAddWpsFields() {
        newWpsEndpointField.setText("");
        newWpsEndpointField.requestFocus();
    }

    private WpsPanel createAndAddWpsPanel(final WpsEntity wps) {
        WpsPanel panel = createWpsPanel(wps);
        addWpsPanel(panel);

        return panel;
    }

    private WpsPanel createWpsPanel(final WpsEntity wps) {
        return new WpsPanel(this, wpsAddPanel, wps);
    }

    private void addWpsPanel(final WpsPanel panel) {
        wpsAddPanel.add(panel, BorderLayout.PAGE_START);
        wpsAddPanel.revalidate();
    }

    private Boolean isEndpointFieldEmpty() {
        return "".equalsIgnoreCase(newWpsEndpointField.getText().trim());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlPanel = new JPanel();
        JPanel jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        JLabel wpsUrlDecoText = new JLabel();
        newWpsEndpointField = new JTextField();
        jPanel3 = new JPanel();
        addWpsButton = new JButton();
        dataSourcesButton = new JButton();
        JPanel decoPanelWpsScroll = new JPanel();
        wpsScrollPane = new JScrollPane();
        wpsAddPanel = new JPanel();
        menuBar = new JMenuBar();
        JMenu monitorMenu = new JMenu();
        dataSourceMenuItem = new JMenuItem();
        showLogsMenuItem = new JMenuItem();
        JPopupMenu.Separator jSeparator1 = new JPopupMenu.Separator();
        JMenuItem settingsMenuItem = new JMenuItem();
        restartButton = new JMenuItem();
        jSeparator2 = new JPopupMenu.Separator();
        JMenuItem exitMenuItem = new JMenuItem();
        JMenu aboutMenu = new JMenu();
        JMenuItem aboutMenuItem = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("WPS Monitor - Admin Interface");
        setBackground(new Color(255, 255, 255));
        setIconImage(new ImageIcon(getClass().getResource("/icons/wpsmonitor-64x64.png")).getImage());
        setMinimumSize(new Dimension(852, 0));
        setName("wpsGui"); // NOI18N
        setResizable(false);

        jPanel1.setBorder(BorderFactory.createTitledBorder("Register a WPS Server in the Monitor"));
        jPanel1.setName("registerWpsGroupPane"); // NOI18N

        jPanel2.setName("groupRegisterTextFields"); // NOI18N

        wpsUrlDecoText.setText("Endpoint");
        wpsUrlDecoText.setName("wpsUrlDecoText"); // NOI18N

        newWpsEndpointField.setName("newWpsEndpointField"); // NOI18N
        newWpsEndpointField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newWpsEndpointFieldActionPerformed(evt);
            }
        });

        jPanel3.setName("groupRegisterButtons"); // NOI18N

        addWpsButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addWpsButton.setText("Add WPS");
        addWpsButton.setName("addWpsButton"); // NOI18N
        addWpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addWpsButtonActionPerformed(evt);
            }
        });

        dataSourcesButton.setIcon(new ImageIcon(getClass().getResource("/icons/database.png"))); // NOI18N
        dataSourcesButton.setText("Add from Data Sources");
        dataSourcesButton.setName("dataSourceButton"); // NOI18N
        dataSourcesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dataSourcesButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(addWpsButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataSourcesButton, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(dataSourcesButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addWpsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(wpsUrlDecoText)
                        .addGap(18, 18, 18)
                        .addComponent(newWpsEndpointField))
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 537, Short.MAX_VALUE)
                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(wpsUrlDecoText)
                    .addComponent(newWpsEndpointField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        decoPanelWpsScroll.setBorder(BorderFactory.createTitledBorder("Registered WPS-Servers"));

        wpsScrollPane.setBorder(null);
        wpsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wpsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        wpsAddPanel.setBackground(new Color(255, 255, 255));
        wpsAddPanel.setName("wpsAddPanel"); // NOI18N
        wpsAddPanel.setLayout(new BoxLayout(wpsAddPanel, BoxLayout.PAGE_AXIS));
        wpsScrollPane.setViewportView(wpsAddPanel);

        GroupLayout decoPanelWpsScrollLayout = new GroupLayout(decoPanelWpsScroll);
        decoPanelWpsScroll.setLayout(decoPanelWpsScrollLayout);
        decoPanelWpsScrollLayout.setHorizontalGroup(decoPanelWpsScrollLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelWpsScrollLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsScrollPane)
                .addContainerGap())
        );
        decoPanelWpsScrollLayout.setVerticalGroup(decoPanelWpsScrollLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(decoPanelWpsScrollLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wpsScrollPane, GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout controlPanelLayout = new GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(controlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decoPanelWpsScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(controlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(decoPanelWpsScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        monitorMenu.setText("Monitor");

        dataSourceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        dataSourceMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/database.png"))); // NOI18N
        dataSourceMenuItem.setText("Data Source Manager");
        dataSourceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dataSourceMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(dataSourceMenuItem);

        showLogsMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/log.png"))); // NOI18N
        showLogsMenuItem.setText("Show Logs");
        showLogsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showLogsMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(showLogsMenuItem);
        monitorMenu.add(jSeparator1);

        settingsMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(settingsMenuItem);

        restartButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        restartButton.setText("Restart");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                restartButtonActionPerformed(evt);
            }
        });
        monitorMenu.add(restartButton);
        monitorMenu.add(jSeparator2);

        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/exit.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        monitorMenu.add(exitMenuItem);

        menuBar.add(monitorMenu);

        aboutMenu.setText("Info");

        aboutMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/dialog-question-2.png"))); // NOI18N
        aboutMenuItem.setText("About WPS Monitor");
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        aboutMenu.add(aboutMenuItem);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void addWpsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addWpsButtonActionPerformed

        try {
            URL wpsEndpoint = new URL(newWpsEndpointField.getText());
            if (isEndpointFieldEmpty()) {
                MessageDialogs.showError(this,
                        "Error!",
                        "Please enter a valid Endpoint!"
                );
            } else if (monitor.ServicegetMonitorControl().isWpsExists(wpsEndpoint)) {
                MessageDialogs.showError(this,
                        "Already exists!",
                        "A WPS-Server with this identifier is already registered in the monitor. Choose another Identifier instead."
                );
            } else {
                addWps(wpsEndpoint);
            }
        } catch (IllegalArgumentException ex) {
            MessageDialogs.showError(this,
                    "Error!",
                    ex.getMessage()
            );
        } catch (MalformedURLException ex) {
            MessageDialogs.showError(this,
                    "Error",
                    "The entered Endpoint is not valid!"
            );
        }
    }//GEN-LAST:event_addWpsButtonActionPerformed

    private void newWpsEndpointFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_newWpsEndpointFieldActionPerformed
        addWpsButtonActionPerformed(evt);
    }//GEN-LAST:event_newWpsEndpointFieldActionPerformed

    private void settingsMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        new MonitorPropertiesDialog(this).setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void exitMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        Boolean yes = MessageDialogs
                .showQuestionDialog(this, "Close Monitor?", "Are you sure to close this Application?");

        if (yes) {
            dispose();

            try {
                monitor.shutdown();
            } catch (MonitorException ex) {

            }
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new AboutDialog(this).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void restartButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_restartButtonActionPerformed
        reInit();
        try {
            monitor.restart();
        } catch (MonitorException ex) {
            MessageDialogs.showError(this, "Can't restart Monitor", "Monitor Exception! Exception was: " + ex.toString());
        } catch (MonitorConfigException ex) {
            MessageDialogs.showError(this, "Can't restart Monitor", "Configuration Exception! Exception was: " + ex.toString());
        }
    }//GEN-LAST:event_restartButtonActionPerformed

    private void dataSourceMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_dataSourceMenuItemActionPerformed
        this.dsDialog.setVisible(true);
    }//GEN-LAST:event_dataSourceMenuItemActionPerformed

    private void dataSourcesButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_dataSourcesButtonActionPerformed
        this.dsDialog.showWpsDialog();
    }//GEN-LAST:event_dataSourcesButtonActionPerformed

    private void showLogsMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showLogsMenuItemActionPerformed
        lvDialog.prepare();
        lvDialog.setVisible(true);
    }//GEN-LAST:event_showLogsMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addWpsButton;
    private JPanel controlPanel;
    private JMenuItem dataSourceMenuItem;
    private JButton dataSourcesButton;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPopupMenu.Separator jSeparator2;
    private JMenuBar menuBar;
    private JTextField newWpsEndpointField;
    private JMenuItem restartButton;
    private JMenuItem showLogsMenuItem;
    private JPanel wpsAddPanel;
    private JScrollPane wpsScrollPane;
    // End of variables declaration//GEN-END:variables
}
