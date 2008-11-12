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

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import java.awt.Image;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.openide.util.Lookup;

/**
 *This is the interface which needs to be registered when registering a new TaskRepository.
 * A task repository is an instance of a Connector type (TaskRepositoryType)
 * @author Anuradha G
 * @version 1.0
 */
public interface TaskRepository {

    /**
     *The state enum represents the posible states of a TaskRepository instance.
     * <LI>
     * <LI>Active - Task repository is ative and avaialble for use</LI>
     * <LI>Synchronizing - Currently synchronizing the task list and temporary unavailable.</LI>
     * <LI>Inactive - Task repositry is inactive and not available fo use</LI>
     * </LI>
     */
    public enum State {

        ACTIVE, SYNCHRONIZING, INACTIVE
    }

    /**
     * Returns the unique ID of the TaskRepository
     * @return TaskRepository ID
     */
    String getId();

    /**
     * Retuens the name of the tsk repository
     * @return repository name
     */
    String getName();

    /**
     * Returns the description of the taks repository
     * @return repository description
     */
    String getDescription();

    /**
     * Returns a Lookup instance which encapsulates all the supported features by the connector implementaion.
     * @return Lookup containing supported featurss
     */
    Lookup getLookup();

    /**
     *Returns an Image which will be used as the icon for the task repository instance.
     * @return
     */
    Image getImage();


    /*Task Element related */
    /**
     * Create a new TaskElement associated with the current TaskRepository.
     * @param summery A summery of the task element
     * @param description A detailed description of the task element
     * @return The newly created TaskElement
     */
    public TaskElement createTaskElement(String summery, String description);

    /**
     * Return the task element associated with the given id. Returns null if no task elements exists with the given id
     * @param id
     * @return
     */
    TaskElement getTaskElementById(String id);

    /**
     * Persist the task element into the repository
     * @param element TaskElement to be persisted
     */
    void persist(TaskElement element);

    /**
     * Sycnchronize the list of task elements with the server
     */
    void synchronize();

    /**
     *Retruns the current state of the TaskRepository
     * @return Repository state
     */
    State getState();


    Notifier<RepositoryEventAdapter> getNotifier();
}
