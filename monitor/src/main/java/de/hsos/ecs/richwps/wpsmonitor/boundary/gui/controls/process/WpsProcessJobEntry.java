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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.controls.process;

import com.toedter.calendar.JDateChooser;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.IntervalComboBoxItem;
import de.hsos.ecs.richwps.wpsmonitor.control.scheduler.TriggerConfig;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;

/**
 * Represents the GUI element for a job entry to create and add triggers to the
 * monitor.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsProcessJobEntry extends JPanel {

    private WpsMonitorAdminGui mainFrame;
    private WpsProcessEntity wpsProcess;
    private TriggerConfig triggerConfig;
    private WpsProcessJobDialog parent;

    /**
     * Constructor.
     *
     * @param mainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     */
    public WpsProcessJobEntry(final WpsMonitorAdminGui mainFrame, final WpsProcessJobDialog parent, 
            final WpsProcessEntity wpsProcess) {
        
        this(mainFrame, parent, wpsProcess, null);
    }

    /**
     * Constructor.
     *
     * @param mainFrame Reference to the WpsMonitorAdminGui of this gui
     * @param parent Parent panel; is needed for delete operation
     * @param wpsProcess WpsProcessEntity instance to create the right trigger
     * @param triggerConfig TriggerConfig instance to restore this panel
     */
    public WpsProcessJobEntry(final WpsMonitorAdminGui mainFrame, final WpsProcessJobDialog parent, 
            final WpsProcessEntity wpsProcess, final TriggerConfig triggerConfig) {
        
        initComponents();

        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));
        this.mainFrame = mainFrame;
        this.wpsProcess = wpsProcess;
        this.parent = parent;
        this.triggerConfig = triggerConfig;

        this.startDate.setDate(new Date());

        saveJobButton.setBackground(new Color(255, 51, 51));
        init();
    }

    private void init() {
        setName("JobEntry" + parent.getComponentCount());
        init(triggerConfig);
    }

    private void init(final TriggerConfig triggerConfig) {
        initComboBox();

        if (triggerConfig != null) {
            this.startDate.setDate(triggerConfig.getStart());
            this.endDate.setDate(triggerConfig.getEnd());
            this.intervalTypeCombooBox.setSelectedItem(new IntervalComboBoxItem(triggerConfig.getIntervalType()));
            this.intervalTypeCombooBox.getModel().setSelectedItem(new IntervalComboBoxItem(triggerConfig.getIntervalType()));
            this.intervalField.setText(triggerConfig.getInterval().toString());
            this.triggerConfig = triggerConfig;

            this.saveJobButton.setBackground(null);
        } else {
            this.triggerConfig = new TriggerConfig();
        }
    }

    /**
     * Reinitialize the panel with a given {@link TriggerConfig} instance.
     *
     * @param config {@link TriggerConfig} instance
     */
    public void reInit(TriggerConfig config) {
        saveJobButton.setBackground(new Color(240, 240, 240));
        init(config);

        revalidate();
        repaint();
    }

    private void initComboBox() {
        IntervalComboBoxItem[] items = new IntervalComboBoxItem[]{
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.SECOND),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.MINUTE),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.HOUR),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.DAY),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.WEEK),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.MONTH),
            new IntervalComboBoxItem(TriggerConfig.IntervalUnit.YEAR)
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
        return validate == null || "".equals(validate.getText().trim());
    }
    
    
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

    private void removeVisual() {
        parent.removeJobEntry(this);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JToolBar jToolBar1 = new JToolBar();
        startDate = new JDateChooser();
        Box.Filler filler1 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        JLabel jLabel1 = new JLabel();
        Box.Filler filler2 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        timeSpinner = new JSpinner();
        Box.Filler filler6 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        JToolBar.Separator jSeparator1 = new JToolBar.Separator();
        Box.Filler filler7 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        endDate = new JDateChooser();
        Box.Filler filler5 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        JToolBar.Separator jSeparator3 = new JToolBar.Separator();
        Box.Filler filler4 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        intervalField = new JTextField();
        intervalTypeCombooBox = new JComboBox();
        Box.Filler filler3 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        JToolBar.Separator jSeparator2 = new JToolBar.Separator();
        Box.Filler filler8 = new Box.Filler(new Dimension(5, 0), new Dimension(10, 0), new Dimension(32767, 0));
        saveJobButton = new JButton();
        deleteJobButton = new JButton();

        setName(""); // NOI18N

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        startDate.setName("startDate"); // NOI18N
        jToolBar1.add(startDate);
        jToolBar1.add(filler1);

        jLabel1.setText("at");
        jToolBar1.add(jLabel1);
        jToolBar1.add(filler2);

        Date spinnerTime = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(spinnerTime, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        DateFormatter formatter = (DateFormatter)de.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        timeSpinner.setEditor(de);
        timeSpinner.setName("timeSpinner"); // NOI18N
        jToolBar1.add(timeSpinner);
        jToolBar1.add(filler6);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler7);

        endDate.setName("endDate"); // NOI18N
        jToolBar1.add(endDate);
        jToolBar1.add(filler5);
        jToolBar1.add(jSeparator3);
        jToolBar1.add(filler4);

        intervalField.setName("intervalField"); // NOI18N
        jToolBar1.add(intervalField);

        intervalTypeCombooBox.setModel(new DefaultComboBoxModel(new String[] { "Milliseconds", "Second", "Minute", "Hour", "Day", "Week", "Month", "Year" }));
        intervalTypeCombooBox.setName("intervalTypeComboBox"); // NOI18N
        jToolBar1.add(intervalTypeCombooBox);
        jToolBar1.add(filler3);
        jToolBar1.add(jSeparator2);
        jToolBar1.add(filler8);

        saveJobButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveJobButton.setText("Save");
        saveJobButton.setName("saveJobButton"); // NOI18N
        saveJobButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveJobButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveJobButton);

        deleteJobButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteJobButton.setText("Delete");
        deleteJobButton.setName("deleteJobButton"); // NOI18N
        deleteJobButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteJobButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteJobButton);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void saveJobButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveJobButtonActionPerformed
        if (isFieldsValid()) {
            IntervalComboBoxItem selectedItem = (IntervalComboBoxItem) intervalTypeCombooBox.getSelectedItem();
            String intervalValue = intervalField.getText();

            try {
                Integer interval = Integer.parseInt(intervalValue);

                Date time = (Date) timeSpinner.getValue();
                Date setStartDate = mergeDateAndTime(startDate.getDate(), time);
                Date setEndDate = mergeDateAndTime(endDate.getDate(), time);

                triggerConfig.setStart(setStartDate);
                triggerConfig.setEnd(setEndDate);
                triggerConfig.setInterval(interval);
                triggerConfig.setIntervalType(selectedItem.getFormatKey());

                triggerConfig = mainFrame.getMonitorReference()
                        .ServicegetMonitorControl()
                        .saveTrigger(wpsProcess, triggerConfig);

                if (!triggerConfig.isSaved()) {
                    MessageDialogs.showError(mainFrame,
                            "Error",
                            "Job was not created. Is Scheduler started? See the logs."
                    );
                } else {
                    this.saveJobButton.setBackground(null);
                }
            } catch (NumberFormatException ex) {
                MessageDialogs.showError(mainFrame,
                        "Invalid Number Format",
                        "\"" + intervalValue + "\" is not a valid Number format."
                );
            }
        }
    }//GEN-LAST:event_saveJobButtonActionPerformed

    private void deleteJobButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteJobButtonActionPerformed
        if (triggerConfig.isSaved()) {
            Boolean deleteTrigger = mainFrame.getMonitorReference()
                    .ServicegetMonitorControl()
                    .deleteTrigger(triggerConfig);

            if (!deleteTrigger) {
                MessageDialogs.showError(mainFrame,
                        "Can't delete",
                        "Can't delete Trigger."
                );
            } else {
                removeVisual();
            }
        } else {
            removeVisual();
        }

        // repaint required, otherwise the last element will not disappear
    }//GEN-LAST:event_deleteJobButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteJobButton;
    private JDateChooser endDate;
    private JTextField intervalField;
    private JComboBox intervalTypeCombooBox;
    private JButton saveJobButton;
    private JDateChooser startDate;
    private JSpinner timeSpinner;
    // End of variables declaration//GEN-END:variables
}
