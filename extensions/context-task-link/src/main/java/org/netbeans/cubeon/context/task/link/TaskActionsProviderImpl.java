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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementActionsProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class TaskActionsProviderImpl implements TaskElementActionsProvider {

    public int getPosition() {
        return 1500;
    }

    public Action[] getActions(TaskElement element) {
        List<Action> actions = new ArrayList<Action>();
        TaskContextManager contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
        TaskContext context = contextManager.getActiveTaskContext();
        if (context != null && !context.getTask().equals(element)) {
            actions.add(new TaskLinkContextAction(element, context));
        }

        return actions.toArray(new Action[actions.size()]);
    }
}
