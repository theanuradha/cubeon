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
package org.netbeans.cubeon.jira.repository;

import com.dolby.jira.net.soap.jira.RemoteFieldValue;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.jira.query.JiraQuerySupport;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraTaskRepository implements TaskRepository {

    private final JiraTaskRepositoryProvider provider;
    private final String id;
    private String name;
    private String description;
    private String url;
    //----------------------------
    private String userName;
    private String password;
    //----------------------------
    private final JiraRepositoryExtension extension;
    private final JiraAttributesPersistence attributesPersistence;    //::::::::::::::::
    private final JiraTaskPriorityProvider jtpp = new JiraTaskPriorityProvider();
    private final JiraTaskTypeProvider jttp = new JiraTaskTypeProvider();
    private final JiraTaskStatusProvider jtsp = new JiraTaskStatusProvider();
    private final JiraTaskResolutionProvider jtrp = new JiraTaskResolutionProvider();
    private final JiraRepositoryAttributes repositoryAttributes = new JiraRepositoryAttributes();
    private final JiraQuerySupport querySupport;
    private final TaskPersistenceHandler handler;
    private Map<String, JiraTask> map = new HashMap<String, JiraTask>();
    private FileObject baseDir;
    private State state = State.INACTIVE;
    private volatile JiraSession session;

    public JiraTaskRepository(JiraTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        extension = new JiraRepositoryExtension(this);
        baseDir = provider.getBaseDir().getFileObject(id);
        if (baseDir == null) {
            try {
                baseDir = provider.getBaseDir().createFolder(id);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        attributesPersistence = new JiraAttributesPersistence(this, baseDir);
        handler = new TaskPersistenceHandler(this, baseDir);
        querySupport = new JiraQuerySupport(this, extension);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this,
                provider, extension, jtpp, jtrp, jtsp, jttp, querySupport);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/jira/repository/jira-repository.png");
    }

    public void validate(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public TaskElement createTaskElement(String summery, String description) {

        return JiraUtils.createTaskElement(this, summery, description);
    }

    public synchronized TaskElement getTaskElementById(String id) {
        JiraTask get = map.get(id);
        if (get == null) {

            get = handler.getTaskElementById(id);
            if (get != null) {
                map.put(id, get);
            }
        }
        return get;
    }

    public void persist(TaskElement element) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        assert jiraTask != null;
        handler.persist(jiraTask);
    }

    public void reset(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public JiraTaskRepositoryProvider getProvider() {
        return provider;
    }

    public JiraRepositoryExtension getExtension() {
        return extension;
    }

    public void updateAttributes() {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                try {
                    setState(State.SYNCHRONIZING);
                    ProgressHandle handle = ProgressHandleFactory.createHandle(getName() + ": Updating Attributes");
                    attributesPersistence.refresh(handle);
                    loadAttributes();
                    handle.finish();
                } catch (JiraException ex) {
                    Logger.getLogger(JiraAttributesPersistence.class.getName()).
                            log(Level.WARNING, ex.getMessage());
                    setState(State.ACTIVE);
                }
            }
        });

    }

    public synchronized void loadAttributes() {
        attributesPersistence.loadAttributes();
        setState(State.ACTIVE);
    }

    public JiraTaskPriorityProvider getJiraTaskPriorityProvider() {
        return jtpp;
    }

    public JiraTaskResolutionProvider getJiraTaskResolutionProvider() {
        return jtrp;
    }

    public JiraTaskStatusProvider getJiraTaskStatusProvider() {
        return jtsp;
    }

    public JiraTaskTypeProvider getJiraTaskTypeProvider() {
        return jttp;
    }

    public JiraRepositoryAttributes getRepositoryAttributes() {
        return repositoryAttributes;
    }

    public State getState() {
        return state;//default
    }

    public void setState(State state) {
        this.state = state;
        extension.fireStateChanged(state);
    }

    public JiraProject getPrefredProject() {
        List<JiraProject> projects = repositoryAttributes.getProjects();
        if (projects.size() > 0) {
            return projects.get(0);//TODO Extenalize this
        }
        return null;
    }

    public void update(JiraTask task) {
        synchronized (task) {
            try {
                JiraSession js = getSession();
                RemoteIssue issue = js.getIssue(task.getId());
                JiraUtils.maregeToTask(this, issue, task);
                persist(task);
                TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
                factory.refresh(task);
            } catch (JiraException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    public void submit(JiraTask task) {
        synchronized (task) {
            try {
                JiraSession js = getSession();
                RemoteFieldValue[] fieldValues = JiraUtils.changedFieldValues(js.getIssue(task.getId()), task);
                if (fieldValues.length > 0) {
                    RemoteIssue updateTask = js.updateTask(task.getId(), fieldValues);
                    JiraUtils.maregeToTask(this, updateTask, task);
                    if (task.getAction() != null) {
                        RemoteIssue remoteIssue = js.progressWorkflowAction(task.getId(),
                                task.getAction().getId(),
                                JiraUtils.changedFieldValuesForAction(task.getAction(),
                                updateTask, task));
                        JiraUtils.maregeToTask(this, remoteIssue, task);
                    }
                    persist(task);
                } else {
                    if (task.getAction() != null) {
                        RemoteIssue remoteIssue = js.progressWorkflowAction(task.getId(),
                                task.getAction().getId(),
                                JiraUtils.changedFieldValuesForAction(task.getAction(),
                                js.getIssue(task.getId()), task));
                        JiraUtils.maregeToTask(this, remoteIssue, task);
                        persist(task);
                    }

                }
            } catch (JiraException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    synchronized JiraSession getSession() throws JiraException {
        if (session == null) {
            session = new JiraSession(url, getUserName(), password);
        }
        return session;
    }

    TaskPersistenceHandler getTaskPersistenceHandler() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JiraTaskRepository other = (JiraTaskRepository) obj;
        if (this.provider != other.provider && (this.provider == null || !this.provider.equals(other.provider))) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.provider != null ? this.provider.hashCode() : 0);
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
