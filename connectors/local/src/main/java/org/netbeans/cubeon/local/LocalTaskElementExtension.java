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
package org.netbeans.cubeon.local;

import java.awt.Image;
import java.util.Collection;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.TaskElementChangeAdapter;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskElementExtension implements Extension {

    private LocalTask localTask;
    private InstanceContent content;
    private Lookup lookup;

    public LocalTaskElementExtension(LocalTask localTask) {
        this.localTask = localTask;
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
    }

    public final void remove(Object inst) {
        content.remove(inst);
    }

    public final void add(Object inst) {
        content.add(inst);
    }

    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        if (localTask.getStatus().equals(LocalTaskStatusProvider.COMPLETED)) {
            buffer.append("<font color=\"#808080\">");
            buffer.append("<s>");
        }
        buffer.append(localTask.getName());
        buffer.append("</html>");
        return buffer.toString();
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
    }
    //events---------------------------
    void fireNameChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.nameChenged();
        }

    }

    void fireDescriptionChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.descriptionChenged();
        }
    }

    void firePriorityChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.priorityChenged();
        }
    }

    void fireStatusChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.statusChenged();
        }
    }

    public Lookup getLookup() {
        return lookup;
    }
}
