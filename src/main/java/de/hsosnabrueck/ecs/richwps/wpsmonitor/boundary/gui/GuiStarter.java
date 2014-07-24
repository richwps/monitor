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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataDriver;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.datasource.semanticproxy.SemanticProxyData;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.boundary.gui.elements.WpsMonitorAdminGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.Monitor;
import java.util.HashSet;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sets a look and feel up and starts the monitor gui with the montior-instance
 * and DataDrivers - if exists - as dependency.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class GuiStarter {

    private static final Logger LOG = LogManager.getLogger();

    public static void start(final Monitor controlDependency) {
        start(controlDependency, null);
    }

    /**
     *
     * @param controlDependency
     * @param drivers
     */
    public static void start(final Monitor controlDependency, final Set<DataDriver> drivers) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            try {
                LOG.warn("Can't load SystemLookAndFeel! Try to fallback to CrossPlatformLookAndFeel!");
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fallback
            } catch (ClassNotFoundException ex) {
                LOG.error("Can't load SystemLookAndFeel.", ex);
            } catch (InstantiationException ex) {
                LOG.error("Can't load SystemLookAndFeel.", ex);
            } catch (IllegalAccessException ex) {
                LOG.error("Can't load SystemLookAndFeel.", ex);
            } catch (UnsupportedLookAndFeelException ex) {
                LOG.error("Can't load SystemLookAndFeel.", ex);
            }
        } catch (ClassNotFoundException ex) {
            LOG.error("Can't load SystemLookAndFeel.", ex);
        } catch (InstantiationException ex) {
            LOG.error("Can't load SystemLookAndFeel.", ex);
        } catch (IllegalAccessException ex) {
            LOG.error("Can't load SystemLookAndFeel.", ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WpsMonitorAdminGui wpsMonitorGui = new WpsMonitorAdminGui(controlDependency, drivers);
                wpsMonitorGui.setVisible(true);
            }
        });
    }

    private GuiStarter() {

    }
}
