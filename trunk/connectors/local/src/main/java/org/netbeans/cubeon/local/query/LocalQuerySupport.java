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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import org.netbeans.cubeon.local.repository.LocalRepositoryExtension;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.ui.query.QueryEditor;
import org.netbeans.cubeon.ui.query.QueryField;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.netbeans.cubeon.ui.query.QuerySupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class LocalQuerySupport implements TaskQuerySupportProvider, QuerySupport<LocalQuery> {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>();
    private LocalTaskRepository repository;
    private LocalRepositoryExtension extension;
    private PersistenceHandler handler;
    List<QueryField> queryFields = new LinkedList<QueryField>();
    private static Logger LOG = Logger.getLogger(LocalQuerySupport.class.getName());

    private enum Field { SUMMARY, DESCRIPTION, TEXT, PRIORITY, TYPE, STATUS };

    public LocalQuerySupport(LocalTaskRepository repository, LocalRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        handler = new PersistenceHandler(this, repository.getProvider().getBaseDir());
        handler.refresh();
        refreshQueryFields();
    }

    public LocalQuery createTaskQuery(String name, String description) {
        return new LocalQuery(handler.nextTaskId(), name, repository);
    }

    public List<TaskQuery> getTaskQuerys() {
        return new ArrayList<TaskQuery>(taskQuerys);
    }

    public void reset(TaskQuery query) {
        throw new UnsupportedOperationException();
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {
        QueryEditor editor = new QueryEditor(this);
        if (query != null) {
            editor.setTaskQuery(query.getLookup().lookup(LocalQuery.class));
        }
        return editor;
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

    public void addTaskQuery(TaskQuery query) {
        LocalQuery localQuery = query.getLookup().lookup(LocalQuery.class);
        handler.addTaskQuery(localQuery);
        taskQuerys.add(query);

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        LocalQuery localQuery = query.getLookup().lookup(LocalQuery.class);
        handler.addTaskQuery(localQuery);
        localQuery.getLocalExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        LocalQuery localQuery = query.getLookup().lookup(LocalQuery.class);
        handler.removeTaskQuery(localQuery);
        taskQuerys.remove(localQuery);
        extension.fireQueryRemoved(localQuery);
        localQuery.getLocalExtension().fireRemoved();
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
        return true;
    }

    public boolean canRemove(TaskQuery query) {
        return true;
    }

    @Override
    public List getQueryFields() {
        return queryFields;
    }

    @Override
    public void setQueryFromFilters(LocalQuery query, List<QueryFilter> filters) {
        // clear query
        query.setSummarySearch(new LinkedHashSet<String>());
        query.setDescriptionSearch(new LinkedHashSet<String>());
        query.setTextSearch(new LinkedHashSet<String>());
        query.setStates(new ArrayList<TaskStatus>());
        query.setPriorities(new ArrayList<TaskPriority>());
        query.setTypes(new ArrayList<TaskType>());

        for (QueryFilter filter : filters) {
            // summary
            if (filter.getField().getName().equals(Field.SUMMARY.toString())) {
                query.setSummaryMatch(filter.getMatch());
                query.setSummarySearch((Set<String>)filter.getValues());
            }
            // description
            if (filter.getField().getName().equals(Field.DESCRIPTION.toString())) {
                query.setDescriptionMatch(filter.getMatch());
                query.setDescriptionSearch((Set<String>)filter.getValues());
            }
            // text
            if (filter.getField().getName().equals(Field.TEXT.toString())) {
                query.setTextMatch(filter.getMatch());
                query.setTextSearch((Set<String>)filter.getValues());
            }
            // status
            if (filter.getField().getName().equals(Field.STATUS.toString())) {
                query.setStatesMatch(filter.getMatch());
                query.setStates(new ArrayList(filter.getValues()));
            }
            // priority
            if (filter.getField().getName().equals(Field.PRIORITY.toString())) {
                query.setPrioritiesMatch(filter.getMatch());
                query.setPriorities(new ArrayList(filter.getValues()));
            }
            // type
            if (filter.getField().getName().equals(Field.TYPE.toString())) {
                query.setTypesMatch(filter.getMatch());
                query.setTypes(new ArrayList(filter.getValues()));
            }
        }
        LOG.info("setQueryFromFilters: query=["+query+"] "+filters); // NOI18N
    }

    @Override
    public List<QueryFilter> createFiltersFromQuery(LocalQuery query) {
        List<QueryFilter> filters = new LinkedList<QueryFilter>();

        // summary
        if (!query.getSummarySearch().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.SUMMARY),
                    query.getSummaryMatch(), query.getSummarySearch()));
        }
        // description
        if (!query.getDescriptionSearch().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.DESCRIPTION),
                    query.getDescriptionMatch(), query.getDescriptionSearch()));
        }
        // text
        if (!query.getTextSearch().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.TEXT),
                    query.getTextMatch(), query.getTextSearch()));
        }
        // status
        if (!query.getStates().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.STATUS),
                    query.getStatesMatch(), new LinkedHashSet(query.getStates())));
        }
        // priority
        if (!query.getPriorities().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.PRIORITY),
                    query.getPrioritiesMatch(), new LinkedHashSet(query.getPriorities())));
        }
        // type
        if (!query.getTypes().isEmpty()) {
            filters.add(new QueryFilter(getQueryField(Field.TYPE),
                    query.getTypesMatch(), new LinkedHashSet(query.getTypes())));
        }

        LOG.info("createFiltersFromQuery: query=["+query+"] "+filters); // NOI18N
        return filters;
    }

    @Override
    public LocalQuery createQuery() {
        // create query with empty name
        return createTaskQuery("", ""); // NOI18N
    }

    @Override
    public void setQueryName(LocalQuery query, String name) {
        query.setName(name);
    }

    private void refreshQueryFields() {
        queryFields.clear();
        int order = 0;
        ResourceBundle bundle = NbBundle.getBundle(LocalQuerySupport.class);
        // summary
        queryFields.add(new QueryField(Field.SUMMARY.toString(),
                bundle.getString("LBL_Field_Summary"),
                QueryField.Type.TEXT, order++, new LinkedHashSet()));
        // description
        queryFields.add(new QueryField(Field.DESCRIPTION.toString(),
                bundle.getString("LBL_Field_Description"),
                QueryField.Type.TEXTAREA, order++, new LinkedHashSet()));
        // text
        queryFields.add(new QueryField(Field.TEXT.toString(),
                bundle.getString("LBL_Field_Text"),
                QueryField.Type.TEXTAREA, order++, new LinkedHashSet()));
        // status
        Set<TaskStatus> states = new LinkedHashSet<TaskStatus>();
        for (TaskStatus status : repository.getLocalTaskStatusProvider().getStatusList())
            states.add(status);
        queryFields.add(new QueryField(Field.STATUS.toString(),
                bundle.getString("LBL_Field_Status"),
                QueryField.Type.RADIO, order++, states));
        // priority
        Set<TaskPriority> priorities = new LinkedHashSet<TaskPriority>();
        for (TaskPriority priority : repository.getLocalTaskPriorityProvider().getTaskPriorities())
            priorities.add(priority);
        queryFields.add(new QueryField(Field.PRIORITY.toString(),
                bundle.getString("LBL_Field_Priority"),
                QueryField.Type.SELECT, order++, priorities));
        // type
        Set<TaskType> types = new LinkedHashSet<TaskType>();
        for (TaskType type : repository.getLocalTaskTypeProvider().getTaskTypes())
            types.add(type);
        queryFields.add(new QueryField(Field.TYPE.toString(),
                bundle.getString("LBL_Field_Type"),
                QueryField.Type.SELECT, order++, types));
    }

    private QueryField getQueryField(Field field) {
        for (QueryField queryField : queryFields)
            if (queryField.getName().equals(field.toString()))
                return queryField;
        return null;
    }

}
