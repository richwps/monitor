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

import de.hsos.ecs.richwps.wpsmonitor.util.Validate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;

/**
 * A container which maps JLabel instances to Strings. This container can 
 * be helpful for different messages to different states.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class StateLabelContainer {
    private final Map<String, JLabel> states;
    
    public StateLabelContainer() {
        states  = new HashMap<>();
    }
    
    public Boolean addState(final String stateName, final JLabel label) {
        Validate.notNull(stateName, label);
        Boolean contains = states.containsKey(stateName);
        
        if(!contains) {
            states.put(stateName, label);
        }
        
        return !contains;
    }
    
    public Boolean removeState(final String stateName) {
        Boolean contains = states.containsKey(stateName);
        
        if(contains) {
            states.remove(stateName);
        }
        
        return contains;
    }
    
    public JLabel getStateLabel(final String stateName) {
        return states.get(stateName);
    }
    
    public void applyState(final String stateName, final JLabel apply) {
        JLabel stateLabel = getStateLabel(stateName);
        
        if(stateLabel != null) {
            apply.setIcon(stateLabel.getIcon());
            apply.setText(stateLabel.getText());
        }
    }
    
    public void mergeWith(Map<String, ? extends JLabel> merge) {
        states.putAll(merge);
    }
}
