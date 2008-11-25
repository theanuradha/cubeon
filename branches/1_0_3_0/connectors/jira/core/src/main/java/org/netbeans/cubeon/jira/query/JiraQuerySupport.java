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
package org.netbeans.cubeon.jira.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.jira.query.ui.JiraFilterQueryEditor;
import org.netbeans.cubeon.jira.repository.JiraRepositoryExtension;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;

/**
 *
 * @author Anuradha
 */
public class JiraQuerySupport implements TaskQuerySupportProvider {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>(0);
    private JiraTaskRepository repository;
    private JiraRepositoryExtension extension;
    private PersistenceHandler handler;
    private final JiraOGChangesQuery outgoingQuery;

    public JiraQuerySupport(JiraTaskRepository repository, JiraRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        outgoingQuery = new JiraOGChangesQuery(repository);
        handler = new PersistenceHandler(this, repository.getBaseDir());
        handler.refresh();

    }

    public AbstractJiraQuery createTaskQuery(AbstractJiraQuery.Type type) {
        switch (type) {
            case FILTER:
                return new JiraFilterQuery(repository, handler.nextTaskId());
        }

        return null;

    }

    public void refresh() {
        handler.refresh();

    }

    public List<TaskQuery> getTaskQuerys() {
        ArrayList<TaskQuery> arrayList = new ArrayList<TaskQuery>(taskQuerys);
        arrayList.add(0, outgoingQuery);
        return arrayList;
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {
        ConfigurationHandler configurationHandler = null;
        if (query != null) {
            AbstractJiraQuery abstractJiraQuery = query.getLookup().lookup(AbstractJiraQuery.class);
            switch (abstractJiraQuery.getType()) {
                case FILTER:
                     {
                        JiraFilterQueryEditor editor = new JiraFilterQueryEditor(this);
                        editor.setQuery(query.getLookup().lookup(JiraFilterQuery.class));
                        configurationHandler = editor;
                    }
                    break;
            }

        } else {
            configurationHandler = new JiraFilterQueryEditor(this);
        }
        return configurationHandler;
    }

    public TaskRepository getTaskRepository() {
        return repository;
    }

    public JiraTaskRepository getJiraTaskRepository() {
        return repository;
    }

    void setTaskQuery(List<AbstractJiraQuery> localQuerys) {
        taskQuerys = new ArrayList<TaskQuery>(localQuerys);
    }

    public void addTaskQuery(TaskQuery query) {

        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        if (localQuery.getType() == AbstractJiraQuery.Type.UTIL) {
            return;
        }
        handler.addTaskQuery(localQuery);
        taskQuerys.add(query);

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        if (localQuery.getType() == AbstractJiraQuery.Type.UTIL) {
            return;
        }
        handler.addTaskQuery(localQuery);
        localQuery.getJiraExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        if (localQuery.getType() == AbstractJiraQuery.Type.UTIL) {
            return;
        }
        handler.removeTaskQuery(localQuery);
        taskQuerys.remove(localQuery);
        extension.fireQueryRemoved(localQuery);
        localQuery.getJiraExtension().fireRemoved();
    }

    public TaskQuery findTaskQueryById(String id) {
        for (TaskQuery query : getTaskQuerys()) {
            if (id.equals(query.getId())) {
                return query;
            }
        }
        return null;
    }

    public boolean canModify(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public boolean canRemove(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public JiraOGChangesQuery getOutgoingQuery() {
        return outgoingQuery;
    }
}
