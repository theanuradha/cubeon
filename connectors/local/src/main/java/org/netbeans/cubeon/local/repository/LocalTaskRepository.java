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
package org.netbeans.cubeon.local.repository;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.query.LocalQuerySupport;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.util.Lookup;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskRepository implements TaskRepository {

    private final LocalTaskRepositoryProvider provider;
    private final String id;
    private String name;
    private String description;
    private List<LocalTask> localTasks = new ArrayList<LocalTask>();
    private final PersistenceHandler persistenceHandler;
    private final LocalTaskPriorityProvider ltpp;
    private final LocalTaskStatusProvider ltsp;
    private final LocalTaskTypeProvider lttp;
    private LocalRepositoryExtension extension;
    private LocalQuerySupport querySupport;
    private final Lookup lookup;

    public LocalTaskRepository(LocalTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        lttp = new LocalTaskTypeProvider(this);
        ltsp = new LocalTaskStatusProvider(this);
        ltpp = new LocalTaskPriorityProvider(this);
        extension = new LocalRepositoryExtension(this);
        persistenceHandler = new PersistenceHandler(this, provider.getBaseDir());
        querySupport = new LocalQuerySupport(this, extension);

        lookup = Lookups.fixed(this, extension, provider, persistenceHandler, ltpp, ltsp, lttp, querySupport);
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

    public List<TaskElement> getTaskElements() {
        return new ArrayList<TaskElement>(localTasks);
    }

    public List<LocalTask> getLocalTasks() {
        return localTasks;
    }

    public TaskElement getTaskElementById(String id) {
        List<TaskElement> taskElements = getTaskElements();
        for (TaskElement taskElement : taskElements) {
            if (taskElement.getId().equals(id)) {
                return taskElement;
            }
        }

        return null;
    }

    void refresh() {
        persistenceHandler.refresh();

    }

    public TaskElement createTaskElement(String summery, String description) {
        LocalTask localTask = new LocalTask(persistenceHandler.nextTaskId(),
                summery, description, this);

        return localTask;
    }

    void setTaskElements(List<LocalTask> taskElements) {
        localTasks.clear();
        localTasks.addAll(taskElements);
    }

    public void persist(TaskElement element) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        persistenceHandler.addTaskElement(localTask);
        if (getTaskElementById(element.getId()) == null) {
            localTasks.add(localTask);
        }
    }

    public void deleteTask(TaskElement element) {
        LocalTask localTask = element.getLookup().lookup(LocalTask.class);
        assert localTask != null;
        persistenceHandler.removeTaskElement(localTask);

        localTasks.remove(localTask);
        extension.fireTaskRemoved(element);
    }

    public void synchronize() {

        List<TaskQuery> taskQuerys = querySupport.getTaskQuerys();
        for (TaskQuery taskQuery : taskQuerys) {
            taskQuery.synchronize();
        }
    }

    public LocalTaskPriorityProvider getLocalTaskPriorityProvider() {
        return ltpp;
    }

    public LocalTaskStatusProvider getLocalTaskStatusProvider() {
        return ltsp;
    }

    public LocalTaskTypeProvider getLocalTaskTypeProvider() {
        return lttp;
    }

    public void delete() {
        persistenceHandler.delete();
    }

    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/local/nodes/local-repository.png");
    }

    public LocalTaskRepositoryProvider getProvider() {
        return provider;
    }

    public LocalRepositoryExtension getExtension() {
        return extension;
    }

    public LocalQuerySupport getQuerySupport() {
        return querySupport;
    }

    public State getState() {
        return State.ACTIVE;//default
    }

    public Notifier<RepositoryEventAdapter> getNotifier() {
        return extension;
    }
}
