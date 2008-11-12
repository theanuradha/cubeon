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
package org.netbeans.cubeon.jira.query;

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class QueryExtension extends Notifier<TaskQueryEventAdapter> {

    private AbstractJiraQuery query;

    public QueryExtension(AbstractJiraQuery query) {
        this.query = query;

    }

    public Lookup getLookup() {
        return Lookups.singleton(this);
    }

    //events---------------------------
    void fireAttributesUpdated() {
        Collection<? extends TaskQueryEventAdapter> adapters = getAll();
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.atributesupdated();
        }

    }

    void fireSynchronizing() {
        Collection<? extends TaskQueryEventAdapter> adapters = getAll();
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.querySynchronizing();
        }

    }

    void fireTaskAdded(TaskElement element) {
        Collection<? extends TaskQueryEventAdapter> adapters = getAll();
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.taskAdded(element);
        }

    }

    void fireSynchronized() {
        Collection<? extends TaskQueryEventAdapter> adapters = getAll();
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.querySynchronized();
        }

    }

    void fireRemoved() {
        Collection<? extends TaskQueryEventAdapter> adapters = getAll();
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.removed();
        }

    }
}
