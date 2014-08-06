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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.elements.datasource;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceException;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsDescription;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsProcessDescription;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.elements.process.WpsProcessDialog;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.elements.wps.WpsPanel;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.structure.WpsTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * A Dialog to display the WPS Server- and Processes of the given DataSource
 * instances.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDialog extends JDialog {

    private final WpsMonitorAdminGui mainFrame;
    private final Set<DataSource> sources;

    /**
     * Creates a new WpsDialog instance.
     *
     * @param parent Monitor gui mainframe
     * @param sources Set of DataSources
     */
    public WpsDialog(final WpsMonitorAdminGui parent, final Set<DataSource> sources) {
        super(parent, true);
        this.mainFrame = parent;
        this.sources = sources == null ? new HashSet<DataSource>() : sources;

        initComponents();
        setLocationRelativeTo(parent);

        init();
    }

    /**
     * Creates a new WpsDialog instance.
     *
     * @param parent Monitor gui mainframe
     * @param source DataSource instance
     */
    public WpsDialog(final WpsMonitorAdminGui parent, final DataSource source) {
        this(parent, new HashSet<>(Arrays.asList(new DataSource[]{source})));
    }

    private void init() {
        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent ce) {
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
            }

            @Override
            public void componentShown(ComponentEvent ce) {
                initTree(sources);
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
            }
        });
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
        closeButton = new JButton();
        addToMonitorButton = new JButton();
        treeScrollPane = new JScrollPane();
        wpsTree = new JTree();
        JLabel jLabel1 = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List of WPS-Servers- and Processes");
        setIconImage(new ImageIcon(getClass().getResource("/icons/database.png")).getImage());
        setMinimumSize(null);
        setResizable(false);

        jPanel1.setBorder(BorderFactory.createTitledBorder(""));

        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        addToMonitorButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addToMonitorButton.setText("Add WPS with Processes");
        addToMonitorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addToMonitorButtonActionPerformed(evt);
            }
        });

        DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode("root");
        wpsTree.setModel(new DefaultTreeModel(treeNode1));
        treeScrollPane.setViewportView(wpsTree);

        jLabel1.setText("<html><body>Here is a list of all registred data-sources. You can pick up processes and WPS and add your choice to the monitor through the \"Add WPS with Processes\"-Button. If you select only a WPS, all processes of this WPS will also  be added. The processes will  be saved, but they have no Jobs or a testrequest.</body></html>");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addToMonitorButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton))
                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(treeScrollPane, GroupLayout.PREFERRED_SIZE, 630, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(treeScrollPane, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addToMonitorButton)
                    .addComponent(closeButton))
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void addToMonitorButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addToMonitorButtonActionPerformed
        Map<String, WpsDescription> wpsDescriptions = assignSelectionsToMap(wpsTree.getSelectionPaths());

        for (Map.Entry e : wpsDescriptions.entrySet()) {
            WpsDescription desc = (WpsDescription) e.getValue();
            String identifier = (String) e.getKey();

            WpsPanel wpsPanel = mainFrame.getPanel(identifier);

            if (wpsPanel == null) {
                wpsPanel = mainFrame.addWps(identifier, desc.getUri().toString());
            }

            if (wpsPanel != null) {
                for (WpsProcessDescription pDesc : desc.getProcesses()) {
                    WpsProcessDialog processDialog = wpsPanel.getWpsProcessDialog();
                    processDialog.addProcess(pDesc.getIdentifier());
                }
            }
        }
    }//GEN-LAST:event_addToMonitorButtonActionPerformed

    private Map<String, WpsDescription> assignSelectionsToMap(final TreePath[] selections) {
        Map<String, WpsDescription> wpsDescriptions = new HashMap<>();

        for (TreePath p : selections) {
            if (p.getLastPathComponent() instanceof WpsTreeNode) {
                WpsTreeNode node = (WpsTreeNode) p.getLastPathComponent();

                WpsTreeNode wps;

                if (node.getType() == WpsTreeNode.NodeType.PROCESS) {
                    wps = (WpsTreeNode) node.getParent();
                } else {
                    wps = node;
                }

                WpsDescription wpsDesc;

                if (node.getType() == WpsTreeNode.NodeType.WPS) {
                    wpsDesc = wps.getDescription();
                } else {
                    WpsDescription tmpDesc = wps.getDescription();
                    wpsDesc = new WpsDescription(tmpDesc.getIdentifier(), tmpDesc.getUri());
                }

                String identifier = wpsDesc.getIdentifier();
                if (!wpsDescriptions.containsKey(identifier)) {
                    wpsDescriptions.put(identifier, wpsDesc);
                }

                if (node.getType() == WpsTreeNode.NodeType.PROCESS) {
                    WpsProcessDescription processDesc = node.getDescription();
                    wpsDescriptions.get(identifier).add(processDesc);
                }
            }
        }

        return wpsDescriptions;
    }

    private void initTree(final Set<DataSource> sources) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Data Sources");
        List<Exception> exceptions = new ArrayList<>();

        for (DataSource source : sources) {

            String rootTitle = source.getUsedDriver() + ": " + source.getRessource();
            DefaultMutableTreeNode wpsRoot = new DefaultMutableTreeNode(rootTitle);

            try {
                for (WpsDescription wpsDesc : source.getWpsList()) {
                    DefaultMutableTreeNode wps = new WpsTreeNode(wpsDesc, WpsTreeNode.NodeType.WPS);

                    for (WpsProcessDescription processDesc : wpsDesc.getProcesses()) {
                        DefaultMutableTreeNode processNode = new WpsTreeNode(processDesc, WpsTreeNode.NodeType.PROCESS);
                        wps.add(processNode);
                    }

                    wpsRoot.add(wps);
                }
            } catch (DataSourceException ex) {
                // gather occured exceptions for later displaying
                exceptions.add(ex);
            }

            root.add(wpsRoot);
        }

        if (!exceptions.isEmpty()) {
            showErrorMessage(exceptionListToString(exceptions));
        }

        wpsTree = new JTree(root);
        treeScrollPane.setViewportView(wpsTree);
    }

    private String exceptionListToString(List<Exception> exs) {
        StringBuilder str = new StringBuilder();

        for (Exception ex : exs) {
            str.append(ExceptionUtils.getStackTrace(ex));
            str.append('\n');
            str.append('\n');
        }

        return str.toString();
    }

    private void showErrorMessage(final String msg) {
        MessageDialogs.showDetailedError(this,
                "Error",
                "DataSource Exceptions occurd.",
                msg);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addToMonitorButton;
    private JButton closeButton;
    private JScrollPane treeScrollPane;
    private JTree wpsTree;
    // End of variables declaration//GEN-END:variables
}
