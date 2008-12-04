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

package org.netbeans.cubeon.javanet.repository;

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTaskRepositoryNotifier extends Notifier<RepositoryEventAdapter> {

    private JavanetTaskRepository repository;
  

    public JavanetTaskRepositoryNotifier(JavanetTaskRepository repository) {
        this.repository = repository;
    }

    public void fireStateChanged(TaskRepository.State state) {
        Collection<? extends RepositoryEventAdapter> adapters =
                getAll();
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.stateChanged(state);
        }
    }
}
