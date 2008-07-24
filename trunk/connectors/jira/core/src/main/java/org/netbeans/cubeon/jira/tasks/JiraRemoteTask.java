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

import org.netbeans.cubeon.jira.repository.attributes.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class JiraRemoteTask {

    private String id;
    private String name;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private TaskType type;
    private TaskResolution resolution;
    private Date created;
    private Date updated;
    private JiraProject project;
    private String environment;
    private String reporter;
    private String assignee;
    private List<JiraProject.Component> components = new ArrayList<JiraProject.Component>(0);
    private List<JiraProject.Version> affectedVersions = new ArrayList<JiraProject.Version>(0);
    private List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>(0);
    private List<JiraComment> comments = new ArrayList<JiraComment>();
    protected JiraTaskRepository taskRepository;

    public JiraRemoteTask(JiraTaskRepository taskRepository, String id, String name, String description) {
        this.taskRepository = taskRepository;
        this.id = id;
        this.name = name;
        this.description = description;



    }

    public JiraTaskRepository getTaskRepository() {
        return taskRepository;
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

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;

    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;

    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;

    }

    public TaskResolution getResolution() {
        return resolution;
    }

    public void setResolution(TaskResolution resolution) {
        this.resolution = resolution;

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

    public List<JiraComment> getComments() {
        return new ArrayList<JiraComment>(comments);
    }

    public void setComments(List<JiraComment> comments) {
        this.comments = new ArrayList<JiraComment>(comments);
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
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
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
        hash = 79 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        hash = 79 * hash + (this.taskRepository != null ? this.taskRepository.hashCode() : 0);
        return hash;
    }
}
