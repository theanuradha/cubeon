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

import javax.swing.Action;

/**
 *This class allows registering custome Actions for a purticular repository type.
 * @author Anuradha
 */
public interface TaskRepositoryActionsProvider {

    /**
     *  Returns the list of custom actions that needs to be connected to the repository type.
     * 
     * @param repository TaskRepository instance to whivh these actions needs to be attached.
     * @return List of actions.
     */
    Action[] getActions(TaskRepository repository);
}
