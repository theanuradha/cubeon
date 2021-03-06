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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.netbeans.cubeon.jira.repository.attributes.JiraComment;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.*;

/**
 *
 * @author Anuradha
 */
public class JiraAttributeHandler implements EditorAttributeHandler {

    private JiraTask task;
    private final TaskEditorSupport editorSupport;
    private TaskEditor editor;
    private ComponentGroup descriptionGroup;
    private ComponentGroup attributesGroup;
    private ComponentGroup actionsGroup;
    private ComponentGroup newCommentGroup;
    private ContainerGroup commentsGroup;
    private final JiraTaskEditor jiraTaskEditor;

    public JiraAttributeHandler(JiraTask task) {
        this.task = task;


        //Description group
        descriptionGroup = new ComponentGroup(
                getMessage(JiraAttributeHandler.class, "LBL_Description"),
                getMessage(JiraAttributeHandler.class, "LBL_Description_Dec"));

        //Attributes Group
        attributesGroup = new ComponentGroup(getMessage(JiraAttributeHandler.class, "LBL_Attributes"),
                getMessage(JiraAttributeHandler.class, "LBL_Attributes_Dec"));
        //if task still local  open Attributes panel by default
        // attributesGroup.setOpen(task.isLocal());

        attributesGroup.setOpen(task.isLocal());
        actionsGroup = new ComponentGroup(getMessage(JiraAttributeHandler.class, "LBL_Actions"),
                NbBundle.getMessage(JiraAttributeHandler.class, "LBL_Actions_Dec"));
        actionsGroup.setOpen(false);

        //comments
        commentsGroup = new ContainerGroup(getMessage(JiraAttributeHandler.class, "LBL_Comments"),
                NbBundle.getMessage(JiraAttributeHandler.class, "LBL_Comments_Dec"));
        commentsGroup.setOpen(false);
        CommentSectionDockAction commentSectionDockAction = new CommentSectionDockAction();

        commentsGroup.setToolbarActions(new Action[]{commentSectionDockAction});
        //new comment group
        newCommentGroup = new ComponentGroup(getMessage(JiraAttributeHandler.class, "LBL_New_Comment"),
                getMessage(JiraAttributeHandler.class, "LBL_New_Comment_Dec"));
        //open new comment depende on user alrady have comment
        newCommentGroup.setOpen(task.getNewComment() != null && task.getNewComment().length() != 0);
        editorSupport = new TaskEditorSupport();
        editor = editorSupport.createEditor();
        editor.setLeftSideGroups(attributesGroup, descriptionGroup, actionsGroup, commentsGroup, newCommentGroup);
        jiraTaskEditor = new JiraTaskEditor(task, editor);
        actionsGroup.setComponent(jiraTaskEditor.getActionAndPeoplePanel());
        attributesGroup.setComponent(jiraTaskEditor.getAttributesPanel());
        descriptionGroup.setComponent(jiraTaskEditor.getDescriptionComponent());
        newCommentGroup.setComponent(jiraTaskEditor.getNewCommentComponent());
        editor.setLeftActiveGroup(descriptionGroup);
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

    public void addChangeListener(ChangeListener changeListener) {
        jiraTaskEditor.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        jiraTaskEditor.removeChangeListener(changeListener);
    }

    public List<Action> getActions() {
        return jiraTaskEditor.getActions();
    }

    public JComponent[] getComponent() {
        return new JComponent[]{editor.getComponent()};
    }

    public void refresh() {
        jiraTaskEditor.refresh();
        refreshComments(commentsGroup);
    }

    public TaskElement save() {

        task = jiraTaskEditor.read();
        task.getExtension().fireStateChenged();
        return task;
    }

    private void refreshComments(ContainerGroup commentsGroup) {
        commentsGroup.clearGroups();
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        List<JiraComment> comments = task.getComments();
        commentsGroup.setSummary("(" + comments.size() + ")");
        for (JiraComment comment : comments) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(dateFormat.format(comment.getUpdated())).append(" By ");
            buffer.append(comment.getAuthor());


            ComponentGroup cg = new ComponentGroup(buffer.toString(), buffer.toString());
            JEditorPane editorPane = new JEditorPane() {

                @Override
                public void setText(String t) {
                    super.setText(t);
                    setCaretPosition(0);
                }
            };
            editorPane.setEditable(false);
            editorPane.setText(comment.getBody());
            cg.setComponent(editorPane);
            cg.setOpen(false);
            commentsGroup.addGroup(cg);
        }

        commentsGroup.refresh();
    }

    private class CommentSectionDockAction extends AbstractAction {

        public CommentSectionDockAction() {
            validate();
        }
        private boolean docked = true;

        public void validate() {
            if (docked) {
                putValue(NAME, "Undock");
                Image undock = ImageUtilities.loadImage("org/netbeans/cubeon/jira/undock.png");
                putValue(SMALL_ICON, ImageUtilities.image2Icon(undock));
            } else {
                putValue(NAME, "Dock");
                Image dock = ImageUtilities.loadImage("org/netbeans/cubeon/jira/dock.png");
                putValue(SMALL_ICON, ImageUtilities.image2Icon(dock));
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (docked) {
                docked = false;
                validate();
                editor.setLeftSideGroups(descriptionGroup, attributesGroup, actionsGroup, newCommentGroup);
                commentsGroup.setOpen(true);
                editor.setRightSideGroups(commentsGroup);

            } else {
                docked = true;
                validate();
                editor.setLeftSideGroups(descriptionGroup, attributesGroup, actionsGroup, commentsGroup, newCommentGroup);

                editor.setRightSideGroups();

            }

            editor.setLeftActiveGroup(descriptionGroup);
        }
    }
}
