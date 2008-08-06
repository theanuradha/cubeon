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
import javax.swing.KeyStroke;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class DeleteTaskFolderAction extends AbstractAction {

    private TaskFolder folder;

    public DeleteTaskFolderAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(DeleteTaskFolderAction.class, "LBL_Delete_Folder"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));//NOI18N

    }

    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(DeleteTaskFolderAction.class, "LBL_Delete_Folder_Dec"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION);
        Object notify = DialogDisplayer.getDefault().notify(d);
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        if (notify == NotifyDescriptor.YES_OPTION) {


            TaskFolder defaultFolder = fileSystem.getDefaultFolder();
            TaskFolder parent = folder.getParent();
            List<TaskElement> taskElements = folder.getTaskElements();
            for (TaskElement taskElement : taskElements) {
                fileSystem.removeTaskElement(folder, taskElement);
                fileSystem.addTaskElement(defaultFolder, taskElement);
            }
            fileSystem.removeFolder(parent, folder);

            TaskFolderRefreshable refreshProvider = fileSystem.getRootTaskFolder().getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshProvider != null;
            refreshProvider.refreshNode();
        } else if (notify == NotifyDescriptor.NO_OPTION) {
            TaskFolder parent = folder.getParent();
            fileSystem.removeFolder(parent, folder);
            TaskFolderRefreshable refreshProvider = parent.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshProvider != null;
            refreshProvider.refreshNode();


        }

    }
}
