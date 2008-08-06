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
package org.netbeans.cubeon.context.task.link;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class TaskLinkContextAction extends AbstractAction {

    private final TaskElement element;
    private final boolean remove;
    private final TaskLinkResourceSet resourceSet;

    public TaskLinkContextAction(final TaskElement element, TaskContext context) {
        this.element = element;
        resourceSet = context.getLookup().lookup(TaskLinkResourceSet.class);
        if (resourceSet != null && resourceSet.contains(element)) {
            putValue(NAME, NbBundle.getMessage(TaskLinkContextAction.class, "LBL_Remove_Context"));
            remove = true;
        } else {
            putValue(NAME, NbBundle.getMessage(TaskLinkContextAction.class, "LBL_Add_Context"));
            remove = false;

        }

        setEnabled(resourceSet != null);
    }

    public void actionPerformed(ActionEvent e) {
        if (remove) {
            resourceSet.remove(element);
        } else {
            resourceSet.addTaskResource(new TaskLinkResource(element));
        }
    }
}
