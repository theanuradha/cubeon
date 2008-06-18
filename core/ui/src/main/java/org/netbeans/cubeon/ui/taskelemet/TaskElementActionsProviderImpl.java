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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskElementActionsProvider;

/**
 *
 * @author Anuradha
 */
public class TaskElementActionsProviderImpl implements TaskElementActionsProvider {

    public int getPosition() {
        return 1000;
    }

    public Action[] getActions(TaskElement element) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new OpenAction(element));
        actions.add(new OpenInBrowserAction(element));
        actions.add(null);
        actions.add(new CopyDetailsAction(element));
        actions.add(new MoveToDefault(element));
        actions.add(null);
        actions.add(new MoveToAction(element));
        actions.add(new MarkAsAction(element));

        return actions.toArray(new Action[actions.size()]);
    }
}
