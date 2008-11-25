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

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskElementExtension extends Notifier<TaskElementChangeAdapter> {

    private LocalTask localTask;

    public LocalTaskElementExtension(LocalTask localTask) {
        this.localTask = localTask;

    }

    //events---------------------------
    void fireNameChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = getAll();
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.nameChenged();
        }

    }

    void fireDescriptionChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = getAll();
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.descriptionChenged();
        }
    }

    void firePriorityChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = getAll();
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.priorityChenged();
        }
    }

    void fireStatusChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = getAll();
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.statusChenged();
        }
    }

    void fireTypeChenged() {
        Collection<? extends TaskElementChangeAdapter> adapters = getAll();
        for (TaskElementChangeAdapter adapter : adapters) {
            adapter.typeChenged();
        }

    }

    public Lookup getLookup() {
        return Lookups.singleton(this);
    }
}
