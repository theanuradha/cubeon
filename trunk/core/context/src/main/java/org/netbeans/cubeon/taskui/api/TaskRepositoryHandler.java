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
package org.netbeans.cubeon.taskui.api;

import java.util.List;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskRepositoryType;

/**
 *
 * @author Anuradha G
 */
public interface TaskRepositoryHandler {

    /**
     * Get all Task Repositories
     * @return list of repositories
     */
    List<TaskRepository> getTaskRepositorys();

    /**
     * Find Task Repository by Id
     * @param id Repository id
     * @return matching Task Repository  may return null 
     */
    TaskRepository getTaskRepositoryById(String id);
    
    List< TaskRepositoryType> getTaskRepositoryTypes();
    
    /**
    void addTaskRepository();
    void removeTaskRepository(TaskRepository repository);
     **/
}
