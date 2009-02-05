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

import java.util.Date;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.ComponentGroup;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.TaskEditorSupport;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.tasks.ui.DescriptionUI;
import org.netbeans.cubeon.trac.tasks.ui.TracTaskEditorPanels;
import static org.openide.util.NbBundle.*;

/**
 *
 * @author Anuradha
 */
public class TracAttributeHandler implements EditorAttributeHandler {

    private TracTask task;
    private TaskEditor editor;
    private ComponentGroup descriptionGroup;
    private ComponentGroup attributesGroup;
    private ComponentGroup actionsGroup;
    private final TracTaskEditorPanels panels;
    private final DescriptionUI descriptionUI = new DescriptionUI();

    public TracAttributeHandler(TracTask task) {
        this.task = task;
        panels = new TracTaskEditorPanels(task);

        //Description group
        descriptionGroup = new ComponentGroup(
                getMessage(TracAttributeHandler.class, "LBL_Description"),
                getMessage(TracAttributeHandler.class, "LBL_Description_Dec"));
        descriptionGroup.setComponent(descriptionUI);
        //Attributes Group
        attributesGroup = new ComponentGroup(getMessage(TracAttributeHandler.class, "LBL_Attributes"),
                getMessage(TracAttributeHandler.class, "LBL_Attributes_Dec"));
        //if task still local  open Attributes panel by default
        // attributesGroup.setOpen(task.isLocal());
        attributesGroup.setComponent(panels.getAttributesPanel());

        actionsGroup = new ComponentGroup(getMessage(TracAttributeHandler.class, "LBL_Actions"),
                getMessage(TracAttributeHandler.class, "LBL_Actions_Dec"));
        actionsGroup.setOpen(false);
        actionsGroup.setComponent(panels.getActionAndPeoplePanel());

        editor = new TaskEditorSupport().createEditor(descriptionGroup, attributesGroup, actionsGroup);
        refresh();


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

    public final void addChangeListener(ChangeListener l) {

        panels.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {

        panels.removeChangeListener(l);
    }

    public List<Action> getActions() {
        return panels.getActions();
    }

    public JComponent[] getComponent() {
        JComponent component = editor.getComponent();
        component.setName(getMessage(TracAttributeHandler.class, "LBL_Primary_Details"));
        return new JComponent[]{component, panels.getCommentsEditor()};
    }

    public void refresh() {
        loadDates();
        editor.setStatus(
                task.isLocal()
                ? getMessage(TracAttributeHandler.class, "LBL_Local")
                : task.get(TracKeys.STATUS));
        editor.removeSummaryDocumentListener(panels.getDocumentListener());
        editor.setSummaryText(task.getName());
        editor.addSummaryDocumentListener(panels.getDocumentListener());
        descriptionUI.getDocument().removeDocumentListener(panels.getDocumentListener());
        descriptionUI.setText(task.getDescription());
        descriptionUI.getDocument().addDocumentListener(panels.getDocumentListener());
        panels.refresh();
    }

    private void loadDates() {

        if (task.getCreatedDate() != 0) {
            editor.setCreatedDate(new Date(task.getCreatedDate()));
        }
        if (task.getUpdatedDate() != 0) {
            editor.setUpdatedDate(new Date(task.getUpdatedDate()));
        }

    }

    public TaskElement save() {

        task = panels.save();
        if (!task.getName().equals(editor.getSummaryText())) {
            task.setSummary(editor.getSummaryText());
        }
        task.setDescription(descriptionUI.getText().trim());
        task.getExtension().fireStateChenged();
        return task;
    }
}
