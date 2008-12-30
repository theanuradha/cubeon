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
package org.netbeans.cubeon.bugzilla.core.repository;

import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.bugzilla.api.BugzillaClient;
import org.netbeans.cubeon.bugzilla.api.MixedModeBugzillaClientImpl;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Map;
import java.util.HashMap;
import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Main class of Cubeon Bugzilla task repository.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTaskRepository implements TaskRepository {

    /**
     * Task repository provider.
     */
    BugzillaTaskRepositoryProvider repositoryProvider;
    /**
     * Bugzilla tasks file manager, it provides tasks file managment logic
     * eg. persist, load, update.
     */
    private BugzillaTasksFileManager tasksFileManager;
    /**
     * Current repository instance ID.
     */
    private String id;
    /**
     * Current repository instance description.
     */
    private String description;
    /**
     * Current repository name.
     */
    private String name;
    /**
     * Actual repository state.
     */
    private State state;
    /**
     * Repository user name.
     */
    private String username;
    /**
     * Repository password.
     */
    private String password;
    /**
     * Repository URL.
     */
    private String url;
    /**
     * Last local task id.
     */
    private int lastLocalTaskNumber;
    /**
     * Bugzilla repository client, it is responsible for retrieving repository data and publishing tasks.
     */
    private BugzillaClient client;
    /**
     * Contains repository attributes.
     */
    private BugzillaRepositoryAttributes repositoryAttributes;
    /**
     * Synchronziation lock object, used during synchronization.
     */
    private final Object SYNCHRONIZATION_LOCK = new Object();
    /**
     * Map of Bugzilla tasks.
     */
    private Map<String, BugzillaTask> bugzillaTasks = new HashMap<String, BugzillaTask>();

    /**
     * Default constructor.
     */
    public BugzillaTaskRepository() {
    }

    /**
     * Four-parameters constructor, it will be used to create instance of BugzillaTaskRepository.
     *
     * @param provider    - task repository provider, it provides basic logic which will be used to manage repositories
     * @param id          - repository ID
     * @param description - repository description
     * @param name        - repository name
     * @param url         - repository URL
     */
    public BugzillaTaskRepository(BugzillaTaskRepositoryProvider provider, String id, String description, String name, String url) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.url = url;
        this.tasksFileManager = createTasksFileManager(provider.getBaseDir());
        this.repositoryProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/bugzilla/repository/bugzilla-repository.png");
    }

    /**
     * {@inheritDoc}
     */
    public TaskElement createTaskElement(String summary, String description) {
        BugzillaTask task = new BugzillaTask();
        BugSummary bugSummary = new BugSummary();
        bugSummary.setSummary(summary);
        bugSummary.setDescription(description);
        //TODO check attributes
        String localTaskId = getNextAvailableLocalTaskId();
        task.setLocalId(localTaskId);
        task.setBugSummary(bugSummary);
        return task;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized TaskElement getTaskElementById(String id) {
        BugzillaTask task = bugzillaTasks.get(id);
        if (task == null) {
            try {
                BugDetails bugDetails = client.getBugDetails(new Integer(id));
                task = new BugzillaTask(bugDetails.getBugSummary(), this);
                bugzillaTasks.put(id, task);
            } catch (BugzillaException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return task;
    }

    /**
     * {@inheritDoc}
     */
    public void persist(TaskElement element) {
        try {
            BugzillaTask task = element.getLookup().lookup(BugzillaTask.class);
            tasksFileManager.persistTask(task);
            bugzillaTasks.put(task.getId(), task);
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void synchronize() {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                synchronized (SYNCHRONIZATION_LOCK) {
                    //todo implement this
                }
            }
        });
    }

    public void updateRepositoryAttributes() {
        try {
            RepositoryAttributes attributes = client.getRepositoryAttributes();
        } catch (BugzillaException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public State getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public Notifier<RepositoryEventAdapter> getNotifier() {
        return null;  //todo implement this
    }

    public String getUrl() {
        return url;
    }

    /**
     * Returns actual Bugzilla client instance.
     *
     * @return - Bugzilla client
     * @throws BugzillaException -throws exception incase of any errors during connection
     */
    public synchronized BugzillaClient getClient() throws BugzillaException {
        if (client == null) {
            reconnect();
        }
        return client;
    }

    /**
     * Reconects to Bugzilla repository.
     *
     * @throws BugzillaException - throws exception in case of any problems during connection
     */
    public synchronized void reconnect() throws BugzillaException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(BugzillaTaskRepository.class, "LBL_Connecting", getName()));
        handle.start();
        handle.switchToIndeterminate();
        try {
            //making client NULL
            client = null;
            //creating new instance of client
            client = new MixedModeBugzillaClientImpl(getUrl(), username, password);
        } finally {
            handle.finish();
        }
    }

    /**
     * Returns synchronized bug summary.
     *
     * @param id - bug id
     * @return - synchronized bug summary
     */
    public BugSummary getSynchronizedTask(final String id) {
        BugSummary bugSummary = null;
        try {
            BugDetails bugDetails = client.getBugDetails(new Integer(id));
            bugSummary = bugDetails.getBugSummary();
        } catch (BugzillaException e) {
            //ignore
        }
        return bugSummary;
    }

    /**
     * Returns URL for bug with given id.
     *
     * @param id - bug id
     * @return - URL address
     */
    public String getBugUrl(String id) {
        return client.getBugUrl(id);
    }

    /**
     * Creates tasks file manager for this Bugzilla repository.
     * @param repositoryConfigurationDir - repository configuration directory
     * @return - tasks file manager
     */
    private BugzillaTasksFileManager createTasksFileManager(FileObject baseConfigDir) {

        BugzillaTasksFileManager fileManager = new BugzillaTasksFileManagerImpl(baseConfigDir);
        return fileManager;
    }

    /**
     * Returns next available id for local task in this Bugzilla repository.
     *
     * @return - next available local task id
     */
    private String getNextAvailableLocalTaskId() {
        return MessageFormat.format("NEW_{0}_{1}", id, lastLocalTaskNumber);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLastLocalTaskNumber(int lastLocalTaskNumber) {
        this.lastLocalTaskNumber = lastLocalTaskNumber;
    }
}
