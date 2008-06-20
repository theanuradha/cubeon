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
package org.netbeans.cubeon.ui.internals;

import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskContainer;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha G
 */
public class TaskNodeFactoryImpl implements TaskNodeFactory {

    public Node createTaskElementNode(TaskElement element, boolean withChildern) {
        return TaskElementNode.createNode(null, element);
    }

    public Node createTaskRepositoryNode(TaskRepository repository, boolean withChildern) {
        return TaskRepositoryNode.createTaskRepositoryNode(repository, withChildern);
    }

    public Node createTaskElementNode(TaskContainer container, TaskElement element, boolean withChildern) {
        return TaskElementNode.createNode(container, element);
    }
}
