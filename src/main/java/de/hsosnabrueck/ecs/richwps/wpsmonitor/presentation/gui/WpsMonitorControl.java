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

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.utils.Param;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author FloH
 */
public class WpsMonitorControl extends javax.swing.JFrame {
    private MonitorControl monitorControlFacade;

    /**
     * Creates new form WpsMonitorControl
     */
    public WpsMonitorControl(final MonitorControl monitorControlFacade) {
        //this.monitorControlFacade = Param.notNull(monitorControlFacade, "monitorControlFacade");
        initComponents();

    }
    
    private Boolean isCreateFieldsValid() {
        return !(wpsToAddField.getText().trim().equalsIgnoreCase("") || wpsToAddUriField.getText().trim().equalsIgnoreCase(""));
    }
    
    private void performInput() {
        resetError();
        addWpsButton.setEnabled(isCreateFieldsValid());
    }

    public JTextField getWpsToAddField() {
        return wpsToAddField;
    }

    public void setWpsToAddField(JTextField wpsToAddField) {
        this.wpsToAddField = wpsToAddField;
    }

    public JTextField getWpsToAddUriField() {
        return wpsToAddUriField;
    }

    public void setWpsToAddUriField(JTextField wpsToAddUriField) {
        this.wpsToAddUriField = wpsToAddUriField;
    }
    
    private void setErrorText(final String errorText) {
        wpsAddErrorLabel.setText(errorText);
    }
    
    private void resetError() {
        setErrorText("");
    }
    
    private void showChanges() {
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wpsToAddField = new javax.swing.JTextField();
        addWpsDecoText = new javax.swing.JLabel();
        addWpsButton = new javax.swing.JButton();
        wpsScrollPane = new javax.swing.JScrollPane();
        wpsAddPanel = new javax.swing.JPanel();
        wpsToAddUriField = new javax.swing.JTextField();
        wpsAddErrorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WPS-Monitor Control Interface");
        setResizable(false);

        wpsToAddField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                wpsCreateFieldsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                wpsCreateFieldsFocusLost(evt);
            }
        });
        wpsToAddField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpsToAddFieldActionPerformed(evt);
            }
        });
        wpsToAddField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                wpsCreateFieldsKeyPressed(evt);
            }
        });

        addWpsDecoText.setText("Add WPS");

        addWpsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addWpsButton.setText("Create WPS");
        addWpsButton.setEnabled(false);
        addWpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWpsButtonActionPerformed(evt);
            }
        });

        wpsAddPanel.setLayout(new javax.swing.BoxLayout(wpsAddPanel, javax.swing.BoxLayout.PAGE_AXIS));
        wpsScrollPane.setViewportView(wpsAddPanel);

        wpsToAddUriField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                wpsCreateFieldsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                wpsCreateFieldsFocusLost(evt);
            }
        });
        wpsToAddUriField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpsToAddUriFieldActionPerformed(evt);
            }
        });
        wpsToAddUriField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                wpsCreateFieldsKeyPressed(evt);
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
                        .addComponent(wpsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addWpsDecoText)
                        .addGap(18, 18, 18)
                        .addComponent(wpsToAddField, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpsToAddUriField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addWpsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wpsAddErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {wpsToAddField, wpsToAddUriField});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wpsAddErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(wpsToAddField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addWpsDecoText)
                        .addComponent(addWpsButton)
                        .addComponent(wpsToAddUriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wpsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void wpsCreateFieldsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_wpsCreateFieldsFocusGained
        performInput();
    }//GEN-LAST:event_wpsCreateFieldsFocusGained

    private void wpsCreateFieldsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_wpsCreateFieldsFocusLost
        performInput();
    }//GEN-LAST:event_wpsCreateFieldsFocusLost

    private void wpsCreateFieldsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wpsCreateFieldsKeyPressed
        performInput();
    }//GEN-LAST:event_wpsCreateFieldsKeyPressed

    private void addWpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWpsButtonActionPerformed
        if(isCreateFieldsValid()) {
            try {
                URL urlCheck = new URL(getWpsToAddUriField().getText());
                URI wpsUri = urlCheck.toURI();

                JPanel wpsPanel = new WpsPanel(this, getWpsToAddField().getText(), wpsUri, wpsAddPanel);
                
                wpsAddPanel.add(wpsPanel, BorderLayout.PAGE_START);
                
                getWpsToAddField().setText("");
                getWpsToAddUriField().setText("");
                
                getWpsToAddField().requestFocus();
                
                wpsAddPanel.revalidate();
            } catch (URISyntaxException ex) {
                setErrorText("The entered URI is not valid!");
            } catch (MalformedURLException ex) {
                setErrorText("The entered URL is not valid!");
            }
        }
    }//GEN-LAST:event_addWpsButtonActionPerformed

    private void wpsToAddFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpsToAddFieldActionPerformed
        addWpsButtonActionPerformed(evt);
    }//GEN-LAST:event_wpsToAddFieldActionPerformed

    private void wpsToAddUriFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpsToAddUriFieldActionPerformed
        addWpsButtonActionPerformed(evt);
    }//GEN-LAST:event_wpsToAddUriFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addWpsButton;
    private javax.swing.JLabel addWpsDecoText;
    private javax.swing.JLabel wpsAddErrorLabel;
    private javax.swing.JPanel wpsAddPanel;
    private javax.swing.JScrollPane wpsScrollPane;
    private javax.swing.JTextField wpsToAddField;
    private javax.swing.JTextField wpsToAddUriField;
    // End of variables declaration//GEN-END:variables
}