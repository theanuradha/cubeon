/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.tasks;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.analyzer.spi.StackTraceProvider;
import org.netbeans.cubeon.common.ui.TaskTypeBadge;
import org.netbeans.cubeon.gcode.api.GCodeComment;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.api.GCodeState;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.utils.GCodeExceptionHandler;
import org.netbeans.cubeon.gcode.utils.GCodeUtils;
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
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class GCodeTask extends GCodeIssue implements TaskElement {

    private final GCodeTaskRepository taskRepository;
    private final GCodeTaskElementExtension extension;
    private final TaskEditorProvider editorProvider;
    private boolean local;
    private boolean modifiedFlag;
    private String newComment;
    private final StackTraceProvider stackTraceProvider;

    public GCodeTask(GCodeTaskRepository taskRepository, String id,
            String summary, String description) {
        super(id, summary, description);
        this.taskRepository = taskRepository;
        extension = new GCodeTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);
        stackTraceProvider = new StackTraceProvider() {

            @Override
            public List<String> getAnalyzableTexts() {
                List<String> stacks = new ArrayList<String>();
                stacks.add(getDescription());
                List<GCodeComment> changes = getComments();
                for (GCodeComment change : changes) {
                    stacks.add(change.getComment());
                }
                return stacks;
            }
        };
    }

    public String getName() {
        return getSummary();
    }

    public String getDisplayName() {
        if (isLocal()) {
            return NbBundle.getMessage(GCodeTask.class, "Key_New") + " : " + getName();
        }
        return getId() + " : " + getName();
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public GCodeTaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension, editorProvider, taskRepository, stackTraceProvider);
    }

    public boolean isCompleted() {
        return GCodeState.CLOSED == getState();
    }

    public Image getImage() {
        Image image = TaskTypeBadge.getTaskImage();
        image = ImageUtilities.mergeImages(image,
                ImageUtilities.loadImage("org/netbeans/cubeon/gcode/gcode-badge.png"), 6, 6);
        return image;
    }

    public String getUrlString() {
        return "http://code.google.com/p/" + taskRepository.getProject()
                + "/issues/detail?id=" + getId();
    }

    public URL getUrl() {

        try {
            return new URL(getUrlString());
        } catch (MalformedURLException ex) {
            //ignore
        }
        return null;
    }

    public void synchronize() {
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.save(this);
        try {
            taskRepository.update(this);
        } catch (GCodeException ex) {
            GCodeExceptionHandler.notify(ex);
        }
    }

    public Notifier<TaskElementChangeAdapter> getNotifier() {
        return extension;
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

    public TaskPriority getPriority() {
        return GCodeUtils.getTaskPriority(this);
    }

    public void setPriority(TaskPriority priority) {
        GCodeUtils.setTaskPriority(this, priority);
        extension.firePriorityChenged();
    }

    public TaskStatus getTaskStatus() {
        if (getStatus() != null) {
            return new TaskStatus(taskRepository, getStatus(), getStatus());
        }
        return null;
    }

    public TaskType getType() {
        return GCodeUtils.getTaskType(this);
    }

    public void setType(TaskType taskType) {
        GCodeUtils.setTaskType(this, taskType);
        extension.fireTypeChenged();
    }

    public void setStatus(TaskStatus status) {
        setStatus(status != null ? status.getId() : null);
        extension.fireStateChenged();
    }

    public boolean isModifiedFlag() {
        return modifiedFlag || local;
    }

    public void setModifiedFlag(boolean modifiedFlag) {
        this.modifiedFlag = modifiedFlag;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GCodeTask other = (GCodeTask) obj;
        if (this.taskRepository != other.taskRepository && (this.taskRepository == null || !this.taskRepository.equals(other.taskRepository))) {
            return false;
        }
        if ((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.taskRepository != null ? this.taskRepository.hashCode() : 0);
        hash = 29 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
}
