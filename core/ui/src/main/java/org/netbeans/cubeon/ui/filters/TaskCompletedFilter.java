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

import org.netbeans.cubeon.tasks.spi.repository.OfflineTaskSupport;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementFilter;
import org.netbeans.cubeon.ui.UIPreferences;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class TaskCompletedFilter implements TaskElementFilter {

    private static final String KEY_CFILTER = "c_filter";//NOI18N
    private boolean enable;

    public TaskCompletedFilter() {
        enable = UIPreferences.getPreferences().getBoolean(KEY_CFILTER, false);
    }

    public String getName() {
        return NbBundle.getMessage(TaskCompletedFilter.class, "LBL_Filter_Completed_Tasks");
    }

    public String getDescription() {
        return NbBundle.getMessage(TaskCompletedFilter.class, "LBL_Filter_Completed_Tasks");
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean b) {
        UIPreferences.getPreferences().putBoolean(KEY_CFILTER, b);
        enable = b;
    }

    public boolean isFiltered(TaskElement element) {
        //Do not filter completed task with outgoing changes
        OfflineTaskSupport offlineTaskSupport = element.getTaskRepository().getLookup().lookup(OfflineTaskSupport.class);
        if (offlineTaskSupport != null && offlineTaskSupport.hasOutgoingChanges(element)) {
            return false;
        }
        
        return element.isCompleted();
    }
}
