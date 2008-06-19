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
import java.util.Date;
import java.util.List;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskElement;
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
    private static final String TAG_ROOT = "querys";
    private static final String TAG_ID = "id";
    private static final String TAG_QUERYS = "querys";
    private static final String TAG_QUERY = "query";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRIORITY = "priority";
    private static final String TAG_STATUS = "status";
    private static final String TAG_URL = "url";
    private static final String TAG_TYPE = "type";
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

    void addTaskQuery(TaskQuery tq) {
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS, NAMESPACE);
            //check tasksElement null and create element
            if (tasksElement == null) {
                tasksElement = document.createElementNS(NAMESPACE, TAG_QUERYS);
                root.appendChild(tasksElement);
            }
            Element taskElement = null;

            //todo check available
            if (taskElement == null) {
                taskElement = document.createElementNS(NAMESPACE, TAG_QUERY);
                tasksElement.appendChild(taskElement);

            }
            taskElement.setAttributeNS(NAMESPACE, TAG_NAME, tq.getName());

            save(document);
        }
    }

    void removeTaskQuery(TaskQuery query) {
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS, NAMESPACE);
            Element taskElement = null;

            NodeList taskNodes =
                    tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_QUERY);

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttributeNS(NAMESPACE, TAG_NAME);
                    if (query.getName().equals(id)) {
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

    void refresh() {
        synchronized (LOCK) {
            List<LocalQuery> localQuerys = new ArrayList<LocalQuery>();
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_QUERYS, NAMESPACE);

            if (tasksElement != null) {
                NodeList taskNodes =
                        tasksElement.getElementsByTagNameNS(NAMESPACE, TAG_QUERY);
                LocalTaskRepository localTaskRepository = localQuerySupport.getLocalTaskRepository();

                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        String name = element.getAttributeNS(NAMESPACE, TAG_NAME);
                        LocalQuery localQuery = new LocalQuery(name, localTaskRepository);


                        localQuerys.add(localQuery);
                    }
                }
                localQuerySupport.setTaskQuery(localQuerys);
            }
        }
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
            rootElement = doc.createElementNS(NAMESPACE, TAG_ROOT);
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
