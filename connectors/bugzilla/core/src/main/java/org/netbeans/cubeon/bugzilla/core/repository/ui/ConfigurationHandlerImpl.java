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
package org.netbeans.cubeon.bugzilla.core.repository.ui;

import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepositoryProvider;

import javax.swing.event.ChangeListener;
import javax.swing.*;

/**
 * Configuration handler is responisble for all repository configuration related tasks
 * like repository adding, connection verification
 *
 * @author radoslaw.holewa
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements TaskRepositoryType.ConfigurationHandler {

    /**
     * One-argument constructor, it creates instance of configuration handler and initializes task repository provider.
     *
     * @param bugzillaTaskRepositoryProvider - task repository provider which will be used to manage repository.
     */
    public ConfigurationHandlerImpl( BugzillaTaskRepositoryProvider bugzillaTaskRepositoryProvider ) {
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener( ChangeListener changeListener ) {
        //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public void removeChangeListener( ChangeListener changeListener ) {
        //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public void setTaskRepository( TaskRepository repository ) {
        //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public TaskRepository getTaskRepository() {
        return null;  //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidConfiguration() {
        return false;  //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return null;  //TODO implement this
    }
}
