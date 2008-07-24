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
package org.netbeans.cubeon.jira.tasks.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class RevertChangesAction extends AbstractAction {

    private JiraTask task;

    public RevertChangesAction(JiraTask task) {
        this.task = task;
        putValue(NAME, "Revert Local Changes");
        putValue(SHORT_DESCRIPTION, "Revert Local Changes");
        //putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/jira/history.png")));
    }

    public void actionPerformed(ActionEvent e) {
        task.getTaskRepository().revert(task);
        TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
        factory.refresh(task);
    }
}
