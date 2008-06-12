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
import javax.swing.KeyStroke;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderOparations;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.ui.taskfolder.AddTaskFolder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class EditTaskFolderAction extends AbstractAction {

    private TaskFolder folder;

    public EditTaskFolderAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(EditTaskFolderAction.class, "LBL_Edit_Folder"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(EditTaskFolderAction.class,
                "LBL_Edit_Folder_Description"));

        //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F2"));//NOI18N

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

            final TaskFolderOparations oparations =
                    folder.getLookup().lookup(TaskFolderOparations.class);
            assert oparations != null;
            //flag that control Node should refresh or not
            boolean refresh = false;
            String newName = atf.getFolderName();

            //check if name has change
            if (!folder.getName().equals(newName)) {
                oparations.rename(newName);
                //flag to refresh
                refresh = true;
            }
            String newDescription = atf.getFolderDescription();
            //check if description has change
            if (!newDescription.equals(folder.getDescription())) {
                oparations.setDescription(newDescription);

            }
            if (refresh) {
                TaskFolderRefreshable refreshProvider = folder.getParent().
                        getLookup().lookup(TaskFolderRefreshable.class);
                assert refreshProvider != null;
                refreshProvider.refeshNode();
            }
        }
    }
}
