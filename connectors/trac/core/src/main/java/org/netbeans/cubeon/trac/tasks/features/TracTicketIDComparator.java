/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.trac.tasks.features;

import java.util.Comparator;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementComparator;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class TracTicketIDComparator implements TaskElementComparator{

    private static final String SORT_BY_TRAC_TICKET_ID = "sort_by_trac_ticket_id";//NOI18N
    private static final String SORT_BY_TRAC_TICKET_ID_ASCE = "sort_by_trac_ticket_id_asce";//NOI18N
    private boolean enabled;
    private boolean asending;

    public TracTicketIDComparator() {
        enabled = FeaturesPreferences.getPreferences().getBoolean(SORT_BY_TRAC_TICKET_ID, false);
        asending = FeaturesPreferences.getPreferences().getBoolean(SORT_BY_TRAC_TICKET_ID_ASCE, true);
    }

    public boolean isEnable() {
        return enabled;
    }

    public void setEnable(boolean b) {
        this.enabled = b;
        FeaturesPreferences.getPreferences().putBoolean(SORT_BY_TRAC_TICKET_ID, b);
    }

    public boolean isAscending() {
        return asending;
    }

    public void setAscending(boolean b) {
        asending = b;
        FeaturesPreferences.getPreferences().putBoolean(SORT_BY_TRAC_TICKET_ID_ASCE, b);
    }

    public String getName() {
        return NbBundle.getMessage(TracTicketIDComparator.class, "LBL_Ticket_ID");
    }

    public String getDescription() {
        return NbBundle.getMessage(TracTicketIDComparator.class, "LBL_Ticket_ID_Dec");
    }

    public Comparator<TaskElement> getComparator() {
        return new Comparator<TaskElement>() {

            public int compare(TaskElement o1, TaskElement o2) {
                TracTask task1 = o1.getLookup().lookup(TracTask.class);
                TracTask task2 = o2.getLookup().lookup(TracTask.class);
                if(task1!=null && task2 !=null)
                {
                   return task1.getId().compareTo(task2.getId());
                }
                return -1;
            }
        };
    }
}
