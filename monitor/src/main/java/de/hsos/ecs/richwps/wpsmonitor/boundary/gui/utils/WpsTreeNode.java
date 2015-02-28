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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Specialisation of DefaultMutableTreeNode-class to determine which type of
 * node the specific instance is.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsTreeNode extends DefaultMutableTreeNode {

    /**
     * Indicates which type this node is - this is important for the selection
     */
    public static enum NodeType {

        DRIVER, WPS, PROCESS
    }
    private final NodeType type;

    public WpsTreeNode(Object obj, NodeType type) {
        super(obj);
        this.type = type;
    }

    /**
     * Gets the type of the Node: Driver Node, WPS Node or WPS-Process Node
     *
     * @return NideType instance
     */
    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.getUserObject().toString();
    }

    /**
     * Casts the userObject of this node to the specific WpsDescription or
     * WPSProcessDescription type. The Datatype depends on the Node-Type.
     *
     * @param <T>
     * @return WpsDescription or WPSProcessDescription
     */
    public <T> T getDescription() {
        T result = null;

        if (userObject != null) {
            result = (T) userObject.getClass().cast(userObject);
        }

        return result;
    }
}
