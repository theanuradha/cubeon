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
package org.netbeans.cubeon.context.task.link;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TaskLinksNode extends AbstractNode {

    private TaskElement taskElement;
    private TaskLinkResourceSet resourceSet;

    public TaskLinksNode(TaskElement taskElement, TaskLinkResourceSet resourceSet) {
        super(new ResourcesChildern(taskElement, resourceSet), Lookups.fixed(taskElement, resourceSet));
        this.resourceSet = resourceSet;
        setDisplayName(resourceSet.getName());
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/cubeon/context/task/link/subtasks.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new AbstractAction(
                    NbBundle.getMessage(TaskLinksNode.class, "LBL_Refresh")) {

                public void actionPerformed(ActionEvent e) {
                    refresh();
                }
            }
                };
    }

    

    private static class ResourcesChildern extends Children.Keys<TaskResource> {

        private TaskElement taskElement;
        private TaskLinkResourceSet resourceSet;

        public ResourcesChildern(TaskElement taskElement, TaskLinkResourceSet resourceSet) {
            this.taskElement = taskElement;
            this.resourceSet = resourceSet;
        }

        @Override
        protected Node[] createNodes(TaskResource resource) {
            return new Node[]{resource.getNode()};
        }

        @Override
        protected void addNotify() {

            List<TaskResource> resources = new ArrayList<TaskResource>();
            for (TaskLinkResource taskResource : resourceSet.getTaskLinkResource()) {
                    resources.add(taskResource);
            }

            setKeys(resources);
        }
    }

    void refresh() {
        setChildren(new ResourcesChildern(taskElement, resourceSet));
    }
}
