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
package org.netbeans.cubeon.local.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.netbeans.cubeon.local.repository.LocalTaskPriorityProvider;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.local.repository.LocalTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.ui.query.QueryFilter;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Anuradha G
 */
class PersistenceHandler {

    private static final String FILESYSTEM_FILE_TAG = "-query.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ROOT = "querys";//NOI18N
    private static final String TAG_QUERYS = "querys";//NOI18N
    private static final String TAG_QUERY = "query";//NOI18N
    private static final String TAG_ID = "id";//NOI18N
    private static final String TAG_NAME = "name";//NOI18N
    private static final String TAG_CONTAIN = "contain";//NOI18N
    private static final String TAG_SUMMARY = "summary";//NOI18N
    private static final String TAG_DESCRIPTION = "description";//NOI18N
    private static final String TAG_PRIORITIES = "priorities";//NOI18N
    private static final String TAG_PRIORITY = "priority";//NOI18N
    private static final String TAG_STATES = "states";//NOI18N
    private static final String TAG_STATUS = "status";//NOI18N
    private static final String TAG_TYPES = "types";//NOI18N
    private static final String TAG_TYPE = "type";//NOI18N
    private static final String TAG_MATCH_TYPE = "match_type";//NOI18N
    private static final String TAG_NEXT_ID = "next";//NOI18N
    private static final String TAG_MATCH = "match";//NOI18N
    private static final String TAG_SUMMARIES = "summaries";//NOI18N
    private static final String TAG_DESCRIPTIONS = "descriptions";//NOI18N
    private static final String TAG_TEXTS = "texts";//NOI18N
    private static final String TAG_TEXT = "text";//NOI18N
   private static final String TAG_VERSION = "version"; // NOI18N

    private static final String VERSION_1 = "1"; // NOI18N

    private LocalQuerySupport localQuerySupport;
    private FileObject baseDir;
    private static final Object LOCK = new Object();

    PersistenceHandler(LocalQuerySupport localQuerySupport, FileObject fileObject) {
        this.localQuerySupport = localQuerySupport;
        this.baseDir = fileObject;
        refresh();
    }

    void vaidate(TaskQuery query) {
        //do validations changes here
    }

    void addTaskQuery(LocalQuery localQuery) {
        synchronized (LOCK) {

            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS);
            //check tasksElement null and create element
            if (tasksElement == null) {
                tasksElement = document.createElement(TAG_QUERYS);
                tasksElement.setAttribute(TAG_VERSION, VERSION_1);
                root.appendChild(tasksElement);
            }
            Element taskQuery = null;

            NodeList nodeList =
                    tasksElement.getElementsByTagName(TAG_QUERY);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    if (localQuery.getId().equals(id)) {
                        taskQuery = element;
                        break;
                    }
                }
            }
            //todo check available
            if (taskQuery == null) {
                taskQuery = document.createElement(TAG_QUERY);
                tasksElement.appendChild(taskQuery);

            }
            taskQuery.setAttribute(TAG_ID, localQuery.getId());
            taskQuery.setAttribute(TAG_NAME, localQuery.getName());

            Element taskpriorities = getEmptyElement(document, taskQuery, TAG_PRIORITIES);
            taskpriorities.setAttribute(TAG_MATCH, localQuery.getPrioritiesMatch().toString());
            for (TaskPriority tp : localQuery.getPriorities()) {
                Element element = document.createElement(TAG_PRIORITY);
                taskpriorities.appendChild(element);
                element.appendChild(document.createTextNode(tp.getId()));
            }
            //---------------------------------------------------------------------------
            Element taskTypes = getEmptyElement(document, taskQuery, TAG_TYPES);
            taskTypes.setAttribute(TAG_MATCH, localQuery.getTypesMatch().toString());
            for (TaskType tt : localQuery.getTypes()) {
                Element element = document.createElement(TAG_TYPE);
                taskTypes.appendChild(element);
                element.appendChild(document.createTextNode(tt.getId()));
            }
            //---------------------------------------------------------------------------
            Element taskStates = getEmptyElement(document, taskQuery, TAG_STATES);
            taskStates.setAttribute(TAG_MATCH, localQuery.getStatesMatch().toString());
            for (TaskStatus ts : localQuery.getStates()) {
                Element element = document.createElement(TAG_STATUS);
                taskStates.appendChild(element);
                element.appendChild(document.createTextNode(ts.getId()));
            }
            //---------------------------------------------------------------------------
            Element taskSummaries = getEmptyElement(document, taskQuery, TAG_SUMMARIES);
            taskSummaries.setAttribute(TAG_MATCH, localQuery.getSummaryMatch().toString());
            for (String summary : localQuery.getSummarySearch()) {
                Element element = document.createElement(TAG_SUMMARY);
                taskSummaries.appendChild(element);
                element.appendChild(document.createTextNode(summary));
            }
            //---------------------------------------------------------------------------
            Element taskDescriptions = getEmptyElement(document, taskQuery, TAG_DESCRIPTIONS);
            taskDescriptions.setAttribute(TAG_MATCH, localQuery.getDescriptionMatch().toString());
            for (String description : localQuery.getDescriptionSearch()) {
                Element element = document.createElement(TAG_DESCRIPTION);
                taskDescriptions.appendChild(element);
                element.appendChild(document.createTextNode(description));
            }
            //---------------------------------------------------------------------------
            Element taskTexts = getEmptyElement(document, taskQuery, TAG_TEXTS);
            taskTexts.setAttribute(TAG_MATCH, localQuery.getTextMatch().toString());
            for (String text : localQuery.getTextSearch()) {
                Element element = document.createElement(TAG_TEXT);
                taskTexts.appendChild(element);
                element.appendChild(document.createTextNode(text));
            }
            save(document);
        }
    }

    private Element getEmptyElement(Document document, Element root, String tag) {
        Element taskpriorities = findElement(root, tag);
        if (taskpriorities != null) {
            root.removeChild(taskpriorities);
        }
        taskpriorities = document.createElement(tag);
        root.appendChild(taskpriorities);
        return taskpriorities;

    }

    void removeTaskQuery(TaskQuery query) {
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS);
            Element taskElement = null;

            NodeList taskNodes =
                    tasksElement.getElementsByTagName(TAG_QUERY);

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    if (query.getId().equals(id)) {
                        taskElement = element;
                        break;
                    }
                }
            }
            assert taskElement != null;

            tasksElement.removeChild(taskElement);

            save(document);
        }
    }

    String nextTaskId() {
        String id = localQuerySupport.getTaskRepository().getId().toUpperCase();
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element nextElement = findElement(root, TAG_NEXT_ID);
            int nextID = 0;
            if (nextElement == null) {
                nextElement = document.createElement(TAG_NEXT_ID);
                nextElement.setAttribute(TAG_ID, String.valueOf(++nextID));
                root.appendChild(nextElement);
            } else {
                nextID = Integer.parseInt(nextElement.getAttribute(TAG_ID));
                nextElement.setAttribute(TAG_ID, String.valueOf(++nextID));
            }
            save(document);
            id = id + "-" + nextID; // NOI18N
        }
        return id;

    }

    void refresh() {
        synchronized (LOCK) {
            List<LocalQuery> localQuerys = new ArrayList<LocalQuery>();
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS);

            if (tasksElement != null) {
                NodeList taskNodes =
                        tasksElement.getElementsByTagName(TAG_QUERY);
                LocalTaskRepository localTaskRepository = localQuerySupport.getLocalTaskRepository();

                LocalTaskPriorityProvider priorityProvider = localTaskRepository.getLocalTaskPriorityProvider();
                LocalTaskStatusProvider statusProvider = localTaskRepository.getLocalTaskStatusProvider();
                LocalTaskTypeProvider localTaskTypeProvider = localTaskRepository.getLocalTaskTypeProvider();

                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        String id = element.getAttribute(TAG_ID);

                        String name = element.getAttribute(TAG_NAME);
                        LocalQuery localQuery = new LocalQuery(id, name, localTaskRepository);


                        Element elementPriority = findElement(element, TAG_PRIORITIES);
                        List<String> tagsTexts = getTagsTexts(elementPriority, TAG_PRIORITY);
                        List<TaskPriority> priorities = new ArrayList<TaskPriority>();
                        for (String pid : tagsTexts) {
                            priorities.add(priorityProvider.getTaskPriorityById(pid));
                        }
                        localQuery.setPriorities(priorities);
                        localQuery.setPrioritiesMatch(getMatch(elementPriority));
                        //----------------------------------------------------------------------
                        Element elementTypes = findElement(element, TAG_TYPES);
                        tagsTexts = getTagsTexts(elementTypes, TAG_TYPE);
                        List<TaskType> types = new ArrayList<TaskType>();
                        for (String tid : tagsTexts) {
                            types.add(localTaskTypeProvider.getTaskTypeById(tid));
                        }
                        localQuery.setTypes(types);
                        localQuery.setTypesMatch(getMatch(elementTypes));
                        //----------------------------------------------------------------------
                        Element elementStates = findElement(element, TAG_STATES);
                        tagsTexts = getTagsTexts(elementStates, TAG_STATUS);
                        List<TaskStatus> states = new ArrayList<TaskStatus>();
                        for (String sid : tagsTexts) {
                            states.add(statusProvider.getTaskStatusById(sid));
                        }
                        localQuery.setStates(states);
                        localQuery.setStatesMatch(getMatch(elementStates));
                        //----------------------------------------------------------------------
                        Element elementSummaries = findElement(element, TAG_SUMMARIES);
                        localQuery.setSummarySearch(
                                new HashSet<String>(getTagsTexts(elementSummaries, TAG_SUMMARY)));
                        localQuery.setSummaryMatch(getMatch(elementSummaries));

                        Element elementDescriptions = findElement(element, TAG_DESCRIPTIONS);
                        localQuery.setDescriptionSearch(
                                new HashSet<String>(getTagsTexts(elementDescriptions, TAG_DESCRIPTION)));
                        localQuery.setDescriptionMatch(getMatch(elementDescriptions));

                        Element elementTexts = findElement(element, TAG_TEXTS);
                        localQuery.setTextSearch(
                                new HashSet<String>(getTagsTexts(elementTexts, TAG_TEXT)));
                        localQuery.setTextMatch(getMatch(elementTexts));
                        //----------------------------------------------------------------------
                        localQuerys.add(localQuery);
                    }
                }
                localQuerySupport.setTaskQuery(localQuerys);
            }
        }
    }

    private List<String> getTagsTexts(Element element, String tag) {
        List<String> texts = new ArrayList<String>();
        if (element == null)
            return texts;
        NodeList nodes =
                element.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            texts.add(node.getTextContent());
        }

        return texts;
    }

    private QueryFilter.Match getMatch(Element element) {
        String matchString = element == null ? null : element.getAttribute(TAG_MATCH);
        if (matchString == null || matchString.trim().length() == 0)
            return QueryFilter.Match.IS;
        return QueryFilter.Match.valueOf(matchString);
    }

    private Document getDocument() {
        final FileObject config = baseDir.getFileObject(localQuerySupport.getTaskRepository().getId() + FILESYSTEM_FILE_TAG);
        Document doc = null;
        if (config != null) {
            InputStream in = null;
            try {
                in = config.getInputStream();
                doc = XMLUtil.parse(new InputSource(in), false, true, null, null);

            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            // check version and update if needed
            if (doc != null)
                updateVersion(doc);

        } else {
            doc = XMLUtil.createDocument(TAG_ROOT, NAMESPACE, null, null);

        }
        return doc;
    }

    private Element getRootElement(Document doc) {
        Element rootElement = doc.getDocumentElement();
        if (rootElement == null) {
            rootElement = doc.createElement(TAG_ROOT);
        }
        return rootElement;
    }

    private void save(Document doc) {

        FileObject config = baseDir.getFileObject(localQuerySupport.getTaskRepository().getId() + FILESYSTEM_FILE_TAG);

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {
                config = baseDir.createData(localQuerySupport.getTaskRepository().getId() + FILESYSTEM_FILE_TAG);
            }
            lck = config.lock();
            out = config.getOutputStream(lck);
            XMLUtil.write(doc, out, "UTF-8"); //NOI18N
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (lck != null) {
                lck.releaseLock();
            }
        }

    }

    private static Element findElement(Element parent, String name) {

        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) l.item(i);
                if (name.equals(el.getNodeName())) {
                    return el;
                }
            }
        }
        return null;
    }

    private void updateVersion(Document doc) {
        // get root element
        Element rootElement = doc.getDocumentElement();
        if (rootElement == null)
            return;
        // get queries element
        Element queriesElement = findElement(rootElement, TAG_QUERYS);
        if (queriesElement == null)
            return;

        // get version
        String version = queriesElement.getAttribute(TAG_VERSION);
        // current version: no updated needed
        if (VERSION_1.equals(version))
            return;

        // set version
        queriesElement.setAttribute(TAG_VERSION, VERSION_1);

        // update
        NodeList queryList = queriesElement.getElementsByTagName(TAG_QUERY);
        for (int i = 0 ; i < queryList.getLength() ; i++) {
            if (queryList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element queryElement = (Element) queryList.item(i);

                // add match IS to priorities, types and states
                Element prioritiesElement = findElement(queryElement, TAG_PRIORITIES);
                if (prioritiesElement != null)
                    prioritiesElement.setAttribute(TAG_MATCH, QueryFilter.Match.IS.toString());
                Element typesElement = findElement(queryElement, TAG_TYPES);
                if (typesElement != null)
                    typesElement.setAttribute(TAG_MATCH, QueryFilter.Match.IS.toString());
                Element statesElement = findElement(queryElement, TAG_STATES);
                if (statesElement != null)
                    statesElement.setAttribute(TAG_MATCH, QueryFilter.Match.IS.toString());

                // match type
                String matchString = queryElement.getAttribute(TAG_MATCH_TYPE);
                QueryFilter.Match match = QueryFilter.Match.CONTAINS;
                if ("STARTS_WITH".equals(matchString)) // NOI18N
                    match = QueryFilter.Match.STARTS_WITH;
                else if ("ENDS_WITH".equals(matchString)) // NOI18N
                    match = QueryFilter.Match.ENDS_WITH;
                else if ("EQUALS".equals(matchString)) // NOI18N
                    match = QueryFilter.Match.IS;

                // summaries element
                Element summariesElement = doc.createElement(TAG_SUMMARIES);
                summariesElement.setAttribute(TAG_MATCH, match.toString());
                queryElement.appendChild(summariesElement);
                // descriptions element
                Element descriptionsElement = doc.createElement(TAG_DESCRIPTIONS);
                descriptionsElement.setAttribute(TAG_MATCH, match.toString());
                queryElement.appendChild(descriptionsElement);
                // texts element
                Element textsElement = doc.createElement(TAG_TEXTS);
                textsElement.setAttribute(TAG_MATCH, match.toString());
                queryElement.appendChild(textsElement);

                // remarks: search for text inside summary or description is
                // supported by a new query field "text".
                boolean summary = Boolean.parseBoolean(queryElement.getAttribute(TAG_SUMMARY));
                boolean description = Boolean.parseBoolean(queryElement.getAttribute(TAG_DESCRIPTION));
                if (summary && description) {
                    // text
                    Element textElement = doc.createElement(TAG_TEXT);
                    textsElement.appendChild(textElement);
                    textElement.appendChild(doc.createTextNode(
                            queryElement.getAttribute(TAG_CONTAIN)));
                } else if (summary) {
                    // summary
                    Element summaryElement = doc.createElement(TAG_SUMMARY);
                    summariesElement.appendChild(summaryElement);
                    summaryElement.appendChild(doc.createTextNode(
                            queryElement.getAttribute(TAG_CONTAIN)));
                } else if (description) {
                    // description
                    Element descriptionElement = doc.createElement(TAG_DESCRIPTION);
                    descriptionsElement.appendChild(descriptionElement);
                    descriptionElement.appendChild(doc.createTextNode(
                            queryElement.getAttribute(TAG_CONTAIN)));
                }

                // remove old unused tags
                queryElement.removeAttribute(TAG_CONTAIN);
                queryElement.removeAttribute(TAG_SUMMARY);
                queryElement.removeAttribute(TAG_DESCRIPTION);
                queryElement.removeAttribute(TAG_MATCH_TYPE);
            }
        }

        // save document
        save(doc);
    }

}
