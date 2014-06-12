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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.derby.impl.tools.sysinfo.Main;

/**
 *
 * @author FloH
 */
public class GuiStarter {
    public static void start(final MonitorControl controlDependency) {
         /* Create and display the form */
        
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fallback
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch (ClassNotFoundException e) {
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (InstantiationException e) {
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (IllegalAccessException e) {
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WpsMonitorControl(controlDependency).setVisible(true);
            }
        });
    }
}
