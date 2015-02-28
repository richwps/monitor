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

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.WpsMonitorAdminGui;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.MessageDialogs;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.IconLabel;
import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils.StateLabelContainer;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsClient;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsProcessInfo;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsRequest;
import de.hsos.ecs.richwps.wpsmonitor.communication.wpsclient.WpsResponse;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.batik.util.gui.xmleditor.XMLEditorKit;

/**
 * A Dialog to use a WpsClient instance to test the request for WPS-Processes.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsRequestTesterDialog extends javax.swing.JDialog {

    private final WpsMonitorAdminGui mainFrame;
    private final StateLabelContainer stateContainer;
    private final WpsProcessEntity process;
    private final WpsProcessPanel parent;

    private SwingWorker worker;

    private static final Map<String, IconLabel> states;

    /**
     * Initialize the static states field.
     */
    static {
        states = new HashMap<>();

        states.put("waiting", new IconLabel("/icons/wait16trans.gif", "Request in Progress."));
        states.put("error", new IconLabel("/icons/cancel-round.png", "WPS-Exception detected"));
        states.put("notreachable", new IconLabel("/icons/cancel.png", "The WPS-Server is not reachable."));
        states.put("fine", new IconLabel("/icons/apply.png", "Request is okay."));
        states.put("jobless", new IconLabel("/icons/refresh.png", "Press \"Try Request\" to test the given Request."));
        states.put("canceled", new IconLabel("/icons/stop.png", "Request canceled by user."));
    }

    /**
     * Creates new form WpsProcessTest
     *
     * @param mainFrame Mainframe of this GUI
     * @param parent Direct parent of this dialog
     */
    public WpsRequestTesterDialog(final WpsMonitorAdminGui mainFrame, final WpsProcessPanel parent) {
        super(mainFrame, true);

        this.mainFrame = mainFrame;
        this.stateContainer = new StateLabelContainer();
        this.process = parent.getWpsProcess();
        this.parent = parent;

        initComponents();
        init();
    }

    private void init() {
        setTitle("Test Request on " + process.getWps().getEndpoint().toString());

        requestTextarea.setEditorKit(new XMLEditorKit());
        responseTextarea.setEditorKit(new XMLEditorKit());

        stateContainer.mergeWith(states);
        stateContainer.applyState("jobless", statusIndicatorLabel);

        setRequestText(parent.getEnteredRequest());
    }

    private void tryRequest() {
        try {
            final WpsClient wpsClient = mainFrame.getMonitorReference()
                    .getBuilderInstance()
                    .getWpsClientFactory()
                    .create();

            worker = new SwingWorker<WpsResponse, Void>() {
                @Override
                protected WpsResponse doInBackground() throws Exception {
                    stateContainer.applyState("waiting", statusIndicatorLabel);
                    return wpsClient.execute(getRequest());
                }

                @Override
                public void done() {
                    try {
                        handleResponse(get());
                    } catch (InterruptedException | CancellationException ignore) {
                    } catch (ExecutionException ex) {
                        MessageDialogs.showError(mainFrame, "Error", ex.getMessage());
                    }
                }

            };

            worker.execute();
        } catch (CreateException ex) {
            MessageDialogs.showError(this, "Can't create WPS Client!", ex.getMessage());
        }
    }

    private WpsRequest getRequest() {
        WpsProcessInfo info = new WpsProcessInfo(process.getWps().getEndpoint(), process.getIdentifier());

        return new WpsRequest(requestTextarea.getText(), info);
    }

    private void handleResponse(final WpsResponse response) {
        if (response.isWpsException()) {
            stateContainer.applyState("error", statusIndicatorLabel);
        } else if (response.isConnectionException()) {
            stateContainer.applyState("notreachable", statusIndicatorLabel);
        } else {
            stateContainer.applyState("fine", statusIndicatorLabel);
        }

        setResponseText(response.getResponseBody());
        responseTextarea.revalidate();

        doRequestButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }

    private void setResponseText(final String responseText) {
        responseTextarea.setText(formatXml(responseText));
        responseTextarea.setCaretPosition(0);
    }

    private void setRequestText(final String requestText) {
        requestTextarea.setText(requestText);
        requestTextarea.setCaretPosition(0);
    }

    private String formatXml(final String format) {
        String result = format;

        if(format == null || format.isEmpty()) {
            return null;
        }
        
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            StringWriter writer = new StringWriter();
            StringReader reader = new StringReader(format);

            StreamResult res = new StreamResult(writer);
            StreamSource sou = new StreamSource(reader);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(sou, res);

            result = writer.getBuffer().toString();
        } catch (TransformerException ignore) {
        }

        return result;
    }

    @Override
    public void setVisible(boolean b) {
        setLocationRelativeTo(mainFrame);
        super.setVisible(b);
    }

    private void cancelWorker() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(false);

            if (worker.isCancelled()) {
                stateContainer.applyState("canceled", statusIndicatorLabel);
                doRequestButton.setEnabled(true);
            }
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

        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        JScrollPane jScrollPane3 = new JScrollPane();
        requestTextarea = new JEditorPane();
        JPanel jPanel3 = new JPanel();
        JScrollPane jScrollPane4 = new JScrollPane();
        responseTextarea = new JEditorPane();
        JPanel jPanel4 = new JPanel();
        doRequestButton = new JButton();
        closeButton = new JButton();
        JLabel statusLabel = new JLabel();
        statusIndicatorLabel = new JLabel();
        cancelButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/icons/testProcess.png")).getImage());
        setResizable(false);

        jPanel2.setBorder(BorderFactory.createTitledBorder("Request"));

        jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setViewportView(requestTextarea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder("Response"));

        jScrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setMaximumSize(new Dimension(793, 176));
        jScrollPane4.setPreferredSize(new Dimension(798, 176));

        responseTextarea.setEditable(false);
        responseTextarea.setBackground(new Color(236, 236, 236));
        jScrollPane4.setViewportView(responseTextarea);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(BorderFactory.createTitledBorder("Options"));

        doRequestButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        doRequestButton.setText("Try Request");
        doRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doRequestButtonActionPerformed(evt);
            }
        });

        closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/apply.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        statusLabel.setText("Status:");

        statusIndicatorLabel.setIcon(new ImageIcon(getClass().getResource("/icons/wait16trans.gif"))); // NOI18N
        statusIndicatorLabel.setText("Nothing to do..");

        cancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/stop.png"))); // NOI18N
        cancelButton.setText("Cancel Request");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(doRequestButton, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusIndicatorLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(doRequestButton)
                    .addComponent(closeButton)
                    .addComponent(statusLabel)
                    .addComponent(statusIndicatorLabel)
                    .addComponent(cancelButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doRequestButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_doRequestButtonActionPerformed
        cancelButton.setEnabled(true);
        tryRequest();
        doRequestButton.setEnabled(false);
    }//GEN-LAST:event_doRequestButtonActionPerformed

    private void cancelButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelWorker();

        cancelButton.setEnabled(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        cancelWorker();
        parent.setEnteredTestRequest(requestTextarea.getText());
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton cancelButton;
    private JButton closeButton;
    private JButton doRequestButton;
    private JEditorPane requestTextarea;
    private JEditorPane responseTextarea;
    private JLabel statusIndicatorLabel;
    // End of variables declaration//GEN-END:variables
}
