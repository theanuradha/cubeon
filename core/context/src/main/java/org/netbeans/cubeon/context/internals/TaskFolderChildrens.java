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
package org.netbeans.cubeon.context.internals;

import org.netbeans.cubeon.context.spi.TaskFolder;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha G
 */
public class TaskFolderChildrens extends Children.Keys<TaskFolder> {

    private TaskFolder folder;

    public TaskFolderChildrens(TaskFolder folder) {
        this.folder = folder;
    }

    @Override
    protected Node[] createNodes(TaskFolder taskFolder) {
        Node node = taskFolder.getLookup().lookup(Node.class);
        return node != null ? new Node[]{node} : new Node[]{};
    }

    @Override
    protected void addNotify() {
        setKeys(folder.getSubFolders());
    }
}
