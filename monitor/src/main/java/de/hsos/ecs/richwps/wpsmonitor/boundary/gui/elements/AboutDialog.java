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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.elements;

import de.hsos.ecs.richwps.wpsmonitor.ApplicationInfo;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 * About Dialog with license informations.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class AboutDialog extends JDialog {

    /**
     * Creates new form About
     *
     * @param parent Parent frame
     */
    public AboutDialog(final Frame parent) {
        super(parent, true);

        initComponents();
        
        monitorInfoTextLabel.setText(monitorInfoTextLabel.getText().replace("{VERSION}", ApplicationInfo.VERSION));
        monitorInfoTextLabel.setText(monitorInfoTextLabel.getText().replace("{PROJECT_SITE}", ApplicationInfo.PROJECT_SITE));
        setLocationRelativeTo(parent);
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
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        monitorInfoTextLabel = new JLabel();
        JLabel jLabel4 = new JLabel();
        closeButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About WPS Monitor");
        setName("aboutDialog"); // NOI18N

        jPanel1.setBorder(BorderFactory.createTitledBorder("About"));

        jLabel1.setText("<html><body style=\"text-align:center\">Copyright 2014 Florian Vogelpohl &lt;floriantobias@gmail.com&gt;.<br/> <br/> Licensed under the Apache License, Version 2.0 (the \"License\");<br/>You may obtain a copy of the License at<br/><br/>      <a href=\"http://www.apache.org/licenses/LICENSE-2.0\">http://www.apache.org/licenses/LICENSE-2.0</a><br/> <br/> Unless required by applicable law or agreed to in writing, software<br/> distributed under the License is distributed on an \"AS IS\" BASIS,<br/> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br/> See the License for the specific language governing permissions and<br/> limitations under the License.</body></html>");

        jLabel2.setIcon(new ImageIcon(getClass().getResource("/images/Logo_HS_Osnabrueck.PNG"))); // NOI18N

        monitorInfoTextLabel.setText("<html> <body style=\"text-align:center\"> <p> <strong style=\"font-size:10px\">WPS Monitor {VERSION}</strong> </p> A system to monitor Web Processing Services (WPS)<br/>Projectsite: <a href=\"{PROJECT_SITE}\">{PROJECT_SITE}</a> </body> </html>");

        jLabel4.setText("<html><body style=\"text-align:center;\">As part of the RichWPS Project of the Faculty of Engineering and Computer Science</body></html>");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 298, GroupLayout.PREFERRED_SIZE)
                            .addComponent(monitorInfoTextLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(38, 38, 38)
                .addComponent(monitorInfoTextLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(closeButton)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton closeButton;
    private JLabel monitorInfoTextLabel;
    // End of variables declaration//GEN-END:variables
}