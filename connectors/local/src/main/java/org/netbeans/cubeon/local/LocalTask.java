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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.netbeans.cubeon.analyzer.spi.StackTraceProvider;
import org.netbeans.cubeon.common.ui.TaskTypeBadge;
import org.netbeans.cubeon.local.internals.TaskEditorProviderImpl;
import org.netbeans.cubeon.local.repository.*;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.util.ImageUtilities;
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
    private String urlString;
    private LocalTaskRepository taskRepository;
    private TaskPriority priority;
    private TaskStatus status;
    private TaskType type;
    private Date created;
    private Date updated;
    private final TaskEditorProvider editorProvider;
    private LocalTaskElementExtension extension;
    private final StackTraceProvider traceProvider;

    public LocalTask(String id, String name, String description,
            LocalTaskRepository taskRepository) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskRepository = taskRepository;
        extension = new LocalTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);
        priority = taskRepository.getLocalTaskPriorityProvider().getDefaultPriority();
        status = taskRepository.getLocalTaskStatusProvider().NEW;
        type = taskRepository.getLocalTaskTypeProvider().TASK;
        traceProvider = new StackTraceProvider() {

            @Override
            public List<String> getAnalyzableTexts() {
                return Arrays.asList(getDescription());
            }
        };
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
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

    public LocalTaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, editorProvider, extension, traceProvider);
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
        return taskRepository.getLocalTaskStatusProvider().COMPLETED.equals(getStatus());
    }

    public Image getImage() {
        Image image = TaskTypeBadge.getTaskImage();
        if (taskRepository.getLocalTaskTypeProvider().BUG.equals(getType())) {
            image = ImageUtilities.mergeImages(image, TaskTypeBadge.getBadge(TaskTypeBadge.DEFACT), 0, 0);
        } else if (taskRepository.getLocalTaskTypeProvider().ENHANCEMENT.equals(getType())) {
            image = ImageUtilities.mergeImages(image, TaskTypeBadge.getBadge(TaskTypeBadge.ENHANCEMENT), 0, 0);
        } else if (taskRepository.getLocalTaskTypeProvider().FEATURE.equals(getType())) {
            image = ImageUtilities.mergeImages(image, TaskTypeBadge.getBadge(TaskTypeBadge.FEATURE), 0, 0);
        } else {
            image = ImageUtilities.mergeImages(image, TaskTypeBadge.getBadge(TaskTypeBadge.TASK), 0, 0);
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

    public void synchronize() {
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.save(this);
        factory.refresh(this);
    }

    public Notifier<TaskElementChangeAdapter> getNotifier() {
        return extension;
    }
}
