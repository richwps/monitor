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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.datasource;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsDescription;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.WpsProcessDescription;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process.WpsProcessDialog;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.wps.WpsPanel;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.structures.WpsTreeNode;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDialog extends javax.swing.JDialog {

    private WpsMonitorGui mainFrame;

    public WpsDialog(WpsMonitorGui parent, Set<DataSource> sources, boolean modal) {
        super(parent, modal);

        init(parent, sources);
    }

    public WpsDialog(WpsMonitorGui parent, DataSource source, boolean modal) {
        super(parent, modal);

        Set<DataSource> sources = new HashSet<DataSource>();
        sources.add(source);

        init(parent, sources);
    }

    private void init(WpsMonitorGui parent, Set<DataSource> sources) {
        initComponents();
        initTree(sources);
        setLocationRelativeTo(parent);

        this.mainFrame = parent;
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
        closeButton = new javax.swing.JButton();
        addToMonitorButton = new javax.swing.JButton();
        treeScrollPane = new javax.swing.JScrollPane();
        wpsTree = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(null);
        setMinimumSize(null);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        addToMonitorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addToMonitorButton.setText("Add WPS with Processes");
        addToMonitorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToMonitorButtonActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        wpsTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeScrollPane.setViewportView(wpsTree);

        jLabel1.setText("<html><body>Here is a list of all registred data-sources. You can pick up processes and WPS and add your choice to the monitor through the \"Add WPS with Processes\"-Button. If you select only a WPS, all processes of this WPS will also  be added. The processes will  be saved, but they have no Jobs or a testrequest.</body></html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addToMonitorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(treeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(treeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToMonitorButton)
                    .addComponent(closeButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void addToMonitorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToMonitorButtonActionPerformed
        Map<URI, List<WpsProcessDescription>> processMap = assignSelectionsToMap(wpsTree.getSelectionPaths());

        for (Map.Entry entry : processMap.entrySet()) {
            URI wpsUri = (URI) entry.getKey();
            String wpsIdentifier = generateWpsName(wpsUri.toString());
            WpsPanel wpsPanel = mainFrame.getPanel(wpsIdentifier);

            if (wpsPanel == null) {
                wpsPanel = mainFrame.addWps(wpsIdentifier, wpsUri.toString());
            }

            if (wpsPanel != null) {
                for (WpsProcessDescription desc : (List<WpsProcessDescription>) entry.getValue()) {
                    WpsProcessDialog processDialog = wpsPanel.getWpsProcessDialog();
                    processDialog.addProcess(desc.getIdentifier());
                }
            }
        }
    }//GEN-LAST:event_addToMonitorButtonActionPerformed

    private Map<URI, List<WpsProcessDescription>> assignSelectionsToMap(TreePath[] selections) {
        Map<URI, List<WpsProcessDescription>> processMap = new HashMap<URI, List<WpsProcessDescription>>();
        Set<WpsTreeNode> cache = new HashSet<WpsTreeNode>();

        for (TreePath p : selections) {
            if (p.getLastPathComponent() instanceof WpsTreeNode) {
                WpsTreeNode node = (WpsTreeNode) p.getLastPathComponent();

                WpsTreeNode wps;
                if (node.getType() == WpsTreeNode.NodeType.PROCESS) {
                    wps = (WpsTreeNode) node.getParent();
                } else {
                    wps = node;
                }

                WpsDescription wpsDesc = wps.getDescription();

                if (!processMap.containsKey(wpsDesc.getUri())) {
                    processMap.put(wpsDesc.getUri(), new ArrayList<WpsProcessDescription>());
                }

                if (node.getType() == WpsTreeNode.NodeType.PROCESS) {
                    WpsProcessDescription processDesc = node.getDescription();
                    processMap.get(wpsDesc.getUri()).add(processDesc);
                }

                if (node.getType() == WpsTreeNode.NodeType.WPS) {
                    cache.add(node);
                }
            }
        }

        // check if the selected wps not inserted by its processes
        // add all processes of the selected wps if no process is selected
        // of this wps
        for (WpsTreeNode node : cache) {
            WpsDescription desc = node.getDescription();

            for (int i = 0; i < node.getChildCount(); i++) {
                WpsTreeNode childAt = (WpsTreeNode) node.getChildAt(i);
                WpsProcessDescription pDesc = childAt.getDescription();

                processMap.get(desc.getUri()).add(pDesc);
            }
        }

        return processMap;
    }

    private String generateWpsName(String uri) {
        String withoutHttp = uri.substring(7);

        return withoutHttp.substring(0, withoutHttp.indexOf("/"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToMonitorButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JTree wpsTree;
    // End of variables declaration//GEN-END:variables

    private void initTree(Set<DataSource> sources) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Data Sources");

        for (DataSource source : sources) {
            String rootTitle = source.getUsedDriver() + ": " + source.getRessource();
            DefaultMutableTreeNode wpsRoot = new DefaultMutableTreeNode(rootTitle);

            for (WpsDescription wpsDesc : source.getWpsList()) {
                DefaultMutableTreeNode wps = new WpsTreeNode(wpsDesc, WpsTreeNode.NodeType.WPS);

                for (WpsProcessDescription processDesc : wpsDesc.getProcesses()) {
                    DefaultMutableTreeNode processNode = new WpsTreeNode(processDesc, WpsTreeNode.NodeType.PROCESS);
                    wps.add(processNode);
                }

                wpsRoot.add(wps);
            }

            root.add(wpsRoot);
        }

        wpsTree = new JTree(root);
        treeScrollPane.setViewportView(wpsTree);
    }
}
