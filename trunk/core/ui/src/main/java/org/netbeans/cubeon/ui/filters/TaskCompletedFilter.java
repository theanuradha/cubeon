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

import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementFilter;
import org.netbeans.cubeon.ui.UIPreferences;

/**
 *
 * @author Anuradha
 */
public class TaskCompletedFilter implements TaskElementFilter {

    private static final String KEY_CFILTER = "c_filter";

    public TaskCompletedFilter() {
        System.out.println("");
    }

    public String getName() {
        return "Filter Completed Tasks";
    }

    public String getDescription() {
        return "Filter Completed Tasks";
    }

    public boolean isEnable() {
        return UIPreferences.getPreferences().getBoolean(KEY_CFILTER, false);
    }

    public void setEnable(boolean b) {
        UIPreferences.getPreferences().putBoolean(KEY_CFILTER, b);
    }

    public boolean isFiltered(TaskElement element) {
        return element.isCompleted();
    }
}
