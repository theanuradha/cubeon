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
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskStatusProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementActionsProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.ui.taskelemet.CopyDetailsAction;
import org.netbeans.cubeon.ui.taskelemet.MoveToAction;
import org.netbeans.cubeon.ui.taskelemet.OpenAction;
import org.netbeans.cubeon.ui.taskelemet.OpenInBrowserAction;
import org.netbeans.cubeon.ui.taskelemet.SynchronizeTaskAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

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
        return SystemAction.get(OpenAction.class);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(SystemAction.get(OpenAction.class));
        actions.add(SystemAction.get(OpenInBrowserAction.class));
        actions.add(null);
        actions.add(new CopyDetailsAction(element));

        actions.add(null);

        actions.add(new MoveToAction(null, element));
        final List<TaskElementActionsProvider> providers =
                new ArrayList<TaskElementActionsProvider>(
                Lookup.getDefault().lookupAll(TaskElementActionsProvider.class));
        boolean sepetatorAdded = false;
        for (TaskElementActionsProvider provider : providers) {
            Action[] as = provider.getActions(element);
            for (Action action : as) {
                //check null and addSeparator
                if (action == null) {
                    //check sepetatorAdd to prevent adding duplicate Separators
                    if (!sepetatorAdded) {
                        //mark sepetatorAdd to true
                        sepetatorAdded = true;
                        actions.add(action);

                    }
                    continue;
                }
                actions.add(action);
                sepetatorAdded = false;
            }
        }
        if (!sepetatorAdded) {
            actions.add(null);
        }
        actions.add(SystemAction.get(SynchronizeTaskAction.class));
        return actions.toArray(new Action[0]);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        buffer.append("<xmp>").append(element.getDisplayName()).append("</xmp>");
        buffer.append("<font color=\"#808080\">");
        buffer.append("  ");
        TaskRepository repository = element.getTaskRepository();
        TaskPriorityProvider tpp = repository.getLookup().lookup(TaskPriorityProvider.class);
        if (tpp != null) {
            TaskPriority taskPriority = tpp.getTaskPriority(element);
            //see Issue 53
            if (taskPriority != null) {
                buffer.append(NbBundle.getMessage(TaskResultNode.class, "LBL_Priority")).append(":");
                buffer.append(taskPriority).append(",");
            }
        }
        TaskTypeProvider ttp = repository.getLookup().lookup(TaskTypeProvider.class);
        if (ttp != null) {
            TaskType taskType = ttp.getTaskType(element);
            if (taskType != null) {
                buffer.append(NbBundle.getMessage(TaskResultNode.class, "LBL_Type")).append(":");
                buffer.append(taskType).append(",");
            }
        }
        TaskStatusProvider tsp = repository.getLookup().lookup(TaskStatusProvider.class);
        if (tsp != null) {
            TaskStatus taskStatus = tsp.getTaskStatus(element);
            if (taskStatus != null) {
                buffer.append(NbBundle.getMessage(TaskResultNode.class, "LBL_Status")).append(":");
                buffer.append(taskStatus);
            }
        }
        buffer.append("</html>");
        return buffer.toString();
    }

    @Override
    public Transferable drag() throws IOException {
        return NodeTransfer.transferable(this, NodeTransfer.DND_COPY);
    }
}
