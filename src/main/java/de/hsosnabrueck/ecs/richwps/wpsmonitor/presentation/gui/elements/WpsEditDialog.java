/*
 * Copyright 2014 Fruchuxs.
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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.MessageDialogs;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.util.Validate;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.swing.ImageIcon;

/**
 * JDialog to edit WPS. Actions with this dialog have an impact on the scheduler
 * and the wps database. All jobs need to unscheduled and replaced with  
 * new ones which match the changes of the wps identifier.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsEditDialog extends javax.swing.JDialog {

    private final WpsPanel addParentPanel;
    private final WpsMonitorGui monitorMainFrame;

    /**
     * Constructor.
     * 
     * @param monitorMainFrame Reference to the MainFrame of this gui
     * @param addParentPanel Parent panel; is needed for delete operation
     * @param modal true for modal dialog
     */
    public WpsEditDialog(WpsMonitorGui monitorMainFrame, WpsPanel addParentPanel, boolean modal) {
        super(monitorMainFrame, modal);
        initComponents();

        setLocationRelativeTo(monitorMainFrame);

        this.monitorMainFrame = monitorMainFrame;
        this.addParentPanel = Validate.notNull(addParentPanel, "parent");
        this.newIdentifierTextField.setText(addParentPanel.getWps()
                .getIdentifier()
        );
        this.newUriTextField.setText(addParentPanel.getWps()
                .getUri()
                .toString()
        );
        
        appendTitle(addParentPanel.getWps().getIdentifier());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel identifierDecoText = new javax.swing.JLabel();
        newIdentifierTextField = new javax.swing.JTextField();
        javax.swing.JLabel uriDecoText = new javax.swing.JLabel();
        newUriTextField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit WPS");
        setIconImage(new ImageIcon(getClass().getResource("/icons/edit.png")).getImage());

        identifierDecoText.setText("Identifier");

        newIdentifierTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newIdentifierTextFieldActionPerformed(evt);
            }
        });

        uriDecoText.setText("URI");

        newUriTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUriTextFieldActionPerformed(evt);
            }
        });

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancel.png"))); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(identifierDecoText)
                        .addGap(18, 18, 18)
                        .addComponent(newIdentifierTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uriDecoText)
                        .addGap(44, 44, 44)
                        .addComponent(newUriTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(identifierDecoText)
                    .addComponent(newIdentifierTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriDecoText)
                    .addComponent(newUriTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Action behavior for saveButton.
     * 
     * @param evt 
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        String wpsIdentifier = newIdentifierTextField.getText();
        String wpsUri = newUriTextField.getText();

        try {
            WpsEntity addWps = new WpsEntity(wpsIdentifier, wpsUri);

            addParentPanel.updateWps(addWps);
            addParentPanel.reInit();
            
            dispose();
        } catch (MalformedURLException ex) {
            showUriErrorDialog();
        } catch (URISyntaxException ex) {
            showUriErrorDialog();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void newIdentifierTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newIdentifierTextFieldActionPerformed
        newUriTextField.requestFocus();
    }//GEN-LAST:event_newIdentifierTextFieldActionPerformed

    private void newUriTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newUriTextFieldActionPerformed
        saveButtonActionPerformed(evt);
    }//GEN-LAST:event_newUriTextFieldActionPerformed

    private void showUriErrorDialog() {
        MessageDialogs.showError(this, "Malformed URI", "The entered URI is not valid!");
    }
    
    private void appendTitle(String name) {
        this.setTitle(getTitle() + " " + name);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField newIdentifierTextField;
    private javax.swing.JTextField newUriTextField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
