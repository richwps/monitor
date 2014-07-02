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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.scheduler.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.structures.IntervalComboBoxItem;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import org.quartz.DateBuilder;
import org.quartz.TriggerKey;

/**
 * Represents the GUI element for a job entry to create and add triggers
 * to the monitor.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessJobEntry extends javax.swing.JPanel {

    private WpsMonitorGui mainFrame;
    private WpsProcessEntity wpsProcess;
    private TriggerKey triggerKey;
    private JPanel parent;

    /**
     * Constructor.
     * 
     * @param mainFrame Reference to the WpsMonitorGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     */
    public WpsProcessJobEntry(WpsMonitorGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess) {
        this(mainFrame, parent, wpsProcess, null);
    }

    /**
     * Constructor.
     * 
     * @param mainFrame Reference to the WpsMonitorGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     * @param triggerConfig  TriggerConfig instance to restore this panel
     */
    public WpsProcessJobEntry(WpsMonitorGui mainFrame, JPanel parent, WpsProcessEntity wpsProcess, TriggerConfig triggerConfig) {
        initComponents();

        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));
        this.mainFrame = mainFrame;
        this.wpsProcess = wpsProcess;
        this.parent = parent;
        
        saveJob.setBackground(new Color(255, 51, 51));
        init(triggerConfig);
    }

    private void init(TriggerConfig triggerConfig) {
        initComboBox();

        if (triggerConfig != null) {
            this.startDate.setDate(triggerConfig.getStart());
            this.endDate.setDate(triggerConfig.getEnd());
            this.intervalTypeCombooBox.setSelectedItem(new IntervalComboBoxItem(triggerConfig.getIntervalType()));
            this.intervalTypeCombooBox.getModel().setSelectedItem(new IntervalComboBoxItem(triggerConfig.getIntervalType()));
            this.intervalField.setText(triggerConfig.getInterval().toString());

            this.triggerKey = triggerConfig.getTriggerKey();
        }
    }

    /**
     * Reinitialize the panel with a given {@link TriggerConfig} instance.
     * 
     * @param config {@link TriggerConfig} instance
     */
    public void reInit(TriggerConfig config) {
        saveJob.setBackground(new Color(240, 240, 240));
        init(config);
        
        revalidate();
        repaint();
    }

    private void initComboBox() {
        IntervalComboBoxItem[] items = new IntervalComboBoxItem[]{
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.SECOND),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.MINUTE),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.HOUR),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.DAY),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.WEEK),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.MONTH),
            new IntervalComboBoxItem(DateBuilder.IntervalUnit.YEAR)
        };

        intervalTypeCombooBox.setSelectedIndex(2);
        intervalTypeCombooBox.setModel(new DefaultComboBoxModel(items));
    }

    private Boolean isFieldsValid() {
        return !isEmpty(intervalField)
                && startDate.getDate() != null
                && endDate.getDate() != null
                && startDate.getDate().before(endDate.getDate());
    }

    private Boolean isEmpty(JTextField validate) {
        return validate == null || validate.getText().trim().equals("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        startDate = new com.toedter.calendar.JDateChooser();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        jLabel1 = new javax.swing.JLabel();
        javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        timeSpinner = new javax.swing.JSpinner();
        javax.swing.Box.Filler filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        jSeparator1 = new javax.swing.JToolBar.Separator();
        javax.swing.Box.Filler filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        endDate = new com.toedter.calendar.JDateChooser();
        javax.swing.Box.Filler filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        jSeparator3 = new javax.swing.JToolBar.Separator();
        javax.swing.Box.Filler filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        intervalField = new javax.swing.JTextField();
        intervalTypeCombooBox = new javax.swing.JComboBox();
        javax.swing.Box.Filler filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        jSeparator2 = new javax.swing.JToolBar.Separator();
        javax.swing.Box.Filler filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        saveJob = new javax.swing.JButton();
        deleteJob = new javax.swing.JButton();

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);
        jToolBar1.add(startDate);
        jToolBar1.add(filler1);

        jLabel1.setText("at");
        jToolBar1.add(jLabel1);
        jToolBar1.add(filler2);

        Date spinnerTime = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(spinnerTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new javax.swing.JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        DateFormatter formatter = (DateFormatter)de.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        timeSpinner.setEditor(de);
        jToolBar1.add(timeSpinner);
        jToolBar1.add(filler6);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler7);
        jToolBar1.add(endDate);
        jToolBar1.add(filler5);
        jToolBar1.add(jSeparator3);
        jToolBar1.add(filler4);
        jToolBar1.add(intervalField);

        intervalTypeCombooBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Milliseconds", "Second", "Minute", "Hour", "Day", "Week", "Month", "Year" }));
        jToolBar1.add(intervalTypeCombooBox);
        jToolBar1.add(filler3);
        jToolBar1.add(jSeparator2);
        jToolBar1.add(filler8);

        saveJob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveJob.setText("Save");
        saveJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJobActionPerformed(evt);
            }
        });
        jToolBar1.add(saveJob);

        deleteJob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteJob.setText("Delete");
        deleteJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteJobActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteJob);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void saveJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJobActionPerformed
        if (isFieldsValid()) {
            IntervalComboBoxItem selectedItem = (IntervalComboBoxItem) intervalTypeCombooBox.getSelectedItem();
            String intervalValue = intervalField.getText();

            try {
                Integer interval = Integer.parseInt(intervalValue);

                Date time = (Date) timeSpinner.getValue();
                Date setStartDate = mergeDateAndTime(startDate.getDate(), time);
                Date setEndDate = mergeDateAndTime(endDate.getDate(), time);

                TriggerConfig tConfig = new TriggerConfig(setStartDate,
                        setEndDate,
                        interval,
                        selectedItem.getFormatKey(),
                        triggerKey
                );

                TriggerKey newTrigger = mainFrame.getMonitorReference()
                        .getMonitorControl()
                        .saveTrigger(wpsProcess, tConfig);

                if (newTrigger == null) {
                    MessageDialogs.showError(mainFrame,
                            "Error",
                            "Job was not created. Is Scheduler started? See the logs."
                    );
                } else {
                    this.triggerKey = newTrigger;
                }
            } catch (NumberFormatException ex) {
                MessageDialogs.showError(mainFrame,
                        "Invalid Number Format",
                        "\"" + intervalValue + "\" is not a valid Number format."
                );
            }
        }
    }//GEN-LAST:event_saveJobActionPerformed

    private Date mergeDateAndTime(Date date, Date time) {
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);

        Calendar calTime = Calendar.getInstance();
        calTime.setTime(time);

        calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
        calDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND));

        return calDate.getTime();
    }
    private void deleteJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteJobActionPerformed
        if (triggerKey != null) { //if triggerkey null, then this job was not saved
            mainFrame.getMonitorReference()
                    .getMonitorControl()
                    .deleteTrigger(triggerKey);
        }

        parent.remove(this);

        parent.revalidate();
        parent.repaint(); // repaint required, otherwise the last element will not disappear
    }//GEN-LAST:event_deleteJobActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteJob;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JTextField intervalField;
    private javax.swing.JComboBox intervalTypeCombooBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton saveJob;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JSpinner timeSpinner;
    // End of variables declaration//GEN-END:variables
}
