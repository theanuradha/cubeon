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
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TracClient;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.api.TracSession;
import org.netbeans.cubeon.trac.query.TracQuerySupport;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TracTaskRepository implements TaskRepository {

    private final TracTaskRepositoryProvider provider;
    private final TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
    private final String id;
    private String name;
    private String description;
    //Url of the Repository
    private String url;
    //----------------------------
    private String userName;
    private String password;
    private boolean  ignoreSSL;
    private FileObject baseDir;
    private final TracRepositoryExtension extension;
    private State state = State.INACTIVE;
    private final TracRepositoryAttributes repositoryAttributes;
    private volatile TracSession _session;
    private final TracTaskPriorityProvider priorityProvider;
    private final TracTaskTypeProvider typeProvider;
    private final TracTaskStatusProvider statusProvider;
    private final TracTaskSeverityProvider severityProvider;
    private final TracTaskResolutionProvider resolutionProvider;
    private final TaskPersistenceHandler handler;
    private final TaskPersistenceHandler cache;
    private final TracQuerySupport querySupport;
    private final TracOfflineTaskSupport offlineTaskSupport;
    private Map<String, TracTask> map = new HashMap<String, TracTask>();
    //locks
    private final Object SYNCHRONIZE_LOCK = new Object();
    public final Object SYNCHRONIZE_QUERY_LOCK = new Object();
    private final Lookup lookup;

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
        offlineTaskSupport = new TracOfflineTaskSupport(this);
        handler = new TaskPersistenceHandler(this, baseDir, "tasks");//NOI18N
        cache = new TaskPersistenceHandler(this, baseDir, "cache");//NOI18N
        querySupport = new TracQuerySupport(this, extension);
        lookup = Lookups.fixed(this, extension, provider, priorityProvider, statusProvider, resolutionProvider, typeProvider, severityProvider, querySupport, offlineTaskSupport);
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
        return lookup;
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/trac/trac-repository.png");
    }

    public TaskElement createTaskElement(String summary, String description) {
        String nextTaskId = handler.nextTaskId();
        nextTaskId = "New-" + nextTaskId;//NOI18N
        TracTask tracTask = new TracTask(this, nextTaskId, -1/*for localy new tickets*/,
                summary, description);
        tracTask.setLocal(true);
        tracTask.put(TracKeys.PRIORITY,
                repositoryAttributes.getTicketFiledByName(TracKeys.PRIORITY).getValue());
        tracTask.put(TracKeys.TYPE,
                repositoryAttributes.getTicketFiledByName(TracKeys.TYPE).getValue());

        return tracTask;
    }

    public synchronized TracTask getTaskElementById(String id) {
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
        //notify to outgoing query about modified state
        if (tracTask.isModifiedFlag()) {
            querySupport.getOutgoingQuery().addTaskId(element.getId());
        } else {
            querySupport.getOutgoingQuery().removeTaskId(element.getId());
        }
    }

    public void revert(TracTask task) {
        synchronized (task) {
            task.setAction(null);
            task.setNewComment(null);
            TracTask cachedTask = cache.getTaskElementById(task.getId());
            task.putAll(cachedTask.getAttributes());
            //TracUtils.remoteToTask(this, getTracRemoteTaskCache(task.getId()), task);
            task.setModifiedFlag(false);
            persist(task);
            task.getExtension().fireStateChenged();
        }
    }

    public void remove(TracTask tracTask) {
        handler.removeTaskElement(tracTask);
        //issue-26 check if loacl as local task not on cache
        if (!tracTask.isLocal()) {
            cache.removeTaskElement(tracTask);
        }
        //make sure to remove from outgoing resultset
        querySupport.getOutgoingQuery().removeTaskId(tracTask.getId());
        extension.fireTaskRemoved(tracTask);
    }

    public void cache(TracTask tracTask) {

        cache.persist(tracTask);
    }

    public void update(TracTask task) throws TracException {
        synchronized (task) {

            TracSession session = getSession();
            update(session, task);

        }

    }

    private void update(TracSession session, TracTask task) throws TracException {
        Ticket issue = session.getTicket(task.getTicketId());

        update(issue, task);
    }

    public void update(Ticket issue, TracTask task) throws TracException {
        synchronized (task) {
            //ignore if local task
            if (!task.isLocal()) {

                TracTask cachedTask = cache.getTaskElementById(task.getId());
                //if remote ticket not modified ignore and log 
                if (cachedTask != null && cachedTask.getUpdatedDate() == issue.getUpdatedDate()) {
                    Logger.getLogger(getClass().getName()).info("Up to date : " + issue.getTicketId());//NOI18N

                } else {
                    //issue - 85 we need to save any changes before task marege
                    factory.save(task);
                    //marege changes with remote ticket
                    TracUtils.maregeToTask(this, issue, cachedTask, task);
                    //read actions
                    List<TicketAction> ticketActions = getSession().getTicketActions(task.getTicketId());
                    task.setActions(ticketActions);
                    persist(task);

                    //make cache up to date
                    cache(TracUtils.issueToTask(this, issue));
                    //refresh task if open in editor
                    factory.refresh(task);
                    //notify task has changed
                    task.getExtension().fireStateChenged();
                }
            }
        }

    }

    public void submit(TracTask task) throws TracException {
        synchronized (task) {
            //if task is local create ticket on server
            if (task.isLocal()) {
                TracUtils.createTicket(this, task);
            } else {
                /*sbmit changes*/
                TracSession session = getSession();
                String comment = task.getNewComment();
                //if comment null set default updated comment
                if (comment == null || comment.trim().length() == 0) {
                    comment = NbBundle.getMessage(TracTaskRepository.class,
                            "LBL_New_Comment", getUserName());
                }


                Ticket ticket = TracUtils.taskToTicket(this, task);
                Ticket updateTicket;
                TicketAction action = task.getAction();
                if (action != null) {
                    updateTicket = session.executeAction(action, comment, ticket, true);

                } else {
                    updateTicket = session.updateTicket(comment, ticket, true);
                }

                TracTask remoteTask = TracUtils.issueToTask(this, updateTicket);
                TracUtils.remoteToTask(this, remoteTask, task);
                //make cache up to date
                cache(remoteTask);
                //persist task changs by server
                task.setModifiedFlag(false);
                //Crear New Comment field 
                task.setNewComment("");
                List<TicketAction> ticketActions = session.getTicketActions(task.getTicketId());
                task.setActions(ticketActions);
                persist(task);
            }
            //you have to notify all
            task.getExtension().fireNameChenged();
            task.getExtension().fireDescriptionChenged();
            task.getExtension().firePriorityChenged();
            task.getExtension().fireResolutionChenged();
            task.getExtension().fireStatusChenged();
            task.getExtension().fireTypeChenged();
            task.getExtension().fireStateChenged();

        }
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
                            NbBundle.getMessage(TracTaskRepository.class,
                            "LBL_Synchronizing_Tasks", getName()));
                    handle.start(taskIds.size());
                    try {
                        TracSession session = getSession();
                        if (session != null) {
                            for (String id : taskIds) {
                                TracTask tracTask = getTaskElementById(id);
                                if (tracTask != null && !tracTask.isLocal()) {

                                    handle.progress(tracTask.getId() + " : " + tracTask.getName(), taskIds.indexOf(id));

                                    update(session, tracTask);


                                }

                            }
                        }
                    } catch (TracException ex) {
                        Logger.getLogger(TracTaskRepository.class.getName()).warning(ex.getMessage());
                    } finally {
                        handle.finish();
                    }


                }

            }
        });
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

    public FileObject getBaseDir() {
        return baseDir;
    }

    void loadAttributes() {
        repositoryAttributes.loadAttributes();
        querySupport.refresh();
        setState(State.ACTIVE);
    }

    public TracRepositoryExtension getNotifier() {
        return extension;
    }

    public synchronized TracSession getSession() throws TracException {
        if (_session == null) {
            reconnect();
        }
        return _session;
    }

    public synchronized void reconnect() throws TracException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(TracTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            _session = null;
            //try to reconnect
            _session = Lookup.getDefault().lookup(TracClient.class).
                    createTracSession(getURL(), getUserName(), getPassword(),isIgnoreSSL());
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

    public TracRepositoryAttributes getRepositoryAttributes() {
        return repositoryAttributes;
    }

    public TracQuerySupport getQuerySupport() {
        return querySupport;
    }

    public boolean isIgnoreSSL() {
        return ignoreSSL;
    }

    public void setIgnoreSSL(boolean ignoreSSL) {
        this.ignoreSSL = ignoreSSL;
    }
    
}
