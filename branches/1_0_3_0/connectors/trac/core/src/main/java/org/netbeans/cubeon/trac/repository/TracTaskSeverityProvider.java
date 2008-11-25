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
import org.netbeans.cubeon.tasks.spi.repository.TaskSeverityProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.repository.TaskStatusProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskSeverity;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha G
 */
public class TracTaskSeverityProvider implements TaskSeverityProvider {

    private List<TaskSeverity> statuses = new ArrayList<TaskSeverity>();

    public TracTaskSeverityProvider() {
    }

    public List<TaskSeverity> getTaskSeverities() {

        return new ArrayList<TaskSeverity>(statuses);
    }


    public void setTaskSeverities(List<TaskSeverity> severities) {
        this.statuses = new ArrayList<TaskSeverity>(severities);
    }

    public TaskSeverity getTaskSeverityById(String id) {
        List<TaskSeverity> severities = getTaskSeverities();
        for (TaskSeverity taskSeverity : severities) {
            if (taskSeverity.getId().equals(id)) {
                return taskSeverity;
            }
        }

        return null;
    }

    public TaskSeverity getTaskSeverity(TaskElement element) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        return tracTask.getSeverity();
    }

}
