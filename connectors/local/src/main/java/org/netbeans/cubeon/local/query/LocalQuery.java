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
package org.netbeans.cubeon.local.query;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalQuery implements TaskQuery {

    private String name;
    private LocalTaskRepository repository;

    public LocalQuery(String name, LocalTaskRepository repository) {
        this.name = name;
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public TaskRepository getTaskRepository() {
        return repository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public void synchronize() {
        //TODO
    }

    public List<TaskElement> getTaskElements() {


        return new ArrayList<TaskElement>();
    }
}
