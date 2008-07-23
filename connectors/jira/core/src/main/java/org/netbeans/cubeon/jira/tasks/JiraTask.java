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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.attributes.JiraComment;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.repository.ui.JiraActionsProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class JiraTask implements TaskElement {

    private String id;
    private String name;
    private String description;
    private String urlString;
    private JiraTaskRepository taskRepository;
    private TaskPriority priority;
    private TaskStatus status;
    private TaskType type;
    private TaskResolution resolution;
    private Date created;
    private Date updated;
    private boolean modifiedFlag;
    private TaskEditorProvider editorProvider;
    private JiraTaskElementExtension extension;
    private boolean local;
    private JiraProject project;
    private String environment;
    private String reporter;
    private String assignee;
    private List<JiraProject.Component> components = new ArrayList<JiraProject.Component>(0);
    private List<JiraProject.Version> affectedVersions = new ArrayList<JiraProject.Version>(0);
    private List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>(0);
    private List<JiraComment> comments = new ArrayList<JiraComment>();
    private List<String> editFieldIds = new ArrayList<String>();
    private JiraActionsProvider actionsProvider = new JiraActionsProvider();
    private JiraAction action;
    private String newComment;

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

    public void setId(String id) {
        this.id = id;
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

    public TaskResolution getResolution() {
        return resolution;
    }

    public void setResolution(TaskResolution resolution) {
        this.resolution = resolution;
        extension.fireResolutionChenged();
    }

    public JiraProject getProject() {
        return project;
    }

    public void setProject(JiraProject project) {
        this.project = project;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isCompleted() {
        return resolution != null;
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        //FIXME
        List<TaskType> taskTypes = taskRepository.getJiraTaskTypeProvider().getTaskTypes();

        int indexOf = taskTypes.indexOf(getType());

        switch (indexOf) {
            case 0:
                image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_defact.png"), 0, 0);
                break;
            case 1:
                image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_enhancement.png"), 0, 0);
                break;
            case 2:
                image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_feature.png"), 0, 0);
                break;
            case 3:
                image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_task.png"), 0, 0);
                break;

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

    public List<Version> getAffectedVersions() {
        return new ArrayList<Version>(affectedVersions);
    }

    public void setAffectedVersions(List<Version> affectedVersions) {
        this.affectedVersions = new ArrayList<Version>(affectedVersions);
    }

    public List<Component> getComponents() {
        return new ArrayList<Component>(components);
    }

    public void setComponents(List<Component> components) {
        this.components = new ArrayList<Component>(components);
    }

    public List<Version> getFixVersions() {
        return new ArrayList<Version>(fixVersions);
    }

    public void setFixVersions(List<Version> fixVersions) {
        this.fixVersions = new ArrayList<Version>(fixVersions);

    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public JiraAction getAction() {
        return action;
    }

    public void setAction(JiraAction action) {
        this.action = action;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public List<JiraAction> getActions() {
        return actionsProvider.getActions();
    }

    public void setActions(List<JiraAction> actions) {
        actionsProvider.setActions(actions);
    }

    public List<JiraComment> getComments() {
        return new ArrayList<JiraComment>(comments);
    }

    public void setComments(List<JiraComment> comments) {
        this.comments = new ArrayList<JiraComment>(comments);
    }

    public JiraActionsProvider getActionsProvider() {
        return actionsProvider;
    }

    public boolean removeEditFieldId(String id) {
        return editFieldIds.remove(id);
    }

    public void setEditFieldIds(List<String> editFieldIds) {
        this.editFieldIds = new ArrayList<String>(editFieldIds);
    }

    public boolean addEditFieldId(String ids) {
        return editFieldIds.add(ids);
    }

    public List<String> getEditFieldIds() {
        return new ArrayList<String>(editFieldIds);
    }

    public boolean isModifiedFlag() {
        return modifiedFlag;
    }

    public void setModifiedFlag(boolean modifiedFlag) {
        this.modifiedFlag = modifiedFlag;
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

    public JiraTaskElementExtension getExtension() {
        return extension;
    }

    public void synchronize() {
        try {
            taskRepository.update(this);
        } catch (JiraException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
