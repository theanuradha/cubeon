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
package org.netbeans.cubeon.gcode.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.cubeon.gcode.persistence.AttributesHandler;
import org.netbeans.cubeon.gcode.persistence.QueryPersistence;
import org.netbeans.cubeon.gcode.repository.GCodeRepositoryExtension;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.ui.query.QueryEditor;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.netbeans.cubeon.ui.query.QueryField;
import org.netbeans.cubeon.ui.query.QuerySupport;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Anuradha
 */
public class GCodeQuerySupport implements TaskQuerySupportProvider, QuerySupport<GCodeFilterQuery> {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>(0);
    private GCodeTaskRepository repository;
    private GCodeRepositoryExtension extension;
    private final GCodeOGChangesQuery outgoingQuery;
    List<QueryField> queryFields = new LinkedList<QueryField>();
    private static Logger LOG = Logger.getLogger(GCodeQuerySupport.class.getName());
    private QueryPersistence handler;

    public GCodeQuerySupport(GCodeTaskRepository repository, GCodeRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        outgoingQuery = new GCodeOGChangesQuery(repository);
        handler = new QueryPersistence(repository, FileUtil.toFile(repository.getBaseDir()));
    }

    public AbstractGCodeQuery createTaskQuery(AbstractGCodeQuery.Type type) {
        switch (type) {
            case FILTER:
                return new GCodeFilterQuery(repository, handler.nextId());
        }

        return null;

    }

    public void refresh() {
        handler.refresh();
        refreshQueryFields();
    }

    public List<TaskQuery> getTaskQuerys() {
        ArrayList<TaskQuery> arrayList = new ArrayList<TaskQuery>(taskQuerys);
        arrayList.add(0, outgoingQuery);
        return arrayList;
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {
        ConfigurationHandler configurationHandler = null;
        if (query != null) {
            AbstractGCodeQuery gCodeQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
            switch (gCodeQuery.getType()) {
                case FILTER: {
                    QueryEditor editor = new QueryEditor(this);
                    editor.setTaskQuery(query.getLookup().lookup(GCodeFilterQuery.class));
                    configurationHandler = editor;
                }
                break;
            }

        } else {
            configurationHandler = new QueryEditor(this);
        }
        return configurationHandler;
    }

    public GCodeTaskRepository getTaskRepository() {
        return repository;
    }

    void setTaskQuery(List<AbstractGCodeQuery> localQuerys) {
        taskQuerys = new ArrayList<TaskQuery>(localQuerys);
    }

    public void addTaskQuery(TaskQuery query) {

        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.persist(((GCodeFilterQuery) localQuery));
        taskQuerys.add(query);

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.persist((GCodeFilterQuery) localQuery);
        localQuery.getTracExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(GCodeFilterQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.remove((GCodeFilterQuery) localQuery);
        taskQuerys.remove(localQuery);
        extension.fireQueryRemoved(localQuery);
        localQuery.getTracExtension().fireRemoved();
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
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public boolean canRemove(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public GCodeOGChangesQuery getOutgoingQuery() {
        return outgoingQuery;
    }

    @Override
    public List<QueryField> getQueryFields() {
        return queryFields;
    }

    @Override
    public void setQueryFromFilters(GCodeFilterQuery query, List<QueryFilter> filters) {
        StringBuilder queryString = new StringBuilder();

        // set native query string
        query.setQuery(queryString.toString());

        LOG.info("setQueryFromFilters: query=[" + queryString + "] " + filters); // NOI18N
    }

    @Override
    public List<QueryFilter> createFiltersFromQuery(GCodeFilterQuery query) {
        List<QueryFilter> filters = new LinkedList<QueryFilter>();

        
        return filters;
    }

    @Override
    public GCodeFilterQuery createQuery() {
        return new GCodeFilterQuery(repository, handler.nextId());
    }

    @Override
    public void setQueryName(GCodeFilterQuery query, String name) {
        query.setName(name);
    }

    private void refreshQueryFields() {
        queryFields.clear();
        int order = 0; // TicketField doesn't supply the order attribute yet
        // TicketField doesn't supply the order attribute yet
        AttributesHandler repositoryAttributes = repository.getRepositoryAttributes();

        queryFields.add(new QueryField("status", "Status",
                QueryField.Type.SELECT, order++, new LinkedHashSet(repositoryAttributes.getStatuses())));
    }


}
