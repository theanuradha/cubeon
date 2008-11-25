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

import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TaskLinkResource implements TaskResource {

    private final TaskElement element;

    public TaskLinkResource(TaskElement element) {
        this.element = element;
    }

    public String getName() {
        return element.getDisplayName();
    }

    public String getDescription() {
        return element.getDescription();
    }

    public Node getNode() {
        TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);
        Node node = factory.createTaskElementNode(element, true);
        return node;
    }

    public void open() {
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.openTask(element);
    }

    public TaskElement getElement() {
        return element;
    }

    public Lookup getLookup() {
        return Lookups.fixed(element, this);
    }
}
