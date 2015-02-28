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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.datasource;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceException;
import de.hsos.ecs.richwps.wpsmonitor.control.event.EventNotFoundException;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEvent;
import de.hsos.ecs.richwps.wpsmonitor.control.event.MonitorEventListener;
import de.hsos.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dialog which can be used to manage and create DataSource instances through
 * DataSourceCreator instances. The dialog registered a new shutdown listener in
 * the Monitor instance to store created DataSource instances in the Monitor
 * Properties.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DataSourceDialog extends JDialog {

    private static final Logger LOG = LogManager.getLogger();

    private final Set<DataSourceCreator> creators;
    private final Set<DataSource> sources;
    private final WpsMonitorAdminGui mainFrame;
    private final WpsDialog wpsDialog;

    /**
     * Creates new form DataSourceDialog
     *
     * @param monitorMainFrame Mainframe of the monitor
     * @param creators Set of DataSourceCreator instances
     */
    public DataSourceDialog(final WpsMonitorAdminGui monitorMainFrame, final Set<DataSourceCreator> creators) {
        super(monitorMainFrame, true);

        this.creators = creators;
        this.mainFrame = monitorMainFrame;
        this.sources = new HashSet<>();
        this.wpsDialog = new WpsDialog(mainFrame, sources);

        initComponents();
        init();
    }

    private void init() {
        if (creators != null) {
            for (DataSourceCreator driver : creators) {
                creatorAddPanel.add(new DataSourceCreatorPanel(this, driver));
            }

            readSources();

            try {
                mainFrame.getMonitorReference()
                        .getEventHandler()
                        .registerListener("monitor.shutdown", new MonitorEventListener() {

                            @Override
                            public void execute(MonitorEvent event) {
                                storeSources();
                            }
                        });
            } catch (EventNotFoundException ex) {
                LOG.warn("Can't register storeSource() Listener at monitor.shutdown Event.", ex);
            }

            creatorAddPanel.revalidate();
        }
    }

    /**
     * Creates and adds a new DataSourcePanel instance to this dialog. The
     * method is typically used by DataSourcePanel instances.
     *
     * @param source DataSource instance to add
     */
    public void addDataSource(final DataSource source) {
        dataSourceAddPanel.add(new DataSourcePanel(mainFrame, this, source));
        sources.add(source);
        dataSourceAddPanel.revalidate();
    }

    /**
     * Removes a DataSourcePanel.
     *
     * @param panel The Panel instance which should be removed
     */
    public void removeDataSource(final DataSourcePanel panel) {
        dataSourceAddPanel.remove(panel);
        removeDataSource(panel.getSource());
        revalidate();
        repaint();
    }

    /**
     * Creates and shows up a new WpsDialog instance which initliazid the tree
     * through the DataSource instances.
     */
    public void showWpsDialog() {
        wpsDialog.setVisible(true);
    }

    private void removeDataSource(final DataSource source) {
        sources.remove(source);
    }

    private void storeSources() {
        MonitorConfig config = mainFrame.getMonitorReference()
                .getConfig();

        StringBuilder serializedDataSources = new StringBuilder();
        for (DataSource s : sources) {
            serializedDataSources.append(s.getUsedDriver());
            serializedDataSources.append("||");
            serializedDataSources.append(s.getRessource());
            serializedDataSources.append(";;");
        }

        config.setCustomProperty("datasource", serializedDataSources.toString());
    }

    private void readSources() {
        MonitorConfig config = mainFrame.getMonitorReference()
                .getConfig();

        String dataSources = config.getCustomProperty("datasource");

        if (dataSources != null && !dataSources.isEmpty()) {
            String[] dataSourcesArr = dataSources.split(";;");

            for (String source : dataSourcesArr) {
                String[] sourceResourceDriver = source.split("\\|\\|");
                String driverName = sourceResourceDriver[0];
                String resource = sourceResourceDriver[1];

                for (DataSourceCreator driver : creators) {
                    if (driver.getCreatorName().equals(driverName)) {
                        try {
                            addDataSource(driver.create(resource));
                        } catch (DataSourceException ex) {
                            LOG.error("Can't restore the DataSource of MonitorConfig custom properties. : ", ex);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void setVisible(boolean b) {
        setLocationRelativeTo(mainFrame);
        
        super.setVisible(b);
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
        JScrollPane jScrollPane1 = new JScrollPane();
        creatorAddPanel = new JPanel();
        JPanel jPanel2 = new JPanel();
        JScrollPane jScrollPane2 = new JScrollPane();
        dataSourceAddPanel = new JPanel();
        closeButton = new JButton();
        showWpsOfSourcesButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Source Manager");
        setIconImage(new ImageIcon(getClass().getResource("/icons/database-add.png")).getImage());
        setName("dataSourceDialog"); // NOI18N
        setResizable(false);

        jPanel1.setBorder(BorderFactory.createTitledBorder("Registered Data Source Creatores"));

        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        creatorAddPanel.setName("creatorAddPanel"); // NOI18N
        creatorAddPanel.setLayout(new BoxLayout(creatorAddPanel, BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(creatorAddPanel);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(BorderFactory.createTitledBorder("Data Sources"));

        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        dataSourceAddPanel.setName("dataSourceAddPanel"); // NOI18N
        dataSourceAddPanel.setLayout(new BoxLayout(dataSourceAddPanel, BoxLayout.PAGE_AXIS));
        jScrollPane2.setViewportView(dataSourceAddPanel);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
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

        showWpsOfSourcesButton.setIcon(new ImageIcon(getClass().getResource("/icons/database.png"))); // NOI18N
        showWpsOfSourcesButton.setText("Show WPS of registered Sources");
        showWpsOfSourcesButton.setName("showWpsOfSourcesButton"); // NOI18N
        showWpsOfSourcesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showWpsOfSourcesButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(showWpsOfSourcesButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(showWpsOfSourcesButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showWpsOfSourcesButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showWpsOfSourcesButtonActionPerformed
        showWpsDialog();
    }//GEN-LAST:event_showWpsOfSourcesButtonActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton closeButton;
    private JPanel creatorAddPanel;
    private JPanel dataSourceAddPanel;
    private JButton showWpsOfSourcesButton;
    // End of variables declaration//GEN-END:variables
}
