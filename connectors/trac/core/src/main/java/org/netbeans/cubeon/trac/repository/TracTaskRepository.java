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

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.TracClient;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.api.TracSession;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TracTaskRepository implements TaskRepository {

    private final TracTaskRepositoryProvider provider;
    private final String id;
    private String name;
    private String description;
    //Url of the Repository
    private String url;

    //----------------------------
    private String userName;
    private String password;
    private FileObject baseDir;
    private final TracRepositoryExtension extension;
    private State state = State.INACTIVE;
    private final TracRepositoryAttributes repositoryAttributes;
    private volatile TracSession session;
    private final TracTaskPriorityProvider priorityProvider;
    private final TracTaskTypeProvider typeProvider;
    private final TracTaskStatusProvider statusProvider;
    private final TracTaskSeverityProvider severityProvider;
    private final TracTaskResolutionProvider resolutionProvider;
    private final TaskPersistenceHandler handler;
    private final TaskPersistenceHandler cache;
    private Map<String, TracTask> map = new HashMap<String, TracTask>();

    public TracTaskRepository(TracTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        extension = new TracRepositoryExtension(this);
        baseDir = provider.getBaseDir().getFileObject(id);
        if (baseDir == null) {
            try {
                baseDir = provider.getBaseDir().createFolder(id);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        priorityProvider = new TracTaskPriorityProvider();
        typeProvider = new TracTaskTypeProvider();
        statusProvider = new TracTaskStatusProvider();
        severityProvider = new TracTaskSeverityProvider();
        resolutionProvider = new TracTaskResolutionProvider();
        repositoryAttributes = new TracRepositoryAttributes(this);
        handler = new TaskPersistenceHandler(this, baseDir, "tasks");//NOI18N
        cache = new TaskPersistenceHandler(this, baseDir, "cache");//NOI18N
    // querySupport = new JiraQuerySupport(this, extension);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return name;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension, provider, priorityProvider,
                statusProvider, resolutionProvider, typeProvider, severityProvider);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/trac/trac-repository.png");
    }

    public TaskElement createTaskElement(String summery, String description) {
        String nextTaskId = handler.nextTaskId();
        nextTaskId = "New-" + nextTaskId;//NOI18N
        TracTask tracTask = new TracTask(this, nextTaskId, -1/*for localy new tickets*/,
                summery, description);
        tracTask.setLocal(true);
        tracTask.put(TracKeys.COMPONENT,
                repositoryAttributes.getTicketFiledByName(TracKeys.COMPONENT).getValue());
        tracTask.put(TracKeys.PRIORITY,
                repositoryAttributes.getTicketFiledByName(TracKeys.PRIORITY).getValue());
        tracTask.put(TracKeys.TYPE,
                repositoryAttributes.getTicketFiledByName(TracKeys.TYPE).getValue());

        return tracTask;
    }

    public synchronized TaskElement getTaskElementById(String id) {
        TracTask get = map.get(id);
        if (get == null) {

            get = handler.getTaskElementById(id);
            if (get != null) {
                map.put(id, get);
            }
        }
        return get;
    }

    public void persist(TaskElement element) {
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        handler.persist(tracTask);
    //notify to outgoing query about modified state FIXME
    }

    public void revert(TracTask task) {
        synchronized (task) {
            task.setAction(null);
            task.setNewComment(null);
            TracTask cachedTask = cache.getTaskElementById(task.getId());
            task.putAll(cachedTask.getAttributes());
            //JiraUtils.remoteToTask(this, getJiraRemoteTaskCache(task.getId()), task);
            task.setModifiedFlag(false);
            persist(task);
            task.getExtension().fireStateChenged();
        }
    }

    public void remove(TracTask tracTask) {
        handler.removeTaskElement(tracTask);
    }

    public void cache(TracTask tracTask) {

        cache.persist(tracTask);
    }

    public void synchronize() {
    }

    public State getState() {
        return state;//default
    }

    public List<String> getTaskIds() {
        return handler.getTaskIds();
    }

    public void setState(State state) {
        this.state = state;
        extension.fireStateChanged(state);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    FileObject getBaseDir() {
        return baseDir;
    }

    void loadAttributes() {
        repositoryAttributes.loadAttributes();

        setState(State.ACTIVE);
    }

    public TracRepositoryExtension getExtension() {
        return extension;
    }

    public synchronized TracSession getSession() throws TracException {
        if (session == null) {
            reconnect();
        }
        return session;
    }

    public synchronized void reconnect() throws TracException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(TracTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            session = null;
            //try to reconnect
            session = Lookup.getDefault().lookup(TracClient.class).
                    createTracSession(getURL(), getUserName(), getPassword());
        } finally {
            handle.finish();
        }
    }

    public void updateAttributes() {
        try {
            setState(State.SYNCHRONIZING);
            ProgressHandle handle = ProgressHandleFactory.createHandle(
                    NbBundle.getMessage(TracTaskRepository.class,
                    "LBL_Updating_Attributes", getName()));
            repositoryAttributes.refresh(handle);
            loadAttributes();
            handle.finish();
        } catch (TracException ex) {
            Logger.getLogger(TracAttributesPersistence.class.getName()).
                    log(Level.WARNING, ex.getMessage());
            setState(State.ACTIVE);
        }
    }

    public TracTaskPriorityProvider getPriorityProvider() {
        return priorityProvider;
    }

    public TracTaskResolutionProvider getResolutionProvider() {
        return resolutionProvider;
    }

    public TracTaskStatusProvider getStatusProvider() {
        return statusProvider;
    }

    public TracTaskTypeProvider getTypeProvider() {
        return typeProvider;
    }

    public TracTaskSeverityProvider getSeverityProvider() {
        return severityProvider;
    }
}
