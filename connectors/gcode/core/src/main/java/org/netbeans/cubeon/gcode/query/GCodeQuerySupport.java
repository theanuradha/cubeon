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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.cubeon.gcode.persistence.QueryPersistence;
import org.netbeans.cubeon.gcode.query.ui.GCodeFilterQueryEditor;
import org.netbeans.cubeon.gcode.repository.GCodeRepositoryExtension;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Anuradha
 */
public class GCodeQuerySupport implements TaskQuerySupportProvider {


    private GCodeTaskRepository repository;
    private GCodeRepositoryExtension extension;
    private final GCodeOGChangesQuery outgoingQuery;
    private static Logger LOG = Logger.getLogger(GCodeQuerySupport.class.getName());
    private QueryPersistence handler;

    public GCodeQuerySupport(GCodeTaskRepository repository, GCodeRepositoryExtension extension) {
        this.repository = repository;
        this.extension = extension;
        outgoingQuery = new GCodeOGChangesQuery(repository);
        handler = new QueryPersistence(repository, FileUtil.toFile(repository.getBaseDir()));
    }

    public AbstractGCodeQuery createTaskQuery(AbstractGCodeQuery.Type type) {
        switch (type) {
            case FILTER:
                return new GCodeFilterQuery(repository, handler.nextId());
        }

        return null;

    }

    public void refresh() {
        handler.refresh();
    }

    public List<TaskQuery> getTaskQuerys() {
        ArrayList<TaskQuery> arrayList = new ArrayList<TaskQuery>(handler.getFilterQuerys());
        arrayList.add(0, outgoingQuery);
        return arrayList;
    }

    public ConfigurationHandler createConfigurationHandler(TaskQuery query) {
        GCodeFilterQueryEditor configurationHandler = new GCodeFilterQueryEditor(this);
        if (query != null) {
            AbstractGCodeQuery gCodeQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
            switch (gCodeQuery.getType()) {
                case FILTER: {
                    configurationHandler.setQuery((GCodeFilterQuery) gCodeQuery);
                  
                }
                break;
            }

        } 
        return configurationHandler;
    }

    public GCodeTaskRepository getTaskRepository() {
        return repository;
    }

   

    public void addTaskQuery(TaskQuery query) {

        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.persist(((GCodeFilterQuery) localQuery));

        extension.fireQueryAdded(query);
    }

    public void modifyTaskQuery(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.persist((GCodeFilterQuery) localQuery);
        localQuery.getGcodeExtension().fireAttributesUpdated();
    }

    public void removeTaskQuery(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(GCodeFilterQuery.class);
        if (localQuery.getType() == AbstractGCodeQuery.Type.UTIL) {
            return;
        }
        handler.remove((GCodeFilterQuery) localQuery);
        extension.fireQueryRemoved(localQuery);
        localQuery.getGcodeExtension().fireRemoved();
    }

    public TaskQuery findTaskQueryById(String id) {
        for (TaskQuery query : getTaskQuerys()) {
            if (id.equals(query.getId())) {
                return query;
            }
        }
        return null;
    }

    public boolean canModify(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public boolean canRemove(TaskQuery query) {
        AbstractGCodeQuery localQuery = query.getLookup().lookup(AbstractGCodeQuery.class);
        switch (localQuery.getType()) {
            case FILTER:
                return true;
            default:
                return false;
        }

    }

    public GCodeOGChangesQuery getOutgoingQuery() {
        return outgoingQuery;
    }

   
    
}
