/*
 *  Copyright 2008 Tomas Knappek.
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

package org.netbeans.cubeon.javanet.repository;

import java.awt.Image;
import java.util.List;
import org.netbeans.cubeon.javanet.persistence.JavanetRepoPersistence;
import org.netbeans.cubeon.javanet.ui.ConfigurationHandlerImpl;
import org.netbeans.cubeon.persistence.PersistenceFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTaskRepositoryProvider implements TaskRepositoryType {

    JavanetRepoPersistence _persistence = null;
    List<JavanetTaskRepository> _repos = null;

    public JavanetTaskRepositoryProvider() {
        _persistence = PersistenceFactory.getPersistence(JavanetRepoPersistence.class);
    }



    public String getId() {
        return "java.net";
    }

    public String getName() {
        return NbBundle.getMessage(JavanetTaskRepositoryProvider.class, "LBL_Javanet_Repository");
    }

    public String getDescription() {
        return NbBundle.getMessage(JavanetTaskRepositoryProvider.class, "LBL_Javanet_Repository_Description");
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/javanet/javanet.png");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public TaskRepository persistRepository(TaskRepository taskRepository) {
        final JavanetTaskRepository javanetTaskRepository =
                taskRepository.getLookup().lookup(JavanetTaskRepository.class);
        if (javanetTaskRepository != null) {
            _persistence.add(javanetTaskRepository);
            _persistence.save();
            _repos.add(javanetTaskRepository);

            JavanetTaskRepositoryNotifier notifier = javanetTaskRepository.getNotifier();
            //notifier.fireNameChenged();
            //notifier.fireDescriptionChenged();
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    try {
                        //reconnect and update repository
                        //javanetTaskRepository.reconnect();
                        //javanetTaskRepository.updateAttributes();
                        javanetTaskRepository.synchronize();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });

            return taskRepository;

        }
        return null;
    }

    public boolean removeRepository(TaskRepository taskRepository) {
        JavanetTaskRepository javanetTaskRepository =
                taskRepository.getLookup().lookup(JavanetTaskRepository.class);
        try {
            _persistence.remove(javanetTaskRepository.getId());
            _persistence.save();
            _repos.remove(javanetTaskRepository);
            return true;
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
            return false;
        }
    }

    public List<JavanetTaskRepository> getRepositorys() {
        if (_repos == null) {
            _repos = _persistence.getAll();

            //TODO: this is ugly - should be redesigned
            for (JavanetTaskRepository repo : _repos) {
                repo.setRepositoryProvider(this);
            }
        }
        return _repos;
    }

    public TaskRepository getRepositoryById(String Id) {
        return _persistence.getById(Id);
    }

    public ConfigurationHandler createConfigurationHandler() {
        return new ConfigurationHandlerImpl(this);
    }

}
