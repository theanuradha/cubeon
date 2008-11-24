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
import org.netbeans.cubeon.tasks.core.api.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
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
public class TaskElementChilren extends Children.Keys<TaskElementChilren.TaskKey> implements RefreshableChildren {

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

        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        //do not filter active task 
        if (!element.equals(context.getActive())) {

            for (TaskElementFilter filter : filters) {
                if (filter.isFiltered(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Node[] createNodes(TaskKey id) {

        return new Node[]{factory.createTaskElementNode(folder, id.getElement(), true)};

    }

    @Override
    protected void addNotify() {
        //remove 
        Node[] ns = getChildren().getNodes(true);
        for (Node node : ns) {
            try {
                node.destroy();
            } catch (IOException ex) {
                //ignore
            }
        }
        setKeys(new TaskKey[0]);
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
        //workaround for task id changing and missmatch hash code
        List<TaskKey> keys = new ArrayList<TaskKey>(elements.size());
        for (TaskElement element : elements) {
            keys.add(new TaskKey(element));
        }
        setKeys(keys);
    }

    public Children getChildren() {
        return this;
    }

    public static class TaskKey {

        private TaskElement element;

        public TaskKey(TaskElement element) {
            this.element = element;
        }

        public TaskElement getElement() {
            return element;
        }
    }
}
