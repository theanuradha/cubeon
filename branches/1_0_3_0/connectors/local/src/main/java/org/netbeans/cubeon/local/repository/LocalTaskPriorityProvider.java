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
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskPriorityProvider implements TaskPriorityProvider {

    public final TaskPriority P1;
    public final TaskPriority P2;
    public final TaskPriority P3;
    public final TaskPriority P4;
    public final TaskPriority P5;

    public LocalTaskPriorityProvider(TaskRepository repository) {

        P1 = new TaskPriority(repository, "P1", "P1");//NOI18N
        P2 = new TaskPriority(repository, "P2", "P2");//NOI18N
        P3 = new TaskPriority(repository, "P3", "P3");//NOI18N
        P4 = new TaskPriority(repository, "P4", "P4");//NOI18N
        P5 = new TaskPriority(repository, "P5", "P5");//NOI18N
    }

    public List<TaskPriority> getTaskPriorities() {
        List<TaskPriority> prioritys = new ArrayList<TaskPriority>();
        prioritys.add(P1);
        prioritys.add(P2);
        prioritys.add(P3);
        prioritys.add(P4);
        prioritys.add(P5);


        return new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriorityById(String priority) {
        for (TaskPriority tp : getTaskPriorities()) {
            if (tp.getId().equals(priority)) {
                return tp;
            }
        }
        // returning P3 if Priority not found
        return null;
    }

    public TaskPriority getDefaultPriority() {

        return P3;
    }

    public TaskPriority getTaskPriority(TaskElement element) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        return localTask.getPriority();
    }

    public void setTaskPriority(TaskElement element, TaskPriority priority) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        localTask.setPriority(priority);
    }
}
