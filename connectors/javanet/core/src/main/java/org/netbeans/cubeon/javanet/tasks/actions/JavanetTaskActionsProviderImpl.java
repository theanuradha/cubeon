/*
 *  Copyright 2008 Tomas Knappek.
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

package org.netbeans.cubeon.javanet.tasks.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.javanet.tasks.JavanetTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementActionsProvider;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTaskActionsProviderImpl implements TaskElementActionsProvider {

    public Action[] getActions(TaskElement element) {
        List<Action> actions = new ArrayList<Action>();
        JavanetTask task = element.getLookup().lookup(JavanetTask.class);
        if (task != null) {
            actions.add(null);
            actions.add(new SubmitTaskAction(task));
            actions.add(null);

        }

        return actions.toArray(new Action[actions.size()]);
    }

}
