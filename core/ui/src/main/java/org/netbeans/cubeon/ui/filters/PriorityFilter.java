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
package org.netbeans.cubeon.ui.filters;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementFilter;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.ui.UIPreferences;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class PriorityFilter implements TaskElementFilter {

    private static final String KEY_FILTERS = "priority_filters";
    private static final String TOKEN = ":";
    private Set<TaskPriority> filters =
            new HashSet<TaskPriority>();

    public PriorityFilter() {
        String filterText = UIPreferences.getPreferences().get(KEY_FILTERS, "");
        StringTokenizer tokenizer = new StringTokenizer(filterText, TOKEN);
        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        TaskRepositoryHandler handler = context.getLookup().lookup(TaskRepositoryHandler.class);
        while (tokenizer.hasMoreTokens()) {
            String repoid = tokenizer.nextToken();
            String priority = tokenizer.nextToken();
            TaskRepository repository = handler.getTaskRepositoryById(repoid);
            if (repository != null) {
                TaskPriorityProvider priorityProvider = repository.getLookup().lookup(TaskPriorityProvider.class);
                if (priorityProvider != null) {
                    TaskPriority tp = priorityProvider.getTaskPriorityById(priority);
                    if (tp != null) {
                        filters.add(tp);
                    }
                }
            }
        }
    }

    public String getName() {
        return "Filter By Priority";
    }

    public String getDescription() {
        return "Filter By Priority";
    }

    public boolean isEnable() {
        return filters.size() > 0;
    }

    public void setEnable(boolean b) {
        //ignore
    }

    public boolean isFiltered(TaskElement element) {
        TaskPriorityProvider tpp = element.getTaskRepository().getLookup().lookup(TaskPriorityProvider.class);
        return tpp != null && !(contains(tpp.getTaskPriority(element)));
    }

    public Set<TaskPriority> getFilters() {
        return new HashSet<TaskPriority>(filters);
    }

    public void removeFilter(TaskPriority priority) {
        if (contains(priority)) {
            filters.remove(priority);
            persist();
        }
    }

    public void addFilter(TaskPriority priority) {
        if (!contains(priority)) {
            filters.add(priority);
            persist();
        }
    }

    public boolean contains(TaskPriority priority) {
        return filters.contains(priority);
    }

    private void persist() {
        StringBuffer buffer = new StringBuffer();
        for (TaskPriority priority : filters) {
            buffer.append(priority.getRepository().getId());
            buffer.append(TOKEN);

            buffer.append(priority.getId());
            buffer.append(TOKEN);

        }
        UIPreferences.getPreferences().put(KEY_FILTERS, buffer.toString());
    }
}
