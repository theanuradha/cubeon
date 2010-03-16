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

import com.dolby.jira.net.soap.jira.RemoteComment;
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
import org.netbeans.cubeon.jira.tasks.JiraRemoteTask;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraTaskRepository implements TaskRepository {

    private final JiraTaskRepositoryProvider provider;
    private final TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
    private final String id;
    private String name;
    private String description;
    /**
     * Url of the Repository 
     * Ex:http://jira.repository 
     */
    private String url;
    /**this is key of the project if repository for singel project   
     * ex(CN is key for http://jira.repository/browse/CN)
     * if null it will download all projects
     */
    private String projectKey;
    //----------------------------
    private String userName;
    private String password;
    private boolean ignoreSSL;
    //----------------------------
    private final JiraRepositoryExtension extension;
    private final JiraTaskPriorityProvider jtpp = new JiraTaskPriorityProvider();
    private final JiraTaskTypeProvider jttp = new JiraTaskTypeProvider();
    private final JiraTaskStatusProvider jtsp = new JiraTaskStatusProvider();
    private final JiraTaskResolutionProvider jtrp = new JiraTaskResolutionProvider();
    private final JiraOfflineTaskSupport offlineTaskSupport;
    private final JiraRepositoryAttributes repositoryAttributes;
    private final JiraQuerySupport querySupport;
    private final TaskPersistenceHandler handler;
    private final TaskPersistenceHandler cache;
    private Map<String, JiraTask> map = new HashMap<String, JiraTask>();
    private FileObject baseDir;
    private State state = State.INACTIVE;
    private volatile JiraSession session;
    //locks
    private final Object SYNCHRONIZE_LOCK = new Object();
    public final Object SYNCHRONIZE_QUERY_LOCK = new Object();
    private final Object FILTER_UPDATE_LOCK = new Object();
    private final Lookup lookup;
    //----

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

        repositoryAttributes = new JiraRepositoryAttributes(this);
        handler = new TaskPersistenceHandler(this, baseDir, "tasks");//NOI18N
        cache = new TaskPersistenceHandler(this, baseDir, "cache");//NOI18N
        querySupport = new JiraQuerySupport(this, extension);
        offlineTaskSupport = new JiraOfflineTaskSupport(this);
        lookup = Lookups.fixed(this,
                provider, extension, jtpp, jtrp, jtsp, jttp, querySupport, offlineTaskSupport);
    }

    public FileObject getBaseDir() {
        return baseDir;
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
        return lookup;
    }

    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/jira/repository/jira-repository.png");
    }

    public List<String> getTaskIds() {
        return handler.getTaskIds();
    }

    public void synchronize() {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                synchronized (SYNCHRONIZE_LOCK) {
                    List<String> taskIds = handler.getTaskIds();
                    if (taskIds.isEmpty()) {
                        return;
                    }
                    ProgressHandle handle = ProgressHandleFactory.createHandle(
                            NbBundle.getMessage(JiraTaskRepository.class,
                            "LBL_Synchronizing_Tasks", getName()));
                    handle.start(taskIds.size());

                    try {
                        JiraSession session = getSession();
                        if (session != null) {
                            for (String id : taskIds) {
                                JiraTask jiraTask = getTaskElementById(id);
                                if (jiraTask != null && !jiraTask.isLocal()) {
                                    handle.progress(jiraTask.getId() + " : " + jiraTask.getName(), taskIds.indexOf(id));
                                    update(session, jiraTask);
                                }
                            }
                        }
                    } catch (JiraException ex) {
                        Logger.getLogger(JiraTaskRepository.class.getName()).warning(ex.getMessage());
                    } finally {
                        handle.finish();
                    }

                }
            }
        });
    }

    public TaskElement createTaskElement(String summery, String description) {

        JiraTask jiraTask = new JiraTask(handler.nextTaskId(),
                summery, description, this);
        jiraTask.setLocal(true);
        jiraTask.setProject(getPrefredProject());
        jiraTask.setType(getJiraTaskTypeProvider().getPrefedTaskType());
        jiraTask.setPriority(jtpp.getPrefredPriority());
        return jiraTask;
    }

    public synchronized JiraTask getTaskElementById(String id) {
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
        //notify to outgoing query about modified state
        if (jiraTask.isModifiedFlag()) {
            querySupport.getOutgoingQuery().addTaskId(element.getId());
        } else {
            querySupport.getOutgoingQuery().removeTaskId(element.getId());
        }
    }

    public void revert(JiraTask task) {
        synchronized (task) {
            task.setAction(null);
            task.setNewComment(null);
            JiraUtils.remoteToTask(this, getJiraRemoteTaskCache(task.getId()), task);
            task.setModifiedFlag(false);
            persist(task);
            task.getExtension().fireStateChenged();
        }
    }

    public void remove(JiraTask jiraTask) {
        handler.removeTaskElement(jiraTask);
        //issue-26 check if loacl as local task not on cache
        if (!jiraTask.isLocal()) {
            cache.removeTaskElement(jiraTask);
        }
        extension.fireTaskRemoved(jiraTask);
        //make sure to remove from outgoing resultset
        querySupport.getOutgoingQuery().removeTaskId(jiraTask.getId());
    }

    public void cache(JiraRemoteTask remoteTask) {

        cache.persist(remoteTask);
    }

    public String getPassword() {
        return password;
    }

    public JiraRemoteTask getJiraRemoteTaskCache(String id) {
        return cache.getJiraRemoteTaskById(id);
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

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public JiraTaskRepositoryProvider getProvider() {
        return provider;
    }

    public JiraRepositoryExtension getNotifier() {
        return extension;
    }

    public void updateAttributes() {

        try {
            setState(State.SYNCHRONIZING);
            ProgressHandle handle = ProgressHandleFactory.createHandle(
                    NbBundle.getMessage(JiraTaskRepository.class,
                    "LBL_Updating_Attributes", getName()));
            repositoryAttributes.refresh(handle);
            loadAttributes();
            handle.finish();
        } catch (JiraException ex) {
            Logger.getLogger(JiraAttributesPersistence.class.getName()).
                    log(Level.WARNING, ex.getMessage());
            setState(State.ACTIVE);
        }


    }

    public void updateFilters() {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(JiraTaskRepository.class,
                "LBL_Updating_Filters", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {


            synchronized (FILTER_UPDATE_LOCK) {
                repositoryAttributes.refreshFilters(handle);
                loadFilters();
            }

        } catch (JiraException ex) {
            Logger.getLogger(JiraAttributesPersistence.class.getName()).
                    log(Level.WARNING, ex.getMessage());

        } finally {
            handle.finish();
        }
    }

    public synchronized void loadAttributes() {
        repositoryAttributes.loadAttributes();
        setState(State.ACTIVE);
    }

    public synchronized void loadFilters() {
        repositoryAttributes.loadFilters();
        querySupport.refresh();

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

    public void update(JiraTask task) throws JiraException {
        synchronized (task) {

            JiraSession js = getSession();

            update(js, task);
        }

    }

    private void update(JiraSession js, JiraTask task) throws JiraException {
        RemoteIssue issue = js.getIssue(task.getId());
        update(issue, task);
    }

    public void update(RemoteIssue issue, JiraTask task) throws JiraException {
        synchronized (task) {
            if (!task.isLocal()) {
                JiraRemoteTask jiraRemoteTask = getJiraRemoteTaskCache(task.getId());
                if (jiraRemoteTask != null && jiraRemoteTask.getUpdated().getTime() == issue.getUpdated().getTime().getTime()) {
                    Logger.getLogger(getClass().getName()).info("Up to date : " + issue.getKey());//NOI18N

                } else {
                    //issue - 85 we need to save any changes before task marege
                    factory.save(task);

                    JiraUtils.maregeToTask(this, issue, jiraRemoteTask, task);
                    persist(task);

                    //make cache up to date
                    cache(JiraUtils.issueToTask(this, issue));


                    factory.refresh(task);

                    task.getExtension().fireStateChenged();
                }
            }
        }

    }

    public void submit(JiraTask task) throws JiraException {
        synchronized (task) {

            if (task.isLocal()) {
                JiraUtils.createTaskElement(this, task);
            } else {

                JiraSession js = getSession();
                if (task.getAction() != null) {

                    js.progressWorkflowAction(task.getId(),
                            task.getAction().getId(),
                            JiraUtils.changedFieldValuesForAction(task.getAction(),
                            getJiraRemoteTaskCache(task.getId()), task));

                    task.setAction(null);
                    JiraUtils.readWorkFlow(this, task);

                }
                RemoteFieldValue[] fieldValues = JiraUtils.changedFieldValues(getJiraRemoteTaskCache(task.getId()), task);

                if (fieldValues.length > 0) {
                    if (task.getNewComment() != null && task.getNewComment().trim().length() > 0) {
                        RemoteComment comment = new RemoteComment();
                        comment.setAuthor(getUserName());
                        comment.setBody(task.getNewComment());
                        js.addComment(task.getId(), comment);
                        task.setNewComment(null);
                    }

                    RemoteIssue remoteIssue = js.updateTask(task.getId(), fieldValues);
                    JiraRemoteTask remoteTask = JiraUtils.issueToTask(this, remoteIssue);
                    JiraUtils.remoteToTask(this, remoteTask, task);

                    //make cache up to date
                    cache(remoteTask);
                }

                if (task.getNewComment() != null && task.getNewComment().trim().length() > 0) {
                    RemoteComment comment = new RemoteComment();
                    comment.setAuthor(getUserName());
                    comment.setBody(task.getNewComment());
                    js.addComment(task.getId(), comment);
                    task.setNewComment(null);
                    RemoteIssue remoteIssue = js.getIssue(task.getId());

                    JiraRemoteTask remoteTask = JiraUtils.issueToTask(this, remoteIssue);
                    JiraUtils.remoteToTask(this, remoteTask, task);

                    //make cache up to date
                    cache(remoteTask);
                }
                task.setModifiedFlag(false);
                persist(task);
            }


            task.getExtension().fireStateChenged();
        }
    }

    public synchronized JiraSession getSession() throws JiraException {
        if (session == null) {
            reconnect();
        }
        return session;
    }

    TaskPersistenceHandler getTaskPersistenceHandler() {
        return handler;
    }

    public JiraQuerySupport getQuerySupport() {
        return querySupport;
    }

    public synchronized void reconnect() throws JiraException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(JiraTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            session = null;
            //try to reconnect
            session = new JiraSession(url, getUserName(), password,isIgnoreSSL());
        } finally {
            handle.finish();
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

    public boolean isIgnoreSSL() {
        return ignoreSSL;
    }

    public void setIgnoreSSL(boolean ignoreSSL) {
        this.ignoreSSL = ignoreSSL;
    }
}
