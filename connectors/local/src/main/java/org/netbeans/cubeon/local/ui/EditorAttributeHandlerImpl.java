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
package org.netbeans.cubeon.local.ui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.cubeon.common.ui.ComponentGroup;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.TaskEditorSupport;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class EditorAttributeHandlerImpl implements EditorAttributeHandler {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private LocalTask localTask;
    private TaskEditorSupport editorSupport;
    private TaskEditor editor;
    private TaskEditorPanels panels;
    private ComponentGroup attributesGroup;
    private ComponentGroup urlGroup;
    private ComponentGroup descriptionGroup;
    final DocumentListener documentListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent arg0) {
            run();
        }

        public void removeUpdate(DocumentEvent arg0) {
            run();
        }

        public void changedUpdate(DocumentEvent arg0) {
            run();
        }

        private void run() {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    fireChangeEvent();
                }
            });

        }
    };

    public EditorAttributeHandlerImpl(LocalTask localTask) {
        this.localTask = localTask;
        panels = new TaskEditorPanels(localTask);
        //attributes
        attributesGroup = new ComponentGroup(
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_Attributes"),
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_Attributes_Dec"));
        attributesGroup.setComponent(panels.getAttributesComponent());
        attributesGroup.setOpen(false);
        //url
        urlGroup = new ComponentGroup(
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_URL"),
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_URL_Dec"));
        urlGroup.setComponent(panels.getURLComponent());
        urlGroup.setToolbarActions(panels.getURLToolbarActions());
        urlGroup.setOpen(false);

        //group
        descriptionGroup = new ComponentGroup(
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_Description"),
                NbBundle.getMessage(EditorAttributeHandlerImpl.class, "LBL_Description_Dec"));
        descriptionGroup.setComponent(panels.getDescriptionComponent());
        editorSupport = new TaskEditorSupport(attributesGroup, urlGroup, descriptionGroup);
        editor = editorSupport.createEditor();
        editorSupport.setActive(attributesGroup);
        editor.hideStatusLable(true);
        panels.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                fireChangeEvent();
            }
        });
        refresh();
    }

    @Override
    public String getName() {
        return localTask != null ? localTask.getName() : "Local Task";//NOI18N
    }

    public String getDisplayName() {
        return localTask.getId();
    }

    public String getShortDescription() {
        return localTask.getName();
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

    public JComponent[] getComponent() {
        return new JComponent[]{editor.getComponent()};
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
    }

    public List<Action> getActions() {

        return new ArrayList<Action>(0);

    }

    private void loadDates(LocalTask localTask) {

        if (localTask.getCreated() != null) {
            editor.setCreatedDate(localTask.getCreated());
        }
        if (localTask.getUpdated() != null) {
            editor.setUpdatedDate(localTask.getUpdated());
        }

    }

    public void refresh() {
        editor.removeSummaryDocumentListener(documentListener);
        editor.setSummaryText(localTask.getName());
        loadDates(localTask);
        editor.addSummaryDocumentListener(documentListener);
        panels.refresh();
        updateGroups();

    }

    private void updateGroups() {
        //update group summary
        urlGroup.setSummary(localTask.getUrlString());
        attributesGroup.setSummary(panels.getAttributesHtmlSummary());

    }

    public TaskElement save() {
        if (!localTask.getName().equals(editor.getSummaryText())) {
            localTask.setName(editor.getSummaryText());
        }
        panels.save();
        updateGroups();
        return localTask;
    }
}
