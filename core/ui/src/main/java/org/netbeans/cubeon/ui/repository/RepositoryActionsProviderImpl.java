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
package org.netbeans.cubeon.ui.repository;

import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryActionsProvider;
import org.netbeans.cubeon.ui.query.NewQueryWizardAction;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class RepositoryActionsProviderImpl implements TaskRepositoryActionsProvider {


    public Action[] getActions(TaskRepository repository) {
        NewTaskWizardAction taskWizardAction = new NewTaskWizardAction(
                NbBundle.getMessage(RepositoryActionsProviderImpl.class, "LBL_Task_New"));
        taskWizardAction.preferredRepository(repository);
        NewQueryWizardAction queryWizardAction = new NewQueryWizardAction(
                NbBundle.getMessage(RepositoryActionsProviderImpl.class, "LBL_Query_New"));
        queryWizardAction.preferredRepository(repository);
        return new Action[]{
                    taskWizardAction,
                    queryWizardAction,
                    null,
                    new SynchronizeTasksAction(repository),
                    null,
                    new RepositoryRemoveAction(repository),
                    null
                };
    }
}
