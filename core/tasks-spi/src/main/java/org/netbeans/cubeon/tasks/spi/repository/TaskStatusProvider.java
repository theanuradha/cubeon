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
 * Allows a Repository Type implementation to communicate the Task Status information with the system
 * @author Anuradha G
 */
public interface TaskStatusProvider {

    /**
     * Returns a list of all the status types which are supported by the
     * implementing task repository.
     * @return List of TaskStatus's supported.
     */
    List<TaskStatus> getStatusList();

    /**
     * Returns the TaskStatus of a purticular TaskElement
     * @param element the element whcih the status needs to be returned.
     * @return Current status of the task
     */
    TaskStatus getTaskStatus(TaskElement element);
}
