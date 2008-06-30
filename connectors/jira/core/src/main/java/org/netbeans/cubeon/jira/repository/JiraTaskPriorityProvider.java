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
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskPriorityProvider implements TaskPriorityProvider {

    public static final TaskPriority BLOCKER =
            TaskPriority.createPriority(TaskPriority.PRIORITY.P1, "Blocker");
    public static final TaskPriority CRITICAL =
            TaskPriority.createPriority(TaskPriority.PRIORITY.P2, "Critical");
    public static final TaskPriority MAJOR =
            TaskPriority.createPriority(TaskPriority.PRIORITY.P3, "Major");
    public static final TaskPriority MINOR =
            TaskPriority.createPriority(TaskPriority.PRIORITY.P1, "Minor");
    public static final TaskPriority TRIVIAL =
            TaskPriority.createPriority(TaskPriority.PRIORITY.P1, "Trivial");
    private List<TaskPriority> prioritys = new ArrayList<TaskPriority>();

    public JiraTaskPriorityProvider() {
        prioritys.add(BLOCKER);
        prioritys.add(CRITICAL);
        prioritys.add(MAJOR);
        prioritys.add(MINOR);
        prioritys.add(TRIVIAL);
    }

    public List<TaskPriority> getTaskPrioritys() {
        return new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriorityById(TaskPriority.PRIORITY priority) {
        for (TaskPriority tp : getTaskPrioritys()) {
            if (priority == tp.getId()) {
                return tp;
            }
        }
        return MAJOR;
    }

    public void setPrioritys(List<TaskPriority> prioritys) {
        this.prioritys = new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriority(TaskElement element) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask!=null;
        return jiraTask.getPriority();
    }

    public void setTaskPriority(TaskElement element, TaskPriority priority) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask!=null;
        jiraTask.setPriority(priority);
    }
}
