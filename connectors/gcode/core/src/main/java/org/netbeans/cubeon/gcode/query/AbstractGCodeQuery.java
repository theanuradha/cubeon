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
package org.netbeans.cubeon.gcode.query;


import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;

/**
 *
 * @author Anuradha G
 */
public abstract class AbstractGCodeQuery implements TaskQuery {

    private final GCodeTaskRepository repository;
    private final String id;
    protected final QueryExtension extension;

    public enum Type {

        FILTER,UTIL
    }

    public AbstractGCodeQuery(GCodeTaskRepository repository, String id) {
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

    public GCodeTaskRepository getRepository() {
        return repository;
    }

    public Notifier<TaskQueryEventAdapter> getNotifier() {
        return extension;
    }

    public QueryExtension getGcodeExtension() {
        return extension;
    }

    public abstract Type getType();
}
