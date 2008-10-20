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

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {

        final TracTaskRepository repository = getRepository();
        if (query != null) {

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    synchronized (TracFilterQuery.this) {
                        extension.fireSynchronizing();
                        ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(TracFilterQuery.class,
                                "LBL_Synchronizing_Query", getName()));
                        handle.start();
                        handle.switchToIndeterminate();
                        try {


                            try {
                                TracSession session = repository.getSession();
                                handle.progress(NbBundle.getMessage(TracFilterQuery.class, "LBL_Requsting_Issues_From_Repository"));
                                List<Integer> remoteIssues = session.queryTickets(query);

                                ids.clear();
                                handle.switchToDeterminate(remoteIssues.size());

                                for (Integer ticketId : remoteIssues) {
                                    Ticket ticket = null;
                                    try {
                                        ticket = session.getTicket(ticketId);
                                        String taskID = TracUtils.ticketToTaskId(ticket);
                                        handle.progress(taskID + " :" + ticket.getSummary(),
                                                remoteIssues.indexOf(ticketId));
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

                                    } catch (TracException tracException) {
                                        //ignore
                                    }



                                }

                            } catch (TracException ex) {
                                TracExceptionHandler.notify(ex);
                            }

                        } finally {
                            repository.getQuerySupport().modifyTaskQuery(TracFilterQuery.this);
                            handle.finish();
                            extension.fireSynchronized();
                        }
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
