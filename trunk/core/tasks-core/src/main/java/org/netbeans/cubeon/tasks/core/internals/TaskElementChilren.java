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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementComparator;
import org.netbeans.cubeon.tasks.spi.task.TaskElementFilter;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class TaskElementChilren extends Children.Keys<List<TaskElement>> implements RefreshableChildren {

    private final TaskFolder folder;
    TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);

    TaskElementChilren(TaskFolder folder) {
        this.folder = folder;

    }

    public void refreshContent() {
        if (isInitialized()) {
            addNotify();
        }
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
    protected Node[] createNodes(List<TaskElement> elements) {
        List<Node> ns = new ArrayList<Node>();
        for (TaskElement node : elements) {
            ns.add(factory.createTaskElementNode(folder, node, true));
        }
        return ns.toArray(new Node[ns.size()]);
    }

    @Override
    protected void addNotify() {
        System.out.println(folder.getName());

        List<TaskElement> elements = new ArrayList<TaskElement>();

        List<TaskElementFilter> filters = new ArrayList<TaskElementFilter>();
        for (TaskElementFilter taskElementFilter : Lookup.getDefault().lookupAll(TaskElementFilter.class)) {
            if (taskElementFilter.isEnable()) {
                filters.add(taskElementFilter);
            }
        }

        for (TaskElement taskElement : folder.getTaskElements()) {
            if (isFilterd(taskElement, filters)) {
                continue;
            }
            elements.add(taskElement);
        }
        for (TaskElementComparator comparator : Lookup.getDefault().lookupAll(TaskElementComparator.class)) {
            if (comparator.isEnable()) {
                Collections.sort(elements, comparator.getComparator());
            }
        }
        //workaround for hash code and equal methode chahges
        List<List<TaskElement>> tes = new ArrayList<List<TaskElement>>(1);
        tes.add(elements);
        setKeys(tes);
    }

    public Children getChildren() {
        return this;
    }
}
