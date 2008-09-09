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

import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.tasks.ui.TracTaskEditorUI;

/**
 *
 * @author Anuradha
 */
public class TracAttributeHandler implements EditorAttributeHandler {

    private TracTask task;
    private final TracTaskEditorUI editorUI;

    public TracAttributeHandler(TracTask task) {
        this.task = task;
        editorUI = new TracTaskEditorUI(task);
    }

    public String getName() {
        return task.getDisplayName();
    }

    public String getDisplayName() {
        return task.getId();
    }

    public String getShortDescription() {
        return task.getName();
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
        return new JComponent[]{editorUI};
    }

    public void refresh() {
        editorUI.refresh();
    }

    public TaskElement save() {

        task = editorUI.save();

        task.getExtension().fireStateChenged();
        return task;
    }
}
