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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui.utils;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Helper class for JOptionPane dialogs.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class MessageDialogs {

    /**
     * Shows a question dialog with YES_NO_OPTION.
     *
     * @param frame Parentframe
     * @param title Title text
     * @param question Question text
     * @return JOptionPane enum value for evaluate YES or NO option
     */
    public static Boolean showQuestionDialog(final Component frame, final String title, final String question) {
        int n = JOptionPane.showConfirmDialog(
                frame,
                question,
                title,
                JOptionPane.YES_NO_OPTION);

        return n == JOptionPane.YES_OPTION;
    }

    /**
     * Shows an error dialog.
     *
     * @param frame Parentframe
     * @param title Title text
     * @param message Error message text
     */
    public static void showError(final Component frame, final String title, final String message) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an detailed error dialog in use of a textarea.
     *
     * @param frame Parentframe
     * @param title Title text
     * @param description Description of the error
     * @param message Error message text
     */
    public static void showDetailedError(final Component frame, final String title, final String description, final String message) {
        DetailedMessage detailedMessage = new DetailedMessage(description, message);
        
        JOptionPane.showMessageDialog(frame,
                detailedMessage,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * private to prevent instantiation.
     */
    private MessageDialogs() {

    }
}
