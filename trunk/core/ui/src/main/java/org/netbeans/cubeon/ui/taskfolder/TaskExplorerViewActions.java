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

import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.TaskExplorerViewActionsProvider;
import org.netbeans.cubeon.ui.TaskRepositoriesAction;
import org.netbeans.cubeon.ui.query.NewQueryWizardAction;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class TaskExplorerViewActions implements TaskExplorerViewActionsProvider {

    private final TaskFolder taskFolder;

    public TaskExplorerViewActions() {
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        taskFolder = fileSystem.getRootTaskFolder();
    }


    public Action[] getActions() {

        return new Action[]{
                    new RefreshTaskFolderAction(taskFolder),
                    null,
                    new ComparatorAction(taskFolder),
                    new FilterByPriorityAction(taskFolder),
                    new FilterCompletedAction(taskFolder),
                    null,
                    new TaskRepositoriesAction(NbBundle.getMessage(TaskExplorerViewActions.class,
                    "LBL_Show_Repository")),
                    null
                };
    }

    public Action[] getNewActions() {


        return new Action[]{
                    new AddTaskFolderAction(taskFolder),
                    new NewTaskWizardAction(NbBundle.getMessage(TaskExplorerViewActions.class,
                    "LBL_Task")),
                    new NewQueryWizardAction("Query...")
                };
    }
}
