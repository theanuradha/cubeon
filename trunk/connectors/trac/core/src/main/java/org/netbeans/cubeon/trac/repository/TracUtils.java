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

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.api.TracSession;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha
 */
public class TracUtils {

    private static final String TICKET = "Ticket #";

    public static void createTicket(TracTaskRepository repository, TracTask task) throws TracException {
        if (task.isLocal()) {
            String old = task.getId();
            TracSession session = repository.getSession();
            Ticket ticket = session.createTicket(task.getSummary(),
                    task.getDescription(),
                    new HashMap<String, Object>(task.getAttributes()), true);
            repository.getTaskPersistenceHandler().removeTaskElement(task);
            task.setId(ticketToTaskId(ticket));
            task.setTicketId(ticket.getTicketId());
            task.setLocal(false);

            //put all atributes to task
            task.putAll(ticket.getAttributes());
            //put created and updated date
            task.setCreatedDate(ticket.getCreatedDate());
            task.setUpdatedDate(ticket.getUpdatedDate());
            repository.cache(issueToTask(repository, ticket));
            task.setModifiedFlag(false);
            List<TicketAction> ticketActions = session.getTicketActions(task.getTicketId());
            task.setActions(ticketActions);
            repository.persist(task);
            repository.getQuerySupport().getOutgoingQuery().removeTaskId(old);
            //notify about task id changed
            repository.getNotifier().fireIdChanged(old, task.getId());
        }
    }

    public static Ticket taskToTicket(TracTaskRepository repository, TracTask task) {
        Ticket ticket = new Ticket(task.getTicketId());
        List<TicketField> fields = repository.getRepositoryAttributes().getTicketFields();
        for (TicketField ticketField : fields) {
            ticket.put(ticketField.getName(), task.get(ticketField.getName()));
        }
        ticket.setTicketChanges(task.getTicketChanges());

        return ticket;
    }

    private TracUtils() {
    }

    public static TracTask issueToTask(TracTaskRepository repository, Ticket ticket) {
        //create new TracTask
        TracTask tracTask = new TracTask(repository, ticketToTaskId(ticket), ticket.getTicketId(),
                ticket.getSummary(), ticket.getSummary());
        //put all atributes to task
        tracTask.putAll(ticket.getAttributes());
        //put created and updated date
        tracTask.setCreatedDate(ticket.getCreatedDate());
        tracTask.setUpdatedDate(ticket.getUpdatedDate());
        tracTask.setTicketId(ticket.getTicketId());
        tracTask.setTicketChanges(ticket.getTicketChanges());
        return tracTask;
    }

    public static void maregeToTask(TracTaskRepository repository, Ticket ticket,
            TracTask cachedTask, TracTask task) {
        if (cachedTask == null) {
            cachedTask = issueToTask(repository, ticket);
            remoteToTask(repository, cachedTask, task);
        }
        //cheack for status change
        String status = ticket.get(TracKeys.STATUS);
        if (status != null && !status.equals(cachedTask.getStatus().getId())) {
            task.setAction(null);
        }
        //put changed atributes to task
        Set<Entry<String, String>> entrySet = ticket.getAttributes().entrySet();
        for (Entry<String, String> entry : entrySet) {
            String valve = cachedTask.get(entry.getKey());
            if (valve == null || !valve.equals(entry.getValue())) {
                task.put(entry.getKey(), entry.getValue());
            }
        }
        //put created and updated date
        task.setCreatedDate(ticket.getCreatedDate());
        task.setUpdatedDate(ticket.getUpdatedDate());
        task.setTicketId(ticket.getTicketId());
        task.setTicketChanges(ticket.getTicketChanges());
    }

    public static void remoteToTask(TracTaskRepository repository, TracTask remoteTask, TracTask tracTask) {
        //clear all 
        tracTask.clear();
        //put all atributes to task
        tracTask.putAll(remoteTask.getAttributes());
        //put created and updated date
        tracTask.setCreatedDate(remoteTask.getCreatedDate());
        tracTask.setUpdatedDate(remoteTask.getUpdatedDate());
        tracTask.setTicketChanges(remoteTask.getTicketChanges());
    }

    public static String ticketToTaskId(Ticket ticket) {
        return TICKET + ticket.getTicketId();
    }
}
