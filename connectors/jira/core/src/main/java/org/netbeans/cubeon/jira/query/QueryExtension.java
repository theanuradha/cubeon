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
package org.netbeans.cubeon.jira.query;

import java.util.Collection;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Anuradha
 */
public class QueryExtension implements Extension {

    private InstanceContent content;
    private Lookup lookup;
    private AbstractJiraQuery query;

    public QueryExtension(AbstractJiraQuery query) {
        this.query = query;
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
    void fireAttributesUpdated() {
        Collection<? extends TaskQueryEventAdapter> adapters = lookup.lookupAll(TaskQueryEventAdapter.class);
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.atributesupdated();
        }

    }

    void fireSynchronized() {
        Collection<? extends TaskQueryEventAdapter> adapters = lookup.lookupAll(TaskQueryEventAdapter.class);
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.querySynchronized();
        }

    }
    void fireRemoved() {
        Collection<? extends TaskQueryEventAdapter> adapters = lookup.lookupAll(TaskQueryEventAdapter.class);
        for (TaskQueryEventAdapter adapter : adapters) {
            adapter.removed();
        }

    }
}
