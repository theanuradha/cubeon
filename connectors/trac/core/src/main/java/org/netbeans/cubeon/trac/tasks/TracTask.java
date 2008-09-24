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
package org.netbeans.cubeon.trac.tasks;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskSeverity;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TracTask extends Ticket implements TaskElement {

    private final TracTaskRepository taskRepository;
    private String id;
    private final TracTaskElementExtension extension;
    private final TaskEditorProvider editorProvider;
    private boolean local;
    private boolean modifiedFlag;
    private List<String> actions = new ArrayList<String>();
    private String newComment;
    private String action;

    public TracTask(TracTaskRepository taskRepository, String id,
            int ticketId, String summary, String description) {
        super(ticketId, summary, description);
        this.taskRepository = taskRepository;
        this.id = id;
        extension = new TracTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return getSummary();
    }

    public String getDisplayName() {
        if (isLocal()) {
            return NbBundle.getMessage(TracTask.class, "Key_New") + " : " + getName();
        }
        return getId() + " : " + getName();
    }

    public TaskPriority getPriority() {
        String priority = get(TracKeys.PRIORITY);
        //resolve priority using PriorityProvider
        return priority != null ? taskRepository.getPriorityProvider().
                getTaskPriorityById(priority) : null;
    }

    public TaskSeverity getSeverity() {
        String severity = get(TracKeys.SEVERITY);
        //resolve Severity using SeverityProvider
        return severity != null ? taskRepository.getSeverityProvider().
                getTaskSeverityById(severity) : null;
    }

    public TaskStatus getStatus() {
        String status = get(TracKeys.STATUS);
        //resolve status using StatusProvider
        return status != null ? taskRepository.getStatusProvider().
                getTaskStatusById(status) : null;
    }

    public TracTaskRepository getTaskRepository() {
        return taskRepository;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        extension.fireDescriptionChenged();
    }

    @Override
    public void setSummary(String summary) {
        super.setSummary(summary);
        extension.fireNameChenged();
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension, editorProvider, taskRepository);
    }

    public TaskType getType() {
        String type = get(TracKeys.TYPE);
        //resolve type using TypeProvider
        return type != null ? taskRepository.getTypeProvider().
                getTaskTypeById(type) : null;
    }

    public boolean isCompleted() {
        //check ticket has resolution 
        return get(TracKeys.RESOLUTION) != null;
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/trac/task.png");
        //FIXME
        TicketField type = taskRepository.getRepositoryAttributes().
                getTicketFiledByName(TracKeys.TYPE);
        int indexOf = type.getOptions().indexOf(getType().getId());

        switch (indexOf) {
            case 0:
                image = Utilities.mergeImages(image,
                        Utilities.loadImage("org/netbeans/cubeon/local/bullet_defact.png"), 0, 0);
                break;
            case 1:
                image = Utilities.mergeImages(image,
                        Utilities.loadImage("org/netbeans/cubeon/local/bullet_enhancement.png"), 0, 0);
                break;
            case 2:
                image = Utilities.mergeImages(image,
                        Utilities.loadImage("org/netbeans/cubeon/local/bullet_feature.png"), 0, 0);
                break;
            case 3:
                image = Utilities.mergeImages(image,
                        Utilities.loadImage("org/netbeans/cubeon/local/bullet_task.png"), 0, 0);
                break;

        }


        return image;
    }

    public String getUrlString() {
        return taskRepository.getURL() + "/ticket/" + getTicketId();
    }

    public URL getUrl() {

        try {
            return new URL(getUrlString());
        } catch (MalformedURLException ex) {
            //ignore
        }
        return null;
    }

    public void setPriority(TaskPriority priority) {
        //put ticket priority
        put(TracKeys.PRIORITY, priority.getId());
        extension.firePriorityChenged();
    }

    public void setType(TaskType taskType) {
        //put ticket type
        put(TracKeys.TYPE, taskType.getId());
        extension.fireTypeChenged();
    }

    public void synchronize() {
    }

    public TaskResolution getResolution() {
        String resolution = get(TracKeys.RESOLUTION);
        //resolve resolution using ResolutionProvider
        return resolution != null ? taskRepository.getResolutionProvider().
                getTaskResolutionById(resolution) : null;
    }

    public void setResolution(TaskResolution resolution) {
        //put ticket resolution
        put(TracKeys.RESOLUTION, resolution.getId());
        extension.fireResolutionChenged();
    }

    public void setStatus(TaskStatus status) {
        //put ticket status
        put(TracKeys.STATUS, status.getId());
        extension.fireStatusChenged();
    }

    public void setSeverity(TaskSeverity severity) {
        //put ticket severity
        put(TracKeys.SEVERITY, severity.getId());
        extension.fireDescriptionChenged();
    }

    public TracTaskElementExtension getExtension() {
        return extension;
    }

    public List<String> getActions() {
        return new ArrayList<String>(actions);
    }

    public void setActions(List<String> actions) {
        this.actions = new ArrayList<String>(actions);
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public TracTaskRepository getTracRepository() {
        return taskRepository;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
        final TracTask other = (TracTask) obj;
        if (this.taskRepository != other.taskRepository && (this.taskRepository == null || !this.taskRepository.equals(other.taskRepository))) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.taskRepository != null ? this.taskRepository.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
