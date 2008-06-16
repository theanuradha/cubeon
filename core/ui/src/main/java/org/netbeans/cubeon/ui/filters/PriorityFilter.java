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
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementFilter;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority.PRIORITY;
import org.netbeans.cubeon.ui.UIPreferences;

/**
 *
 * @author Anuradha
 */
public class PriorityFilter implements TaskElementFilter {

    private static final String KEY_FILTERS = "priority_filters";
    private static final String TOKEN = ":";
    private Set<TaskPriority.PRIORITY> filters =
            new HashSet<TaskPriority.PRIORITY>();

    public PriorityFilter() {
        String filterText = UIPreferences.getPreferences().get(KEY_FILTERS, "");
        StringTokenizer tokenizer = new StringTokenizer(filterText, TOKEN);
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            PRIORITY priority = TaskPriority.PRIORITY.valueOf(nextToken);
            if (priority != null) {
                filters.add(priority);
            }
        }
    }

    public String getName() {
        return "Filter Priority";
    }

    public String getDescription() {
        return "Filter Priority";
    }

    public boolean isEnable() {
        return filters.size() > 0;
    }

    public void setEnable(boolean b) {
        //ignore
    }

    public boolean isFiltered(TaskElement element) {
        return !contains(element.getPriority().getId());
    }

    public Set<PRIORITY> getFilters() {
        return new HashSet<PRIORITY>(filters);
    }

    public void removeFilter(PRIORITY priority) {
        if (contains(priority)) {
            filters.remove(priority);
            persist();
        }
    }

    public void addFilter(PRIORITY priority) {
        if (!contains(priority)) {
            filters.add(priority);
            persist();
        }
    }

    public boolean contains(PRIORITY priority) {
        return filters.contains(priority);
    }

    private void persist() {
        StringBuffer buffer = new StringBuffer();
        for (PRIORITY priority : filters) {
            buffer.append(priority.toString());
            buffer.append(TOKEN);
        }
        UIPreferences.getPreferences().get(KEY_FILTERS, buffer.toString());
    }
}
