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
import org.netbeans.cubeon.tasks.spi.TaskType;
import org.netbeans.cubeon.tasks.spi.TaskTypeProvider;

/**
 *
 * @author Anuradha
 */
public class LocalTaskTypeProvider implements TaskTypeProvider {

    public static TaskType BUG = new TaskType("defect", "Defect");
    public static TaskType ENHANCEMENT = new TaskType("enhancement", "Enhancement");
    public static TaskType FEATURE = new TaskType("feature ", "Feature");
    public static TaskType TASK = new TaskType("task", "Task");

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
}
