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
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.ui.query.TaskQueryChildern;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Anuradha
 */
public class TaskRepositoryNode extends AbstractNode {

    private TaskRepository repository;
    private final Extension extension;
    private RepositoryEventAdapter eventAdapter;

    public static TaskRepositoryNode createTaskRepositoryNode(final TaskRepository repository, boolean withChildern) {
        final TaskRepositoryNode node;

        if (withChildern && repository.getLookup().lookup(TaskQuerySupportProvider.class) != null) {
            final TaskQueryChildern childern = new TaskQueryChildern(repository);
            node = new TaskRepositoryNode(childern, repository);
            node.eventAdapter = new RepositoryEventAdapter() {

                @Override
                public void nameChenged() {
                    node.setDisplayName(repository.getName());
                }

                @Override
                public void descriptionChenged() {
                    node.setShortDescription(repository.getDescription());
                }

                @Override
                public void queryAdded() {
                    childern.refreshNodes();
                }

                @Override
                public void queryUpdated() {
                    childern.refreshNodes();
                }

                @Override
                public void queryRemoved() {
                    childern.refreshNodes();
                }
            };
            node.extension.add(node.eventAdapter);
        } else {
            node = new TaskRepositoryNode(Children.LEAF, repository);

            node.eventAdapter = new RepositoryEventAdapter() {

                @Override
                public void nameChenged() {
                    node.setDisplayName(repository.getName());
                }

                @Override
                public void descriptionChenged() {
                    node.setShortDescription(repository.getDescription());
                }
            };
            node.extension.add(node.eventAdapter);
        }
        return node;
    }

    private TaskRepositoryNode(Children children, final TaskRepository repository) {
        super(children);
        extension = repository.getLookup().lookup(Extension.class);
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

    @Override
    public void destroy() throws IOException {
        super.destroy();
        extension.remove(eventAdapter);
    }
}
