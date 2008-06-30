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
package org.netbeans.cubeon.jira.tasks;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.netbeans.cubeon.jira.repository.JiraTaskPriorityProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskStatusProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class JiraTask implements TaskElement {

    private final String id;
    private String name;
    private String description;
    private String urlString;
    private JiraTaskRepository taskRepository;
    private TaskPriority priority = JiraTaskPriorityProvider.MAJOR;
    private TaskStatus status = JiraTaskStatusProvider.OPEN;
    private TaskType type = JiraTaskTypeProvider.BUG;
    private Date created;
    private Date updated;
    private TaskEditorProvider editorProvider;
    private JiraTaskElementExtension extension;
    private boolean local;

    public JiraTask(String id, String name, String description,
            JiraTaskRepository taskRepository) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskRepository = taskRepository;
        extension = new JiraTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);

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
        return JiraTaskStatusProvider.CLOSED.equals(getStatus());
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        if (JiraTaskTypeProvider.BUG.equals(getType())) {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_defact.png"), 0, 0);
        } else if (JiraTaskTypeProvider.IMPROVEMENT.equals(getType())) {
            image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_enhancement.png"), 0, 0);
        } else if (JiraTaskTypeProvider.FEATURE.equals(getType())) {
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

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JiraTask other = (JiraTask) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.taskRepository != other.taskRepository && (this.taskRepository == null || !this.taskRepository.equals(other.taskRepository))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.taskRepository != null ? this.taskRepository.hashCode() : 0);
        return hash;
    }

    
}
