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
package org.netbeans.cubeon.local.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.local.query.ui.QueryEditor;
import org.netbeans.cubeon.local.repository.LocalRepositoryExtension;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;

/**
 *
 * @author Anuradha
 */
public class LocalQuerySupport implements TaskQuerySupportProvider {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>();
    private LocalTaskRepository repository;
    private LocalRepositoryExtension extension;
    private PersistenceHandler handler;

    public LocalQuerySupport(LocalTaskRepository repository,LocalRepositoryExtension extension) {
        this.repository = repository;
        handler = new PersistenceHandler(this, repository.getProvider().getBaseDir());
        handler.refresh();
        
    }

    public TaskQuery createTaskQuery() {
        return new LocalQuery("New Query", repository);
    }

    public List<TaskQuery> getTaskQuerys() {
        return new ArrayList<TaskQuery>(taskQuerys);
    }

    public void persist(TaskQuery query) {
        handler.addTaskQuery(query);
        taskQuerys.add(query);
        extension.fireQueryAdded();
    }

    public void reset(TaskQuery query) {
        throw new UnsupportedOperationException();
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {

        return new QueryEditor(query, repository);
    }

    public TaskRepository getTaskRepository() {
        return repository;
    }

    public LocalTaskRepository getLocalTaskRepository() {
        return repository;
    }

    void setTaskQuery(List<LocalQuery> localQuerys) {
        taskQuerys = new ArrayList<TaskQuery>(localQuerys);
    }
}
