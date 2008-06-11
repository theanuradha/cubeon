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
package org.netbeans.cubeon.ui.internals;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TaskRepositoryNode extends AbstractNode {

    private TaskRepository repository;
    private Extension extension;

    public TaskRepositoryNode(TaskRepository repository) {
        super(Children.LEAF, Lookups.singleton(repository));
        this.repository = repository;
        setDisplayName(repository.getName());
        setShortDescription(repository.getDescription());
        extension = repository.getLookup().lookup(Extension.class);
    }

    @Override
    public Image getIcon(int arg0) {

        return extension.getImage();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }

    @Override
    public String getHtmlDisplayName() {
        return extension.getHtmlDisplayName();
    }
}
