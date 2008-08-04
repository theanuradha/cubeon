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
package org.netbeans.cubeon.context.api;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.context.spi.TaskResourceSet;
import org.netbeans.cubeon.context.spi.TaskResouresProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public final class TaskContext {

    private final TaskElement task;
    private final List<TaskResourceSet> resourceSets;

    public TaskContext(TaskElement task) {
        this.task = task;
        resourceSets = new ArrayList<TaskResourceSet>();
        TaskContextManager tcm = Lookup.getDefault().lookup(TaskContextManager.class);
        List<TaskResouresProvider> providers = tcm.getResouresProviders(task);
        for (TaskResouresProvider trp : providers) {
            resourceSets.add(trp.createResourceSet(task));
        }
    }

    public TaskElement getTask() {
        return task;
    }

    public Lookup getLookup() {
        return Lookups.fixed(resourceSets.toArray());
    }

    public List<TaskResourceSet> getResourceSets() {

        return new ArrayList<TaskResourceSet>(resourceSets);
    }
}
