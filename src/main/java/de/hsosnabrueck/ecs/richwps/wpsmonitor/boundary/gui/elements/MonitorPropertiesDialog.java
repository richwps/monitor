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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.config.MonitorConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

/**
 * Dialog to configure the montior.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MonitorPropertiesDialog extends javax.swing.JDialog {

    private final WpsMonitorAdminGui monitorMainFrame;

    /**
     * Constructor.
     *
     * @param monitorMainFrame Reference to the MainFrame of this gui
     */
    public MonitorPropertiesDialog(WpsMonitorAdminGui monitorMainFrame) {
        super(monitorMainFrame, true);

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
        dateDeleteSpinner.getModel()
                .setValue(config.getDeleteIntervalInDays());
        timeSpinner.getModel()
                .setValue(config.getDeleteTime().getTime());
        timeoutSpinner.getModel()
                .setValue(config.getWpsClientTimeout() / 1000 / 60);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveButton = new JButton();
        cancelButton = new JButton();
        JPanel jPanel2 = new JPanel();
        JLabel jLabel4 = new JLabel();
        SpinnerModel numberModelForTimeout = new SpinnerNumberModel(10, 0, null, 1);
        timeoutSpinner = new JSpinner(numberModelForTimeout);
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JPanel jPanel1 = new JPanel();
        spinnerTime = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(spinnerTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new JSpinner(sm);
        JLabel jLabel1 = new JLabel();
        JLabel jLabel3 = new JLabel();
        deleteIsActiveCheckbox = new JCheckBox();
        JLabel jLabel2 = new JLabel();
        SpinnerModel numberModelForDelete = new SpinnerNumberModel(10, 0, null, 1);
        dateDeleteSpinner = new JSpinner(numberModelForDelete);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/cancel.png"))); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(BorderFactory.createTitledBorder("WPS-Client Timeout"));

        jLabel4.setText("Timeout after");

        timeoutSpinner.setEditor(new JSpinner.NumberEditor(timeoutSpinner, "####"));
        JFormattedTextField numberModelForTimeoutTxt = ((JSpinner.NumberEditor) timeoutSpinner.getEditor()).getTextField();
        ((NumberFormatter) numberModelForTimeoutTxt .getFormatter()).setAllowsInvalid(false);

        jLabel5.setText("Minutes.");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(timeoutSpinner, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(timeoutSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setText("Changes are applied after a restart.");

        jPanel1.setBorder(BorderFactory.createTitledBorder("Delete QoS Data"));

        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        DateFormatter formatter = (DateFormatter)de.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        timeSpinner.setEditor(de);

        jLabel1.setText("Everyday at");

        jLabel3.setText("Days.");

        deleteIsActiveCheckbox.setText("Delete QoS-Data");

        jLabel2.setText("older than");

        dateDeleteSpinner.setEditor(new JSpinner.NumberEditor(dateDeleteSpinner, "####"));
        JFormattedTextField numberModelForDeleteTxt = ((JSpinner.NumberEditor) dateDeleteSpinner.getEditor()).getTextField();
        ((NumberFormatter) numberModelForDeleteTxt.getFormatter()).setAllowsInvalid(false);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteIsActiveCheckbox)
                .addGap(6, 6, 6)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(dateDeleteSpinner, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(timeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(timeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(deleteIsActiveCheckbox)
                    .addComponent(jLabel2)
                    .addComponent(dateDeleteSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        MonitorConfig config = monitorMainFrame.getMonitorReference()
                .getConfig();

        try {
            Integer deleteInterval = (Integer) dateDeleteSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);

            config.setDeleteIntervalInDays(deleteInterval);
            config.setDeleteJobActiv(deleteIsActiveCheckbox.isSelected());
            config.setDeleteTime(cal);

            config.setWpsClientTimeout(((Integer) timeoutSpinner.getValue()) * 1000 * 60);

            if (!config.getDeleteIntervalInDays().equals(deleteInterval)) {
                throw new NumberFormatException();
            }

            dispose();
        } catch (NumberFormatException ex) {
            MessageDialogs.showError(this, "Error", "Delete intervale value is not valid.");
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton cancelButton;
    private JSpinner dateDeleteSpinner;
    private JCheckBox deleteIsActiveCheckbox;
    private JButton saveButton;
    private JSpinner timeSpinner;
    private Date spinnerTime;
    private JSpinner timeoutSpinner;
    // End of variables declaration//GEN-END:variables
}
