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
package org.netbeans.cubeon.trac.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.trac.query.ui.TracFilterQueryEditor;
import org.netbeans.cubeon.trac.repository.TracRepositoryExtension;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;

/**
 *
 * @author Anuradha
 */
public class TracQuerySupport implements TaskQuerySupportProvider {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>(0);
    private TracTaskRepository repository;
    private TracRepositoryExtension extension;
    private PersistenceHandler handler;
    private final TracOGChangesQuery outgoingQuery;

    public TracQuerySupport(TracTaskRepository repository, TracRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        outgoingQuery = new TracOGChangesQuery(repository);
        handler = new PersistenceHandler(this, repository.getBaseDir());
        handler.refresh();

    }

    public AbstractTracQuery createTaskQuery(AbstractTracQuery.Type type) {
        switch (type) {
            case FILTER:
                return new TracFilterQuery(repository, handler.nextTaskId());
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
            AbstractTracQuery abstractJiraQuery = query.getLookup().lookup(AbstractTracQuery.class);
            switch (abstractJiraQuery.getType()) {
                case FILTER:
                     {
                        TracFilterQueryEditor editor = new TracFilterQueryEditor(this);
                        editor.setQuery(query.getLookup().lookup(TracFilterQuery.class));
                        configurationHandler = editor;
                    }
                    break;
            }

        } else {
            configurationHandler = new TracFilterQueryEditor(this);
        }
        return configurationHandler;
    }

    public TracTaskRepository getTaskRepository() {
        return repository;
    }

    void setTaskQuery(List<AbstractTracQuery> localQuerys) {
        taskQuerys = new ArrayList<TaskQuery>(localQuerys);
    }

    public void addTaskQuery(TaskQuery query) {

        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        if (localQuery.getType() == AbstractTracQuery.Type.UTIL) {
            return;
        }
        handler.addTaskQuery(localQuery);
        taskQuerys.add(query);

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        if (localQuery.getType() == AbstractTracQuery.Type.UTIL) {
            return;
        }
        handler.addTaskQuery(localQuery);
        localQuery.getJiraExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        if (localQuery.getType() == AbstractTracQuery.Type.UTIL) {
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
        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public boolean canRemove(TaskQuery query) {
        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public TracOGChangesQuery getOutgoingQuery() {
        return outgoingQuery;
    }
}
