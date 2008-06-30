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

    public  static final TaskType BUG = new TaskType("bug", "Bug");
    public static final TaskType IMPROVEMENT = new TaskType("improvement", "Improvement");
    public static final TaskType FEATURE = new TaskType("new_feature", "New Feature");
    public static final TaskType TASK = new TaskType("task", "Task");
    public List<TaskType> taskTypes = new ArrayList<TaskType>();

    public JiraTaskTypeProvider() {
        taskTypes.add(BUG);
        taskTypes.add(IMPROVEMENT);
        taskTypes.add(FEATURE);
        taskTypes.add(TASK);
    }

    public List<TaskType> getTaskTypes() {

        return new ArrayList<TaskType>(taskTypes);
    }

    public TaskType getTaskTypeById(String id) {
        for (TaskType type : getTaskTypes()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return BUG;
    }

    public void setTaskTypes(List<TaskType> taskTypes) {
        this.taskTypes = new ArrayList<TaskType>(taskTypes);
    }

    public TaskType getTaskType(TaskElement element) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask!=null;
        return jiraTask.getType();
    }

    public void setTaskType(TaskElement element, TaskType taskType) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask!=null;
         jiraTask.setType(taskType);
    }
}
