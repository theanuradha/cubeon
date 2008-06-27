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
package org.netbeans.cubeon.tasks.core.internals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
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

    private static final String FILESYSTEM_FILE_NAME = "tasks.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ROOT = "tasks";
    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_QUERY = "query";
    private static final String TAG_TASK = "task";
    private static final String TAG_NAME = "name";
    private TaskFolderImpl folderImpl;

    PersistenceHandler(TaskFolderImpl folderImpl) {
        this.folderImpl = folderImpl;
    }

    void addTaskElement(TaskElement te) {
        Document document = getDocument();
        Element root = getRootElement(document);
        Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);
        //check tasksElement null and create element
        if (tasksElement == null) {
            tasksElement = document.createElementNS(NAMESPACE, TAG_TASKS);
            root.appendChild(tasksElement);
        }

        Element taskElement = document.createElementNS(NAMESPACE, TAG_TASK);
        tasksElement.appendChild(taskElement);

        taskElement.setAttributeNS(NAMESPACE, TAG_ID, te.getId());
        taskElement.setAttributeNS(NAMESPACE, TAG_REPOSITORY, te.getTaskRepository().getId());

        save(document);
    }

    void changeTaskElementId(String oldId, String newId) {
       Document document = getDocument();
        Element root = getRootElement(document);
        Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);
        Element taskElement = null;

        NodeList taskNodes =
                tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_TASK);

        for (int i = 0; i < taskNodes.getLength(); i++) {
            Node node = taskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                if (oldId.equals(id)) {
                    taskElement = element;
                    element.setAttributeNS(NAMESPACE, TAG_ID, newId);
                    break;
                }
            }
        }
        if(taskElement!=null){
         save(document);
        }

    }

    void removeTaskElement(TaskElement te) {
        Document document = getDocument();
        Element root = getRootElement(document);
        Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);
        Element taskElement = null;

        NodeList taskNodes =
                tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_TASK);

        for (int i = 0; i < taskNodes.getLength(); i++) {
            Node node = taskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                if (te.getId().equals(id)) {
                    taskElement = element;
                    break;
                }
            }
        }
        assert taskElement != null;

        tasksElement.removeChild(taskElement);

        save(document);
    }

    void setTaskQuery(TaskQuery query) {
        Document document = getDocument();
        Element root = getRootElement(document);
        Element taskQuery = findElement(root, TAG_QUERY, NAMESPACE);
        //check tasksElement null and create element
        if (taskQuery == null) {
            taskQuery = document.createElementNS(NAMESPACE, TAG_QUERY);
            root.appendChild(taskQuery);
        }
        if (query != null) {
            taskQuery.setAttribute(TAG_NAME, query.getName());
            taskQuery.setAttribute(TAG_REPOSITORY, query.getTaskRepository().getId());
        } else {
            root.removeChild(taskQuery);
        }
        save(document);
    }

    void removeTaskQuery() {
        Document document = getDocument();
        Element root = getRootElement(document);
        Element taskQuery = findElement(root, TAG_QUERY, NAMESPACE);

        if (taskQuery != null) {

            root.removeChild(taskQuery);
            save(document);
        }
    }

    TaskQuery getTaskQuery() {
        Document document = getDocument();
        Element root = getRootElement(document);
        Element taskQuery = findElement(root, TAG_QUERY, NAMESPACE);

        if (taskQuery != null) {
            CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
            TaskRepositoryHandler repositoryHandler = context.getLookup().
                    lookup(TaskRepositoryHandler.class);
            String name = taskQuery.getAttribute(TAG_NAME);
            String repository = taskQuery.getAttribute(TAG_REPOSITORY);
            TaskRepository taskRepository = repositoryHandler.getTaskRepositoryById(repository);
            TaskQuerySupportProvider provider = taskRepository.getLookup().lookup(TaskQuerySupportProvider.class);
            return provider.findTaskQueryByName(name);
        }
        return null;
    }

    void refresh() {
        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        TaskRepositoryHandler repositoryHandler = context.getLookup().
                lookup(TaskRepositoryHandler.class);
        List<TaskElement> taskElements = new ArrayList<TaskElement>();
        Document document = getDocument();
        Element root = getRootElement(document);
        Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);

        if (tasksElement != null) {
            NodeList taskNodes =
                    tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_TASK);
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                    String repository = element.getAttributeNS(NAMESPACE, TAG_REPOSITORY);

                    TaskRepository taskRepository =
                            repositoryHandler.getTaskRepositoryById(repository);
                    assert taskRepository != null;

                    TaskElement taskElement = taskRepository.getTaskElementById(id);
                    assert taskElement != null;
                    taskElements.add(taskElement);
                }
            }
            folderImpl.setTaskElements(taskElements);
        }
    }

    private Document getDocument() {
        final FileObject config = folderImpl.getFileObject().getFileObject(FILESYSTEM_FILE_NAME);
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
            rootElement = doc.createElementNS(NAMESPACE, TAG_ROOT);
        }
        return rootElement;
    }

    private void save(Document doc) {

        FileObject config = folderImpl.getFileObject().getFileObject(FILESYSTEM_FILE_NAME);

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {
                config = folderImpl.getFileObject().createData(FILESYSTEM_FILE_NAME);
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

    private static Element findElement(Element parent, String name, String namespace) {
        Element result = null;
        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) l.item(i);
                if (name.equals(el.getLocalName()) &&
                        ((namespace == el.getNamespaceURI()) /*check both namespaces are null*/ || (namespace != null && namespace.equals(el.getNamespaceURI())))) {
                    if (result == null) {
                        result = el;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }
}
