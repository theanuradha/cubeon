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

import com.dolby.jira.net.soap.jira.RemoteComponent;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import com.dolby.jira.net.soap.jira.RemoteVersion;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
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
    private final TaskPersistenceHandler handler;
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
                provider, extension, jtpp, jtrp, jtsp, jttp);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/jira/repository/jira-repository.png");
    }

    public void submit(JiraTask task) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void validate(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public TaskElement createTaskElement(String summery, String description) {
        TaskType prefedTaskType = jttp.getPrefedTaskType();
        TaskPriority prefredPriority = jtpp.getPrefredPriority();
        JiraProject prefredProject = getPrefredProject();
        try {
            JiraSession js = getSession();
            RemoteIssue issue = new RemoteIssue();
            issue.setSummary(summery);
            issue.setDescription(description);
            issue.setProject(prefredProject.getId());
            issue.setReporter(getUserName());
            issue.setType(prefedTaskType.getId());
            issue.setPriority(prefredPriority.getId());
            issue = js.createTask(issue);

            JiraTask jiraTask = new JiraTask(issue.getKey(), summery, description, this);
            jiraTask.setUrlString(url + "/browse/" + issue.getKey());//NOI18N

            maregeToTask(issue, jiraTask);
            return jiraTask;
        } catch (JiraException ex) {
            Logger.getLogger(JiraTaskRepository.class.getName()).log(Level.WARNING, ex.getMessage());
        }



        JiraTask jiraTask = new JiraTask(handler.nextTaskId(), summery, description, this);
        jiraTask.setLocal(true);
        jiraTask.setProject(prefredProject);
        jiraTask.setType(prefedTaskType);
        return jiraTask;
    }

    public TaskElement getTaskElementById(String id) {

        return handler.getTaskElementById(id);
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
                    ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
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
                maregeToTask(issue, task);
                persist(task);
            } catch (JiraException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    public synchronized JiraSession getSession() throws JiraException {
        if (session == null) {
            session = new JiraSession(url, getUserName(), password);
        }
        return session;
    }

    private void maregeToTask(RemoteIssue issue, JiraTask jiraTask) {
        jiraTask.setName(issue.getSummary());
        jiraTask.setDescription(issue.getDescription());
        jiraTask.setEnvironment(issue.getEnvironment());
        JiraProject project = repositoryAttributes.getProjectById(issue.getProject());
        jiraTask.setProject(project);
        jiraTask.setType(jttp.getTaskTypeById(issue.getType()));
        jiraTask.setPriority(jtpp.getTaskPriorityById(issue.getPriority()));
        jiraTask.setStatus(jtsp.getTaskStatusById(issue.getStatus()));
        jiraTask.setResolution(jtrp.getTaskResolutionById(issue.getResolution()));
        jiraTask.setReporter(issue.getReporter());
        jiraTask.setAssignee(issue.getAssignee());

        //----------------------------------------------------------------------
        RemoteComponent[] components = issue.getComponents();
        List<JiraProject.Component> cs = new ArrayList<JiraProject.Component>();
        for (RemoteComponent rc : components) {
            Component component = project.getComponentById(rc.getId());
            if (component != null) {
                cs.add(component);
            }
        }
        jiraTask.setComponents(cs);

        //----------------------------------------------------------------------
        RemoteVersion[] affectsRemoteVersions = issue.getAffectsVersions();
        List<JiraProject.Version> affectsVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : affectsRemoteVersions) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                affectsVersions.add(version);
            }
        }
        jiraTask.setAffectedVersions(affectsVersions);
        //----------------------------------------------------------------------
        RemoteVersion[] rvs = issue.getFixVersions();
        List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : rvs) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                fixVersions.add(version);
            }
        }
        jiraTask.setFixVersions(fixVersions);
        //----------------------------------------------------------------------

        Calendar created = issue.getCreated();
        if (created != null) {
            jiraTask.setCreated(created.getTime());
        }
        Calendar updated = issue.getUpdated();
        if (updated != null) {
            jiraTask.setUpdated(updated.getTime());
        }
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
