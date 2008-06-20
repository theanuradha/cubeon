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
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskType;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
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
    private List<TaskPriority> priorities = new ArrayList<TaskPriority>();
    private List<TaskType> types = new ArrayList<TaskType>();
    private List<TaskStatus> states = new ArrayList<TaskStatus>();
    private String contain;
    private boolean summary;
    private boolean description;

    public LocalQuery(String name, LocalTaskRepository repository) {
        this.name = name;
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<TaskPriority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<TaskPriority> priorities) {
        this.priorities = new ArrayList<TaskPriority>(priorities);
    }

    public LocalTaskRepository getRepository() {
        return repository;
    }

    public void setRepository(LocalTaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskStatus> getStates() {
        return states;
    }

    public void setStates(List<TaskStatus> states) {
        this.states = new ArrayList<TaskStatus>(states);
    }

    public List<TaskType> getTypes() {
        return types;
    }

    public void setTypes(List<TaskType> types) {
        this.types = new ArrayList<TaskType>(types);
    }

    public List<TaskElement> getTaskElements() {


        return repository.getTaskElements();//FIXME
    }

    public String getContain() {
        return contain;
    }

    public void setContain(String contain) {
        this.contain = contain;
    }

    public boolean isDescription() {
        return description;
    }

    public void setDescription(boolean description) {
        this.description = description;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }
}
