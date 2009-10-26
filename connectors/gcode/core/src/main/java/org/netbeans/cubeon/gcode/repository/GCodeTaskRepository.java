/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.repository;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.gcode.api.GCodeClient;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.api.GCodeSession;
import org.netbeans.cubeon.gcode.api.GCodeState;
import org.netbeans.cubeon.gcode.persistence.AttributesHandler;
import org.netbeans.cubeon.gcode.persistence.TaskPersistence;
import org.netbeans.cubeon.gcode.query.GCodeQuerySupport;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.gcode.utils.GCodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class GCodeTaskRepository implements TaskRepository {

    private final String id;
    private String name;
    private String description;
    private String project;
    private String user;
    private String password;
    private final GCodeTaskRepositoryProvider provider;
    private final Lookup lookup;
    private State state = State.INACTIVE;
    private final GCodeRepositoryExtension extension;
    private GCodeSession _session;
    private final AttributesHandler repositoryAttributes;
    private FileObject baseDir;
    private final GCodeTaskPriorityProvider priorityProvider;
    private final GCodeTaskTypeProvider typeProvider;
    private final GCodeTaskStatusProvider statusProvider;
    private final GCodeOfflineTaskSupport offlineTaskSupport;
    private final GCodeQuerySupport querySupport;
    private final TaskPersistence handler;
    private Map<String, GCodeTask> map = new HashMap<String, GCodeTask>();
    private final TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
    //locks
    private final Object SYNCHRONIZE_LOCK = new Object();
    public final Object SYNCHRONIZE_QUERY_LOCK = new Object();

    public GCodeTaskRepository(GCodeTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        extension = new GCodeRepositoryExtension(this);
        baseDir = provider.getBaseDir().getFileObject(id);
        if (baseDir == null) {
            try {
                baseDir = provider.getBaseDir().createFolder(id);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        repositoryAttributes = new AttributesHandler(
                new File(FileUtil.toFile(baseDir), "attributes.json"));

        priorityProvider = new GCodeTaskPriorityProvider();
        typeProvider = new GCodeTaskTypeProvider();
        statusProvider = new GCodeTaskStatusProvider();
        offlineTaskSupport = new GCodeOfflineTaskSupport(this);
        handler = new TaskPersistence(FileUtil.toFile(baseDir), this);
        querySupport = new GCodeQuerySupport(this, extension);
        lookup = Lookups.fixed(this, provider, extension, priorityProvider, typeProvider, statusProvider, offlineTaskSupport, querySupport);

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description != null ? description : name;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/gcode/gcode-repository.png");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(State state) {
        this.state = state;
        extension.fireStateChanged(state);
    }

    public TaskElement createTaskElement(String summary, String description) {
        String nextTaskId = handler.nextId();
        nextTaskId = "New-" + nextTaskId;//NOI18N
        GCodeTask codeTask = new GCodeTask(this, nextTaskId,
                summary, description);
        codeTask.setLocal(true);
        List<String> openStatueses = repositoryAttributes.getOpenStatueses();
        if (openStatueses.size() > 0) {
            codeTask.setStatus(openStatueses.get(0));
        }
        codeTask.setState(GCodeState.OPEN);
        codeTask.setLocal(true);

        return codeTask;
    }

    public GCodeTask getTaskElementById(String id) {
        GCodeTask get = map.get(id);
        if (get == null) {
            get = handler.getGCodeTask(id);
            if (get != null) {
                map.put(id, get);
            }
        }
        return get;
    }

    public void persist(TaskElement element) {
        GCodeTask tracTask = element.getLookup().lookup(GCodeTask.class);
        assert tracTask != null;
        handler.persist(tracTask);
        //notify to outgoing query about modified state
        if (tracTask.isModifiedFlag()) {
            querySupport.getOutgoingQuery().addTaskId(element.getId());
        } else {
            querySupport.getOutgoingQuery().removeTaskId(element.getId());
        }
    }

    public void revert(GCodeTask task) {
        synchronized (task) {
            task.setNewComment(null);
            GCodeTask cachedTask = handler.getCachedGCodeTask(task.getId());
            GCodeUtils.toCodeTask(task, cachedTask);
            task.setModifiedFlag(false);
            persist(task);
            task.getExtension().fireStateChenged();
        }
    }

    public void remove(GCodeTask tracTask) {
        handler.remove(tracTask);
        //issue-26 check if loacl as local task not on cache
        if (!tracTask.isLocal()) {
            handler.removeCache(tracTask);
        }
        //make sure to remove from outgoing resultset
        querySupport.getOutgoingQuery().removeTaskId(tracTask.getId());
        extension.fireTaskRemoved(tracTask);
    }

    public void cache(GCodeTask tracTask) {

        handler.persistCache(tracTask);
    }

    public void update(GCodeTask task) throws GCodeException {
        synchronized (task) {

            GCodeSession session = getSession();
            update(session, task);

        }

    }

    private void update(GCodeSession session, GCodeTask task) throws GCodeException {
        GCodeIssue issue = session.getIssue(task.getId());

        update(issue, task);
    }

    public void update(GCodeIssue issue, GCodeTask task) {
        synchronized (task) {
            //ignore if local task
            if (!task.isLocal()) {

                GCodeTask cachedTask = handler.getCachedGCodeTask(task.getId());
                //if remote ticket not modified ignore and log
                if (cachedTask != null && cachedTask.getUpdatedDate() == issue.getUpdatedDate()) {
                    Logger.getLogger(getClass().getName()).info("Up to date : " + issue.getId());//NOI18N

                } else {
                    //issue - 85 we need to save any changes before task marege
                    factory.save(task);
                    //marege changes with remote ticket
                    GCodeUtils.maregeToTask(this, issue, cachedTask, task);

                    persist(task);

                    //make cache up to date
                    cache(GCodeUtils.toCodeTask(this, issue));
                    //refresh task if open in editor
                    factory.refresh(task);
                    //notify task has changed
                    task.getExtension().fireStateChenged();
                }
            }
        }

    }

    public State getState() {
        return state;
    }

    public GCodeRepositoryExtension getNotifier() {
        return extension;
    }

    public List<String> getTaskIds() {
        return handler.getTaskIds();
    }

    public FileObject getBaseDir() {
        return baseDir;
    }

    public synchronized GCodeSession getSession() throws GCodeException {
        if (_session == null) {
            reconnect();
        }
        return _session;
    }

    public synchronized void reconnect() throws GCodeException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(GCodeTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            _session = null;
            //try to reconnect
            _session = Lookup.getDefault().lookup(GCodeClient.class).
                    createSession(project, user, password);
        } finally {
            handle.finish();
        }
    }

    public void updateAttributes() {

        setState(State.SYNCHRONIZING);
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(GCodeTaskRepository.class,
                "LBL_Updating_Attributes", getName()));
        //TODO : http://code.google.com/p/support/issues/detail?id=3203
        handle.start();
        handle.switchToIndeterminate();
        repositoryAttributes.loadDefultAttributes();
        repositoryAttributes.persistAttributes();
        loadAttributes();
        handle.finish();
        setState(State.ACTIVE);

    }

    public void loadAttributes() {
        repositoryAttributes.loadAttributes();
        repositoryAttributes.loadProviders(this);
        querySupport.refresh();
        setState(State.ACTIVE);
    }

    public AttributesHandler getRepositoryAttributes() {
        return repositoryAttributes;
    }

    public GCodeTaskPriorityProvider getPriorityProvider() {
        return priorityProvider;
    }

    public GCodeTaskStatusProvider getStatusProvider() {
        return statusProvider;
    }

    public GCodeTaskTypeProvider getTypeProvider() {
        return typeProvider;
    }

    public GCodeRepositoryExtension getExtension() {
        return extension;
    }

    public void submit(GCodeTask task) throws GCodeException {
        throw new UnsupportedOperationException("Not yet implemented");
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
                            NbBundle.getMessage(GCodeTaskRepository.class,
                            "LBL_Synchronizing_Tasks", getName()));
                    handle.start(taskIds.size());
                    try {
                        GCodeSession session = getSession();
                        if (session != null) {
                            for (String id : taskIds) {
                                GCodeTask tracTask = getTaskElementById(id);
                                if (tracTask != null && !tracTask.isLocal()) {

                                    handle.progress(tracTask.getId() + " : " + tracTask.getName(), taskIds.indexOf(id));

                                    update(session, tracTask);


                                }

                            }
                        }
                    } catch (GCodeException ex) {
                        Logger.getLogger(GCodeTaskRepository.class.getName()).warning(ex.getMessage());
                    } finally {
                        handle.finish();
                    }


                }

            }
        });
    }

    public GCodeQuerySupport getQuerySupport() {
        return querySupport;
    }
}
