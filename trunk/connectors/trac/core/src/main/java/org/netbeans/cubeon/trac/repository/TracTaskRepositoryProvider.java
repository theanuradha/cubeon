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

import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.trac.repository.ui.ConfigurationHandlerImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
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
    private AtomicBoolean initiailzed = new AtomicBoolean(false);
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

    public TaskRepository persistRepository(TaskRepository repository) {

        final TracTaskRepository tracTaskRepository =
                repository.getLookup().lookup(TracTaskRepository.class);
        if (tracTaskRepository != null) {
            persistence.addRepository(tracTaskRepository);
            if (!taskRepositorys.contains(tracTaskRepository)) {
                taskRepositorys.add(tracTaskRepository);
            }
            TracRepositoryExtension extension = tracTaskRepository.getExtension();
            extension.fireNameChenged();
            extension.fireDescriptionChenged();
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    //TODO add read attibute
                }
            });

            return repository;
        }

        return null;
    }

    public boolean removeRepository(TaskRepository repository) {

        TracTaskRepository tracTaskRepository =
                repository.getLookup().lookup(TracTaskRepository.class);
        if (tracTaskRepository != null) {
            persistence.removeRepository(tracTaskRepository);
            taskRepositorys.remove(repository);
//            List<String> taskIds = jiraTaskRepository.getTaskIds();
//            TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
//            for (String id : taskIds) {
//                JiraTask task = jiraTaskRepository.getTaskElementById(id);
//                if (task != null) {
//                    jiraTaskRepository.getExtension().fireTaskRemoved(task);
//                    factory.closeTask(task);
//                }
//
//            }
//            List<TaskQuery> querys = jiraTaskRepository.getQuerySupport().getTaskQuerys();
//            for (TaskQuery query : querys) {
//                jiraTaskRepository.getQuerySupport().removeTaskQuery(query);
//            }

            try {
                FileObject baseDir1 = tracTaskRepository.getBaseDir();

                baseDir1.delete();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return true;
        }

        return false;
    }

    public List<TaskRepository> getRepositorys() {
        if (!initiailzed.getAndSet(true)) {
            taskRepositorys.addAll(persistence.getTracTaskRepositorys());

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
