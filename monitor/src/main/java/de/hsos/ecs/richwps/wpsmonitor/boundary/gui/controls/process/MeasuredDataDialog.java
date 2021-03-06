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
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.MeasuredDataEntity;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dialog to show the measured data. The measured data will be shown in a
 * {@link MeasuredDataPanel} JPanel in the scoll area of this dialog.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MeasuredDataDialog extends JDialog {

    private final WpsMonitorAdminGui monitorMainFrame;
    private final WpsProcessEntity wpsProcess;

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Creates a new ShowMeasuredDataDialog instance.
     *
     * @param monitorMainFrame Reference to the MainFrame of this gui
     * @param process WpsProcessEntity to select the right measured data
     */
    public MeasuredDataDialog(final WpsMonitorAdminGui monitorMainFrame, final WpsProcessEntity process) {
        super(monitorMainFrame, true);
        initComponents();

        this.monitorMainFrame = monitorMainFrame;
        this.wpsProcess = process;
        setTitle(getTitle() + " of " + process.getIdentifier());
    }

    /**
     * Reinitialize the dialog with new data from the database.
     */
    public void recaptureData() {
        Range range = new Range(null, 100);

        LOG.debug("Recapture Data from process {}", wpsProcess);

        List<MeasuredDataEntity> measuredData = monitorMainFrame.getMonitorReference()
                .ServicegetMonitorControl()
                .getMeasuredData(wpsProcess, range);

        // clear
        measuredDataAddPanel.removeAll();

        // add new
        for (MeasuredDataEntity e : measuredData) {
            MeasuredDataPanel measuredDataPane = new MeasuredDataPanel(e.getCreateTime() + ": " + e.toString());

            measuredDataAddPanel.add(measuredDataPane);
        }

        measuredDataAddPanel.revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean b) {
        setLocationRelativeTo(monitorMainFrame);

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
        measuredDataAddPanel = new JPanel();
        deleteAllButton = new JButton();
        deleteByDateButton = new JButton();
        JLabel jLabel1 = new JLabel();
        deleteOlderAsDate = new JDateChooser();
        refreshButton = new JButton();
        closeButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Measured data");
        setIconImage(new ImageIcon(getClass().getResource("/icons/measure.png")).getImage());
        setName("measuredDataDialog"); // NOI18N

        jPanel1.setBorder(BorderFactory.createTitledBorder("Measured Data"));

        measuredDataAddPanel.setName("measuredDataAddPanel"); // NOI18N
        measuredDataAddPanel.setLayout(new BoxLayout(measuredDataAddPanel, BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(measuredDataAddPanel);

        deleteAllButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteAllButton.setText("Delete All");
        deleteAllButton.setName("deleteAllButton"); // NOI18N
        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });

        deleteByDateButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png"))); // NOI18N
        deleteByDateButton.setText("Delete");
        deleteByDateButton.setName("deleteByDateButton"); // NOI18N
        deleteByDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteByDateButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Delete all older as");

        deleteOlderAsDate.setName("deleteOlderAsDate"); // NOI18N

        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.setName("refreshButton"); // NOI18N
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteOlderAsDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteByDateButton)
                        .addGap(10, 10, 10)
                        .addComponent(deleteAllButton)
                        .addGap(10, 10, 10)
                        .addComponent(closeButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(deleteAllButton)
                        .addComponent(deleteByDateButton)
                        .addComponent(closeButton))
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(refreshButton))
                        .addComponent(deleteOlderAsDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteAllButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        this.monitorMainFrame
                .getMonitorReference()
                .ServicegetMonitorControl()
                .deleteMeasuredDataOfProcess(wpsProcess);

        recaptureData();
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void deleteByDateButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteByDateButtonActionPerformed
        if (deleteOlderAsDate.getDate() == null) {
            MessageDialogs.showError(this, "Error", "Please select a valid Date!");
        } else {
            Date olderAs = getZeroTimeDate();

            this.monitorMainFrame
                    .getMonitorReference()
                    .ServicegetMonitorControl()
                    .deleteMeasuredDataOfProcess(wpsProcess, olderAs);

            recaptureData();
        }
    }//GEN-LAST:event_deleteByDateButtonActionPerformed

    private Date getZeroTimeDate() {
        Date result = deleteOlderAsDate.getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    private void refreshButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        recaptureData();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton closeButton;
    private JButton deleteAllButton;
    private JButton deleteByDateButton;
    private JDateChooser deleteOlderAsDate;
    private JPanel measuredDataAddPanel;
    private JButton refreshButton;
    // End of variables declaration//GEN-END:variables
}
