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
import java.util.List;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.ui.JiraActionsProvider;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
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
public class JiraTask extends JiraRemoteTask implements TaskElement {

    private boolean local;
    private List<String> editFieldIds = new ArrayList<String>();
    private JiraActionsProvider actionsProvider = new JiraActionsProvider();
    private JiraAction action;
    private String urlString;
    private String newComment;
    private boolean modifiedFlag;
    private TaskEditorProvider editorProvider;
    private JiraTaskElementExtension extension;

    public JiraTask(String id, String name, String description,
            JiraTaskRepository taskRepository) {
        super(taskRepository, id, name, description);

        extension = new JiraTaskElementExtension(this);
        editorProvider = new TaskEditorProviderImpl(this);

    }

    public String getDisplayName() {
        return getId() + " : " + getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        extension.fireNameChenged();
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        extension.fireDescriptionChenged();
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, editorProvider, extension);
    }

    @Override
    public void setPriority(TaskPriority priority) {
        super.setPriority(priority);
        extension.firePriorityChenged();
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        extension.fireStatusChenged();
    }

    @Override
    public void setType(TaskType type) {
        super.setType(type);
        extension.fireTypeChenged();
    }

    @Override
    public void setResolution(TaskResolution resolution) {
        super.setResolution(resolution);
        extension.fireResolutionChenged();
    }

    public boolean isCompleted() {
        return getResolution() != null;
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        //FIXME
        if (getProject() != null) {
            List<String> taskTypes = getProject().getTypes();

            int indexOf = taskTypes.indexOf(getType().getId());

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

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
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
        return modifiedFlag || local;
    }

    public void setModifiedFlag(boolean modifiedFlag) {
        this.modifiedFlag = modifiedFlag;
    }

    public JiraTaskElementExtension getExtension() {
        return extension;
    }

    public void synchronize() {
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.save(this);
        try {
            taskRepository.update(this);
        } catch (JiraException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
