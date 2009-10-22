/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.repository;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.persistence.RepositoryInfo;
import org.netbeans.cubeon.gcode.persistence.RepositoryPersistence;
import org.netbeans.cubeon.gcode.utils.GCodeExceptionHandler;
import org.netbeans.cubeon.tasks.core.api.RepositoryUtils;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class GCodeTaskRepositoryProvider implements TaskRepositoryType {

    static final String BASE_PATH = "cubeon/gcode_repositories/";//NOI18N
    private List<GCodeTaskRepository> taskRepositorys = new ArrayList<GCodeTaskRepository>();
    private FileObject baseDir = null;
    private AtomicBoolean initiailzed = new AtomicBoolean(false);
    private final RepositoryPersistence persistence;

    public GCodeTaskRepositoryProvider() {
        try {
            baseDir = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert baseDir != null;
        persistence = new RepositoryPersistence(FileUtil.toFile(baseDir));
    }

    public String getId() {
        return "gcode";//NOI18N
    }

    public String getName() {
        return NbBundle.getMessage(GCodeTaskRepositoryProvider.class, "LBL_Trac_Repository");
    }

    public String getDescription() {
        return NbBundle.getMessage(GCodeTaskRepositoryProvider.class, "LBL_Trac_Repository_Description");
    }
 
    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/gcode/gcode.png");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

 public TaskRepository persistRepository(TaskRepository repository) {

        final GCodeTaskRepository gCodeTaskRepository =
                repository.getLookup().lookup(GCodeTaskRepository.class);
        if (gCodeTaskRepository != null) {
            
            if (!taskRepositorys.contains(gCodeTaskRepository)) {
                taskRepositorys.add(gCodeTaskRepository);
            }
            List<RepositoryInfo> repositoryInfos = getRepositoryInfos();
            persistence.persistRepositoryInfos(repositoryInfos);
            GCodeRepositoryExtension extension = gCodeTaskRepository.getNotifier();
            extension.fireNameChenged();
            extension.fireDescriptionChenged();
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    try {
                        //reconnect and update repository
                        gCodeTaskRepository.reconnect();
                        gCodeTaskRepository.updateAttributes();
                        gCodeTaskRepository.synchronize();
                    } catch (GCodeException ex) {
                        GCodeExceptionHandler.notify(ex);
                    }
                }
            });

            return repository;
        }

        return null;
    }

    private  List<RepositoryInfo> getRepositoryInfos() {
        List<RepositoryInfo> repositoryInfos = new ArrayList<RepositoryInfo>(taskRepositorys.size());
        for (GCodeTaskRepository gctr : taskRepositorys) {
            repositoryInfos.add(new RepositoryInfo(gctr.getId(), gctr.getName(), gctr.getDescription(), gctr.getProject(), gctr.getUser(), RepositoryUtils.encodePassword(gctr.getUser(), gctr.getPassword())));
        }
        return repositoryInfos;
    }

    public boolean removeRepository(TaskRepository repository) {

        GCodeTaskRepository gcodeTaskRepository =
                repository.getLookup().lookup(GCodeTaskRepository.class);
        if (gcodeTaskRepository != null) {

            taskRepositorys.remove(gcodeTaskRepository);
            persistence.persistRepositoryInfos(getRepositoryInfos());
            List<String> taskIds = gcodeTaskRepository.getTaskIds();
            TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
            for (String id : taskIds) {
                TaskElement task = gcodeTaskRepository.getTaskElementById(id);
                if (task != null) {
                    gcodeTaskRepository.getNotifier().fireTaskRemoved(task);
                    factory.closeTask(task);
                }

            }
            //FIXME
//            List<TaskQuery> querys = gcodeTaskRepository.getQuerySupport().getTaskQuerys();
//            for (TaskQuery query : querys) {
//                gcodeTaskRepository.getQuerySupport().removeTaskQuery(query);
//            }

            try {
                FileObject baseDir1 = gcodeTaskRepository.getBaseDir();

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
            List<RepositoryInfo> repositoryInfos = persistence.getRepositoryInfos();
            for (RepositoryInfo repositoryInfo : repositoryInfos) {
                GCodeTaskRepository repository = new GCodeTaskRepository(this, repositoryInfo.getId(), repositoryInfo.getName(), repositoryInfo.getDescription());
                repository.setUser(repositoryInfo.getUser());
                repository.setProject(repositoryInfo.getProject());
                repository.setPassword(RepositoryUtils.decodePassword(repositoryInfo.getUser(),
                repository.getPassword()));
                taskRepositorys.add(repository);
            }
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
        return null;//new ConfigurationHandlerImpl(this);
    }

    public FileObject getBaseDir() {
        return baseDir;
    }
}
