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

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsProcessEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.Monitor;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.MonitorControl;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.monitor.control.TriggerConfig;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsMonitorGui;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsPanel;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsProcessJobEntry;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.gui.elements.WpsProcessPanel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.derby.impl.tools.sysinfo.Main;

/**
 *
 * @author FloH
 */
public class GuiStarter {
    public static void start(final Monitor controlDependency) {
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
                new WpsMonitorGui(controlDependency).setVisible(true);
            }
        });
    }
    
    public static void restoreGui(Monitor monitor, WpsMonitorGui gui) throws GuiErrorException {
        MonitorControl control = monitor.getMonitorControl();
        
        List<WpsEntity> wpsEntities = control.getWpsList();
        
        for(WpsEntity wpsEntity : wpsEntities) {
            List<WpsProcessEntity> wpsProcessEntities = control.getProcessesOfWps(wpsEntity.getIdentifier());
            
            //WpsPanel(WpsMonitorGui mainFrame, JPanel parent, final Wps wps)
            WpsPanel wpsPanel = new WpsPanel(gui, gui.getWpsAddPanel(), wpsEntity);
            
            
            // add processes to wpsPanel's dialog
            for(WpsProcessEntity processEntity : wpsProcessEntities) {
                JPanel addProcessPane = wpsPanel.getWpsProcessDialog().getAddProcessPane();
                WpsProcessPanel wpsProcessPanel = new WpsProcessPanel(gui, wpsPanel.getWpsProcessDialog(), processEntity, true);
                
                //select trriggerconfig objects 
                JPanel addJobEntryPanel = wpsProcessPanel.getWpsProcessJobDialog().getAddJobPane(); 
                List<TriggerConfig> triggers = control.getTriggers(wpsEntity.getIdentifier(), processEntity.getIdentifier());
                
                // add WpsProcessJobEntries to addJobEntryPanel
                for(TriggerConfig triggerConfig : triggers) {
                    WpsProcessJobEntry jobEntry = new WpsProcessJobEntry(gui, addProcessPane, processEntity, triggerConfig);
                    addJobEntryPanel.add(jobEntry);
                }
                
                addProcessPane.add(wpsProcessPanel);
            }
            
            gui.getWpsAddPanel().add(gui);
        }
    }
    
    private GuiStarter() {
        
    }
}
