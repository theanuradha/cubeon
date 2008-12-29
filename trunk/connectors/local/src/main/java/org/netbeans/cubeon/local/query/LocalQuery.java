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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.netbeans.cubeon.ui.query.QueryFilter.Match;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalQuery implements TaskQuery {

    private final String id;
    private String name;
    private LocalTaskRepository repository;
    private QueryFilter.Match prioritiesMatch = QueryFilter.Match.IS;
    private List<TaskPriority> priorities = new ArrayList<TaskPriority>();
    private QueryFilter.Match typesMatch = QueryFilter.Match.IS;;
    private List<TaskType> types = new ArrayList<TaskType>();
    private QueryFilter.Match statesMatch = QueryFilter.Match.IS;;
    private List<TaskStatus> states = new ArrayList<TaskStatus>();
    private QueryFilter.Match summaryMatch = QueryFilter.Match.IS;;
    private Set<String> summarySearch = new LinkedHashSet<String>();
    private QueryFilter.Match descriptionMatch = QueryFilter.Match.IS;;
    private Set<String> descriptionSearch = new LinkedHashSet<String>();
    private QueryFilter.Match textMatch = QueryFilter.Match.IS;;
    private Set<String> textSearch = new LinkedHashSet<String>();
    private final QueryExtension extension;

    public LocalQuery(String id, String name, LocalTaskRepository repository) {
        this.id = id;
        this.name = name;
        this.repository = repository;
        extension = new QueryExtension(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return NbBundle.getMessage(LocalQuery.class, "LBL_Local_Query",getName()) ;
    }

    public void setName(String name) {
        this.name = name;

    }

    public TaskRepository getTaskRepository() {
        return repository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {
        extension.fireSynchronized();
    }

    public Match getPrioritiesMatch() {
        return prioritiesMatch;
    }

    public void setPrioritiesMatch(Match prioritiesMatch) {
        this.prioritiesMatch = prioritiesMatch;
    }

    public List<TaskPriority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<TaskPriority> priorities) {
        this.priorities = new ArrayList<TaskPriority>(priorities);
    }

    public LocalTaskRepository getRepository() {
        return repository;
    }

    public void setRepository(LocalTaskRepository repository) {
        this.repository = repository;
    }

    public Match getStatesMatch() {
        return statesMatch;
    }

    public void setStatesMatch(Match statesMatch) {
        this.statesMatch = statesMatch;
    }

    public List<TaskStatus> getStates() {
        return states;
    }

    public void setStates(List<TaskStatus> states) {
        this.states = new ArrayList<TaskStatus>(states);
    }

    public Match getTypesMatch() {
        return typesMatch;
    }

    public void setTypesMatch(Match typesMatch) {
        this.typesMatch = typesMatch;
    }

    public List<TaskType> getTypes() {
        return types;
    }

    public void setTypes(List<TaskType> types) {
        this.types = new ArrayList<TaskType>(types);
    }

    @Override
    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = new ArrayList<TaskElement>();
        for (LocalTask element : repository.getLocalTasks()) {
            if ((checkText(element.getName(), textMatch, textSearch) ||
                    checkText(element.getDescription(), textMatch, textSearch)) &&
                    checkText(element.getName(), summaryMatch, summarySearch) &&
                    checkText(element.getDescription(), descriptionMatch, descriptionSearch) &&
                    checkList(element.getStatus(), statesMatch, states) &&
                    checkList(element.getPriority(), prioritiesMatch, priorities) &&
                    checkList(element.getType(), typesMatch, types))
                elements.add(element);
        }
        return elements;
    }

    public Notifier<TaskQueryEventAdapter> getNotifier() {
        return extension;
    }

    public QueryExtension getLocalExtension() {
        return extension;
    }

    public Match getSummaryMatch() {
        return summaryMatch;
    }

    public void setSummaryMatch(Match summaryMatch) {
        this.summaryMatch = summaryMatch;
    }

    public Set<String> getSummarySearch() {
        return summarySearch;
    }

    public void setSummarySearch(Set<String> summarySearch) {
        this.summarySearch = summarySearch;
    }

    public Match getDescriptionMatch() {
        return descriptionMatch;
    }

    public void setDescriptionMatch(Match descriptionMatch) {
        this.descriptionMatch = descriptionMatch;
    }

    public Set<String> getDescriptionSearch() {
        return descriptionSearch;
    }

    public void setDescriptionSearch(Set<String> descriptionSearch) {
        this.descriptionSearch = descriptionSearch;
    }

    public Match getTextMatch() {
        return textMatch;
    }

    public void setTextMatch(Match textMatch) {
        this.textMatch = textMatch;
    }

    public Set<String> getTextSearch() {
        return textSearch;
    }

    public void setTextSearch(Set<String> textSearch) {
        this.textSearch = textSearch;
    }

    private boolean checkText(String text, QueryFilter.Match match, Set<String> values) {
        if (values.isEmpty())
            return true;
        if (text == null || text.trim().length() == 0)
            return false;

        boolean found = false;
        Iterator<String> iterator = values.iterator();
        while (!found && iterator.hasNext()) {
            String value = iterator.next();
            switch (match) {
                case IS:
                case IS_NOT:
                    found = text.equalsIgnoreCase(value);
                    break;
                case CONTAINS:
                case CONTAINS_NOT:
                    found =  text.toLowerCase().contains(value.toLowerCase());
                    break;
                case STARTS_WITH:
                    found =  text.toLowerCase().startsWith(value.toLowerCase());
                    break;
                case ENDS_WITH:
                    found = text.toLowerCase().endsWith(value.toLowerCase());
                    break;
            }
        }
        return match == QueryFilter.Match.IS_NOT
                || match == QueryFilter.Match.CONTAINS_NOT ? !found : found;
    }

    private boolean checkList(Object element, QueryFilter.Match match, List values) {
        if (values.isEmpty())
            return true;
        if (element == null)
            return false;

        boolean found = false;
        Iterator iterator = values.iterator();
        while (!found && iterator.hasNext())
            found = element.equals(iterator.next());

        return match == QueryFilter.Match.IS_NOT ? !found : found;
    }

}
