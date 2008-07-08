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
package org.netbeans.cubeon.ui.query;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskStatusProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.ui.taskelemet.CopyDetailsAction;
import org.netbeans.cubeon.ui.taskelemet.MoveToAction;
import org.netbeans.cubeon.ui.taskelemet.OpenAction;
import org.netbeans.cubeon.ui.taskelemet.OpenInBrowserAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Anuradha G
 */
public class TaskResultNode extends AbstractNode {

    private TaskElement element;

    public TaskResultNode(TaskElement element) {
        super(Children.LEAF, element.getLookup());

        this.element = element;
        this.element = element;
    }

    @Override
    public Image getIcon(int arg0) {
        return element.getImage();
    }

    @Override
    public Action getPreferredAction() {
        return new OpenAction(element);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
                    new OpenAction(element),
                    new OpenInBrowserAction(element),
                    null,
                    new CopyDetailsAction(element),
                    new MoveToAction(null, element)
                };
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        buffer.append(element.getName());
        buffer.append("<font color=\"#808080\">");
        buffer.append("  ");
        TaskRepository repository = element.getTaskRepository();
        TaskPriorityProvider tpp = repository.getLookup().lookup(TaskPriorityProvider.class);
        if (tpp != null) {
            buffer.append("Priority :");
            buffer.append(tpp.getTaskPriority(element).toString());
        }
        TaskTypeProvider ttp = repository.getLookup().lookup(TaskTypeProvider.class);
        if (ttp != null) {
            buffer.append(", Type :");
            buffer.append(ttp.getTaskType(element).toString());
        }
        TaskStatusProvider tsp = repository.getLookup().lookup(TaskStatusProvider.class);
        if (tsp != null) {
            buffer.append(", Status :");
            buffer.append(tsp.getTaskStatus(element).toString());
        }
        buffer.append("</html>");
        return buffer.toString();
    }
}