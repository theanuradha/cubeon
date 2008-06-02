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
import org.netbeans.cubeon.taskui.api.TaskFolder;
import org.netbeans.cubeon.taskui.api.TaskFolderRefreshable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class AddTaskFolderAction extends AbstractAction {

    private final TaskFolder folder;

       
    public AddTaskFolderAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(AddTaskFolderAction.class, "LBL_Add_Folder"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(AddTaskFolderAction.class,
                "LBL_Add_Folder_Description"));
    }

    public void actionPerformed(ActionEvent e) {
        final AddTaskFolder atf = AddTaskFolder.createAddUI(folder);
        DialogDescriptor dd = new DialogDescriptor(atf,
                NbBundle.getMessage(AddTaskFolderAction.class, "TIT_Add_Folder"));
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

            folder.addNewFolder(atf.getFolderName(), atf.getFolderDescription());
            TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshProvider != null;
            refreshProvider.refreshContent();
        }
    }
}
