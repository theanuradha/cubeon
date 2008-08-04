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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.cubeon.context.api.TaskContextHandler;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.cubeon.context.spi.TaskResourceSet;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TaskLinkResourceSet implements TaskResourceSet {

    private final TaskElement element;
    private final TaskLinkProvider provider;
    private final TaskContextManager contextManager;
    private final List<TaskLinkResource> resources = new ArrayList<TaskLinkResource>();
    private AtomicBoolean lasyInit = new AtomicBoolean(true);
    private TaskLinksNode node;

    public TaskLinkResourceSet(TaskElement element, TaskLinkProvider provider) {
        this.element = element;
        this.provider = provider;
        contextManager = Lookup.getDefault().lookup(TaskContextManager.class);

    }

    public Node getNode() {
        return node = new TaskLinksNode(element, this);
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public void addTaskResource(TaskResource tr) {
        TaskLinkResource linkResource = tr.getLookup().lookup(TaskLinkResource.class);
        if (linkResource != null) {
            resources.add(linkResource);
            TaskContextHandler contextHandler = contextManager.getActiveContextHandler();
            if (contextHandler != null) {
                new ResourcesPersistenceHandler(contextHandler).add(linkResource);
            }
            if (node != null) {
                node.refresh();
            }
        }
    }

    public void removeTaskResource(TaskResource tr) {
        TaskLinkResource linkResource = tr.getLookup().lookup(TaskLinkResource.class);
        if (linkResource != null) {
            resources.remove(linkResource);
            TaskContextHandler contextHandler = contextManager.getActiveContextHandler();
            if (contextHandler != null) {
                new ResourcesPersistenceHandler(contextHandler).remove(linkResource);
            }
            if (node != null) {
                node.refresh();
            }

        }

    }

    public List<TaskResource> getResources() {
        return new ArrayList<TaskResource>(getTaskLinkResource());
    }

    public List<TaskLinkResource> getTaskLinkResource() {
        if (lasyInit.getAndSet(false)) {
            resources.addAll(new ResourcesPersistenceHandler(
                    contextManager.getActiveContextHandler()).refresh());
        }
        return resources;
    }

    public String getName() {
        return "Related Tasks";
    }

    boolean contains(TaskElement element) {
        for (TaskLinkResource resource : getTaskLinkResource()) {
            if (element.equals(resource.getElement())) {
                return true;
            }
        }
        return false;
    }

    void remove(TaskElement element) {
        TaskResource tr = null;
        for (TaskLinkResource resource : getTaskLinkResource()) {
            if (element.equals(resource.getElement())) {
                tr = resource;
                break;
            }
        }
        if (tr != null) {
            removeTaskResource(tr);
        }
    }
}
