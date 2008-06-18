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
package org.netbeans.cubeon.ui.taskelemet;

import java.util.Comparator;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementComparator;
import org.netbeans.cubeon.ui.UIPreferences;

/**
 *
 * @author Anuradha
 */
public class SortByPriority implements TaskElementComparator {

    private static final String SORT_BY_PRIORITY = "sort_by_priority";
    private static final String SORT_BY_PRIORITY_ASCE = "sort_by_priority_asce";
    private boolean enabled;
    private boolean asending;

    public SortByPriority() {
        enabled = UIPreferences.getPreferences().getBoolean(SORT_BY_PRIORITY, false);
        asending = UIPreferences.getPreferences().getBoolean(SORT_BY_PRIORITY_ASCE, true);
    }

    public String getName() {
        return "Priority";
    }

    public String getDescription() {
        return "Sort tasks by priority";
    }

    public boolean isEnable() {
        return enabled;
    }

    public void setEnable(boolean b) {
        this.enabled = b;
        UIPreferences.getPreferences().putBoolean(SORT_BY_PRIORITY, b);
    }

    public Comparator<TaskElement> getComparator() {
        if (isAscending()) {
            return new Comparator<TaskElement>() {

                public int compare(TaskElement o1, TaskElement o2) {
                    return o1.getPriority().getId().compareTo(o2.getPriority().getId());
                }
            };
        }
        return new Comparator<TaskElement>() {

            public int compare(TaskElement o1, TaskElement o2) {
                return o2.getPriority().getId().compareTo(o1.getPriority().getId());
            }
        };
    }

    public boolean isAscending() {
        return asending;
    }

    public void setAscending(boolean b) {
        asending = b;
        UIPreferences.getPreferences().putBoolean(SORT_BY_PRIORITY_ASCE, b);
    }
}