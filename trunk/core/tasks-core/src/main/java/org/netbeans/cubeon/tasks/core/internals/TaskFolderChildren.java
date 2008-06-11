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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

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
        Node[] ns = getNodes();
        for (Node node : ns) {
            try {
                node.destroy();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        remove(ns);

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
        TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);
        for (TaskElement taskElement : elements) {
            Node node = factory.createTaskElementNode(taskElement);

            assert node != null;
            newNodes.add(node);
        }
        add(newNodes.toArray(new Node[0]));
    }
}
