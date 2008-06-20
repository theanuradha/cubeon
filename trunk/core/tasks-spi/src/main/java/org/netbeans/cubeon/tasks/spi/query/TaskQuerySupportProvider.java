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
package org.netbeans.cubeon.tasks.spi.query;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;

/**
 *
 * @author Anuradha G
 */
public interface TaskQuerySupportProvider {

    /**
     *
     * @return
     */
    TaskQuery createTaskQuery();

    /**
     *
     * @return
     */
    List<TaskQuery> getTaskQuerys();

    /**
     * save modifid attributes
     * @param query
     */
    void addTaskQuery(TaskQuery query);

    /**
     *
     * @param query
     */
    void modifyTaskQuery(TaskQuery query);

    /**
     *
     * @param query
     */
    void removeTaskQuery(TaskQuery query);

    /**
     * reset modifid attributes
     * @param query
     */
    void reset(TaskQuery query);

    /**
     *
     * @return
     */
    TaskRepository getTaskRepository();

    /**
     *
     * @return
     */
    ConfigurationHandler createConfigurationHandler(TaskQuery query);

    /**
     *
     */
    public interface ConfigurationHandler {

        /**
         *
         * @return
         */
        TaskQuery getTaskQuery();

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
