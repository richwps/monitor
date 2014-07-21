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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.process;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.util.List;
import javax.swing.ImageIcon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shows all {@link Trigger}s of the selected Wps-Process.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessJobDialog extends javax.swing.JDialog {

    private final WpsMonitorGui mainframe;
    private final WpsProcessEntity wpsProcess;

    private final static Logger log = LogManager.getLogger();

    /**
     * Creates new form WpsProcessJobDialog.
     *
     * @param mainFrame Reference to the WpsMonitorGui of this gui
     * @param wpsProcess WpsProcessEntity instance to request the right trigger
     * @param modal true for modal form
     */
    public WpsProcessJobDialog(WpsMonitorGui mainFrame, WpsProcessEntity wpsProcess, boolean modal) {
        super(mainFrame, modal);
        initComponents();

        setLocationRelativeTo(mainFrame);

        this.wpsProcess = wpsProcess;
        this.mainframe = Validate.notNull(mainFrame, "mainFrame");

        init();
    }

    private void init() {
        List<TriggerConfig> triggers = mainframe.getMonitorReference()
                .getMonitorControl()
                .getTriggers(wpsProcess);

        log.debug("init WpsProcessJobDialog");
        for (TriggerConfig config : triggers) {
            WpsProcessJobEntry jobEntryPane = createNewJobEntryPane();
            jobEntryPane.reInit(config);

            log.debug("reInit jobEntryPane with {}", config.toString());

            addJobEntryPane(jobEntryPane);
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

        newJobButton = new javax.swing.JButton();
        jobScrollPane = new javax.swing.JScrollPane();
        addJobPane = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add, remove or edit Process Job");
        setIconImage(new ImageIcon(getClass().getResource("/icons/time.png")).getImage());
        setResizable(false);

        newJobButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/time.png"))); // NOI18N
        newJobButton.setText("New Job");
        newJobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newJobButtonActionPerformed(evt);
            }
        });

        jobScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jobScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        addJobPane.setLayout(new javax.swing.BoxLayout(addJobPane, javax.swing.BoxLayout.PAGE_AXIS));
        jobScrollPane.setViewportView(addJobPane);

        jLabel1.setText("Start");

        jLabel2.setText("End");

        jLabel3.setText("Time Unit");

        jLabel4.setText("Interval");

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(newJobButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeButton))
                            .addComponent(jobScrollPane)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 281, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(147, 147, 147)
                        .addComponent(jLabel4)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel3)
                        .addGap(174, 174, 174)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newJobButton)
                    .addComponent(closeButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newJobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newJobButtonActionPerformed
        WpsProcessJobEntry newJobEntry = createNewJobEntryPane();
        addJobEntryPane(newJobEntry);
    }//GEN-LAST:event_newJobButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private WpsProcessJobEntry createNewJobEntryPane() {
        return new WpsProcessJobEntry(mainframe, addJobPane, wpsProcess);
    }

    private void addJobEntryPane(WpsProcessJobEntry pane) {
        addJobPane.add(pane);
        addJobPane.revalidate();
        addJobPane.repaint();
    }

    /**
     * reinitialize the form
     */
    public void reInit() {
        addJobPane.removeAll();
        init();
        revalidate();
        repaint();
    }

    private void appendTitle(String name) {
        this.setTitle(getTitle() + " " + name);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addJobPane;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jobScrollPane;
    private javax.swing.JButton newJobButton;
    // End of variables declaration//GEN-END:variables
}