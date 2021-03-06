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
import org.netbeans.cubeon.tasks.core.spi.RepositorysViewActionsProvider;
import org.netbeans.cubeon.ui.TaskExplorerAction;
import org.netbeans.cubeon.ui.query.NewQueryWizardAction;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class RepositorysViewActions implements RepositorysViewActionsProvider {


    public Action[] getActions() {
        return new Action[]{
                    new NewRepositoryWizardAction(),
                    new NewTaskWizardAction(NbBundle.getMessage(RepositorysViewActions.class, "LBL_Task_New")),
                    new NewQueryWizardAction(NbBundle.getMessage(RepositorysViewActions.class, "LBL_Query_New")),
                    null,
                    new TaskExplorerAction(NbBundle.getMessage(RepositorysViewActions.class,
                    "LBL_Show_Task_Explore")),
                    null
                };
    }
}
