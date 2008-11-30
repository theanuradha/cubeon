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
package org.netbeans.cubeon.bugzilla.core.repository;

import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.bugzilla.api.BugzillaClient;
import org.netbeans.cubeon.bugzilla.api.MixedModeBugzillaClientImpl;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Main class of Cubeon Bugzilla task repository.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTaskRepository implements TaskRepository {

    /**
     * Bugzilla task repository provider, it provides basic logic used to manage repositories.
     */
    private BugzillaTaskRepositoryProvider provider;

    /**
     * Current repository instance ID.
     */
    private String id;

    /**
     * Current repository instance description.
     */
    private String description;

    /**
     * Current repository name.
     */
    private String name;

    /**
     * Actual repository state.
     */
    private State state;

    /**
     * Repository user name.
     */
    private String username;

    /**
     * Repository password.
     */
    private String password;

    /**
     * Repository URL.
     */
    private String url;

    /**
     * Bugzilla repository client, it is responsible for retrieving repository data and publishing tasks.
     */
    private BugzillaClient client;

    /**
     * Synchronziation lock object, used during synchronization.
     */
    private final Object SYNCHRONIZATION_LOCK = new Object();

    /**
     * Map of Bugzilla tasks.
     */
    private Map<String, BugzillaTask> bugzillaTasks = new HashMap<String, BugzillaTask>();

    /**
     * Default constructor.
     */
    public BugzillaTaskRepository() {
    }

    /**
     * Four-parameters constructor, it will be used to create instance of BugzillaTaskRepository.
     *
     * @param provider    - task repository provider, it provides basic logic which will be used to manage repositories
     * @param id          - repository ID
     * @param description - repository description
     * @param name        - repository name
     * @param url         - repository URL
     */
    public BugzillaTaskRepository( BugzillaTaskRepositoryProvider provider, String id, String description, String name, String url ) {
        this.provider = provider;
        this.id = id;
        this.description = description;
        this.name = name;
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public Lookup getLookup() {
        return null;  //TODO implement this
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return Utilities.loadImage( "org/netbeans/cubeon/bugzilla/repository/bugzilla-repository.png" );
    }

    /**
     * {@inheritDoc}
     */
    public TaskElement createTaskElement( String summary, String description ) {
        return null;  //todo implement this
    }

    /**
     * {@inheritDoc}
     */
    public TaskElement getTaskElementById( String id ) {
        BugzillaTask task = bugzillaTasks.get( id );
        if( task == null ) {
            try {
                BugDetails bugDetails = client.getBugDetails( new Integer( id ) );
                task = new BugzillaTask( bugDetails.getBugSummary(), this );
                bugzillaTasks.put( id, task );
            } catch( BugzillaException e ) {
                //todo implement this
            }
        }
        return task;
    }

    /**
     * {@inheritDoc}
     */
    public void persist( TaskElement element ) {
        //todo implement this
    }

    /**
     * {@inheritDoc}
     */
    public void synchronize() {
        RequestProcessor.getDefault().post( new Runnable() {

            public void run() {
                synchronized( SYNCHRONIZATION_LOCK ) {

                }
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public State getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public Notifier<RepositoryEventAdapter> getNotifier() {
        return null;  //todo implement this
    }


    public String getUrl() {
        return url;
    }

    /**
     * Returns actual Bugzilla client instance.
     *
     * @return - Bugzilla client
     * @throws BugzillaException -throws exception incase of any errors during connection
     */
    public synchronized BugzillaClient getClient() throws BugzillaException {
        if( client == null ) {
            reconnect();
        }
        return client;
    }

    /**
     * Reconects to Bugzilla repository.
     *
     * @throws BugzillaException - throws exception in case of any problems during connection
     */
    public synchronized void reconnect() throws BugzillaException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage( BugzillaTaskRepository.class, "LBL_Connecting", getName() ) );
        handle.start();
        handle.switchToIndeterminate();
        try {
            //making client NULL
            client = null;
            //creating new instance of client
            client = new MixedModeBugzillaClientImpl( getUrl(), username, password );
        } finally {
            handle.finish();
        }
    }

    /**
     * Returns synchronized bug summary.
     *
     * @param id - bug id
     * @return - synchronized bug summary
     */
    public BugSummary getSynchronizedTask( final String id ) {
        BugSummary bugSummary = null;
        try {
            BugDetails bugDetails = client.getBugDetails( new Integer( id ) );
            bugSummary = bugDetails.getBugSummary();
        } catch( BugzillaException e ) {
            //ignore
        }
        return bugSummary;
    }

    /**
     * Returns URL for bug with given id.
     *
     * @param id - bug id
     * @return - URL address
     */
    public String getBugUrl( String id ) {
        return client.getBugUrl( id );
    }

    public void setProvider( BugzillaTaskRepositoryProvider provider ) {
        this.provider = provider;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public void setUrl( String url ) {
        this.url = url;
    }
}
