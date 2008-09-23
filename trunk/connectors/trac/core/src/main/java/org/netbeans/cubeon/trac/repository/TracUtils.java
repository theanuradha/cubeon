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
package org.netbeans.cubeon.trac.repository;

import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha
 */
class TracUtils {

    private TracUtils() {
    }

    static TracTask issueToTask(TracTaskRepository repository, Ticket ticket) {
        //create new TracTask
        TracTask tracTask = new TracTask(repository, "Ticket #" + ticket.getTicketId(), ticket.getTicketId(),
                ticket.getSummary(), ticket.getSummary());
        //put all atributes to task
        tracTask.putAll(ticket.getAttributes());
        //put created and updated date
        tracTask.setCreatedDate(ticket.getCreatedDate());
        tracTask.setUpdatedDate(ticket.getUpdatedDate());

        //TODO ADD Comments to task
        return tracTask;
    }

    static void maregeToTask(TracTaskRepository repository, Ticket ticket,
            TracTask cachedTask, TracTask task) {
        if (cachedTask == null) {
            cachedTask = issueToTask(repository, ticket);
            remoteToTask(repository, cachedTask, task);
        }
        //cheack for status change
        if (!ticket.get(TracKeys.STATUS).equals(cachedTask.getStatus().getId())) {
            task.setAction(null);
        }
        //put changed atributes to task
        Set<Entry<String, String>> entrySet = ticket.getAttributes().entrySet();
        for (Entry<String, String> entry : entrySet) {
            String valve = cachedTask.get(entry.getKey());
            if (valve == null || valve.equals(entry.getValue())) {
                task.put(entry.getKey(), entry.getValue());
            }
        }
        //put created and updated date
        task.setCreatedDate(ticket.getCreatedDate());
        task.setUpdatedDate(ticket.getUpdatedDate());
    }

    public static void remoteToTask(TracTaskRepository repository, TracTask remoteTask, TracTask tracTask) {
        //clear all 
        tracTask.clear();
        //put all atributes to task
        tracTask.putAll(remoteTask.getAttributes());
        //put created and updated date
        tracTask.setCreatedDate(remoteTask.getCreatedDate());
        tracTask.setUpdatedDate(remoteTask.getUpdatedDate());
    //TODO ADD Comments to task
    }
}
