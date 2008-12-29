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

import java.util.ArrayList;
import org.netbeans.cubeon.bugzilla.core.tasks.BugzillaTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.core.exception.BugzillaRepositoryException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Bugzilla configuration file manager implementation.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTasksFileManagerImpl extends BaseXMLPersistenceImpl implements BugzillaTasksFileManager {

    /**
     * Task element tag name.
     */
    private static final String ELEMENT_TASK = "task";
    /**
     * Task id tag name.
     */
    private static final String ELEMENT_ID = "id";
    /**
     * Task name tag name.
     */
    private static final String ELEMENT_NAME = "name";
    /**
     * Task url tag name.
     */
    private static final String ELEMENT_URL = "url";
    /**
     * Task display name tag name.
     */
    private static final String ELEMENT_DISPLAY_NAME = "display_name";
    /**
     * Task description tag name.
     */
    private static final String ELEMENT_DESCRIPTION = "description";
    /**
     * FileObject for tasks file in which tasks will be stored.
     */
    private FileObject tasksFile;

    /**
     * One-argument constructor, it is initialized using FileObject which contains
     * handle to file with stored tasks.
     *
     * @param tasksFile - file object with handle to file in which tasks will be stored
     */
    public BugzillaTasksFileManagerImpl(FileObject tasksFile) {
        this.tasksFile = tasksFile;
    }

    /**
     * {@inheritDoc}
     */
    public void persistTask(BugzillaTask bugzillaTask) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement(ELEMENT_TASK);
            Map<String, String> elementsMap = new LinkedHashMap<String, String>();
            elementsMap.put(ELEMENT_ID, bugzillaTask.getId());
            elementsMap.put(ELEMENT_NAME, bugzillaTask.getName());
            elementsMap.put(ELEMENT_URL, bugzillaTask.getUrl().toString());
            elementsMap.put(ELEMENT_DISPLAY_NAME, bugzillaTask.getDisplayName());
            elementsMap.put(ELEMENT_DESCRIPTION, bugzillaTask.getDescription());
            root = createCompleteElement(root, elementsMap, document);
            saveDocumentToFile(document, tasksFile);
        } catch (ParserConfigurationException e) {
            throw new BugzillaRepositoryException("Could not persist task in tasks file.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public BugzillaTask loadTask(String taskId) {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void removeTask(String taskId) {
        //todo implement this
    }

    /**
     * {@inheritDoc}
     */
    public List<BugzillaTask> loadAllTasks() {
        List<BugzillaTask> result = new ArrayList<BugzillaTask>();
        Document document = loadDocumentFromFile(tasksFile);
        if (document != null) {
            NodeList nodeList = document.getElementsByTagName(ELEMENT_TASK);
            BugzillaTask bugzillaTask = null;
            Node node = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);
                bugzillaTask = parseTaskNode(node);
                if(bugzillaTask != null) {
                    result.add(bugzillaTask);
                }
            }
        }
        return result;
    }

    /**
     * Parses given task node.
     *
     * @param node - task node that will be parsed
     * @return - BugzillaTask representation
     */
    private BugzillaTask parseTaskNode(Node node) {
        BugzillaTask result = null;
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            String nodeName = null;
            Node childNode = null;
            result = new BugzillaTask();
            BugSummary bugSummary = new BugSummary();
            result.setBugSummary(bugSummary);
            for (int i = 0; i < nodeList.getLength(); i++) {
                childNode = nodeList.item(i);
                nodeName = childNode.getNodeName();
                //TODO is it OK?
                if (ELEMENT_DISPLAY_NAME.equals(nodeName)) {
                    bugSummary.setSummary(childNode.getTextContent());
                } else if (ELEMENT_ID.equals(nodeName)) {
                    bugSummary.setId(childNode.getTextContent());
                } else if(ELEMENT_DESCRIPTION.equals(nodeName)) {
                    bugSummary.setDescription(childNode.getTextContent());
                }
            }
        }
        return result;
    }
}
