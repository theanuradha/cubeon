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

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>();
    private JiraTaskRepository repository;
    private JiraRepositoryExtension extension;
    private PersistenceHandler handler;

    public JiraQuerySupport(JiraTaskRepository repository, JiraRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        handler = new PersistenceHandler(this, repository.getProvider().getBaseDir());
        handler.refresh();

    }

    public AbstractJiraQuery createTaskQuery(AbstractJiraQuery.Type type) {
        switch (type) {
            case FILTER:
                return new JiraFilterQuery(repository, handler.nextTaskId());
        }

        return null;

    }

    public List<TaskQuery> getTaskQuerys() {
        return new ArrayList<TaskQuery>(taskQuerys);
    }

    public void reset(TaskQuery query) {
        throw new UnsupportedOperationException();
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
        handler.addTaskQuery(localQuery);
        taskQuerys.add(query);

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
        handler.addTaskQuery(localQuery);
        localQuery.getJiraExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractJiraQuery localQuery = query.getLookup().lookup(AbstractJiraQuery.class);
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
}
