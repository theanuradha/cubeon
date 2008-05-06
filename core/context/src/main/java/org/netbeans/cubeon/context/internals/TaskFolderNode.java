/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.cubeon.context.internals;

import java.awt.Image;
import org.netbeans.cubeon.context.api.NodeUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Anuradha G
 */
public class TaskFolderNode extends AbstractNode {

    public TaskFolderNode(String name, String description) {
        super(Children.LEAF);//todo add children

        setDisplayName(name);
        setShortDescription(description);

    }

    @Override
    public Image getIcon(int arg0) {
        return NodeUtils.getTreeFolderIcon(false);
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return NodeUtils.getTreeFolderIcon(true);
    }
}
