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
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha
 */
public class TracTaskTypeProvider implements TaskTypeProvider {

    public List<TaskType> taskTypes = new ArrayList<TaskType>(0);

    public TracTaskTypeProvider() {
    }

    public List<TaskType> getTaskTypes() {

        return new ArrayList<TaskType>(taskTypes);
    }


    public TaskType getTaskTypeById(String id) {
        for (TaskType type : taskTypes) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return null;
    }

    public TaskType getPrefedTaskType() {
        if (!taskTypes.isEmpty()) {
            return taskTypes.get(0);
        }
        return null;
    }

    public void setTaskTypes(List<TaskType> taskTypes) {
        this.taskTypes = new ArrayList<TaskType>(taskTypes);
    }

    public TaskType getTaskType(TaskElement element) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        return tracTask.getType();
    }

    public void setTaskType(TaskElement element, TaskType taskType) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        tracTask.setType(taskType);
    }
}
