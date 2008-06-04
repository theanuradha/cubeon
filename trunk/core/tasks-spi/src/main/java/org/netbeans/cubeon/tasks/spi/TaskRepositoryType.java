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
package org.netbeans.cubeon.tasks.spi;

import java.awt.Image;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 * 
 * 
 */
public interface TaskRepositoryType {

    /**
     * 
     * @return
     */
    String getName();

    /**
     * 
     * @return
     */
    String getDescription();

    /**
     * 
     * @return
     */
    Image getImage();

    /**
     * 
     * @return
     */
    Lookup getLookup();

    /**
     * 
     * @param taskRepository
     * @return
     */
    TaskRepository addRepository(TaskRepository taskRepository);

    /**
     * 
     * @param taskRepository
     * @return
     */
    boolean removeRepository(TaskRepository taskRepository);

    /**
     * 
     * @return
     */
    List<TaskRepository> getRepositorys();

    /**
     * 
     * @param Id
     * @return
     */
    TaskRepository getRepositoryById(String Id);

    /**
     * 
     * @return
     */
    ConfigurationHandler createConfigurationHandler();

    /**
     * 
     */
    public interface ConfigurationHandler {

        /**
         * 
         * @param changeListener
         */
        void addChangeListener(ChangeListener changeListener);

        /**
         * 
         * @param changeListener
         */
        void removeChangeListener(ChangeListener changeListener);
        
        
        /**
         * 
         * @param repository
         */
        void setTaskRepository(TaskRepository repository);
        
        /**
         * 
         * @return
         */
        TaskRepository getTaskRepository();
        
        /**
         * 
         * @return
         */
        boolean isValidConfiguration();
        
        /**
         * 
         * @return
         */
        JComponent getComponent();
    }
}
