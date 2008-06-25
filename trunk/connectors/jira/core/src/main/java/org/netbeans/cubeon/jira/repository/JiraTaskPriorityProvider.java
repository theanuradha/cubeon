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
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskPriorityProvider implements TaskPriorityProvider {

    private List<TaskPriority> prioritys = new ArrayList<TaskPriority>();

    public JiraTaskPriorityProvider() {
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
        return null;
    }

    public void setPrioritys(List<TaskPriority> prioritys) {
        this.prioritys = new ArrayList<TaskPriority>(prioritys);
    }
}
