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
package org.netbeans.cubeon.tasks.spi.task;

import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import java.awt.Image;
import java.net.URL;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.openide.util.Lookup;

/**
 * Common interface used to represent task.
 *
 * @author Anuradha G
 */
public interface TaskElement {

    /**
     * Returns task ID.
     *
     * @return - task ID
     */
    String getId();

    /**
     * Returns task name.
     *
     * @return - task name
     */
    String getName();

    /**
     * Returns task display name, this name will be displayed on UI components.
     *
     * @return - dosplay name
     */
    String getDisplayName();

    /**
     * Returns task description.
     *
     * @return - task description
     */
    String getDescription();

    /**
     * Returns task repository in which thask was created.
     *
     * @return - task repository
     */
    TaskRepository getTaskRepository();

    /**
     * Returns task lookup.
     *
     * @return - task lookup
     */
    Lookup getLookup();

    /**
     * Retirns true if task was completed.
     *
     * @return - true if task was completed, false if it isn't
     */
    boolean isCompleted();

    /**
     * Returns task image.
     *
     * @return - task image
     */
    Image getImage();

    /**
     * Returns task URL, this URL will be used to render links.
     *
     * @return - task URL
     */
    URL getUrl();

    /**
     * Synchronizes state of task.
     */
    void synchronize();

    /**
     * Returns notifier which will is used to notify about task changes
     *
     * @return - task notifier
     */
    Notifier<TaskElementChangeAdapter> getNotifier();
}
