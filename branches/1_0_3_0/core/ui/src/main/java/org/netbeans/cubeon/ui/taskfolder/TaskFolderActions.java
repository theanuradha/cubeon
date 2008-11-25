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
package org.netbeans.cubeon.ui.taskfolder;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.spi.TaskFolderActionsProvider;
import org.netbeans.cubeon.ui.NavigateFromHereAction;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class TaskFolderActions implements TaskFolderActionsProvider {

    public Action[] getNewActions(final TaskFolder taskFolder) {
        return new Action[]{
                    new NewTaskWizardAction(NbBundle.getMessage(TaskFolderActions.class,
                    "LBL_Task"), taskFolder)
                };
    }


    public Action[] getActions(TaskFolder taskFolder) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new EditTaskFolderAction(taskFolder));
        actions.add(new DeleteTaskFolderAction(taskFolder));
        actions.add(new NavigateFromHereAction(taskFolder));
        if (taskFolder.getTaskQuery() != null) {
            actions.add(null);
            actions.add(new RemoveTaskQueryAction(taskFolder));

        }
        actions.add(null);
        actions.add(new RefreshTaskFolderAction(taskFolder));


        return actions.toArray(new Action[actions.size()]);
    }
}
