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

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.cubeon.local.repository.ui.ConfigurationHandlerImpl;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskRepositoryProvider implements TaskRepositoryType {

    static final String BASE_PATH = "cubeon/local_repositorys/";
    private List<LocalTaskRepository> taskRepositorys = new ArrayList<LocalTaskRepository>();
    private final LocalRepositoryPersistence persistence;
    private AtomicBoolean initiailzed = new AtomicBoolean(false);
    private FileObject baseDir = null;
    public LocalTaskRepositoryProvider() {
        
        try {
            baseDir = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert baseDir != null;
        persistence = new LocalRepositoryPersistence(this,baseDir);
    }

    public String getName() {
        return NbBundle.getMessage(LocalTaskRepository.class, "LBL_LOCAL_REPO");
    }

    public String getDescription() {
        return NbBundle.getMessage(LocalTaskRepository.class, "LBL_LOCAL_REPO_DEC");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/local/repository/local-connector.png");
    }

    public TaskRepository addRepository(TaskRepository repository) {

        LocalTaskRepository localTaskRepository =
                repository.getLookup().lookup(LocalTaskRepository.class);
        if (localTaskRepository != null) {
            persistence.addRepository(localTaskRepository);
            taskRepositorys.add(localTaskRepository);

            return repository;
        }

        return null;
    }

    public boolean removeRepository(TaskRepository repository) {

        LocalTaskRepository localTaskRepository =
                repository.getLookup().lookup(LocalTaskRepository.class);
        if (localTaskRepository != null) {
            persistence.removeRepository(localTaskRepository);
            taskRepositorys.remove(repository);

            return true;
        }

        return false;

    }

    public List<TaskRepository> getRepositorys() {

        if (!initiailzed.getAndSet(true)) {
            taskRepositorys.addAll(persistence.getLocalTaskRepositorys());

        }
        return new ArrayList<TaskRepository>(taskRepositorys);
    }

    public TaskRepository getRepositoryById(String id) {
        for (TaskRepository taskRepository : taskRepositorys) {
            if (taskRepository.getId().equals(id)) {
                return taskRepository;
            }
        }

        return null;
    }

    public ConfigurationHandler createConfigurationHandler() {
        return new ConfigurationHandlerImpl(this);
    }

    public FileObject getBaseDir() {
        return baseDir;
    }
    
    
}
