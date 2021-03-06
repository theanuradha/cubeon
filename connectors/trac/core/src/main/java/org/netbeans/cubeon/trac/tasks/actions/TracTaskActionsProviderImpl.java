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
package org.netbeans.cubeon.trac.tasks.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementActionsProvider;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha
 */
public class TracTaskActionsProviderImpl implements TaskElementActionsProvider {

    public Action[] getActions(TaskElement element) {
        List<Action> actions = new ArrayList<Action>();
        TracTask tracTask = element.getLookup().lookup(TracTask.class);
        if (tracTask != null) {
            actions.add(null);
            actions.add(new SubmitTaskAction(tracTask));
            actions.add(new RevertChangesAction(tracTask));
            actions.add(null);

        }

        return actions.toArray(new Action[actions.size()]);
    }
}
