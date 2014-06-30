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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsMonitorGui;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Setups a look and feel and starts the monitor gui with the montior
 * as dependencie.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class GuiStarter {

    private final static Logger log = LogManager.getLogger();

    public static void start(final Monitor controlDependency) {
        /* Create and display the form */

        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            try {
                log.warn("Can't load SystemLookAndFeel! Try to fallback to CrossPlatformLookAndFeel!");
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fallback
            } catch (ClassNotFoundException ex) {
                log.error(ex);
            } catch (InstantiationException ex) {
                log.error(ex);
            } catch (IllegalAccessException ex) {
                log.error(ex);
            } catch (UnsupportedLookAndFeelException ex) {
                log.error(ex);
            }
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        } catch (InstantiationException ex) {
            log.error(ex);
        } catch (IllegalAccessException ex) {
            log.error(ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WpsMonitorGui wpsMonitorGui = new WpsMonitorGui(controlDependency);
                wpsMonitorGui.setVisible(true);
            }
        });
    }

    private GuiStarter() {

    }
}
