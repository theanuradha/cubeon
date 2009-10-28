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
package org.netbeans.cubeon.gcode.tasks;

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
import org.netbeans.cubeon.gcode.api.GCodeComment;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.*;

/**
 *
 * @author Anuradha
 */
public class GCodeAttributeHandler implements EditorAttributeHandler {

    private GCodeTask task;
    private final TaskEditorSupport editorSupport;
    private final TaskEditor editor;
    private ComponentGroup descriptionGroup;
    private ComponentGroup attributesGroup;
    private ComponentGroup newCommentGroup;
    private ContainerGroup commentsGroup;
    private final GCodeTaskEditor tracTaskEditor;

    public GCodeAttributeHandler(GCodeTask task) {
        this.task = task;


        //Description group
        descriptionGroup = new ComponentGroup(
                getMessage(GCodeAttributeHandler.class, "LBL_Description"),
                getMessage(GCodeAttributeHandler.class, "LBL_Description_Dec"));

        //Attributes Group 
        attributesGroup = new ComponentGroup(getMessage(GCodeAttributeHandler.class, "LBL_Attributes"),
                getMessage(GCodeAttributeHandler.class, "LBL_Attributes_Dec"));
        //if task still local  open Attributes panel by default




        //comments
        commentsGroup = new ContainerGroup(getMessage(GCodeAttributeHandler.class, "LBL_Comments"),
                NbBundle.getMessage(GCodeAttributeHandler.class, "LBL_Comments_Dec"));
        commentsGroup.setOpen(false);
        //new comment group
        newCommentGroup = new ComponentGroup(getMessage(GCodeAttributeHandler.class, "LBL_New_Comment"),
                getMessage(GCodeAttributeHandler.class, "LBL_New_Comment_Dec"));
        //open new comment depende on user alrady have comment
        newCommentGroup.setOpen(task.getNewComment() != null && task.getNewComment().length() != 0);

        CommentSectionDockAction commentSectionDockAction = new CommentSectionDockAction();

        commentsGroup.setToolbarActions(new Action[]{commentSectionDockAction});
        editorSupport = new TaskEditorSupport();
        editor = editorSupport.createEditor();
        editor.setLeftSideGroups(descriptionGroup, attributesGroup, commentsGroup, newCommentGroup);
        tracTaskEditor = new GCodeTaskEditor(editor, task);
        // attributesGroup.setOpen(task.isLocal());
        attributesGroup.setComponent(tracTaskEditor.getAttributesPanel());

        newCommentGroup.setComponent(tracTaskEditor.getNewCommentComponent());
        descriptionGroup.setComponent(tracTaskEditor.getDescriptionComponent());
        editor.setLeftActiveGroup(descriptionGroup);
        refresh();


    }

    public String getName() {
        return task.getDisplayName();
    }

    public String getDisplayName() {
        return GCodeTask.issueToTaskId(task);
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
        component.setName(getMessage(GCodeAttributeHandler.class, "LBL_Primary_Details"));
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
        List<GCodeComment> codeComments = task.getComments();
        commentsGroup.setSummary("(" + codeComments.size() + ")");
        for (GCodeComment comment : codeComments) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(dateFormat.format(new Date(comment.getCommentDate()))).append(" By ");
            buffer.append(comment.getAuthor());


            ComponentGroup cg = new ComponentGroup("Comment - " + comment.getCommentId(), buffer.toString());
            cg.setSummary(buffer.toString());
            JEditorPane editorPane = new JEditorPane() {

                @Override
                public void setText(String t) {
                    super.setText(t);
                    setCaretPosition(0);
                }
            };

            editorPane.setEditable(false);
            editorPane.setText(buildChangesSet(buffer, comment));
            cg.setComponent(editorPane);
            cg.setOpen(false);
            commentsGroup.addGroup(cg);
        }
        commentsGroup.refresh();
    }

    private String buildChangesSet(StringBuffer buffer, GCodeComment comment) {
        buffer.append("\n");
        buffer.append(comment.getComment() != null ? comment.getComment()
                : "(No comment was entered for this change.)").append("\n");
        if (comment.getSummary() != null) {
            buffer.append("Summary: ").append(comment.getSummary()).append("\n");
        }
        if (comment.getStatus() != null) {
            buffer.append("Status: ").append(comment.getStatus()).append("\n");
        }
        if (comment.getOwner() != null) {
            buffer.append("Owner: ").append(comment.getOwner()).append("\n");
        }
        if (comment.getCcs().size() > 0) {
            buffer.append("Cc: ");
            for (String cc : comment.getCcs()) {
                buffer.append(cc).append(" ");
            }
            buffer.append("\n");
        }
        if (comment.getLabels().size() > 0) {
            buffer.append("Labels: ");
            for (String label : comment.getLabels()) {
                buffer.append(label).append(" ");
            }
            buffer.append("\n");
        }


        return buffer.toString();
    }

    private class CommentSectionDockAction extends AbstractAction {

        public CommentSectionDockAction() {
            validate();
        }
        private boolean docked = true;

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
                editor.setLeftSideGroups(descriptionGroup, attributesGroup, newCommentGroup);
                commentsGroup.setOpen(true);
                editor.setRightSideGroups(commentsGroup);

            } else {
                docked = true;
                validate();
                editor.setLeftSideGroups(descriptionGroup, attributesGroup, commentsGroup, newCommentGroup);
                //remove rigrt side groups 
                editor.setRightSideGroups();

            }

            editor.setLeftActiveGroup(descriptionGroup);
        }
    }
}
