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
package de.hsos.ecs.richwps.wpsmonitor.boundary.gui;

import de.hsos.ecs.richwps.wpsmonitor.boundary.gui.datasource.DataSourceCreator;
import de.hsos.ecs.richwps.wpsmonitor.control.Monitor;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sets a look and feel up at the first call and starts the monitor gui with the
 * montior-instance, the directory of logs and DataDrivers - if exists - as
 * dependency.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class GuiStarter {

    private static final Logger LOG = LogManager.getLogger();

    static {
        setupLookAndFeel();
    }

    /**
     * Starts the Java Swing gui as new thread.
     *
     * @param monitor {@link Monitor} instance
     * @param logDirectory Directory to the log files
     */
    public static void start(final Monitor monitor, final String logDirectory) {
        start(monitor, logDirectory, null);
    }

    /**
     * Starts the Java Swing gui as new thread.
     *
     * @param monitor {@link Monitor} instance
     * @param logDirectory Directory to the log files
     * @param dataSourceCreators Set of DataSourceCreator-Instances
     */
    public static void start(final Monitor monitor, final String logDirectory, final Set<DataSourceCreator> dataSourceCreators) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WpsMonitorAdminGui wpsMonitorGui = new WpsMonitorAdminGui(monitor, logDirectory, dataSourceCreators);
                wpsMonitorGui.setVisible(true);
            }
        });
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            try {
                LOG.warn("Can't load SystemLookAndFeel! Try to fallback to CrossPlatformLookAndFeel!");
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fallback
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                LOG.error("Can't load CrossPlatformLookAndFeel.", ex);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            LOG.error("Can't load SystemLookAndFeel.", ex);
        }
    }

    private GuiStarter() {

    }
}
