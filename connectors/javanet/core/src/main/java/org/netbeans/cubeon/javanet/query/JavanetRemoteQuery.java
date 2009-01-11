/*
 *  Copyright 2009 Tomas Knappek.
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

package org.netbeans.cubeon.javanet.query;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetRemoteQuery implements TaskQuery{

    JavanetTaskRepository _repository;
    String _id;
    QueryExtension _extension;
    String _projectName;

    public JavanetRemoteQuery(JavanetTaskRepository repository) {
        this._repository = repository;
        this._extension = new QueryExtension(this);
    }

    public JavanetRemoteQuery() {
        this._extension = new QueryExtension(this);
    }
    
    public String getId() {
        return _id;
    }
    
    public void setName(String name) {
        this._id = name;
    }

    /**
     * name is the id
     * @return
     */
    public String getName() {
        return _id;
    }

    public String getDescription() {
        return "";
    }

    public TaskRepository getTaskRepository() {
        return _repository;
    }

    public void setTaskRepository(TaskRepository taskRepo) {
        this._repository = (JavanetTaskRepository) taskRepo;
    }

    public void setProjectName(String name) {
        this._projectName = name;
    }

    public String getProjectName() {
        if (_repository != null) {
            return _repository.getName();
        }
        return _projectName;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, _extension);
    }

    public void synchronize() {
        //TODO: implement synchronize
    }

    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = _repository.executeRemoteQuery(_id);
        return elements;
    }

    public Notifier<TaskQueryEventAdapter> getNotifier() {
        return _extension;
    }

}
