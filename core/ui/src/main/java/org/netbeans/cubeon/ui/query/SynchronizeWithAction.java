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
package org.netbeans.cubeon.ui.query;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha
 */
public class SynchronizeWithAction extends AbstractAction implements Menu, Popup {

    private TaskQuery query;

    public SynchronizeWithAction(TaskQuery query) {
        this.query = query;
        putValue(NAME, "Synchronize With");
    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuPresenter() {
        JMenu menuItem = new JMenu(this);
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        List<TaskFolder> folders = fileSystem.getRootTaskFolder().getSubFolders();
        for (TaskFolder taskFolder : folders) {
            if (!query.equals(taskFolder.getTaskQuery())) {
                menuItem.add(new Link(taskFolder));
            }
        }
//        menuItem.addSeparator();
//        menuItem.add(new CreateNewFolder());
        return menuItem;
    }

    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    private class Link extends AbstractAction {

        private TaskFolder folder;

        public Link(TaskFolder folder) {
            this.folder = folder;
            putValue(NAME, folder.getName());
        }

        public void actionPerformed(ActionEvent e) {
            folder.setTaskQuery(query);
            List<TaskElement> taskElements = query.getTaskElements();
            for (TaskElement taskElement : taskElements) {
                if (!folder.contains(taskElement)) {
                    folder.addTaskElement(taskElement);
                }
            }
            TaskFolderRefreshable newTfr = folder.getLookup().lookup(TaskFolderRefreshable.class);
            newTfr.refreshNode();
        }
    }
}
