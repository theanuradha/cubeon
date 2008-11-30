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

import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Bugzilla repository persistence class, it will be used to add new repository or delete one
 * of available repositories.
 *
 * @author radoslaw.holewa
 */
public class BugzillaRepositoryPersistence {

    /**
     * Bugzilla repositories configuration file.
     */
    public static final String BUGZILLA_REPOSITORIES_CONF_FILE = "repositories.xml";

    /**
     * Repository node name.
     */
    private static final String NODE_REPOSITORY = "repository";

    /**
     * Password node name.
     */
    private static final String NODE_PASSWORD = "password";

    /**
     * Username node name.
     */
    private static final String NODE_USERNAME = "username";

    /**
     * Repository URL address node name.
     */
    private static final String NODE_URL = "url";

    /**
     * Repository name node name.
     */
    private static final String NODE_NAME = "name";

    /**
     * Description node name.
     */
    private static final String NODE_DESCRIPTION = "description";

    /**
     * Base directory for Bugzilla repository configuration data.
     */
    private FileObject baseDir;
    /**
     * Task repository provider.
     */
    private BugzillaTaskRepositoryProvider taskRepositoryProvider;

    /**
     * Two-arguments constructor, it will be used to create repository persistance manager.
     *
     * @param taskRepositoryProvider - task repository provider which provides repository managment logic
     * @param baseDir                - base Bugzilla repository configuration directory
     */
    public BugzillaRepositoryPersistence( BugzillaTaskRepositoryProvider taskRepositoryProvider, FileObject baseDir ) {
        this.taskRepositoryProvider = taskRepositoryProvider;
        this.baseDir = baseDir;
    }

    /**
     * Adds given repository to the list of available repositories.
     *
     * @param taskRepository - task repository which will be added
     */
    public void addRepository( BugzillaTaskRepository taskRepository ) {
        //todo implement this
    }

    /**
     * Removes given repository.
     *
     * @param taskRepository - repository which will be removed
     */
    public void removeRepository( BugzillaTaskRepository taskRepository ) {
        Document document = null;
        try {
            taskRepository.getUrl();
            FileObject configFile = getConfigurationFile( BUGZILLA_REPOSITORIES_CONF_FILE );
            document = XMLUtil.parse( new InputSource( configFile.getInputStream() ), false, true, null, null );
            NodeList nodeList = document.getElementsByTagName( "repository" );
            Node node = null;
            NodeList childNodes = null;
            for( int i = 0; i < nodeList.getLength(); i++ ) {
                node = nodeList.item( i );
                childNodes = node.getChildNodes();
            }
        } catch( Exception e ) {
            Exceptions.printStackTrace( e );
        }
    }

    /**
     * Returns list of all configured Bugzilla repositories.
     *
     * @return - list of configured Bugzilla repositories
     */
    public Collection<? extends BugzillaTaskRepository> getBugzillaTaskRepositories() {
        Document document = null;
        try {
            FileObject configFile = getConfigurationFile( BUGZILLA_REPOSITORIES_CONF_FILE );
            document = XMLUtil.parse( new InputSource( configFile.getInputStream() ), false, true, null, null );
            NodeList nodeList = document.getElementsByTagName( "repository" );
            return parseRepositories( nodeList );
        } catch( Exception e ) {
            Exceptions.printStackTrace( e );
        }
        return null;
    }

    /**
     * Parses nodeList containing repositories configurations.
     *
     * @param nodeList - node list to parse
     * @return - collection of Bugzilla task repositories
     */
    private Collection<? extends BugzillaTaskRepository> parseRepositories( NodeList nodeList ) {
        List<BugzillaTaskRepository> repositories = new ArrayList<BugzillaTaskRepository>();
        BugzillaTaskRepository bugzillaTaskRepository = null;
        Node node = null;
        for( int i = 0; i < nodeList.getLength(); i++ ) {
            node = nodeList.item( i );
            if( NODE_REPOSITORY.equals( node.getNodeName() ) ) {
                bugzillaTaskRepository = parseRepository( nodeList.item( i ).getChildNodes() );
                repositories.add( bugzillaTaskRepository );
            }
        }
        return repositories;
    }

    /**
     * Rarses one repository element.
     *
     * @param nodeList - list of one-repository related nodes
     */
    private BugzillaTaskRepository parseRepository( NodeList nodeList ) {
        BugzillaTaskRepository repository = new BugzillaTaskRepository();
        Node node = null;
        String name, value;
        for( int i = 0; i < nodeList.getLength(); i++ ) {
            node = nodeList.item( i );
            name = node.getNodeName();
            value = node.getTextContent();
            if( NODE_PASSWORD.equals( name ) ) {
                repository.setPassword( value );
            } else if( NODE_USERNAME.equals( name ) ) {
                repository.setUsername( value );
            } else if( NODE_URL.equals( name ) ) {
                repository.setUrl( value );
            } else if( NODE_NAME.equals( name ) ) {
                repository.setName( value );
            } else if( NODE_DESCRIPTION.equals( name ) ) {
                repository.setDescription( value );
            }
        }
        repository.setProvider( taskRepositoryProvider );
        return repository;
    }

    /**
     * Saves given element in file.
     *
     * @param element - element to add
     */
    private void saveElement( Element element ) {
        Document document = null;
        try {
            FileObject configFile = getConfigurationFile( BUGZILLA_REPOSITORIES_CONF_FILE );
            document = XMLUtil.parse( new InputSource( configFile.getInputStream() ), false, true, null, null );
        } catch( IOException e ) {
            Exceptions.attachMessage( e, "Error while opening configuration file." );
        } catch( SAXException e ) {
            Exceptions.attachMessage( e, "Error while parsing configuration file" );
        }
        document.appendChild( element );
        //todo implement this
    }

    /**
     * Returns configuration file.
     *
     * @param fileName - configuration file name
     * @return - file object
     * @throws IOException - throws exception in case of any errors during file opening
     */
    private FileObject getConfigurationFile( String fileName ) throws IOException {
        FileObject configFile = baseDir.getFileObject( fileName );
        if( configFile == null ) {
            configFile = baseDir.createData( BUGZILLA_REPOSITORIES_CONF_FILE );
        }
        return configFile;
    }


}
