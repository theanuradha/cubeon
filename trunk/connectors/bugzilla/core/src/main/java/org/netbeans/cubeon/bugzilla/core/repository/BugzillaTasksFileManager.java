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
package org.netbeans.cubeon.bugzilla.core.repository;

import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;

import java.util.List;

/**
 * Bugzilla configuration file manager.
 * Loads and stores configuration in configuration file.
 *
 * @author radoslaw.holewa
 */
public interface BugzillaTasksFileManager {

    /**
     * Paersists given task in file repository.
     *
     * @param bugzillaTask - task to persist
     */
    void persistTask( BugzillaTask bugzillaTask );

    /**
     * Loads task from repository.
     *
     * @param taskId - task id
     * @return - bugzilla task
     */
    BugzillaTask loadTask( String taskId );

    /**
     * Removes task with given ID.
     *
     * @param taskId - task ID
     */
    void removeTask( String taskId );

    /**
     * Loads all persisted tasks.
     *
     * @return - list of all persisted tasks.
     */
    List<BugzillaTask> loadAllTasks();

}
