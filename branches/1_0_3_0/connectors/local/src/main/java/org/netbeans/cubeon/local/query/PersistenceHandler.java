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
import java.util.List;
import org.netbeans.cubeon.local.repository.LocalTaskPriorityProvider;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.local.repository.LocalTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
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
            List<TaskPriority> priorities = localQuery.getPriorities();

            Element taskpriorities = getEmptyElement(document, taskQuery, TAG_PRIORITIES);
            for (TaskPriority tp : priorities) {
                Element element = document.createElement(TAG_PRIORITY);
                taskpriorities.appendChild(element);
                element.appendChild(document.createTextNode(tp.getId()));
            }
            //---------------------------------------------------------------------------
            Element taskTypes = getEmptyElement(document, taskQuery, TAG_TYPES);
            for (TaskType tt : localQuery.getTypes()) {
                Element element = document.createElement(TAG_TYPE);
                taskTypes.appendChild(element);
                element.appendChild(document.createTextNode(tt.getId()));
            }
            //---------------------------------------------------------------------------
            Element taskStates = getEmptyElement(document, taskQuery, TAG_STATES);
            for (TaskStatus ts : localQuery.getStates()) {
                Element element = document.createElement(TAG_STATUS);
                taskStates.appendChild(element);
                element.appendChild(document.createTextNode(ts.getId()));
            }

            taskQuery.setAttribute(TAG_CONTAIN, localQuery.getContain());
            taskQuery.setAttribute(TAG_MATCH_TYPE, localQuery.getMatchType().name());
            taskQuery.setAttribute(TAG_SUMMARY, String.valueOf(localQuery.isSummary()));
            taskQuery.setAttribute(TAG_DESCRIPTION, String.valueOf(localQuery.isDescription()));
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
            id = id + "-" + nextID;
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
                        //----------------------------------------------------------------------
                        Element elementtypes = findElement(element, TAG_TYPES);
                        tagsTexts = getTagsTexts(elementtypes, TAG_TYPE);
                        List<TaskType> types = new ArrayList<TaskType>();
                        for (String tid : tagsTexts) {
                            types.add(localTaskTypeProvider.getTaskTypeById(tid));
                        }
                        localQuery.setTypes(types);
                        //----------------------------------------------------------------------
                        Element elementStates = findElement(element, TAG_STATES);
                        tagsTexts = getTagsTexts(elementStates, TAG_STATUS);
                        List<TaskStatus> states = new ArrayList<TaskStatus>();
                        for (String sid : tagsTexts) {
                            states.add(statusProvider.getTaskStatusById(sid));
                        }
                        localQuery.setStates(states);
                        //----------------------------------------------------------------------
                        String content = element.getAttribute(TAG_CONTAIN);
                        String match_Type = element.getAttribute(TAG_MATCH_TYPE);
                        if (match_Type != null && match_Type.trim().length() > 0) {
                            MatchType matchType = MatchType.valueOf(match_Type);
                            localQuery.setMatchType(matchType);
                        }
                        localQuery.setContain(content);

                        String summary = element.getAttribute(TAG_SUMMARY);
                        localQuery.setSummary(Boolean.parseBoolean(summary));
                        String description = element.getAttribute(TAG_DESCRIPTION);
                        localQuery.setDescription(Boolean.parseBoolean(description));
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
        NodeList nodes =
                element.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            texts.add(node.getTextContent());
        }

        return texts;
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
}
