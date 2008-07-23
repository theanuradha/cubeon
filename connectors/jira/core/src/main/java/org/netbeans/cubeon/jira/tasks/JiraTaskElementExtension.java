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
package org.netbeans.cubeon.jira.tasks;

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskElementExtension implements Extension {

    private JiraTask jiraTask;
    private InstanceContent content;
    private Lookup lookup;

    public JiraTaskElementExtension(JiraTask jiraTask) {
        this.jiraTask = jiraTask;
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
    }

    public final void remove(Object inst) {
        content.remove(inst);
    }

    public final void add(Object inst) {
        content.add(inst);
    }


    //events---------------------------
    public void fireNameChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.nameChenged();
        }

    }

    public void fireDescriptionChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.descriptionChenged();
        }
    }

    public void firePriorityChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.priorityChenged();
        }
    }

    public void fireStatusChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.statusChenged();
        }
    }

    public void fireTypeChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.typeChenged();
        }

    }

    public void fireResolutionChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.resolutionChenged();
        }

    }

    public void fireStateChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = lookup.lookupAll(TaskElementChangeAdapter.class);
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.stateChange();
        }

    }

    public Lookup getLookup() {
        return lookup;
    }
}
