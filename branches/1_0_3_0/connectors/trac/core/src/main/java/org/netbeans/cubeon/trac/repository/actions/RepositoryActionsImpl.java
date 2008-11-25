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
package org.netbeans.cubeon.trac.repository.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryActionsProvider;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;

/**
 *
 * @author Anuradha
 */
public class RepositoryActionsImpl implements TaskRepositoryActionsProvider {


    public Action[] getActions(TaskRepository repository) {

        TracTaskRepository ttr = repository.getLookup().lookup(TracTaskRepository.class);
        if (ttr != null) {
            List<Action> actions = new ArrayList<Action>();

            actions.add(new ReconnectAction(ttr));
            actions.add(new UpdateAttributesAction(ttr));
            actions.add(null);
            return actions.toArray(new Action[actions.size()]);
        }
        return new Action[0];
    }
}
