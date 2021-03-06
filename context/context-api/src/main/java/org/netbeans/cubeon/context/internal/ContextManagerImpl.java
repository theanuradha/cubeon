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
package org.netbeans.cubeon.context.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextHandler;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResouresProvider;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
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
public class ContextManagerImpl implements TaskContextManager {

    static final String BASE_PATH = "cubeon/contexts/";//NOI18N
    private FileObject contextDir;
    private static final Object LOCK = new Object();
    private TaskContext activeContext;
    private TaskContextHandler activeContextHandler;
    private final CubeonContext context;

    public ContextManagerImpl() {
        try {
            contextDir = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert contextDir != null;
        context = Lookup.getDefault().lookup(CubeonContext.class);


// <editor-fold defaultstate="collapsed" desc="TODO: REMOVE after version 1.2">
        //move with old context data to repository base location
        File toFile = FileUtil.toFile(contextDir);
        if (toFile != null) {
            File[] listFiles = toFile.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".xml");
                }
            });
            if (listFiles.length > 0) {
                //lookup CubeonContext

                TaskRepositoryHandler repositoryHandler = context.getLookup().lookup(TaskRepositoryHandler.class);
                List<TaskRepository> taskRepositorys = repositoryHandler.getTaskRepositorys();
                OUTER:
                for (File file : listFiles) {
                    for (TaskRepository repository : taskRepositorys) {
                        String fileName = file.getName().replace(".xml", "");
                        TaskElement taskElement = repository.getTaskElementById(fileName);
                        if (taskElement != null) {
                            try {
                                FileObject taskContextDir = FileUtil.createFolder(contextDir, repository.getId());
                                FileObject taskFileObject = FileUtil.toFileObject(file);
                                if (taskFileObject != null) {
                                    FileUtil.moveFile(taskFileObject, taskContextDir, fileName);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(ContextManagerImpl.class.getName()).warning(ex.getMessage());
                            }
                            continue OUTER;
                        }
                    }
                    file.delete();
                }
            }

        }// </editor-fold>

        context.addContextListener(new CubeonContextListener() {

            public void taskActivated(TaskElement element) {
            }

            public void taskDeactivated(TaskElement element) {
                synchronized (LOCK) {
                    if (activeContext != null && activeContext.getTask().equals(element)) {
                        activeContext = null;
                        activeContextHandler = null;
                    }
                }
            }
        });

    }

    public TaskNodeView getContextView() {

        TaskNodeView contextView = null;
        Lookup.getDefault().lookupAll(TaskNodeView.class);//FIXME may be lookup bug
        contextView = Lookup.getDefault().lookup(ContextNodeView.class);
        assert contextView != null;
        return contextView;
    }

    public List<TaskResouresProvider> getAllResouresProviders() {
        Collection<? extends TaskResouresProvider> providers = Lookup.getDefault().lookupAll(TaskResouresProvider.class);
        return new ArrayList<TaskResouresProvider>(providers);
    }

    public List<TaskResouresProvider> getResouresProviders(TaskElement element) {
        List<TaskResouresProvider> supportedProviders = new ArrayList<TaskResouresProvider>();
        List<TaskResouresProvider> providers = getAllResouresProviders();
        for (TaskResouresProvider provider : providers) {
            if (provider.isSupported(element)) {
                supportedProviders.add(provider);
            }
        }

        return supportedProviders;
    }

    public TaskContextHandler getContextHandler(TaskElement element) {
        TaskRepository taskRepository = element.getTaskRepository();

        try {
            FileObject taskContextDir = FileUtil.createFolder(contextDir, taskRepository.getId());
            return new ContextHandlerImpl(taskContextDir, element);
        } catch (IOException ex) {
            Logger.getLogger(ContextManagerImpl.class.getName()).warning(ex.getMessage());
        }
        //use old dir
        return new ContextHandlerImpl(contextDir, element);
    }

    public TaskContext getTaskContext(TaskElement element) {
        return new TaskContext(element);
    }

    public TaskContext getActiveTaskContext() {

        synchronized (LOCK) {
            if (activeContext == null && context.getActive() != null) {
                activeContext = new TaskContext(context.getActive());
            }
        }

        return activeContext;
    }

    public TaskContextHandler getActiveContextHandler() {
        synchronized (LOCK) {
            if (activeContextHandler == null && context.getActive() != null) {
                activeContextHandler = getContextHandler(context.getActive());
            }
        }

        return activeContextHandler;
    }
}
