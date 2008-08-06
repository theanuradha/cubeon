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
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class LocalTaskTypeProvider implements TaskTypeProvider {

    public final TaskType BUG;
    public final TaskType ENHANCEMENT;
    public final TaskType FEATURE;
    public final TaskType TASK;

    public LocalTaskTypeProvider(LocalTaskRepository repository) {
        BUG = new TaskType(repository, "defect",//NOI18N
                NbBundle.getMessage(LocalTaskTypeProvider.class, "LBL_Defect"));
        ENHANCEMENT = new TaskType(repository, "enhancement",//NOI18N
                NbBundle.getMessage(LocalTaskTypeProvider.class, "LBL_Enhancement"));
        FEATURE = new TaskType(repository, "feature ",//NOI18N
                NbBundle.getMessage(LocalTaskTypeProvider.class, "LBL_Feature"));
        TASK = new TaskType(repository, "task",//NOI18N
                NbBundle.getMessage(LocalTaskTypeProvider.class, "LBL_Feature"));
    }

    public List<TaskType> getTaskTypes() {
        List<TaskType> taskTypes = new ArrayList<TaskType>(4);
        taskTypes.add(BUG);
        taskTypes.add(ENHANCEMENT);
        taskTypes.add(FEATURE);
        taskTypes.add(TASK);
        return taskTypes;
    }

    public TaskType getTaskTypeById(String id) {
        for (TaskType type : getTaskTypes()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return TASK;
    }

    public TaskType getTaskType(TaskElement element) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        return localTask.getType();
    }

    public void setTaskType(TaskElement element, TaskType taskType) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        localTask.setType(taskType);
    }
}
