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

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.netbeans.cubeon.local.internals.TaskEditorProviderImpl;
import org.netbeans.cubeon.local.repository.*;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskType;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTask implements TaskElement {

    private final String id;
    private String name;
    private String description;
    private String urlString;
    private LocalTaskRepository taskRepository;
    private TaskPriority priority;
    private TaskStatus status = LocalTaskStatusProvider.NEW;
    private TaskType type = LocalTaskTypeProvider.TASK;
    private Date created;
    private Date updated;
    private final TaskEditorProvider editorProvider;
    private LocalTaskElementExtension extension;

    public LocalTask(String id, String name, String description,
            LocalTaskRepository taskRepository) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskRepository = taskRepository;
        extension = new LocalTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);
        priority = taskRepository.getLocalTaskPriorityProvider().getDefaultPriority();
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        extension.fireNameChenged();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        extension.fireDescriptionChenged();
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, editorProvider, extension);
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        extension.firePriorityChenged();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        extension.fireStatusChenged();
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
        extension.fireTypeChenged();
    }

    public boolean isCompleted() {
        return LocalTaskStatusProvider.COMPLETED.equals(getStatus());
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        if (LocalTaskTypeProvider.BUG.equals(getType())) {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_defact.png"), 0, 0);
        } else if (LocalTaskTypeProvider.ENHANCEMENT.equals(getType())) {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_enhancement.png"), 0, 0);
        } else if (LocalTaskTypeProvider.FEATURE.equals(getType())) {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_feature.png"), 0, 0);
        } else {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_task.png"), 0, 0);
        }

        return image;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public URL getUrl() {
        if (urlString != null) {
            try {
                return new URL(urlString);
            } catch (MalformedURLException ex) {
                //ignore
            }

        }
        return null;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
