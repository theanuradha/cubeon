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
package org.netbeans.cubeon.trac.repository;

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha G
 */
public class TracRepositoryExtension implements Extension {

    private TracTaskRepository repository;
    private InstanceContent content;
    private Lookup lookup;

    public TracRepositoryExtension(TracTaskRepository repository) {
        this.repository = repository;
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
    }

    public final void remove(Object inst) {
        content.remove(inst);
    }

    public final void add(Object inst) {
        content.add(inst);
    }

    public Lookup getLookup() {
        return lookup;
    }
    //events---------------------------

    public void fireNameChenged() {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.nameChenged();
        }

    }

    public void fireDescriptionChenged() {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.descriptionChenged();
        }
    }

    public void fireQueryAdded(TaskQuery query) {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.queryAdded(query);
        }
    }

    public void fireQueryRemoved(TaskQuery query) {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.queryRemoved(query);
        }
    }

    public void fireStateChanged(TaskRepository.State state) {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.stateChanged(state);
        }
    }

    public void fireIdChanged(String oldId, String newId) {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.taskElementIdChenged(repository.getId(), oldId, newId);
        }
    }

    public void fireTaskRemoved(TaskElement element) {
        Collection<? extends RepositoryEventAdapter> adapters =
                lookup.lookupAll(RepositoryEventAdapter.class);
        for (RepositoryEventAdapter adapter : adapters) {
            adapter.taskElementRemoved(element);
        }
    }
}
