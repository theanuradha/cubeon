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
package org.netbeans.cubeon.trac.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha G
 */
public class TracTaskPriorityProvider implements TaskPriorityProvider {

    private List<TaskPriority> prioritys = new ArrayList<TaskPriority>();

    public TracTaskPriorityProvider() {
    }

    public List<TaskPriority> getTaskPriorities() {
        return new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriorityById(String priority) {
        for (TaskPriority tp : getTaskPriorities()) {
            if (tp.getId().equals(priority)) {
                return tp;
            }
        }

        return null;
    }

    public TaskPriority getPrefredPriority() {
        int size = prioritys.size();
        if (size > 0) {

            return prioritys.get(size/2);
        }
        return null;
    }

    public void setPriorities(List<TaskPriority> prioritys) {
        this.prioritys = new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriority(TaskElement element) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        return tracTask.getPriority();
    }

    public void setTaskPriority(TaskElement element, TaskPriority priority) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        tracTask.setPriority(priority);
    }
}
