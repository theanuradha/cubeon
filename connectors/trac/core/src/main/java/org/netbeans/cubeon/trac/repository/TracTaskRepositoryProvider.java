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
package org.netbeans.cubeon.trac.repository;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class TracTaskRepositoryProvider implements TaskRepositoryType {

    static final String BASE_PATH = "cubeon/trac_repositories/";//NOI18N
    private List<TracTaskRepository> taskRepositorys = new ArrayList<TracTaskRepository>();
    private FileObject baseDir = null;
    private final TracRepositoryPersistence persistence;

    public TracTaskRepositoryProvider() {
        try {
            baseDir = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert baseDir != null;
        persistence = new TracRepositoryPersistence(this, baseDir);
    }

    public String getName() {
        return NbBundle.getMessage(TracTaskRepositoryProvider.class, "LBL_Trac_Repository");
    }

    public String getDescription() {
        return NbBundle.getMessage(TracTaskRepositoryProvider.class, "LBL_Trac_Repository_Description");
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/trac/trac.png");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public TaskRepository persistRepository(TaskRepository taskRepository) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeRepository(TaskRepository taskRepository) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskRepository> getRepositorys() {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FileObject getBaseDir() {
        return baseDir;
    }
}
