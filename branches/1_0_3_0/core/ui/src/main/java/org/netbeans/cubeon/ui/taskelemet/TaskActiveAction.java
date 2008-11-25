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
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class TaskActiveAction extends AbstractAction {

    private TaskElement element;
    private final CubeonContext context;

    public TaskActiveAction(TaskElement element) {
        context = Lookup.getDefault().lookup(CubeonContext.class);
        this.element = element;
        if (element.equals(context.getActive())) {
            putValue(NAME, NbBundle.getMessage(TaskActiveAction.class, "LBL_Deactive"));
        } else {
            putValue(NAME, NbBundle.getMessage(TaskActiveAction.class, "LBL_Activate"));
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (element.equals(context.getActive())) {
            context.setActive(null);
        } else {
            context.setActive(element);
        }
    }
}
