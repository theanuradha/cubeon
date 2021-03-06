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
package org.netbeans.cubeon.jira.repository;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.repository.ui.ConfigurationHandlerImpl;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.jira.utils.JiraExceptionHandler;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskRepositoryProvider implements TaskRepositoryType {

    static final String BASE_PATH = "cubeon/jira_repositories/";//NOI18N
    private List<JiraTaskRepository> taskRepositorys = new ArrayList<JiraTaskRepository>();
    private final JiraRepositoryPersistence persistence;
    private AtomicBoolean initiailzed = new AtomicBoolean(false);
    private FileObject baseDir = null;

    public JiraTaskRepositoryProvider() {
        try {
            baseDir = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert baseDir != null;
        persistence = new JiraRepositoryPersistence(this, baseDir);
    }

    public String getId() {
        return "jira";//NOI18N
    }

    public String getName() {
        return NbBundle.getMessage(JiraTaskRepositoryProvider.class, "LBL_Jira_Name");
    }

    public String getDescription() {
        return NbBundle.getMessage(JiraTaskRepositoryProvider.class, "LBL_Jira_Description");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/jira/repository/jira-logo.gif");
    }

    public TaskRepository persistRepository(TaskRepository repository) {

        final JiraTaskRepository jiraTaskRepository =
                repository.getLookup().lookup(JiraTaskRepository.class);
        if (jiraTaskRepository != null) {
            persistence.addRepository(jiraTaskRepository);
            if (!taskRepositorys.contains(jiraTaskRepository)) {
                taskRepositorys.add(jiraTaskRepository);
            }
            JiraRepositoryExtension extension = jiraTaskRepository.getNotifier();
            extension.fireNameChenged();
            extension.fireDescriptionChenged();
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    try {
                        //reconnect and update repository
                        jiraTaskRepository.reconnect();
                        jiraTaskRepository.updateAttributes();
                        jiraTaskRepository.synchronize();
                    } catch (JiraException ex) {
                        JiraExceptionHandler.notify(ex);
                    }

                }
            });

            return repository;
        }

        return null;
    }

    public boolean removeRepository(TaskRepository repository) {

        JiraTaskRepository jiraTaskRepository =
                repository.getLookup().lookup(JiraTaskRepository.class);
        if (jiraTaskRepository != null) {
            persistence.removeRepository(jiraTaskRepository);
            taskRepositorys.remove(jiraTaskRepository);
            List<String> taskIds = jiraTaskRepository.getTaskIds();
            TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
            for (String id : taskIds) {
                JiraTask task = jiraTaskRepository.getTaskElementById(id);
                if (task != null) {
                    jiraTaskRepository.getNotifier().fireTaskRemoved(task);
                    factory.closeTask(task);
                }

            }
            List<TaskQuery> querys = jiraTaskRepository.getQuerySupport().getTaskQuerys();
            for (TaskQuery query : querys) {
                jiraTaskRepository.getQuerySupport().removeTaskQuery(query);
            }
         
            try {
                FileObject baseDir1 = jiraTaskRepository.getBaseDir();
 
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
            taskRepositorys.addAll(persistence.getJiraTaskRepositorys());

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
