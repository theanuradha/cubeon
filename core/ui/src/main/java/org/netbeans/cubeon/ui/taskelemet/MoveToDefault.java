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

import java.util.HashSet;
import java.util.Set;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Anuradha G
 */
public class MoveToDefault extends NodeAction {

    private MoveToDefault() {

        putValue(NAME, "Remove Task");
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        Set<TaskFolder> refreshFolders = new HashSet<TaskFolder>(activatedNodes.length);
        for (Node node : activatedNodes) {
            TaskElement element = node.getLookup().lookup(TaskElement.class);
            TaskFolder container = node.getLookup().lookup(TaskFolder.class);
            if (element != null &&
                    container != null) {
                TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                fileSystem.removeTaskElement(container, element);
                refreshFolders.add(container);
            }

        }
        for (TaskFolder container : refreshFolders) {
            TaskFolderRefreshable oldTfr = container.getLookup().lookup(TaskFolderRefreshable.class);
            if (oldTfr != null) {
                oldTfr.refreshNode();
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            if (node.getLookup().lookup(TaskElement.class) == null ||
                    node.getLookup().lookup(TaskFolder.class) == null) {
                return false;
            }

        }
        return true;
    }

    @Override
    public String getName() {
        return (String) getValue(NAME);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(MoveToDefault.class);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
