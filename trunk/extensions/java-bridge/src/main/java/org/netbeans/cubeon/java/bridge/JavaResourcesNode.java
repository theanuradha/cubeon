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
package org.netbeans.cubeon.java.bridge;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TagNode;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class JavaResourcesNode extends AbstractNode {

    private TaskElement taskElement;
    private JavaResourceSet resourceSet;

    public JavaResourcesNode(TaskElement taskElement, JavaResourceSet resourceSet) {
        super(new ResourcesChildern(taskElement, resourceSet), Lookups.fixed(taskElement, resourceSet));
        this.resourceSet = resourceSet;
        setDisplayName(resourceSet.getName());
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/cubeon/java/bridge/sourcegroup.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }

    private static class ResourcesChildern extends Children.Keys<TaskResource> {

        private static final TaskResource LOADINFG = new JavaResource("Loading") {

            @Override
            public Node getNode() {
                return TagNode.createNode("Loading...", "Loading...",
                        Utilities.loadImage("org/netbeans/cubeon/java/bridge/wait.png"));
            }
        };
        private TaskElement taskElement;
        private JavaResourceSet resourceSet;

        public ResourcesChildern(TaskElement taskElement, JavaResourceSet resourceSet) {
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
                    for (JavaResource resource : resourceSet.getJavaResources()) {
                        resource.init();
                    }
                    setKeys(resourceSet.getResources());
                }
            });

        }
    }

    void refresh() {
        setChildren(new ResourcesChildern(taskElement, resourceSet));
    }
}
