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
package org.netbeans.cubeon.jira.query;

import com.dolby.jira.net.soap.jira.RemoteIssue;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraFilterQuery extends AbstractJiraQuery {

    private JiraFilter filter;
    private List<String> ids = new ArrayList<String>();

    public JiraFilterQuery(JiraTaskRepository repository, String id) {
        super(repository, id);
    }

    @Override
    public Type getType() {
        return Type.FILTER;
    }

    public String getName() {
        return filter.getName();
    }

    public String getDescription() {
        return filter.getDescription();
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {

        final JiraTaskRepository repository = getRepository();
        if (filter != null) {

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    synchronized (JiraFilterQuery.this) {
                        extension.fireSynchronizing();
                        try{
                        ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing Query : " + getName());
                        handle.start();
                        handle.switchToIndeterminate();

                        try {
                            JiraSession session = repository.getSession();
                            handle.progress("Requsting Issues From Repository"); 
                            RemoteIssue[] remoteIssues = session.getIssuesFromFilter(filter.getId());

                            ids.clear();
                            for (RemoteIssue remoteIssue : remoteIssues) {
                                handle.progress(remoteIssue.getKey()+" :"+remoteIssue.getSummary()); 
                                TaskElement element = repository.getTaskElementById(remoteIssue.getKey());
                                if (element != null) {
                                    repository.update(remoteIssue, element.getLookup().lookup(JiraTask.class));
                                } else {
                                    JiraTask jiraTask = new JiraTask(remoteIssue.getKey(), remoteIssue.getSummary(), remoteIssue.getDescription(), repository);
                                    jiraTask.setUrlString(repository.getURL() + "/browse/" + remoteIssue.getKey());//NOI18N
                                    repository.update(remoteIssue, jiraTask);
                                    element = jiraTask;
                                }
                                ids.add(element.getId());
                            }
                        } catch (JiraException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        repository.getQuerySupport().modifyTaskQuery(JiraFilterQuery.this);
                        handle.finish();
                        }finally{
                        extension.fireSynchronized();
                        }
                    }
                }
            });


        }

    }

    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = new ArrayList<TaskElement>();
        JiraTaskRepository repository = getRepository();
        for (String key : ids) {
            TaskElement element = repository.getTaskElementById(key);
            if (element != null) {
                elements.add(element);
            }
        }

        return elements;
    }

    public JiraFilter getFilter() {
        return filter;
    }

    public void setFilter(JiraFilter filter) {
        this.filter = filter;
    }

    public List<String> getIds() {
        return new ArrayList<String>(ids);
    }

    public void setIds(List<String> ids) {
        this.ids = new ArrayList<String>(ids);
    }
}
