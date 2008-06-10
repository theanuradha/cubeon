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
package org.netbeans.cubeon.local;

import org.netbeans.cubeon.local.internals.TaskEditorProviderImpl;
import org.netbeans.cubeon.local.nodes.LocalTaskNode;
import org.netbeans.cubeon.local.repository.*;
import org.netbeans.cubeon.local.ui.BasicAttributeHandlerImpl;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider.BasicAttributeHandler;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskPriority;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTask implements TaskElement {

    private final String id;
    private String name;
    private String description;
    private LocalTaskRepository taskRepository;
    private LocalTaskNode node;
    private TaskPriority priority = LocalTaskPriorityProvider.P3;//default priority  is p3
    private TaskStatus status = LocalTaskStatusProvider.INCOMPLETE;
    private final TaskEditorProvider editorProvider;

    public LocalTask(String id, String name, String description,
            LocalTaskRepository taskRepository) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskRepository = taskRepository;
        editorProvider = new TaskEditorProviderImpl(this);
        node = new LocalTaskNode(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        node.setDisplayName(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        node.setShortDescription(description);
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, node, editorProvider);
    }

    public void open() {
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.createTaskEditor(this);
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        node.refreshIcon();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        node.refeshDisplayName();
    }
}
