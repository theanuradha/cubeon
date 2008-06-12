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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.List;
import org.netbeans.cubeon.tasks.core.api.RefreshableChildren;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha G
 */
class TaskFolderChildren extends Children.Keys<TaskFolder> implements RefreshableChildren {

    private final TaskFolder folder;

    TaskFolderChildren(TaskFolder folder) {
        this.folder = folder;

    }

    public void refreshContent() {
         System.out.println(folder.getName() );
        addNotify();
    }

    @Override
    protected Node[] createNodes(TaskFolder taskFolder) {
        RefreshableChildren rc = taskFolder.getLookup().lookup(RefreshableChildren.class);
        return new Node[]{new TaskFolderNode(taskFolder, rc.getChildren())};
    }

    @Override
    protected void addNotify() {
        List<TaskFolder> subFolders = folder.getSubFolders();
        setKeys(subFolders);
    }

    public Children getChildren() {
        return this;
    }
}
