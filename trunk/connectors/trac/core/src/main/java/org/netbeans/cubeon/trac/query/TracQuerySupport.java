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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.ui.query.QueryEditor;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.netbeans.cubeon.ui.query.QueryField;
import org.netbeans.cubeon.ui.query.QuerySupport;
import org.netbeans.cubeon.trac.repository.TracRepositoryExtension;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;

/**
 *
 * @author Anuradha
 */
public class TracQuerySupport implements TaskQuerySupportProvider, QuerySupport<TracFilterQuery> {

    private List<TaskQuery> taskQuerys = new ArrayList<TaskQuery>(0);
    private TracTaskRepository repository;
    private TracRepositoryExtension extension;
    private PersistenceHandler handler;
    private final TracOGChangesQuery outgoingQuery;
    List<QueryField> queryFields = new LinkedList<QueryField>();
    private static Logger LOG = Logger.getLogger(TracQuerySupport.class.getName());

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
            AbstractTracQuery abstractJiraQuery = query.getLookup().lookup(AbstractTracQuery.class);
            switch (abstractJiraQuery.getType()) {
                case FILTER:
                     {
                        QueryEditor editor = new QueryEditor(this);
                        editor.setTaskQuery(query.getLookup().lookup(TracFilterQuery.class));
                        configurationHandler = editor;
                    }
                    break;
            }

        } else {
            configurationHandler = new QueryEditor(this);
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
        localQuery.getTracExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractTracQuery localQuery = query.getLookup().lookup(AbstractTracQuery.class);
        if (localQuery.getType() == AbstractTracQuery.Type.UTIL) {
            return;
        }
        handler.removeTaskQuery(localQuery);
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

    @Override
    public List<QueryField> getQueryFields() {
        return queryFields;
    }

    @Override
    public void setQueryFromFilters(TracFilterQuery query, List<QueryFilter> filters) {
        StringBuilder queryString = new StringBuilder();

        boolean firstField = true;
        for (QueryFilter filter : filters) {
            // append field seperator
            if (firstField)
                firstField = false;
            else
                queryString.append("&"); // NOI18N

            // append field name
            queryString.append(filter.getField().getName());

            // append match
            queryString.append(getMatchString(filter.getMatch()));

            // append values
            boolean firstValue = true;
            for (Object value : filter.getValues()) {
                // append value sperator
                if (firstValue)
                    firstValue = false;
                else
                    queryString.append("|"); // NOI18N
                // append value
                //TODO: mask value (=,&,|); by now these characters are replaced by space
                queryString.append(value.toString().replaceAll("[=&|]", " ")); // NOI18N
            }
        }

        // set native query string
        query.setQuery(queryString.toString());

        LOG.info("setQueryFromFilters: query=["+queryString+"] "+filters); // NOI18N
    }

    @Override
    public List<QueryFilter> createFiltersFromQuery(TracFilterQuery query) {
        List<QueryFilter> filters = new LinkedList<QueryFilter>();

        // split native query string to fields
        String queryString = query.getQuery();
        String[] fields = queryString.split("&"); // NOI18N

        // pattern to split field name, match and values
        Pattern fieldPattern = Pattern.compile("(\\w*)(=|~=|\\^=|\\$=|!=|!~=|!\\^=|!\\$=)(.*)"); // NOI18N

        for (String field : fields) {
            Matcher fieldSplit = fieldPattern.matcher(field);
            if (fieldSplit.matches()) {
                // get query field
                QueryField queryField = getQueryField(fieldSplit.group(1));
                if (queryField != null) {
                    // get match
                    QueryFilter.Match match = getMatch(fieldSplit.group(2));
                    // get values
                    List<String> values = Arrays.asList(fieldSplit.group(3).split("\\|")); // NOI18N
                    //TODO: unmask values (=,&,|)
                    // workaround for old queries like "status!=closed"
                    if ((queryField.getType() == QueryField.Type.CHECKBOX ||
                            queryField.getType() == QueryField.Type.RADIO) &&
                            match != QueryFilter.Match.IS) {
                        List<String> allValues = 
                                queryField.getType() == QueryField.Type.RADIO ?
                                new ArrayList(queryField.getOptions()) :
                                new ArrayList(Arrays.asList("0", "1")); // NOI18N
                        allValues.removeAll(values);
                        values = allValues;
                        match = QueryFilter.Match.IS;
                    }
                    if (!values.isEmpty())
                        // create filter and add to list
                        filters.add(new QueryFilter(queryField,
                                match,
                                new LinkedHashSet(values))); // NOI18N
                } else
                    LOG.info("unknown field name '"+fieldSplit.group(1)+ // NOI18N
                            "' for query string: "+queryString); // NOI18N
            } else
                LOG.info("malformed field '"+field+ // NOI18N
                        "' for query string: "+queryString); // NOI18N
        }

        LOG.info("createFiltersFromQuery: query=["+queryString+"] "+filters); // NOI18N
        return filters;
    }

    @Override
    public TracFilterQuery createQuery() {
        return new TracFilterQuery(repository, handler.nextTaskId());
    }

    @Override
    public void setQueryName(TracFilterQuery query, String name) {
        query.setName(name);
    }

    private void refreshQueryFields() {
        queryFields.clear();
        int order = 0; // TicketField doesn't supply the order attribute yet
        for (TicketField field : repository.getRepositoryAttributes().getTicketFields()) {
            QueryField.Type type = QueryField.Type.TEXT;
            if ("textarea".equals(field.getType())) { // NOI18N
                type = QueryField.Type.TEXTAREA;
            } else if ("radio".equals(field.getType())) { // NOI18N
                type = QueryField.Type.RADIO;
            } else if ("checkbox".equals(field.getType())) { // NOI18N
                type = QueryField.Type.CHECKBOX;
            } else if ("select".equals(field.getType())) { // NOI18N
                type = QueryField.Type.SELECT;
            }
            queryFields.add(new QueryField(field.getName(), field.getLabel(),
                    type, order++, new LinkedHashSet(field.getOptions())));
        }
    }

    private QueryFilter.Match getMatch(String match) {
        if ("~=".equals(match)) // NOI18N
            return QueryFilter.Match.CONTAINS;
        else if ("^=".equals(match)) // NOI18N
            return QueryFilter.Match.STARTS_WITH;
        else if ("$=".equals(match)) // NOI18N
            return QueryFilter.Match.ENDS_WITH;
        else if ("!=".equals(match)) // NOI18N
            return QueryFilter.Match.IS_NOT;
        else if ("!~=".equals(match)) // NOI18N
            return QueryFilter.Match.CONTAINS_NOT;
        return QueryFilter.Match.IS;
    }

    private QueryField getQueryField(String name) {
        for (QueryField queryField : queryFields)
            if (queryField.getName().equals(name))
                return queryField;
        return null;
    }

    private String getMatchString(QueryFilter.Match match) {
        switch (match) {
            case CONTAINS:
                return "~="; // NOI18N
            case STARTS_WITH:
                return "^="; // NOI18N
            case ENDS_WITH:
                return "$="; // NOI18N
            case IS_NOT:
                return "!="; // NOI18N
            case CONTAINS_NOT:
                return "!~="; // NOI18N
            default:
                return "="; // NOI18N
        }
    }

}
