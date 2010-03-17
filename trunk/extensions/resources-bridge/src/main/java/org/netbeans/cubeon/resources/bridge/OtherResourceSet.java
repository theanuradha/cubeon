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
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class OtherResourceSet implements TaskResourceSet {

    private final TaskElement element;
    private final OtherResourceProvider provider;
    private final TaskContextManager contextManager;
    private final List<OtherResource> resources = new ArrayList<OtherResource>();
    private AtomicBoolean lasyInit = new AtomicBoolean(true);
    private OtherResourcesNode node;

    public OtherResourceSet(TaskElement element, OtherResourceProvider provider) {
        this.element = element;
        this.provider = provider;
        contextManager = Lookup.getDefault().lookup(TaskContextManager.class);

    }

    public Node getNode() {
        return node = new OtherResourcesNode(element, this);
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public void addTaskResource(TaskResource tr) {
        OtherResource linkResource = tr.getLookup().lookup(OtherResource.class);
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
        OtherResource linkResource = tr.getLookup().lookup(OtherResource.class);
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
        return new ArrayList<TaskResource>(getJavaResources());
    }

    public List<OtherResource> getJavaResources() {
        if (lasyInit.getAndSet(false)) {
            List<OtherResource> refresh =
                    new ResourcesPersistenceHandler(contextManager.getActiveContextHandler()).refresh();
            resources.addAll(refresh);
        }
        return resources;
    }

    public String getName() {
        return NbBundle.getMessage(OtherResourceSet.class, "LBL_Java_Sources");
    }

    boolean contains(OtherResource element) {
        for (OtherResource resource : getJavaResources()) {
            if (element.getPath().equals(resource.getPath())) {
                return true;
            }
        }
        return false;
    }

    void remove(OtherResource element) {
        TaskResource tr = null;
        for (OtherResource resource : getJavaResources()) {
            if (element.getPath().equals(resource.getPath())) {
                tr = resource;
                break;
            }
        }
        if (tr != null) {
            removeTaskResource(tr);
        }
    }
}
