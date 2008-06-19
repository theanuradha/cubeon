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
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.ui.query.TaskQueriesNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TaskRepositoryNode extends AbstractNode {

    private TaskRepository repository;

    public TaskRepositoryNode(TaskRepository repository) {
        super(getRepositoryChildren(repository), Lookups.singleton(repository));
        this.repository = repository;
        setDisplayName(repository.getName());
        setShortDescription(repository.getDescription());

    }

    @Override
    public Image getIcon(int arg0) {

        return repository.getImage();
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }

    private static Children getRepositoryChildren(TaskRepository repository) {
        Children.Array array = new Children.Array();

        Lookup lookup = repository.getLookup();
        TaskQuerySupportProvider provider = lookup.lookup(TaskQuerySupportProvider.class);
        if (provider != null) {
            array.add(new Node[]{new TaskQueriesNode(provider)});
        }
        return array;

    }
}
