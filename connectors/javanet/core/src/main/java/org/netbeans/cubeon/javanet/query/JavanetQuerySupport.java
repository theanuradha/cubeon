/*
 *  Copyright 2009 Tomas Knappek.
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
package org.netbeans.cubeon.javanet.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.javanet.persistence.JavanetQueryPersistence;
import org.netbeans.cubeon.javanet.query.ui.JavanetQueryEditor;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepositoryNotifier;
import org.netbeans.cubeon.persistence.PersistenceFactory;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.ui.query.QueryField;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.netbeans.cubeon.ui.query.QuerySupport;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetQuerySupport implements TaskQuerySupportProvider, QuerySupport<JavanetRemoteQuery> {

    JavanetTaskRepository _repository = null;
    JavanetQueryPersistence _persistence = null;
    JavanetTaskRepositoryNotifier _notifier = null;

    
    public JavanetQuerySupport(JavanetTaskRepository repository, JavanetTaskRepositoryNotifier notifier) {
        this._repository = repository;
        this._persistence = PersistenceFactory.getPersistence(JavanetQueryPersistence.class);
        this._notifier = notifier;
    }



    public TaskQuery findTaskQueryById(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<JavanetRemoteQuery> getTaskQuerys() {
        List<JavanetRemoteQuery> queries = _persistence.getAll();
        for (JavanetRemoteQuery query : queries) {
            query.setTaskRepository(_repository);
        }
        return queries;
    }

    public void addTaskQuery(TaskQuery query) {
        _persistence.add((JavanetRemoteQuery)query);
        _persistence.save();

        _notifier.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTaskQuery(TaskQuery query) {
        _persistence.remove(query.getId());

        _notifier.fireQueryRemoved(query);
    }

    public boolean canModify(TaskQuery query) {
        return false;
    }

    public boolean canRemove(TaskQuery query) {
        return true;
    }

    public TaskRepository getTaskRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {
        ConfigurationHandler configurationHandler = null;
        if (query != null) {
            JavanetRemoteQuery remoteQuery = query.getLookup().lookup(JavanetRemoteQuery.class);

            JavanetQueryEditor editor = new JavanetQueryEditor(this);
            //editor.setTaskQuery(query.getLookup().lookup(JavanetRemoteQuery.class));
            configurationHandler = editor;

        } else {
            configurationHandler = new JavanetQueryEditor(this);
        }
        return configurationHandler;
    }

    public List<QueryField> getQueryFields() {
        return new ArrayList<QueryField>();
    }

    public void setQueryFromFilters(JavanetRemoteQuery query, List<QueryFilter> filters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<QueryFilter> createFiltersFromQuery(JavanetRemoteQuery query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JavanetRemoteQuery createQuery() {
        return new JavanetRemoteQuery(_repository);
    }

    public void setQueryName(JavanetRemoteQuery query, String name) {
        query.setName(name);
    }
}
