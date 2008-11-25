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
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class EditTaskFolderAction extends AbstractAction {

    private static final long serialVersionUID = -5852493137443898928L;
    private TaskFolder folder;

    public EditTaskFolderAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(EditTaskFolderAction.class, "LBL_Edit_Folder"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(EditTaskFolderAction.class,
                "LBL_Edit_Folder_Description"));
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        //if default folder do not allow edit
        setEnabled(!fileSystem.getDefaultFolder().equals(folder));

    }

    public void actionPerformed(ActionEvent e) {
        final AddTaskFolder atf = AddTaskFolder.createEditUI(folder);
        atf.setFolderName(folder.getName());
        atf.setFolderDiescription(folder.getDescription());
        DialogDescriptor dd = new DialogDescriptor(atf,
                NbBundle.getMessage(EditTaskFolderAction.class, "TIT_Edit_Folder"));
        dd.setClosingOptions(new Object[]{
                    atf.getOKButton(),
                    DialogDescriptor.CANCEL_OPTION
                });
        dd.setOptions(new Object[]{
                    atf.getOKButton(),
                    DialogDescriptor.CANCEL_OPTION
                });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (atf.getOKButton() == ret) {

            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            //flag that control Node should refresh or not
            boolean refresh = false;
            String newName = atf.getFolderName();

            //check if name has change
            if (!folder.getName().equals(newName)) {

                //flag to refresh
                refresh = true;
            }
            String newDescription = atf.getFolderDescription();
            //check if description has change
            if (!newDescription.equals(folder.getDescription())) {
                refresh = true;

            }
            if (refresh) {
                fileSystem.rename(folder, newName, newDescription);
                TaskFolderRefreshable refreshProvider = folder.getParent().
                        getLookup().lookup(TaskFolderRefreshable.class);
                assert refreshProvider != null;
                refreshProvider.refreshNode();
            }
        }
    }
}
