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
import javax.swing.ImageIcon;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha
 */
public class SubmitTaskAction extends AbstractAction {

    private JiraTask task;

    public SubmitTaskAction(JiraTask task) {
        this.task = task;
        putValue(NAME, "Submit");
        putValue(SHORT_DESCRIPTION, "Submit Task Changes");
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/jira/task-submit.png")));
    }

    public void actionPerformed(ActionEvent e) {
        JiraTaskRepository repository = task.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);

        repository.submit(task);
    }
}
