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
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.repository.TaskStatusProvider;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskStatusProvider implements TaskStatusProvider {

    private List<TaskStatus> statuses = new ArrayList<TaskStatus>();

    public JiraTaskStatusProvider() {
    }

    public List<TaskStatus> getStatusList() {

        return new ArrayList<TaskStatus>(statuses);
    }


    public void setStatuses(List<TaskStatus> statuses) {
        this.statuses = new ArrayList<TaskStatus>(statuses);
    }

    public TaskStatus getTaskStatusById(String id) {
        List<TaskStatus> statusList = getStatusList();
        for (TaskStatus taskStatus : statusList) {
            if (taskStatus.getId().equals(id)) {
                return taskStatus;
            }
        }

        return null;
    }

    public TaskStatus getTaskStatus(TaskElement element) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask != null;
        return jiraTask.getStatus();
    }

}
