/*
 *  Copyright 2008 Tomas Knappek.
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

package org.netbeans.cubeon.javanet.repository;

import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.kohsuke.jnt.JNIssue;
import org.kohsuke.jnt.JNIssueComponent;
import org.kohsuke.jnt.JNIssueTracker;
import org.kohsuke.jnt.JNProject;
import org.kohsuke.jnt.JavaNet;
import org.kohsuke.jnt.ProcessingException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.javanet.query.JavanetQuerySupport;
import org.netbeans.cubeon.javanet.tasks.JavanetTask;
import org.netbeans.cubeon.javanet.tasks.actions.Revertable;
import org.netbeans.cubeon.javanet.tasks.actions.Submitable;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.ui.query.QuerySupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTaskRepository implements TaskRepository, Submitable, Revertable {

    private JavaNet _javanet = null;
    private JavanetTaskRepositoryProvider _taskRepoProvider = null;
    private JNProject _jnProject = null;
    private JNIssueTracker _jnIssueTracker = null;
    private String _userName = null;
    private String _password = null;
    private String _projectName = null;
    private String _id = null;
    private State _state = State.ACTIVE;
    private JavanetTaskRepositoryNotifier _notifier = new JavanetTaskRepositoryNotifier(this);
    private QuerySupport _querySupport = null;
    Map<String, JNIssueComponent> _components;

     //locks
    private final Object SYNCHRONIZE_LOCK = new Object();

    private synchronized void connect() {
        if (_javanet != null) {
            //TODO: need to check the session is not expired? how?
            return;
        }
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(JavanetTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            _javanet = JavaNet.connect(_userName, _password);
            _jnProject = _javanet.getProject(_projectName);
            _jnIssueTracker = _jnProject.getIssueTracker();
        } catch (ProcessingException ex) {
            _state = State.INACTIVE;
            _notifier.fireStateChanged(_state);
            Exceptions.printStackTrace(ex);
        } finally {
            handle.finish();
        }        
        
    }

    public JavanetTaskRepository(JavanetTaskRepositoryProvider taskRepoProvider, String id,
            String projectName, String userName, String password) {        
        _taskRepoProvider = taskRepoProvider;
        _id = id;
        _projectName = projectName;
        _userName = userName;
        _password = password;
        _querySupport = new JavanetQuerySupport(this, _notifier);
    }

    public JavanetTaskRepository(JavanetTaskRepositoryProvider taskRepoProvider, JavaNet javaNet,
            JNProject jnProject, String userName, String password) {
        _id = jnProject.getName();
        _javanet = javaNet;
        _taskRepoProvider = taskRepoProvider;
        _jnProject = jnProject;
        _projectName = jnProject.getName();
        _userName = userName;
        _password = password;
        _querySupport = new JavanetQuerySupport(this, _notifier);
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _projectName;
    }

    public String getDescription() {
        String desc = getProjectName() + " repository";
        try {
            if (_jnProject != null) {
                desc = _jnProject.getSummary();
            }
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);            
        }
        return desc;
    }

    public Lookup getLookup() {
        //TODO: not complete
        return Lookups.fixed(this, _taskRepoProvider, _querySupport);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/javanet/javanet.png");
    }

    public TaskElement createTaskElement(String arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TaskElement getTaskElementById(String sid) {

        int id = Integer.parseInt(sid);
        try {
            JNIssue issue = _jnIssueTracker.get(id);
            return new JavanetTask(this, issue);
        } catch (ProcessingException ex) {
            throw new IllegalStateException(ex);
        }
        
    }

    public List<TaskElement> executeRemoteQuery(String name) {
        try {
            connect();
            Map<Integer, JNIssue> mIssues = _jnIssueTracker.getIssuesByQuery(name);
            List<TaskElement> lIssues = new LinkedList<TaskElement>();
            for (JNIssue issue: mIssues.values()) {
                lIssues.add(new JavanetTask(this, issue));
            }
            return lIssues;
        } catch (ProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }


    public void persist(TaskElement arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void synchronize() {
        connect();
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {                
                synchronized (SYNCHRONIZE_LOCK) {
//                    List<String> taskIds = handler.getTaskIds();
//                    if (taskIds.isEmpty()) {
//                        return;
//                    }
                    ProgressHandle handle = ProgressHandleFactory.createHandle(
                            NbBundle.getMessage(JavanetTaskRepository.class,
                            "LBL_Synchronizing_Tasks", getName()));
//                    handle.start(taskIds.size());
                    handle.start();
                    try {
//                        for (String id : taskIds) {
//                            TracTask tracTask = getTaskElementById(id);
//                            if (tracTask != null && !tracTask.isLocal()) {
//
//                                handle.progress(tracTask.getId() + " : " + tracTask.getName(), taskIds.indexOf(id));
//                                try {
//                                    update(tracTask);
//                                } catch (TracException ex) {
//                                    Logger.getLogger(TracTaskRepository.class.getName()).warning(ex.getMessage());
//                                }
//
//                            }
//
//                        }
                    } finally {
                        handle.finish();
                    }
                }
            }
        });
        
    }

    public State getState() {
        return _state;
    }

    public void setState(State state) {
        _state = state;
        _notifier.fireStateChanged(state);
    }

    public JavanetTaskRepositoryNotifier getNotifier() {
        return _notifier;
    }

    public String getUserName() {
        return _userName;
    }

    public String getPassword() {
        return _password;
    }

    public String getURL() {
        return (_jnProject != null) ? _jnProject.getURL().toString() : null;
    }

    public String getProjectName () {
        return _projectName;
    }

    void setRepositoryProvider(JavanetTaskRepositoryProvider provider) {
        _taskRepoProvider = provider;
    }

    private Map<String, JNIssueComponent> getComponentsMap() throws ProcessingException {
        if (_components == null) {
            _components = _jnIssueTracker.getComponents();
        }
        return _components;
    }

    public List<String> getComponents() {        
        try {
            return new ArrayList<String>(getComponentsMap().keySet());
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<String> getSubComponents(String component) {
        try {
            JNIssueComponent comp = getComponentsMap().get(component);
            return new LinkedList<String>(comp.getSubcomponents().keySet());
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<String> getVersions(String component) {
        try {
            JNIssueComponent comp = getComponentsMap().get(component);
            return comp.getVersions();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<String> getMilestones(String component) {
        try {
            JNIssueComponent comp = getComponentsMap().get(component);
            return comp.getTargetMilestones();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<String> getIssueType() {
        try {
            return _jnIssueTracker.getIssueTypes();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<String> getPriorities() {
        try {
            return _jnIssueTracker.getPriorities();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public List<String> getPlatforms() {
        try {
            return _jnIssueTracker.getPlatforms();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public List<String> getSystems() {
        try {
            return _jnIssueTracker.getOpSystems();
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }


    public void submit(JavanetTask task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(JavanetTask task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void revert(JavanetTask task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
