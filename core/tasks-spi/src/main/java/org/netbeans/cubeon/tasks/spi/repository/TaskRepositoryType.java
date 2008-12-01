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
package org.netbeans.cubeon.tasks.spi.repository;

import java.awt.Image;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 * This is the interface used to register a new connector.
 * Any conector should implement TaskRepositoryType
 * @author Anuradha G
 * @version 1.0
 */
public interface TaskRepositoryType {
    /**
     * Returns unique Id of implementing connector.
     * @return
     */
    String getId();

    /**
     * Returns the name of the implementing connector.
     * This name will be used as the alias to refer to this connector type.
     * @return Name of the conector
     */
    String getName();

    /**
     * Returns a textual description of the connector.
     * This description wil be used to hint the user of this connectors purpose when required
     * @return Textual Descrptions
     */
    String getDescription();

    /**
     * Returns an Image object which will be displayd as the default icon for the connector type
     * @return Image for the connector Icons
     */
    Image getImage();

    /**
     * Returns a Lookup instance which encapsulates all the supported features by the connector implementaion.
     * @return Lookup containing supported featurss
     */
    Lookup getLookup();

    /**
     * Saves the curent changes in the repository
     * @param taskRepository The repository provided by this conector
     * @return Saved instance of the TaskRepository
     */
    TaskRepository persistRepository(TaskRepository taskRepository);

    /**
     * Removes the given TaskRepository from the current connector instance
     * @param taskRepository he repository instance to be removed.
     * @return Whether the removal was a succes sor not
     */
    boolean removeRepository(TaskRepository taskRepository);

    /**
     * Returns a list of all the TaskRepository's registered with the implementing connector instance
     * @return List of TaskRepository instances
     */
    List<? extends TaskRepository> getRepositorys();

    /**
     * Returns a repository associated with a specific id (String)
     * @param Id id of the repository which needs to be retuned
     * @return TaskRepository asociated with the given Id. If there are no TaskRepository's registered with the name then the method should return null
     */
    TaskRepository getRepositoryById(String Id);

    /**
     * Creates a new ConfigurationHandler and returns to be used by other associating classes
     * @return The newly created ConfigurationHandler object
     */
    ConfigurationHandler createConfigurationHandler();

    /**
     * A ConfigurtionHandler instance provides all the required methods which lets
     * nother associate clas customize the repository settings.
     * @author Anuradha G
     * @version 1.0
     */
    public interface ConfigurationHandler {

        /**
         * Registers new change listener which will be invoked when a change occurs in the ConfigurationHandler UI.
         * @param changeListener INstance of the ChangeListenr class which will handle the change
         */
        void addChangeListener(ChangeListener changeListener);

        /**
         * Removes a change listener from the set of registered listeners
         * @param changeListener Instance of the ChangeListenr to be removed
         */
        void removeChangeListener(ChangeListener changeListener);

        /**
         * Sets the task repository which needs to be managed by the curent instance.
         * @param repository TsskRepository instance to be managed
         */
        void setTaskRepository(TaskRepository repository);

        /**
         * Get the TaskRpository managed by this ConfigurationHandler
         * @return TaskRepository instance managed by the configuration handler
         */
        TaskRepository getTaskRepository();

        /**
         * Retruns whether the current configuraions are valid or not
         * @return Retruns  true if the configurations are valid.
         */
        boolean isValidConfiguration();

        /**
         * Return the GUI widget which will be displayed to the user which will allow modifying the configurations
         * @return GUI Widget
         */
        JComponent getComponent();
    }
}
