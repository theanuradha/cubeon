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
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class RemoveTaskQueryAction extends AbstractAction {

    private TaskFolder folder;

    public RemoveTaskQueryAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(RemoveTaskQueryAction.class, "LBL_Remove_Query_Folder"));


    }

    public void actionPerformed(ActionEvent e) {
        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);

        fileSystem.setTaskQuery(folder, null);
    }
}
