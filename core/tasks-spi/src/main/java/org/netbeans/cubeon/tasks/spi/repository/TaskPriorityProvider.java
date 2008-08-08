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
package org.netbeans.cubeon.tasks.spi.repository;

import org.netbeans.cubeon.tasks.spi.task.*;
import java.util.List;

/**
 *Implementaion of this interface will let the repository to provide proiorties
 * E.g. X repository has the following priority types.
 * And register it via Repository Lookup by using interface
 * 1. P1
 * 2. P2
 * 3. P3
 * 4. P4
 * 5. P5
 * @author Anuradha G
 */
public interface TaskPriorityProvider {

    /**
     * Returns a list of task priorities for the current repository
     * @return list of task priorities
     */
    List<TaskPriority> getTaskPriorities();

    /**
     * Retruns the TaskPriority associated wit the given priority ID.
     * @param Priority ID
     * @return TaskPriority instance associated wit the id.
     */
    TaskPriority getTaskPriorityById(String priority);

    /**
     * Returns the current priority of a TaskElement
     * @param element TaskElement instance
     * @return TaksPriority of the TaskElement
     */
    TaskPriority getTaskPriority(TaskElement element);

    /**
     * Modifies the TaskPriority fr a given TaskElement
     * @param element TaskElement which needs to be set the priority
     * @param priority The new TaskPriority for the task.
     */
    void setTaskPriority(TaskElement element, TaskPriority priority);
}
