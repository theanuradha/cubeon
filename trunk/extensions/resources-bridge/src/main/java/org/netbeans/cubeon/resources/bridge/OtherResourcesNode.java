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
package org.netbeans.cubeon.resources.bridge;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.tasks.core.api.TagNode;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class OtherResourcesNode extends AbstractNode {

    private TaskElement taskElement;
    private OtherResourceSet resourceSet;

    public OtherResourcesNode(TaskElement taskElement, OtherResourceSet resourceSet) {
        super(new ResourcesChildern(taskElement, resourceSet), Lookups.fixed(taskElement, resourceSet));
        this.resourceSet = resourceSet;
        setDisplayName(resourceSet.getName());
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/cubeon/java/bridge/sourcegroup.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new AbstractAction(NbBundle.getMessage(OtherResourcesNode.class, "LBL_Refresh")) {

                public void actionPerformed(ActionEvent e) {
                    refresh();
                }
            }
                };
    }

    private static class ResourcesChildern extends Children.Keys<TaskResource> {

        private static final TaskResource LOADINFG = new OtherResource(NbBundle.getMessage(OtherResourcesNode.class, "LBL_Loading")) {

            @Override
            public Node getNode() {
                return TagNode.createNode(getPath(), getPath(),
                        ImageUtilities.loadImage("org/netbeans/cubeon/java/bridge/wait.gif"));
            }
        };
        private TaskElement taskElement;
        private OtherResourceSet resourceSet;

        public ResourcesChildern(TaskElement taskElement, OtherResourceSet resourceSet) {
            this.taskElement = taskElement;
            this.resourceSet = resourceSet;
        }

        @Override
        protected Node[] createNodes(TaskResource resource) {
            return new Node[]{resource.getNode()};
        }

        @Override
        protected void addNotify() {
            setKeys(new TaskResource[]{LOADINFG});
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                       setKeys(resourceSet.getResources());
                        }
                    });

                }
            });

        }
    }

    void refresh() {
        setChildren(new ResourcesChildern(taskElement, resourceSet));
    }
}
