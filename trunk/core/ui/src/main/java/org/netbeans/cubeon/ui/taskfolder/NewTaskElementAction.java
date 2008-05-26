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

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.context.api.CubeonContext;
import org.netbeans.cubeon.context.api.TaskFolder;
import org.netbeans.cubeon.context.api.TaskFolderRefreshable;
import org.netbeans.cubeon.context.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class NewTaskElementAction extends AbstractAction {

    private TaskFolder folder;

    public NewTaskElementAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(NewTaskElementAction.class, "LBL_Add_Task"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(NewTaskElementAction.class,
                "LBL_Add_Task_Description"));
    }

    public void actionPerformed(ActionEvent e) {
        //lookup CubeonContext
        CubeonContext cubeonContext = Lookup.getDefault().lookup(CubeonContext.class);
        assert cubeonContext != null : "CubeonContext can't be null";

        //lookup TaskRepositoryHandler
        TaskRepositoryHandler repositoryHandler = cubeonContext.getLookup().lookup(TaskRepositoryHandler.class);
        assert repositoryHandler != null : "TaskRepositoryHandler can't be null";
        List<TaskRepository> taskRepositorys = repositoryHandler.getTaskRepositorys();
        TaskRepository repository = taskRepositorys.get(0);
        folder.addTaskElement(repository.createTaskElement());
        TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
        assert refreshProvider != null;
        refreshProvider.refreshContent();

    }
}
