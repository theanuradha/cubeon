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

import org.netbeans.cubeon.bugzilla.core.repository.ui.ConfigurationHandlerImpl;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * It provides information about repositories and repositories managment logic (add/get/delete).
 *
 * @author radoslaw.holewa
 */
public class BugzillaTaskRepositoryProvider implements TaskRepositoryType {

    /**
     * Base path of Bugzilla repositories configuration.
     */
    private static final String BASE_PATH = "cubeon/bugzilla/repositories/";

    /**
     * Task repository ID.
     */
    private static final String BUGZILLA_ID = "bugzilla";

    /**
     * FileObject that points to base path of Bugzilla repositories configuration.
     */
    private FileObject baseDir;

    /**
     * Persistance manager, it's responsible for persisting Bugzilla repositories configuration.
     */
    private BugzillaTaskRepositoryPersistence persistence;

    /**
     * List of all configured Bugzilla repositories.
     */
    private List<BugzillaTaskRepository> repositories = new ArrayList<BugzillaTaskRepository>();

    /**
     * This flag informs about initialization state of this provider.
     */
    private AtomicBoolean initiailzed = new AtomicBoolean( false );

    /**
     * Default provider constructor.
     * It initializes persistence manager.
     */
    public BugzillaTaskRepositoryProvider() {
        try {
            baseDir = FileUtil.createFolder( Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH );
        } catch( IOException ex ) {
            Exceptions.printStackTrace( ex );
        }
        assert baseDir != null;
        persistence = new BugzillaTaskRepositoryPersistence( this, baseDir );
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return BUGZILLA_ID;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return NbBundle.getMessage( BugzillaTaskRepositoryProvider.class, "LBL_Bugzilla_Name" );
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return NbBundle.getMessage( BugzillaTaskRepositoryProvider.class, "LBL_Bugzilla_Description" );
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return Utilities.loadImage( "org/netbeans/cubeon/bugzilla/core/repository/bugzilla-logo.gif" );
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
    public TaskRepository persistRepository( TaskRepository taskRepository ) {
        final BugzillaTaskRepository bugzillaTaskRepository = taskRepository.getLookup()
                .lookup( BugzillaTaskRepository.class );
        if( bugzillaTaskRepository != null ) {
            persistence.addRepository( bugzillaTaskRepository );
            if( !repositories.contains( bugzillaTaskRepository ) ) {
                repositories.add( bugzillaTaskRepository );
            }
            RequestProcessor.getDefault().post( new Runnable() {
                public void run() {
                    bugzillaTaskRepository.synchronize();
                }
            } );
        }
        return bugzillaTaskRepository;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeRepository( TaskRepository repository ) {
        BugzillaTaskRepository bugzillaTaskRepository = repository.getLookup().lookup( BugzillaTaskRepository.class );
        if( bugzillaTaskRepository != null ) {
            persistence.removeRepository( bugzillaTaskRepository );
            repositories.remove( repository );
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public java.util.List<TaskRepository> getRepositorys() {
        if( !initiailzed.getAndSet( true ) ) {
            Collection<? extends BugzillaTaskRepository> repos = persistence.getBugzillaTaskRepositories();
            if( repos != null ) {
                repositories.addAll( repos );
            }
        }
        return new ArrayList<TaskRepository>( repositories );
    }

    /**
     * {@inheritDoc}
     */
    public TaskRepository getRepositoryById( String id ) {
        for( TaskRepository repository : repositories ) {
            if( repository.getId().equals( id ) ) {
                return repository;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ConfigurationHandler createConfigurationHandler() {
        return new ConfigurationHandlerImpl( this );
    }
}
