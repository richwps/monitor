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

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSource;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

/**
 * Representates a DataSource instance GUI element.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class DataSourcePanel extends JPanel {

    private final DataSource source;
    private final WpsMonitorAdminGui monitorMainFrame;
    private final DataSourceDialog parent;

    /**
     * Creates a new DataSourcePanel instance.
     * 
     * @param monitorMainFrame The mainframe of the monitor gui
     * @param parent Parent frame
     * @param source DataSource instance which will be used by this panel
     */
    public DataSourcePanel(final WpsMonitorAdminGui monitorMainFrame, final DataSourceDialog parent, final DataSource source) {
        this.source = source;
        this.monitorMainFrame = monitorMainFrame;
        this.parent = parent;

        initComponents();
        this.dataSourceLabel.setText(source.getUsedDriver());
        this.resourceLabel.setText(source.getRessource());
    }

    /**
     * Gets the DataSource instance of these DataSourcePanel.
     * 
     * @return DataSource instance 
     */
    public DataSource getSource() {
        return source;
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
        dataSourceLabel = new JLabel();
        deleteSource = new JButton();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        resourceLabel = new JLabel();

        setMaximumSize(new Dimension(32767, 116));

        jPanel1.setBorder(BorderFactory.createTitledBorder(""));

        dataSourceLabel.setText("jLabel1");

        deleteSource.setIcon(new ImageIcon(getClass().getResource("/icons/database-delete.png"))); // NOI18N
        deleteSource.setText("Delete");
        deleteSource.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteSourceActionPerformed(evt);
            }
        });

        jLabel1.setText("Driver used:");

        jLabel2.setText("Resource:");

        resourceLabel.setText("jLabel3");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteSource)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resourceLabel))
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataSourceLabel)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dataSourceLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(resourceLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteSource))
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
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteSourceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteSourceActionPerformed
        parent.removeDataSource(this);
    }//GEN-LAST:event_deleteSourceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel dataSourceLabel;
    private JButton deleteSource;
    private JLabel resourceLabel;
    // End of variables declaration//GEN-END:variables
}