/*
 *  Copyright 2009 tom.
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

package org.netbeans.cubeon.javanet.tasks.ui;

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
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.TaskEditorSupport;
import org.netbeans.cubeon.javanet.tasks.JavanetTask;

/**
 *
 * @author tom
 */
public class EditorAttributeHandlerImpl implements EditorAttributeHandler {

    private final Set<ChangeListener> _listeners = new HashSet<ChangeListener>(1);

    private TaskEditor _editor;
    private JavanetTask _task;
    private JavanetTaskEditorUI _panels;

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

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (_listeners) {
            it = new HashSet<ChangeListener>(_listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    public EditorAttributeHandlerImpl(JavanetTask task) {
        _task = task;
        _panels = new JavanetTaskEditorUI(task);

        ComponentGroup cgDescription = new ComponentGroup("Description", "Issue description");
        cgDescription.setComponent(_panels.getDescription());

        ComponentGroup cgAttributes = new ComponentGroup("Attributes", "Issue attributes");
        cgAttributes.setComponent(_panels.getAttributesPanel());

        ComponentGroup cgComment = new ComponentGroup("Add comment", "Add comment");
        cgComment.setComponent(_panels.getCommentPanel());

        ComponentGroup cgAction = new ComponentGroup("Action", "Issue Action");
        cgAction.setComponent(_panels.getActionsPanel());
        

        _editor = new TaskEditorSupport().createEditor(cgDescription, cgAttributes, cgComment, cgAction);
        _editor.hideStatusLable(false);
        _panels.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                fireChangeEvent();
            }
        });
        refresh();
    }



    public String getName() {
        return _task.getName();
    }

    public String getDisplayName() {
        return _task.getDisplayName();
    }

    public String getShortDescription() {
        return _task.getDescription();
    }

    public void addChangeListener(ChangeListener changeListener) {
        synchronized (_listeners) {
            _listeners.add(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (_listeners) {
            _listeners.remove(changeListener);
        }
    }

    public List<Action> getActions() {
        return new ArrayList<Action>(0);
    }

    public JComponent[] getComponent() {
        return new JComponent[]{_editor.getComponent()};
    }

    public void refresh() {
        if (_task.getStatus() != null ) {
            _editor.setStatus(_task.getStatus());
        }

        if (_task.getSummary() != null ) {
            _editor.setSummaryText(_task.getSummary());
        }

        loadDates();
    }

    private void loadDates() {

        if (_task.getCreationDate() != null) {
            _editor.setCreatedDate(_task.getCreationDate().getTime());
        }
        if (_task.getLastModified() != null) {
            _editor.setUpdatedDate(_task.getLastModified().getTime());
        }

    }

    public TaskElement save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
