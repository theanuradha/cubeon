package org.netbeans.cubeon.bugzilla.core.repository;

import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.File;

/**
 * Bugzilla configuration file manager implementation.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTasksFileManagerImpl implements BugzillaTasksFileManager {

    private static final String ELEMENT_TASK = "task";

    private static final String ELEMENT_ID = "id";

    private static final String ELEMENT_NAME = "name";

    private static final String ELEMENT_URL = "url";

    private static final String ELEMENT_DISPLAY_NAME = "display_name";

    private static final String ELEMENT_DESCRIPTION = "description";

    /**
     * {@inheritDoc}
     */
    public void persistTask( BugzillaTask bugzillaTask ) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement( ELEMENT_TASK );
            Map<String, String> elementsMap = new LinkedHashMap<String, String>();
            elementsMap.put( ELEMENT_ID, bugzillaTask.getId() );
            elementsMap.put( ELEMENT_NAME, bugzillaTask.getName() );
            elementsMap.put( ELEMENT_URL, bugzillaTask.getUrl().toString() );
            elementsMap.put( ELEMENT_DISPLAY_NAME, bugzillaTask.getDisplayName() );
            elementsMap.put( ELEMENT_DESCRIPTION, bugzillaTask.getDescription() );
            root = createCompleteElement( root, elementsMap, document );
        } catch( ParserConfigurationException e ) {
            Exceptions.printStackTrace( e );
        }
    }

    /**
     * Creates complete content for given root element and it's childrens provided as a map of values.
     *
     * @param rootElement - root element, it will contain child elements created using provided map
     * @param values      - map of values
     * @param document    - document, it will be used to create child elements
     * @return - filled root element
     */
    private Element createCompleteElement( Element rootElement, Map<String, String> values, Document document ) {
        Element childElement = null;
        for( String name : values.keySet() ) {
            childElement = document.createElement( name );
            childElement.setTextContent( values.get( name ) );
            rootElement.appendChild( childElement );
        }
        return rootElement;
    }

    /**
     * {@inheritDoc}
     */
    public BugzillaTask loadTask( String taskId ) {
        //todo implement this
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void removeTask( String taskId ) {
        //todo implement this
    }

    /**
     * {@inheritDoc}
     */
    public List<BugzillaTask> loadAllTasks() {
        //todo implement this
        return null;
    }
}
