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
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.ComponentGroup;
import org.netbeans.cubeon.common.ui.ContainerGroup;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.TaskEditorSupport;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.TicketChange;
import org.netbeans.cubeon.trac.api.TicketChange.FieldChange;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.*;

/**
 *
 * @author Anuradha
 */
public class TracAttributeHandler implements EditorAttributeHandler {

    private TracTask task;
    private final TaskEditorSupport editorSupport;
    private final TaskEditor editor;
    private ComponentGroup descriptionGroup;
    private ComponentGroup attributesGroup;
    private ComponentGroup actionsGroup;
    private ComponentGroup newCommentGroup;
    private ContainerGroup commentsGroup;
    private final TracTaskEditor tracTaskEditor;

    public TracAttributeHandler(TracTask task) {
        this.task = task;


        //Description group
        descriptionGroup = new ComponentGroup(
                getMessage(TracAttributeHandler.class, "LBL_Description"),
                getMessage(TracAttributeHandler.class, "LBL_Description_Dec"));

        //Attributes Group 
        attributesGroup = new ComponentGroup(getMessage(TracAttributeHandler.class, "LBL_Attributes"),
                getMessage(TracAttributeHandler.class, "LBL_Attributes_Dec"));
        //if task still local  open Attributes panel by default


        actionsGroup = new ComponentGroup(getMessage(TracAttributeHandler.class, "LBL_Actions"),
                NbBundle.getMessage(TracAttributeHandler.class, "LBL_Actions_Dec"));
        actionsGroup.setOpen(false);

        //comments
        commentsGroup = new ContainerGroup(getMessage(TracAttributeHandler.class, "LBL_Comments"),
                NbBundle.getMessage(TracAttributeHandler.class, "LBL_Comments_Dec"));
        commentsGroup.setOpen(false);
        //new comment group
        newCommentGroup = new ComponentGroup(getMessage(TracAttributeHandler.class, "LBL_New_Comment"),
                getMessage(TracAttributeHandler.class, "LBL_New_Comment_Dec"));
        //open new comment depende on user alrady have comment
        newCommentGroup.setOpen(task.getNewComment() != null && task.getNewComment().length() != 0);

        CommentSectionDockAction commentSectionDockAction = new CommentSectionDockAction();

        commentsGroup.setToolbarActions(new Action[]{commentSectionDockAction});
        editorSupport = new TaskEditorSupport(descriptionGroup, attributesGroup, actionsGroup,commentsGroup, newCommentGroup);
        editor = editorSupport.createEditor();
        tracTaskEditor = new TracTaskEditor(editor, task);
        // attributesGroup.setOpen(task.isLocal());
        attributesGroup.setComponent(tracTaskEditor.getAttributesPanel());
        actionsGroup.setComponent(tracTaskEditor.getActionAndPeoplePanel());
        newCommentGroup.setComponent(tracTaskEditor.getNewCommentComponent());
        descriptionGroup.setComponent(tracTaskEditor.getDescriptionComponent());
        editorSupport.setActive(descriptionGroup);
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

        tracTaskEditor.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {

        tracTaskEditor.removeChangeListener(l);
    }

    public List<Action> getActions() {
        return tracTaskEditor.getActions();
    }

    public JComponent[] getComponent() {
        JComponent component = editor.getComponent();
        component.setName(getMessage(TracAttributeHandler.class, "LBL_Primary_Details"));
        return new JComponent[]{component};
    }

    public void refresh() {

        tracTaskEditor.refresh();
        refreshComments(commentsGroup);
    }

    public TaskElement save() {

        task = tracTaskEditor.read();

        task.getExtension().fireStateChenged();
        return task;
    }

    private void refreshComments(ContainerGroup commentsGroup) {
        commentsGroup.clearGroups();
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        List<TicketChange> ticketChanges = task.getTicketChanges();
        commentsGroup.setSummary("(" + ticketChanges.size() + ")");
        for (TicketChange ticketChange : ticketChanges) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(dateFormat.format(new Date(ticketChange.getTime()))).append(" By ");
            buffer.append(ticketChange.getAuthor());


            ComponentGroup cg = new ComponentGroup(buffer.toString(), buffer.toString());
            JEditorPane editorPane = new JEditorPane() {

                @Override
                public void setText(String t) {
                    super.setText(t);
                    setCaretPosition(0);
                }
            };

            editorPane.setEditable(false);
            editorPane.setText(buildChangesSet(ticketChange));
            cg.setComponent(editorPane);
            cg.setOpen(false);
            commentsGroup.addGroup(cg);
        }
        commentsGroup.refresh();
    }

    private String buildChangesSet(TicketChange comment) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(comment.getComment()).append("\n");
        if (!comment.getFieldChanges().isEmpty()) {
            buffer.append(NbBundle.getMessage(TracTaskEditor.class, "LBL_Field_Changes"));
            List<FieldChange> fieldChanges = comment.getFieldChanges();
            for (FieldChange fieldChange : fieldChanges) {
                buffer.append("\n").append(fieldChange.getField()).append(" : ");
                buffer.append(fieldChange.getOldValue()).append(" => ");
                buffer.append(fieldChange.getNewValuve());
            }
        }
        return buffer.toString();
    }

    private class CommentSectionDockAction extends AbstractAction {

        public CommentSectionDockAction() {
            validate();
        }
        private boolean docked=true;

        public void validate() {
            if (docked) {
                putValue(NAME, "Undock");
                Image undock = ImageUtilities.loadImage("org/netbeans/cubeon/trac/undock.png");
                putValue(SMALL_ICON, ImageUtilities.image2Icon(undock));
            } else {
                putValue(NAME, "Dock");
                Image dock = ImageUtilities.loadImage("org/netbeans/cubeon/trac/dock.png");
                putValue(SMALL_ICON, ImageUtilities.image2Icon(dock));
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (docked) {
                docked = false;
                validate();
                editor.setMasterGroups(descriptionGroup, attributesGroup, actionsGroup, newCommentGroup);
                commentsGroup.setOpen(true);
                editor.setDetailGroups(commentsGroup);
                
            } else {
                docked = true;
                validate();
                editor.setMasterGroups(descriptionGroup, attributesGroup, actionsGroup, commentsGroup, newCommentGroup);

                editor.setDetailGroups();
                
            }

            editorSupport.setActive(descriptionGroup);
        }
    }
}
