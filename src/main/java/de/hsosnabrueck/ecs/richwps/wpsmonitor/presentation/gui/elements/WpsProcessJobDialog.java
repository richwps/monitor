/*
 * Copyright 2014 FloH.
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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.structures.WpsProcess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author FloH
 */
public class WpsProcessJobDialog extends javax.swing.JDialog {
    private WpsMonitorGui mainframe;
    private WpsProcess wpsProcess;
    
    /**
     * Creates new form WpsProcessJobDialog
     */
    public WpsProcessJobDialog(WpsMonitorGui mainFrame, WpsProcess wpsProcess, boolean modal) {
        super(mainFrame, modal);
        initComponents();
        
        this.wpsProcess = wpsProcess;
        this.mainframe = Param.notNull(mainFrame, "mainFrame");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newJobButton = new javax.swing.JButton();
        jobScrollPane = new javax.swing.JScrollPane();
        addJobPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add, remove or edit Process Job");
        setResizable(false);

        newJobButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/time.png"))); // NOI18N
        newJobButton.setText("New Job");
        newJobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newJobButtonActionPerformed(evt);
            }
        });

        addJobPane.setLayout(new javax.swing.BoxLayout(addJobPane, javax.swing.BoxLayout.PAGE_AXIS));
        jobScrollPane.setViewportView(addJobPane);

        jLabel1.setText("Start");

        jLabel2.setText("End");

        jLabel3.setText("Time Unit");

        jLabel4.setText("Interval");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newJobButton))
                    .addComponent(jobScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1)
                        .addGap(66, 66, 66)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 426, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(85, 85, 85)
                        .addComponent(jLabel3)
                        .addGap(102, 102, 102)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jobScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newJobButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newJobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newJobButtonActionPerformed
        WpsProcessJobEntry newJobEntry = new WpsProcessJobEntry(mainframe, wpsProcess);
        addJobPane.add(newJobEntry);
        addJobPane.revalidate();
    }//GEN-LAST:event_newJobButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addJobPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jobScrollPane;
    private javax.swing.JButton newJobButton;
    // End of variables declaration//GEN-END:variables
}
