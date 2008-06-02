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
package org.netbeans.cubeon.taskui.internals;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.taskui.api.TaskFolder;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha G
 */
class TaskFolderChildren extends Children.Array {

    private final TaskFolder folder;

    TaskFolderChildren(TaskFolder folder) {
        this.folder = folder;
        refreshContent();
    }

    void clear() {
        remove(getNodes());

    }

    void refreshContent() {
        clear();
        List<Node> newNodes = new ArrayList<Node>();

        List<TaskFolder> subFolders = folder.getSubFolders();


        for (TaskFolder taskFolder : subFolders) {
            Node node = taskFolder.getLookup().lookup(Node.class);

            assert node != null;
            newNodes.add(node);
        }
        List<TaskElement> elements = folder.getTaskElements();
        //todo add Comparator
        for (TaskElement taskElement : elements) {
            Node node = taskElement.getLookup().lookup(Node.class);

            assert node != null;
            newNodes.add(node);
        }
        add(newNodes.toArray(new Node[0]));
    }
}
