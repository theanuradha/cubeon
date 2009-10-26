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
package org.netbeans.cubeon.gcode.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.api.GCodeSession;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.gcode.utils.GCodeExceptionHandler;
import org.netbeans.cubeon.gcode.utils.GCodeUtils;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class GCodeFilterQuery extends AbstractGCodeQuery {

    private String name;
    private String query;
    private List<String> ids = new ArrayList<String>();

    public GCodeFilterQuery(GCodeTaskRepository repository, String id) {
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

        final GCodeTaskRepository repository = getRepository();
        if (query != null) {

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    synchronized (repository.SYNCHRONIZE_QUERY_LOCK) {
                        extension.fireSynchronizing();
                        ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(GCodeFilterQuery.class,
                                "LBL_Synchronizing_Query", getName()));
                        handle.start();
                        handle.switchToIndeterminate();



                        try {
                            GCodeSession session = repository.getSession();
                            handle.progress(NbBundle.getMessage(GCodeFilterQuery.class, "LBL_Requsting_Issues_From_Repository"));
                            List<GCodeIssue> remoteIssues = session.getIssuesByQueryString(query);

                            ids.clear();
                            handle.switchToDeterminate(remoteIssues.size());
                            readTickets(handle, remoteIssues);
                            handle.finish();
                        } catch (GCodeException ex) {
                            GCodeExceptionHandler.notify(ex);
                        } finally {
                            repository.getQuerySupport().modifyTaskQuery(GCodeFilterQuery.this);
                            handle.finish();
                            extension.fireSynchronized();
                        }
                    }

                }

                private void readTickets(ProgressHandle handle, List<GCodeIssue> remoteIssues) {


                    for (GCodeIssue issue : remoteIssues) {
                        String taskID = GCodeTask.issueToTaskId(issue);
                        handle.progress(taskID + " :" + issue.getSummary(), remoteIssues.indexOf(issue));
                        if (issue != null) {
                            TaskElement element = repository.getTaskElementById(taskID);
                            if (element != null) {
                                repository.update(issue, element.getLookup().lookup(GCodeTask.class));
                            } else {
                                GCodeTask gct = GCodeUtils.toCodeTask(repository, issue);
                                repository.update(issue, gct);
                                element = gct;
                            }
                            extension.fireTaskAdded(element);
                            ids.add(element.getId());
                        }
                    }

                }
            });

        }

    }

    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = new ArrayList<TaskElement>();
        GCodeTaskRepository repository = getRepository();
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GCodeFilterQuery other = (GCodeFilterQuery) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
}
