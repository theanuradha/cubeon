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
package org.netbeans.cubeon.local.nodes;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalRepositoryNode extends AbstractNode {

    public LocalRepositoryNode(LocalTaskRepository repository) {
        super(Children.LEAF, Lookups.singleton(repository));
        setDisplayName(repository.getName());
        setShortDescription(repository.getDescription());
    }

    @Override
    public Image getIcon(int arg0) {
 
        return Utilities.loadImage("org/netbeans/cubeon/local/nodes/local-repository.png");
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }
    
    
}
