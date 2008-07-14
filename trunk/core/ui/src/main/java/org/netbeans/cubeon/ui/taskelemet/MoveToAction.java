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
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.ui.taskfolder.AddTaskFolder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha
 */
public class MoveToAction extends AbstractAction implements Menu, Popup {

    private TaskElement element;
    private TaskFolder container;

    public MoveToAction(TaskFolder container, TaskElement element) {
        this.element = element;
        this.container = container;
        if (container != null) {
            putValue(NAME, NbBundle.getMessage(MoveToAction.class, "LBL_Move_To"));
        } else {
            putValue(NAME, NbBundle.getMessage(MoveToAction.class, "LBL_Add_To"));
        }
    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuPresenter() {
        JMenu menuItem = new JMenu(this);
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        List<TaskFolder> folders = fileSystem.getRootTaskFolder().getSubFolders();
        boolean folderAdded = false;
        for (TaskFolder taskFolder : folders) {
            if (!taskFolder.contains(element)) {
                folderAdded = true;
                menuItem.add(new MoveAction(taskFolder));
            }
        }
        if (folderAdded) {
            menuItem.addSeparator();
        }
        menuItem.add(new CreateNewFolder());
        return menuItem;
    }

    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    private class MoveAction extends AbstractAction {

        private TaskFolder folder;

        public MoveAction(TaskFolder folder) {
            this.folder = folder;
            putValue(NAME, folder.getName());
            putValue(SMALL_ICON, new ImageIcon(NodeUtils.getTreeFolderIcon(true)));
        }

        public void actionPerformed(ActionEvent e) {
            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            if (container != null) {

                fileSystem.removeTaskElement(container, element);
                TaskFolderRefreshable oldTfr = container.getLookup().lookup(TaskFolderRefreshable.class);
                if (oldTfr != null) {
                    oldTfr.refreshNode();
                }
            }

            fileSystem.addTaskElement(folder, element);
            TaskFolderRefreshable newTfr = folder.getLookup().lookup(TaskFolderRefreshable.class);
            newTfr.refreshNode();

        }
    }

    public class CreateNewFolder extends AbstractAction {

        private TaskFolder folder;

        public CreateNewFolder() {
            putValue(NAME, "New Folder...");
            putValue(SMALL_ICON, new ImageIcon(NodeUtils.getTreeFolderIcon(true)));
            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            folder = fileSystem.getRootTaskFolder();
        }

        public void actionPerformed(ActionEvent e) {

            final AddTaskFolder atf = AddTaskFolder.createAddUI(folder);
            DialogDescriptor dd = new DialogDescriptor(atf, "New Task Folder");
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
                if (container != null) {
                    fileSystem.removeTaskElement(container, element);
                    TaskFolderRefreshable oldTfr = container.getLookup().lookup(TaskFolderRefreshable.class);
                    if (oldTfr != null) {
                        oldTfr.refreshNode();
                    }
                }
                TaskFolder newFolder = fileSystem.newFolder(atf.getFolderName(), atf.getFolderDescription());
                fileSystem.addNewFolder(fileSystem.getRootTaskFolder(), newFolder);
                fileSystem.addTaskElement(newFolder, element);
                TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
                assert refreshProvider != null;
                refreshProvider.refreshNode();

            }
        }
    }
}
