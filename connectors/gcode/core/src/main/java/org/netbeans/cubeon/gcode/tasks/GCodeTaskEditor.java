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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.components.ComponentBuilder;
import org.netbeans.cubeon.common.ui.components.ComponentContainer;
import org.netbeans.cubeon.common.ui.components.TextEditorUI;
import org.netbeans.cubeon.gcode.tasks.actions.OpenInBrowserTaskAction;
import org.netbeans.cubeon.gcode.tasks.actions.SubmitTaskAction;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class GCodeTaskEditor {


    private static final String EMPTY = "";
    private final GCodeTask task;
    private final TaskEditor editor;
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
    private ComponentBuilder builder = new ComponentBuilder() {

        @Override
        public void valueChanged() {
            fireChangeEvent();
        }
    };
    private ComponentContainer arrributesContainer = new ComponentContainer();
    private final TextEditorUI descriptionComponent = new TextEditorUI();
    private final TextEditorUI newCommentComponent = new TextEditorUI();
    //default trac fields
    private JComboBox status;
    private JLabel reportedBy;
    private JTextField assignee;
    private JTextField cc;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public GCodeTaskEditor(TaskEditor editor, GCodeTask task) {
        this.editor = editor;
        this.task = task;
        openInBrowserTaskAction = new OpenInBrowserTaskAction(task);
        submitTaskAction = new SubmitTaskAction(task);
        editor.addSummaryDocumentListener(builder.getDocumentListener());
        descriptionComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        newCommentComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        buildComponents();
    }

    private void buildComponents() {
        //build attribute



        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(GCodeTaskEditor.class,
                "LBL_Reported_By")), reportedBy = builder.createLabelField(" - "));
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(GCodeTaskEditor.class,
                "LBL_Status")), status = builder.createComboBox());
        //move to next row
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(GCodeTaskEditor.class,
                "LBL_Assignee")), assignee = builder.createTextField());
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(GCodeTaskEditor.class,
                "LBL_CC")), cc = builder.createTextField());


    }

    public void refresh() {
        //desable notify
        builder.setNotifyMode(false);
        loadDates();
        editor.setStatus(
                task.isLocal()
                ? NbBundle.getMessage(GCodeTaskEditor.class, "LBL_Local")
                : task.getStatus());
        editor.setSummaryText(task.getName());




        descriptionComponent.setText(task.getDescription());
        newCommentComponent.setEditable(!task.isLocal());
        descriptionComponent.setEditable(task.isLocal());

        newCommentComponent.setText(task.getNewComment());



        reportedBy.setText(task.getReportedBy());
        assignee.setText(task.getOwner());

        cc.setText(getAsString(task.getCcs()));
        _loadCombos(status, task.getTaskRepository().getRepositoryAttributes().getStatuses(),
                false, task.getStatus());

        //enable notify
        builder.setNotifyMode(true);
        openInBrowserTaskAction.setEnabled(!task.isLocal());
        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
    }
    public String getAsString(List<String> strings){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(s).append(",");
        }
        return stringBuilder.toString();
    }

    private void _loadCombos(JComboBox comboBox, List<String> options,
            boolean optinal, String selected) {
        comboBox.removeAllItems();
        if (options.isEmpty()) {
            comboBox.setEnabled(false);
        } else {
            comboBox.setEnabled(true);
            if (optinal) {
                //if optional component add EMPTY tag to options
                comboBox.addItem(EMPTY);
                //if valuve optional and selected null set selected as EMPTY
                if (selected == null || selected.trim().length() == 0) {
                    selected = EMPTY;
                }
            }
            for (String string : options) {
                comboBox.addItem(string);
            }
            if (selected == null || selected.equals(EMPTY)) {
                comboBox.setSelectedIndex(-1);
            } else {
                comboBox.setSelectedItem(selected);
            }
        }
    }

    public GCodeTask read() {
        if (!task.getName().equals(editor.getSummaryText())) {
            task.setSummary(editor.getSummaryText());
        }
        if (task.isLocal()) {
            task.setDescription(descriptionComponent.getText().trim());
        }
        if (!task.isLocal()) {
            task.setNewComment(newCommentComponent.getText().trim());
        }

        String owner = assignee.getText().trim();
        task.setOwner(owner);
        //set as modified if already or actuvaly modified
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }

        submitTaskAction.setEnabled(task.isModifiedFlag());
        task.getTaskRepository().persist(task);

        return task;
    }

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
        modifiedFlag.set(true);
        submitTaskAction.setEnabled(true);
    }

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private String getSelectedValve(JComboBox comboBox) {
        Object selected = comboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }

    private void loadDates() {

        if (task.getCreatedDate() != 0) {
            editor.setCreatedDate(new Date(task.getCreatedDate()));
        }
        if (task.getUpdatedDate() != 0) {
            editor.setUpdatedDate(new Date(task.getUpdatedDate()));
        }

    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                submitTaskAction);
    }

    public JComponent getAttributesPanel() {
        return arrributesContainer;
    }

    public TextEditorUI getNewCommentComponent() {
        return newCommentComponent;
    }

    public TextEditorUI getDescriptionComponent() {
        return descriptionComponent;
    }
}
