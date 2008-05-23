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
package org.netbeans.cubeon.local.repository;

import org.netbeans.cubeon.local.nodes.LocalRepositoryNode;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskRepository implements TaskRepository {

    private final String id;
    private String name;
    private String description;

    public LocalTaskRepository(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;//NOI18N
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this,
                new LocalRepositoryNode(this));
    }

    public TaskElement getTaskElementById(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TaskElement createTaskElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
