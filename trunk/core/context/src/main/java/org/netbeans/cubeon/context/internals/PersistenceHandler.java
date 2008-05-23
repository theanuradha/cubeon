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
package org.netbeans.cubeon.context.internals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.cubeon.context.api.TaskFolder;
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

    private static final String FILESYSTEM_FILE_NAME = "tasks-filesystem.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ROOT = "tasks-filesystem";
    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_FOLDERS = "folders";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_TASK = "task";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private FileObject baseDir;

    public PersistenceHandler(FileObject baseDir) {
        this.baseDir = baseDir;
    }

    public TaskFolder addTaskFolder(TaskFolder parent, TaskFolder folder) {
        Element baseElement = getConfigurationFragment(TAG_FOLDERS, NAMESPACE);
        Element element = findTaskFolderElement(baseElement, parent);
        if (element == null) {
            element = baseElement;
        }
        Document document = element.getOwnerDocument();
        Element folderElement = document.createElementNS(NAMESPACE, TAG_FOLDER);
        element.appendChild(folderElement);
        //put basic informations
        folderElement.setAttributeNS(NAMESPACE, TAG_NAME, folder.getName());
        folderElement.setAttributeNS(NAMESPACE, TAG_DESCRIPTION, folder.getDescription());



        putConfigurationFragment(baseElement);
        return folder;
    }

    public void removeTaskFolder(TaskFolder parent, TaskFolder folder) {
        Element baseElement = getConfigurationFragment(TAG_FOLDERS, NAMESPACE);


        Element parentElement = findTaskFolderElement(baseElement, parent);
        Element folderElement = findTaskFolderElement(baseElement, folder);

        parentElement.removeChild(folderElement);

        putConfigurationFragment(baseElement);
    }

    public List<TaskFolder> getTaskFolders(TaskFolder taskFolder) {
        List<TaskFolder> taskFolders = new ArrayList<TaskFolder>();
        Element baseElement = getConfigurationFragment(TAG_FOLDERS, NAMESPACE);
        Element foldersElement = findTaskFolderElement(baseElement, taskFolder);
        if (foldersElement == null) {
            foldersElement = baseElement;
        }

        if (foldersElement != null) {
            NodeList nodeList =
                    foldersElement.getElementsByTagNameNS(NAMESPACE, TAG_FOLDER);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getAttribute(TAG_NAME);
                    String description = element.getAttribute(TAG_DESCRIPTION);
                    taskFolders.add(new TaskFolderImpl(this, taskFolder, name, description));
                }
            }
        }





        return taskFolders;
    }

    private List<TaskFolder> getHierarchyAsList(TaskFolder folder) {
        List<TaskFolder> folders = new ArrayList<TaskFolder>();
        readParents(folders, folder);

        Collections.reverse(folders);
        return folders;
    }

    private void readParents(List<TaskFolder> folders, TaskFolder folder) {
        folders.add(folder);
        TaskFolder parent = folder.getParent();
        if (parent != null) {
            readParents(folders, parent);
        }
    }
    //xml related
    private Element findTaskFolderElement(Element element, TaskFolder taskFolder) {

        //check is root folder 
        if (taskFolder.getParent() != null) {
            List<TaskFolder> folders = getHierarchyAsList(taskFolder);
            for (TaskFolder tf : folders) {
                element = findMatchingElement(element, tf);
                assert element != null;
            }
        }

        return element;
    }

    private Element findMatchingElement(Element parent, TaskFolder tf) {
        NodeList nodeList = parent.getElementsByTagNameNS(NAMESPACE, TAG_FOLDERS);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getAttribute(TAG_NAME);
                if (tf.getName().equals(name)) {
                    return element;
                }
            }
        }
        return null;
    }

    private Element getDocumentElement() {
        final FileObject config = baseDir.getFileObject(FILESYSTEM_FILE_NAME);
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
            doc = XMLUtil.createDocument(TAG_ROOT, null, null, null);

        }
        return doc.getDocumentElement();
    }

    private Element getConfigurationFragment(final String elementName, final String namespace) {
        Element documentElement = getDocumentElement();
        Element findElement = findElement(documentElement, elementName, namespace);
        if (findElement == null) {
            findElement = documentElement.getOwnerDocument().createElementNS(namespace, elementName);
        }
        return findElement;
    }

    private void putConfigurationFragment(final Element fragment) throws IllegalArgumentException {

        Document doc = null;
        FileObject config = baseDir.getFileObject(FILESYSTEM_FILE_NAME);

        if (config != null) {
            try {
                doc = XMLUtil.parse(new InputSource(config.getInputStream()), false, true, null, null);
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {

            doc = XMLUtil.createDocument(TAG_ROOT, null, null, null);

        }

        if (doc != null) {
            Element el = findElement(doc.getDocumentElement(), fragment.getNodeName(), fragment.getNamespaceURI());
            if (el != null) {
                doc.getDocumentElement().removeChild(el);
            }
            doc.getDocumentElement().appendChild(doc.importNode(fragment, true));
        }

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {
                config = baseDir.createData(FILESYSTEM_FILE_NAME);
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
