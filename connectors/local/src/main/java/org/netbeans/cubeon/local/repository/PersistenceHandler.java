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
package org.netbeans.cubeon.local.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskType;
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

    private static final String FILESYSTEM_FILE_TAG = "-tasks.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ROOT = "tasks";
    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_NEXT_ID = "next";
    private static final String TAG_TASK = "task";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRIORITY = "priority";
    private static final String TAG_STATUS = "status";
    private static final String TAG_URL = "url";
    private static final String TAG_TYPE = "type";
    private static final String TAG_CREATED_DATE = "cdate";
    private static final String TAG_UPDATE_DATE = "udate";
    private static final String TAG_DESCRIPTION = "description";
    private LocalTaskRepository localTaskRepository;
    private FileObject baseDir;

    private static final Object LOCK = new Object();

    PersistenceHandler(LocalTaskRepository localTaskRepository, FileObject fileObject) {
        this.localTaskRepository = localTaskRepository;
        this.baseDir = fileObject;
        refresh();
    }

    void vaidate(TaskElement element) {
        //do validations changes here
       

    }


    
    void addTaskElement(TaskElement te) {
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);
            //check tasksElement null and create element
            if (tasksElement == null) {
                tasksElement = document.createElementNS(NAMESPACE, TAG_TASKS);
                root.appendChild(tasksElement);
            }
            Element taskElement = null;

            if (localTaskRepository.getTaskElementById(te.getId()) != null) {
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
            }
            if (taskElement == null) {
                taskElement = document.createElementNS(NAMESPACE, TAG_TASK);
                tasksElement.appendChild(taskElement);
                taskElement.setAttributeNS(NAMESPACE, TAG_ID, te.getId());
            }

            LocalTask localTask = te.getLookup().lookup(LocalTask.class);
            taskElement.setAttributeNS(NAMESPACE, TAG_NAME, te.getName());
            taskElement.setAttributeNS(NAMESPACE, TAG_DESCRIPTION, te.getDescription());
            taskElement.setAttributeNS(NAMESPACE, TAG_REPOSITORY, te.getTaskRepository().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_PRIORITY, te.getPriority().getId().toString());
            taskElement.setAttributeNS(NAMESPACE, TAG_STATUS, te.getStatus().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_TYPE, te.getType().getId());
            if (localTask.getUrlString() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_URL, localTask.getUrlString());
            }
            Date now = new Date();

            if (localTask.getCreated() == null) {
                localTask.setCreated(now);
            } else {
                localTask.setUpdated(now);
            }

            taskElement.setAttributeNS(NAMESPACE, TAG_CREATED_DATE, String.valueOf(localTask.getCreated().getTime()));
            if (localTask.getUpdated() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_UPDATE_DATE, String.valueOf(localTask.getUpdated().getTime()));
            }

            save(document);
        }
    }

    void removeTaskElement(TaskElement te) {
        synchronized (LOCK) {
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
    }

    String nextTaskId() {
        String id = localTaskRepository.getId().toUpperCase();
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element nextElement = findElement(root, TAG_NEXT_ID, NAMESPACE);
            int nextID = 0;
            if (nextElement == null) {
                nextElement = document.createElementNS(NAMESPACE, TAG_NEXT_ID);
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
            List<LocalTask> taskElements = new ArrayList<LocalTask>();
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_TASKS, NAMESPACE);

            if (tasksElement != null) {
                NodeList taskNodes =
                        tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_TASK);
                LocalTaskPriorityProvider priorityProvider = localTaskRepository.getLocalTaskPriorityProvider();
                LocalTaskStatusProvider statusProvider = localTaskRepository.getLocalTaskStatusProvider();
                LocalTaskTypeProvider localTaskTypeProvider = localTaskRepository.getLocalTaskTypeProvider();
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                        String name = element.getAttributeNS(NAMESPACE, TAG_NAME);
                        String description = element.getAttributeNS(NAMESPACE, TAG_DESCRIPTION);
                        //read priority
                        String priority = element.getAttributeNS(NAMESPACE, TAG_PRIORITY);
                        TaskPriority taskPriority = priorityProvider.getTaskPriorityById(TaskPriority.PRIORITY.valueOf(priority));
                        //read status
                        String status = element.getAttributeNS(NAMESPACE, TAG_STATUS);
                        TaskStatus taskStatus = statusProvider.getTaskStatusById(status);
                        //read type
                        String type = element.getAttributeNS(NAMESPACE, TAG_TYPE);
                        TaskType taskType = localTaskTypeProvider.getTaskTypeById(type);

                        String url = element.getAttributeNS(NAMESPACE, TAG_URL);

                        Date createdDate = null;
                        Date updatedDate = null;
                        String created = element.getAttributeNS(NAMESPACE, TAG_CREATED_DATE);
                        if (created != null && created.trim().length() != 0) {

                            calendar.setTimeInMillis(Long.parseLong(created));
                            createdDate = calendar.getTime();
                        } else {
                            createdDate = new Date();
                        }
                        String updated = element.getAttributeNS(NAMESPACE, TAG_UPDATE_DATE);
                        if (updated != null && updated.trim().length() != 0) {
                            calendar.setTimeInMillis(Long.parseLong(updated));
                            updatedDate = calendar.getTime();
                        }

                        LocalTask taskElement = new LocalTask(id, name, description, localTaskRepository);
                        taskElement.setPriority(taskPriority);
                        taskElement.setStatus(taskStatus);
                        taskElement.setType(taskType);
                        taskElement.setUrlString(url);
                        taskElement.setCreated(createdDate);
                        taskElement.setUpdated(updatedDate);
                        taskElements.add(taskElement);

                    }
                }
                localTaskRepository.setTaskElements(taskElements);
            }
        }
    }

    private Document getDocument() {
        final FileObject config = baseDir.getFileObject(localTaskRepository.getId() + FILESYSTEM_FILE_TAG);
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

        FileObject config = baseDir.getFileObject(localTaskRepository.getId() + FILESYSTEM_FILE_TAG);

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {
                config = baseDir.createData(localTaskRepository.getId() + FILESYSTEM_FILE_TAG);
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
