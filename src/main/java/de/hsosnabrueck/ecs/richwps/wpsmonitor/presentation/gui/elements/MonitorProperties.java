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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.MessageDialogs;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;

/**
 * Dialog to configure the montior.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorProperties extends javax.swing.JDialog {

    private final WpsMonitorGui monitorMainFrame;

    /**
     * Constructor.
     *
     * @param monitorMainFrame Reference to the MainFrame of this gui
     * @param modal true for modal dialog
     */
    public MonitorProperties(WpsMonitorGui monitorMainFrame, boolean modal) {
        super(monitorMainFrame, modal);

        this.monitorMainFrame = monitorMainFrame;

        initComponents();
        init();

        setLocationRelativeTo(monitorMainFrame);
    }

    /**
     * Load {@link MonitorConfig} into the forms.
     */
    private void init() {
        MonitorConfig config = monitorMainFrame.getMonitorReference()
                .getConfig();

        deleteIsActiveCheckbox.setSelected(config.isDeleteJobActiv());
        intervalValueField.setText(config.getDeleteIntervalInDays().toString());
        timeSpinner.getModel()
                .setValue(config.getDeleteTime().getTime());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        spinnerTime = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(spinnerTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new javax.swing.JSpinner(sm);
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        intervalValueField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        deleteIsActiveCheckbox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Delete QoS Data"));

        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        DateFormatter formatter = (DateFormatter)de.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        timeSpinner.setEditor(de);

        jLabel1.setText("at time");

        jLabel3.setText("Days");

        deleteIsActiveCheckbox.setText("Delete QoS-Data");

        jLabel2.setText("all");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteIsActiveCheckbox)
                .addGap(6, 6, 6)
                .addComponent(jLabel2)
                .addGap(10, 10, 10)
                .addComponent(intervalValueField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(intervalValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(deleteIsActiveCheckbox)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        MonitorConfig config = monitorMainFrame.getMonitorReference()
                .getConfig();

        try {
            Integer deleteInterval = Integer.parseInt(intervalValueField.getText());
            Date time = (Date) timeSpinner.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);

            config.setDeleteIntervalInDays(deleteInterval);
            config.setDeleteJobActiv(deleteIsActiveCheckbox.isSelected());
            config.setDeleteTime(cal);

            if (!config.getDeleteIntervalInDays().equals(deleteInterval)) {
                throw new NumberFormatException();
            }

            dispose();
        } catch (NumberFormatException ex) {
            MessageDialogs.showError(this, "Error", "Delete intervale value is not valid.");
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox deleteIsActiveCheckbox;
    private javax.swing.JTextField intervalValueField;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton saveButton;
    private javax.swing.JSpinner timeSpinner;
    private Date spinnerTime;
    // End of variables declaration//GEN-END:variables
}
