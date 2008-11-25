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
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;

/**
 *
 * @author Anuradha
 */
public class JiraTaskTypeProvider implements TaskTypeProvider {

    public List<JiraTaskType> taskTypes = new ArrayList<JiraTaskType>(0);

    public JiraTaskTypeProvider() {
    }

    public List<TaskType> getTaskTypes() {

        return new ArrayList<TaskType>(taskTypes);
    }

    public List<JiraTaskType> getJiraTaskTypes() {

        return new ArrayList<JiraTaskType>(taskTypes);
    }

    public JiraTaskType getTaskTypeById(String id) {
        for (JiraTaskType type : taskTypes) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return null;
    }

    public JiraTaskType getPrefedTaskType() {
        if (!taskTypes.isEmpty()) {
            return taskTypes.get(0);
        }
        return null;
    }

    public void setTaskTypes(List<JiraTaskType> taskTypes) {
        this.taskTypes = new ArrayList<JiraTaskType>(taskTypes);
    }

    public TaskType getTaskType(TaskElement element) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask != null;
        return jiraTask.getType();
    }

    public void setTaskType(TaskElement element, TaskType taskType) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask != null;
        jiraTask.setType(taskType);
    }
}
