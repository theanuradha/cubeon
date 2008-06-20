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
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
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
    private final LocalTaskPriorityProvider ltpp = new LocalTaskPriorityProvider();
    private final LocalTaskStatusProvider ltsp = new LocalTaskStatusProvider();
    private final LocalTaskTypeProvider lttp = new LocalTaskTypeProvider();
    private LocalRepositoryExtension extension;
    private LocalQuerySupport querySupport;


    public LocalTaskRepository(LocalTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;

        extension = new LocalRepositoryExtension(this);
        persistenceHandler = new PersistenceHandler(this, provider.getBaseDir());
        querySupport = new LocalQuerySupport(this,extension);
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

    public Lookup getLookup() {
        return Lookups.fixed(this,
                extension, provider, persistenceHandler, ltpp, ltsp, lttp, querySupport);
    }

    public List<TaskElement> getTaskElements() {
        return new ArrayList<TaskElement>(localTasks);
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

    public TaskElement createTaskElement() {
        LocalTask localTask = new LocalTask(persistenceHandler.nextTaskId(),
                "New Task", "", this);

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
        localTasks.add(localTask);
    }

    public void reset(TaskElement element) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void validate(TaskElement element) {
        persistenceHandler.vaidate(element);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/local/nodes/local-repository.png");
    }

    public LocalTaskRepositoryProvider getProvider() {
        return provider;
    }
}
