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
package org.netbeans.cubeon.jira.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;

/**
 *
 * @author Anuradha
 */
public class JiraTaskTypeProvider implements TaskTypeProvider {

    List<TaskType> taskTypes = new ArrayList<TaskType>();

    public List<TaskType> getTaskTypes() {

        return new ArrayList<TaskType>(taskTypes);
    }

    public TaskType getTaskTypeById(String id) {
        for (TaskType type : getTaskTypes()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return null;
    }

    public void setTaskTypes(List<TaskType> taskTypes) {
        this.taskTypes = new ArrayList<TaskType>(taskTypes);
    }
}
