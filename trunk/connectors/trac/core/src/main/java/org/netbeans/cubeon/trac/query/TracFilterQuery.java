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
package org.netbeans.cubeon.trac.query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracSession;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.repository.TracUtils;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.netbeans.cubeon.trac.utils.TracExceptionHandler;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TracFilterQuery extends AbstractTracQuery {

    private String name;
    private String query;
    private List<String> ids = new ArrayList<String>();

    public TracFilterQuery(TracTaskRepository repository, String id) {
        super(repository, id);
    }

    @Override
    public Type getType() {
        return Type.FILTER;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {

        final TracTaskRepository repository = getRepository();
        if (query != null) {

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    synchronized (repository.SYNCHRONIZE_QUERY_LOCK) {
                        extension.fireSynchronizing();
                        ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(TracFilterQuery.class,
                                "LBL_Synchronizing_Query", getName()));
                        handle.start();
                        handle.switchToIndeterminate();



                        try {
                            TracSession session = repository.getSession();
                            handle.progress(NbBundle.getMessage(TracFilterQuery.class, "LBL_Requsting_Issues_From_Repository"));
                            List<Integer> remoteIssues = session.queryTickets(query);

                            ids.clear();
                            handle.switchToDeterminate(remoteIssues.size());
                            final int size = remoteIssues.size() < 5 ? remoteIssues.size() : 5;
                            int[] idarray = new int[size];
                            int current = 0;

                            for (int i = 0; i < remoteIssues.size(); i++) {
                                int ticketId = remoteIssues.get(i);
                                idarray[current++] = ticketId;
                                if (current == size) {
                                    readTickets(session, idarray, handle, remoteIssues);
                                    current = 0;
                                    idarray = new int[size];
                                }
                            }
                            //Issue 50 
                            if (current > 0) {
                                int[] modidarray = new int[current];
                                for (int i = 0; i < current; i++) {
                                     modidarray[i]=idarray[i];
                                }
                                readTickets(session, modidarray, handle, remoteIssues);
                                current = 0;
                                idarray = null;
                            }
                        } catch (TracException ex) {
                            TracExceptionHandler.notify(ex);
                        } finally {
                            repository.getQuerySupport().modifyTaskQuery(TracFilterQuery.this);
                            handle.finish();
                            extension.fireSynchronized();
                        }
                    }

                }

                private void readTickets(TracSession session, int[] idarray, ProgressHandle handle, List<Integer> remoteIssues) {
                    try {
                        List<Ticket> tickets = session.getTickets(idarray);
                        for (Ticket ticket : tickets) {
                            String taskID = TracUtils.ticketToTaskId(ticket);
                            handle.progress(taskID + " :" + ticket.getSummary(), remoteIssues.indexOf(ticket.getTicketId()));
                            if (ticket != null) {
                                TaskElement element = repository.getTaskElementById(taskID);
                                if (element != null) {
                                    repository.update(ticket, element.getLookup().lookup(TracTask.class));
                                } else {
                                    TracTask jiraTask = TracUtils.issueToTask(repository, ticket);
                                    repository.update(ticket, jiraTask);
                                    element = jiraTask;
                                }
                                extension.fireTaskAdded(element);
                                ids.add(element.getId());
                            }
                        }
                    } catch (TracException tracException) {
                        Logger.getLogger(getClass().getName()).warning(tracException.getMessage());
                    }
                }
            });

        }

    }

    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = new ArrayList<TaskElement>();
        TracTaskRepository repository = getRepository();
        for (String key : ids) {
            TaskElement element = repository.getTaskElementById(key);
            if (element != null) {
                elements.add(element);
            }

        }

        return elements;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getIds() {
        return new ArrayList<String>(ids);
    }

    public void setIds(List<String> ids) {
        this.ids = new ArrayList<String>(ids);
    }
}
