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
package org.netbeans.cubeon.local.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskPriorityProvider implements TaskPriorityProvider {

    public LocalTaskPriorityProvider() {
    }

    public List<TaskPriority> getTaskPrioritys() {
        List<TaskPriority> prioritys = new ArrayList<TaskPriority>();
        prioritys.add(TaskPriority.createPriority(TaskPriority.PRIORITY.P1, "P1"));
        prioritys.add(TaskPriority.createPriority(TaskPriority.PRIORITY.P2, "P2"));
        prioritys.add(TaskPriority.createPriority(TaskPriority.PRIORITY.P3, "P3"));
        prioritys.add(TaskPriority.createPriority(TaskPriority.PRIORITY.P4, "P4"));
        prioritys.add(TaskPriority.createPriority(TaskPriority.PRIORITY.P5, "P5"));

        return new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriorityById(TaskPriority.PRIORITY priority) {
        for (TaskPriority tp : getTaskPrioritys()) {
            if (priority==tp.getId()) {
                return tp;
            }
        }
        // returning P3 if Priority not found
        return getDefaultPriority();
    }

    public TaskPriority getDefaultPriority() {

        return TaskPriority.createPriority(TaskPriority.PRIORITY.P3, "P3");
    }

    public TaskPriority getTaskPriority(TaskElement element) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask!=null;
        return localTask.getPriority();
    }

    public void setTaskPriority(TaskElement element, TaskPriority priority) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask!=null;
        localTask.setPriority(priority);
    }
}
