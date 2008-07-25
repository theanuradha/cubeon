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
package org.netbeans.cubeon.jira.tasks.ui;

import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

/**
 *
 * @author Anuradha
 */
public class JiraAttributeHandler implements EditorAttributeHandler {

    private final JiraTask jiraTask;
    private final JiraTaskEditorUI editorUI;

    public JiraAttributeHandler(JiraTask jiraTask) {
        this.jiraTask = jiraTask;
        editorUI = new JiraTaskEditorUI(jiraTask);
    }

    public String getName() {
        return jiraTask.getName();
    }

    public String getDisplayName() {
        return jiraTask.getId();
    }

    public String getShortDescription() {
        return jiraTask.getName();
    }

    public void addChangeListener(ChangeListener changeListener) {
        editorUI.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        editorUI.removeChangeListener(changeListener);
    }

    public List<Action> getActions() {
        return editorUI.getActions();
    }

    public JComponent[] getComponent() {
        return new JComponent[]{editorUI, editorUI.getCommentsEditor()};
    }

    public void refresh() {
        editorUI.refresh();
    }

    public TaskElement save() {
       
        JiraTask task = editorUI.save();
        
        task.getExtension().fireStateChenged();
        return task;
    }
}
