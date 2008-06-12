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

import org.netbeans.cubeon.tasks.core.api.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementFilter;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class TaskElementChilren extends Children.Keys<TaskElement> implements RefreshableChildren {

    private final TaskFolder folder;
    TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);

    TaskElementChilren(TaskFolder folder) {
        this.folder = folder;

    }

    public void clear() {
        Node[] ns = getNodes();
        for (Node node : ns) {
            try {
                node.destroy();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    public void refreshContent() {
       addNotify();
    }

    private static boolean isFilterd(TaskElement element, List<TaskElementFilter> filters) {


        for (TaskElementFilter filter : filters) {
                if (filter.isFiltered(element)) {
                    return true;
                }
        }
        return false;
    }

    @Override
    protected Node[] createNodes(TaskElement element) {
        return new Node[]{factory.createTaskElementNode(element)};
    }

    @Override
    protected void addNotify() {
        System.out.println(folder.getName() );

        List<TaskElement> elements = new ArrayList<TaskElement>();
        List<TaskElementFilter> filters = new ArrayList<TaskElementFilter>();
        for (TaskElementFilter taskElementFilter : Lookup.getDefault().lookupAll(TaskElementFilter.class)) {
            if (taskElementFilter.isEnable()) {
                filters.add(taskElementFilter);
            }
        }
        //todo add Comparator


        for (TaskElement taskElement : folder.getTaskElements()) {
            if (isFilterd(taskElement, filters)) {
                continue;
            }
            elements.add(taskElement);
        }
        setKeys(elements);
    }

    public Children getChildren() {
        return this;
    }
}
