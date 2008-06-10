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
import org.netbeans.cubeon.local.LocalTaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskStatusProvider;

/**
 *
 * @author Anuradha
 */
public class LocalTaskStatusProvider implements TaskStatusProvider {

    public static final LocalTaskStatus COMPLETED = new LocalTaskStatus("completed", "Completed");
    public static final LocalTaskStatus NEW = new LocalTaskStatus("new", "New");
    public static final LocalTaskStatus STARTED = new LocalTaskStatus("started", "Started");

    public List<TaskStatus> getStatusList() {
        List<TaskStatus> taskStatuses = new ArrayList<TaskStatus>();
        taskStatuses.add(COMPLETED);
        taskStatuses.add(STARTED);
        taskStatuses.add(NEW);
        return taskStatuses;
    }

    public TaskStatus getTaskStatusById(String id) {
        for (TaskStatus taskStatus : getStatusList()) {
            if (taskStatus.getId().equals(id)) {
                return taskStatus;
            }
        }

        return NEW;
    }
}
