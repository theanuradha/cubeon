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
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public class MoveToDefault extends AbstractAction {

    private TaskFolder defaultFolder;
    private TaskElement element;

    public MoveToDefault(TaskElement taskElement) {
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        this.element = taskElement;
        defaultFolder = fileSystem.getDefaultFolder();
        putValue(NAME, "Move to " + defaultFolder.getName());
    }

    public void actionPerformed(ActionEvent e) {


        TaskFolder old = findContainTaskFolder(element);
        if (old != null) {
            old.removeTaskElement(element);
            TaskFolderRefreshable oldTfr = old.getLookup().lookup(TaskFolderRefreshable.class);
            oldTfr.refeshNode();
        }
        defaultFolder.addTaskElement(element);
        TaskFolderRefreshable newTfr = defaultFolder.getLookup().
                lookup(TaskFolderRefreshable.class);
        newTfr.refeshNode();
    }

    private TaskFolder findContainTaskFolder(TaskElement element) {
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        List<TaskFolder> folders = fileSystem.getRootTaskFolder().getSubFolders();
        for (TaskFolder taskFolder : folders) {
            if (taskFolder.contains(element)) {
                return taskFolder;
            }

        }
        return null;
    }
}
