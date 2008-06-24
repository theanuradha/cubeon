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
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.ui.taskelemet.CopyDetailsAction;
import org.netbeans.cubeon.ui.taskelemet.MoveToAction;
import org.netbeans.cubeon.ui.taskelemet.OpenAction;
import org.netbeans.cubeon.ui.taskelemet.OpenInBrowserAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Anuradha
 */
public class TaskResultNode extends AbstractNode {

    private static final String TAG = "<font color=\"#808080\"> <s> ";
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
        if (element.isCompleted()) {

            buffer.append(TAG);
            buffer.append(element.getName());
            buffer.append("</s>");
        } else {
            buffer.append(element.getName());
        }
        buffer.append("<font color=\"#808080\">");
        buffer.append("  ");
        buffer.append("Priority :");
        buffer.append(element.getPriority().toString());
        buffer.append(", Type :");
        buffer.append(element.getType().toString());
        buffer.append(", Status :");
        buffer.append(element.getStatus().toString());
        buffer.append("</html>");
        return buffer.toString();
    }
}
