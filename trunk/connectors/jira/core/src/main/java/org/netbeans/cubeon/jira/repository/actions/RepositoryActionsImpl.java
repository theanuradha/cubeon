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
package org.netbeans.cubeon.jira.repository.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryActionsProvider;

/**
 *
 * @author Anuradha
 */
public class RepositoryActionsImpl implements TaskRepositoryActionsProvider {

    public int getPosition() {
        return 2000;
    }

    public Action[] getActions(TaskRepository repository) {

        JiraTaskRepository jtr = repository.getLookup().lookup(JiraTaskRepository.class);
        if (jtr != null) {
            List<Action> actions = new ArrayList<Action>();

            actions.add(new ReconnectAction(jtr));
            actions.add(new UpdateAttributesAction(jtr));
            actions.add(null);
            return actions.toArray(new Action[actions.size()]);
        }
        return new Action[0];
    }
}
