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
package org.netbeans.cubeon.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha
 */
public class TaskEditorFactoryImpl implements TaskEditorFactory {

    private final Map<TaskElement, TaskEditorTopComponent> map =
            new HashMap<TaskElement, TaskEditorTopComponent>();

    private TaskEditorTopComponent createTaskEditor(TaskElement element) {
        TaskEditorTopComponent topComponent = new TaskEditorTopComponent(this, element);
        map.put(element, topComponent);
        return topComponent;
    }

    public void notifyRemove(TaskElement element) {
        synchronized (this) {
            map.remove(element);
        }
    }

    public void openTask(TaskElement element) {
        TaskEditorTopComponent topComponent = null;
        synchronized (this) {
            topComponent = map.get(element);
        }
        if (topComponent == null) {
            topComponent = createTaskEditor(element);
        }
        topComponent.open();
        topComponent.requestActive();
    }

    public void closeTask(TaskElement element) {
        TaskEditorTopComponent topComponent = null;
        synchronized (this) {
            topComponent = map.get(element);
        }
        if (topComponent != null) {
            //component will call  notifyRemove
            topComponent.close();
        }
    }

    public boolean isOpen(TaskElement element) {
        TaskEditorTopComponent topComponent = null;
        synchronized (this) {
            topComponent = map.get(element);
        }
        return topComponent != null;
    }

    public List<TaskElement> getTasks() {
        return new ArrayList<TaskElement>(map.keySet());
    }

    public void refresh(TaskElement element) {
        TaskEditorTopComponent topComponent = null;
        synchronized (this) {
            topComponent = map.get(element);
        }
        if (topComponent != null) {
            topComponent.refresh();
        }
    }

    public void save(TaskElement element) {
        TaskEditorTopComponent topComponent = null;
        synchronized (this) {
            topComponent = map.get(element);
        }
        if (topComponent != null) {
            try {
                topComponent.save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
