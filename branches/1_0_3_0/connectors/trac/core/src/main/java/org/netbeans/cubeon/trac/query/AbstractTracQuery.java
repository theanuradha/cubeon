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
package org.netbeans.cubeon.trac.query;


import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;

/**
 *
 * @author Anuradha G
 */
public abstract class AbstractTracQuery implements TaskQuery {

    private final TracTaskRepository repository;
    private final String id;
    protected final QueryExtension extension;

    public enum Type {

        FILTER,UTIL
    }

    public AbstractTracQuery(TracTaskRepository repository, String id) {
        this.repository = repository;
        this.id = id;
        extension = new QueryExtension(this);
    }

    public String getId() {
        return id;
    }

    public TaskRepository getTaskRepository() {
        return repository;
    }

    public TracTaskRepository getRepository() {
        return repository;
    }

    public Notifier<TaskQueryEventAdapter> getNotifier() {
        return extension;
    }

    public QueryExtension getJiraExtension() {
        return extension;
    }

    public abstract Type getType();
}
