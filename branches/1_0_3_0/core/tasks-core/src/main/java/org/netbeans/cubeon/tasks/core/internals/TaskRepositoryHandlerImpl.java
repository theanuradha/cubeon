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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
class TaskRepositoryHandlerImpl implements TaskRepositoryHandler {

    public List<TaskRepository> getTaskRepositorys() {
        final List<TaskRepository> taskRepositorys = new ArrayList<TaskRepository>();
        final List<TaskRepositoryType> repositoryTypes = getTaskRepositoryTypes();
        for (TaskRepositoryType taskRepositoryType : repositoryTypes) {
            taskRepositorys.addAll(taskRepositoryType.getRepositorys());
        }
     
        return taskRepositorys;
    }

    public TaskRepository getTaskRepositoryById(String id) {
        List<TaskRepository> repositorys = getTaskRepositorys();
        for (TaskRepository tr : repositorys) {
            if (tr.getId().equals(id)) {
                return tr;
            }
        }

        return null;
    }

    public List<TaskRepositoryType> getTaskRepositoryTypes() {
        return new ArrayList<TaskRepositoryType>(Lookup.getDefault().
                lookupAll(TaskRepositoryType.class));
    }
}
