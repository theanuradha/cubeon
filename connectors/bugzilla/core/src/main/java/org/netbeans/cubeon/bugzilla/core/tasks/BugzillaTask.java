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
package org.netbeans.cubeon.bugzilla.core.tasks;

import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Cubeon-friendly representation of Bugzilla bug, it implements necessary interfaces which give
 * Cubeon a possibility to use instances of this class.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTask implements TaskElement {

    /**
     * Bug summary.
     */
    private BugSummary bugSummary;

    /**
     * Task repository.
     */
    private BugzillaTaskRepository taskRepository;

    /**
     * One-argument constructor, it takes BugSummary as a parameter.
     *
     * @param bugSummary     - bug summary which contains basic informations about bug
     * @param taskRepository - task repository containing this task
     */
    public BugzillaTask( BugSummary bugSummary, BugzillaTaskRepository taskRepository ) {
        this.bugSummary = bugSummary;
        this.taskRepository = taskRepository;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return bugSummary.getId();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return bugSummary.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return bugSummary.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return bugSummary.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    /**
     * {@inheritDoc}
     */
    public Lookup getLookup() {
        return Lookups.fixed( this );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCompleted() {
        return BugSummary.CLOSED_STATUS.equals( bugSummary.getStatus() );
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        Image image = Utilities.loadImage( "org/netbeans/cubeon/bugzilla/core/task.png" );
        if( bugSummary.isEnhancment() ) {
            image = Utilities.mergeImages( image,
                    Utilities.loadImage( "org/netbeans/cubeon/local/bullet_enhancement.png" ), 0, 0 );
        } else {
            image = Utilities.mergeImages( image,
                    Utilities.loadImage( "org/netbeans/cubeon/local/bullet_defact.png" ), 0, 0 );
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    public URL getUrl() {
        URL url = null;
        try {
            url = new URL( taskRepository.getBugUrl( bugSummary.getId() ) );
        } catch( MalformedURLException e ) {
            //ignore
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public void synchronize() {
        BugSummary summary = taskRepository.getSynchronizedTask( bugSummary.getId() );
        if( summary != null ) {
            bugSummary = summary;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Notifier<TaskElementChangeAdapter> getNotifier() {
        return null;  //TODO implement this
    }

    public void setBugSummary( BugSummary bugSummary ) {
        this.bugSummary = bugSummary;
    }
}
