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
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementComparator;
import org.netbeans.cubeon.ui.UIPreferences;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class SortBySummery implements TaskElementComparator {

    private static final String SORT_BY_SUMMERY = "sort_by_summery";//NOI18N
    private static final String SORT_BY_SUMMERY_ASCE = "sort_by_summery_asce";//NOI18N
    private boolean enabled;
    private boolean asending;

    public SortBySummery() {
        enabled = UIPreferences.getPreferences().getBoolean(SORT_BY_SUMMERY, true);
        asending = UIPreferences.getPreferences().getBoolean(SORT_BY_SUMMERY_ASCE, true);
    }

    public String getName() {
        return NbBundle.getMessage(SortBySummery.class, "LBL_Summary");
    }

    public String getDescription() {
        return NbBundle.getMessage(SortBySummery.class, "LBL_Summary_Dec");
    }

    public boolean isEnable() {
        return enabled;
    }

    public void setEnable(boolean b) {
        this.enabled = b;
        UIPreferences.getPreferences().putBoolean(SORT_BY_SUMMERY, b);
    }

    public Comparator<TaskElement> getComparator() {
        if (isAscending()) {
            return new Comparator<TaskElement>() {

                public int compare(TaskElement o1, TaskElement o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
        }
        return new Comparator<TaskElement>() {

            public int compare(TaskElement o1, TaskElement o2) {
                return o2.getName().compareTo(o1.getName());
            }
        };
    }

    public boolean isAscending() {
        return asending;
    }

    public void setAscending(boolean b) {
        asending = b;
        UIPreferences.getPreferences().putBoolean(SORT_BY_SUMMERY_ASCE, b);
    }
}
