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
package org.netbeans.cubeon.tasks.core.internals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.Extension;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public class DefaultFileSystem implements TasksFileSystem {

    static final String TASKS_XML_PATH = "cubeon/tasks";
    private final PersistenceHandler handler;

    public DefaultFileSystem() {

        FileObject fileObject = null;

        try {
            fileObject = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), TASKS_XML_PATH);


        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert fileObject != null;
        handler = new PersistenceHandler(fileObject);


        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);

        RepositoryEventAdapter adapter = new RepositoryEventAdapter() {

            @Override
            public void taskElementIdChenged(String oldId, String newId) {
                handler.changeTaskElementId(oldId, newId);
            }

            @Override
            public void taskElementRemoved(TaskElement element) {
                List<TaskFolder> subFolders = getRootTaskFolder().getSubFolders();
                for (TaskFolder taskFolder : subFolders) {
                    if (taskFolder.contains(element)) {
                        TaskFolderImpl folderImpl = taskFolder.getLookup().lookup(TaskFolderImpl.class);
                        assert folderImpl != null;
                        folderImpl.removeTaskElement(element);
                        handler.removeTaskElement(folderImpl, element);
                    }
                }
            }
        };
        TaskRepositoryHandler repositoryHandler = context.getLookup().lookup(TaskRepositoryHandler.class);
        for (TaskRepository repository : repositoryHandler.getTaskRepositorys()) {
            Extension extension = repository.getLookup().lookup(Extension.class);
            extension.add(adapter);
        }
    }

    public TaskFolder getDefaultFolder() {

        return handler.getDefaultFolder();
    }

    public List<TaskFolder> getFolders() {

        return new ArrayList<TaskFolder>(handler.getRootfTaskFolder().getSubFolders());
    }

    public TaskFolder getRootTaskFolder() {
        return handler.getRootfTaskFolder();
    }

    public void addNewFolder(TaskFolder parent, TaskFolder folder) {
        TaskFolderImpl perantImpl = parent.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;
        TaskFolderImpl folderImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert folderImpl != null;
        handler.addFolder(folderImpl);
        perantImpl.addNewFolder(folder);

    }

    public TaskElement addTaskElement(TaskFolder folder, TaskElement element) {
        TaskFolderImpl perantImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;
        handler.addTaskElement(perantImpl, element);
        perantImpl.addTaskElement(element);
        return element;
    }

    public boolean removeFolder(TaskFolder parent, TaskFolder folder) {
        TaskFolderImpl perantImpl = parent.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;
        TaskFolderImpl folderImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert folderImpl != null;

        handler.removeFolder(folderImpl);

        return perantImpl.removeFolder(folder);
    }

    public boolean removeTaskElement(TaskFolder folder, TaskElement element) {
        TaskFolderImpl perantImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;
        handler.removeTaskElement(perantImpl, element);
        return perantImpl.removeTaskElement(element);

    }

    public boolean rename(TaskFolder folder, String name, String description) {
        TaskFolderImpl perantImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;

        perantImpl.setDescription(description);
        handler.persistFolder(perantImpl, name);

        return perantImpl.rename(name);
    }

    public void setTaskQuery(TaskFolder folder, TaskQuery query) {
        TaskFolderImpl perantImpl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert perantImpl != null;
        handler.setTaskQuery(perantImpl, query);
        perantImpl.setTaskQuery(query);
    }

    public TaskFolder newFolder(String folderName, String folderDescription) {
        return new TaskFolderImpl(null, folderName, folderDescription);
    }
}
