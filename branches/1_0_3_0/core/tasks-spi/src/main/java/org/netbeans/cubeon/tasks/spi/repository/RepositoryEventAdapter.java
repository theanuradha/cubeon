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

import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

/**
 *This class acts as an Adapter to the repository events. Once an event occurs in the repository
 * the appropriate method of the RepositoryEventAdapter will be invoked. Actions can be customized by
 * extending this class to add owns logic.
 * @author Anuradha G
 * @version 1.0
 */
public class RepositoryEventAdapter {

    /**
     *This method will be invoked when the name of the repository is changed.
     */
    public void nameChenged() {
    }

    /**
     * This method will be invoked when the taskElementID is changed.
     * @param repoId Repository ID - Repository to that task belongs to
     * @param oldId Previous ID of th task element
     * @param newId New ID of the task element
     */
    public void taskElementIdChenged(String repoId, String oldId, String newId) {
    }

    /**
     * This method will be invoked once a task element is removed from the repository.
     * @param element Refrence to the TaskElement instance removed.
     */
    public void taskElementRemoved(TaskElement element) {
    }

    /**
     * This method will be invoked one the description of the repository is altered.
     */
    public void descriptionChenged() {
    }

    /**
     * This method will be invoked once a new task query has been aded to the repository.
     * @param query The new TaskQuery instance added
     */
    public void queryAdded(TaskQuery query) {
    }

    /**
     * This method gets invoked once a query is removed from the repository.
     * @param query The instance of the removed query
     */
    public void queryRemoved(TaskQuery query) {
    }

    /**
     * Notifyies that the current state of the repository has been changed to another state
     * @param state New state of the repository.
     */
    public void stateChanged(TaskRepository.State state) {
    }
}
