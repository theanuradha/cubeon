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
package org.netbeans.cubeon.jira.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
class JiraRepositoryPersistence {

    private static final String REPOSITORYS_FILE_NAME = "repositorys.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ROOT = "jira-task-repository-configuration";
    private static final String TAG_REPOSITORIES = "repositories";
    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_USERID = "user";
    private static final String TAG_URL = "url";
    private static final String TAG_PASSWORD_HASH = "password";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private JiraTaskRepositoryProvider provider;
    private FileObject baseDir;

    JiraRepositoryPersistence(JiraTaskRepositoryProvider provider, FileObject baseDir) {
        this.provider = provider;
        this.baseDir = baseDir;
    }

    void addRepository(JiraTaskRepository repository) {
        Element repositoryElement = null;

        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES, NAMESPACE);
        if (repositorysElement == null) {
            Document doc = XMLUtil.createDocument(TAG_ROOT, NAMESPACE, null, null);
            repositorysElement = doc.createElementNS(NAMESPACE, TAG_REPOSITORIES);
        }

        NodeList repositoryNodes =
                repositorysElement.getElementsByTagNameNS(NAMESPACE, TAG_REPOSITORY);

        for (int i = 0; i < repositoryNodes.getLength(); i++) {
            Node node = repositoryNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute(TAG_ID);
                if (repository.getId().equals(id)) {
                    repositoryElement = element;
                    break;
                }
            }
        }

        if (repositoryElement == null) {
            Document document = repositorysElement.getOwnerDocument();
            repositoryElement = document.createElementNS(NAMESPACE, TAG_REPOSITORY);
            repositorysElement.appendChild(repositoryElement);
        }

        repositoryElement.setAttributeNS(NAMESPACE, TAG_ID, repository.getId());
        repositoryElement.setAttributeNS(NAMESPACE, TAG_NAME, repository.getName());
        repositoryElement.setAttributeNS(NAMESPACE, TAG_DESCRIPTION, repository.getDescription());
        repositoryElement.setAttributeNS(NAMESPACE, TAG_USERID, repository.getUserName());
        repositoryElement.setAttributeNS(NAMESPACE, TAG_URL, repository.getURL());
        repositoryElement.setAttributeNS(NAMESPACE, TAG_PASSWORD_HASH, repository.getPassword());//FIXME add hash

        putConfigurationFragment(repositorysElement);
    }

    void removeRepository(JiraTaskRepository repository) {
        Element repositoryElement = null;

        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES, NAMESPACE);
        if (repositorysElement == null) {
            return;
        }

        NodeList repositoryNodes =
                repositorysElement.getElementsByTagNameNS(NAMESPACE, TAG_REPOSITORY);

        for (int i = 0; i < repositoryNodes.getLength(); i++) {
            Node node = repositoryNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                if (repository.getId().equals(id)) {
                    repositoryElement = element;
                    break;
                }
            }
        }

        if (repositoryElement != null) {

            repositorysElement.removeChild(repositoryElement);
        }


        putConfigurationFragment(repositorysElement);
    }

    List<JiraTaskRepository> getJiraTaskRepositorys() {
        List<JiraTaskRepository> repositorys = new ArrayList<JiraTaskRepository>();
        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES, NAMESPACE);

        if (repositorysElement != null) {

            NodeList repositoryNodes =
                    repositorysElement.getElementsByTagNameNS(NAMESPACE, TAG_REPOSITORY);

            for (int i = 0; i < repositoryNodes.getLength(); i++) {
                Node node = repositoryNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    String name = element.getAttribute(TAG_NAME);
                    String description = element.getAttribute(TAG_DESCRIPTION);
                    String url = element.getAttribute(TAG_URL);
                    String user = element.getAttribute(TAG_USERID);
                    String password = element.getAttribute(TAG_PASSWORD_HASH);//FIXME
                    //FIXME
                    JiraTaskRepository jiraTaskRepository = new JiraTaskRepository(provider, id, name, description);
                    jiraTaskRepository.setUserName(user);
                    jiraTaskRepository.setPassword(password);
                    jiraTaskRepository.setURL(url);
                    jiraTaskRepository.loadAttributes();
                    repositorys.add(jiraTaskRepository);
                }
            }
        }


        return repositorys;


    }






    //xml related
    private Element getConfigurationFragment(final String elementName, final String namespace) {

        final FileObject config = baseDir.getFileObject(REPOSITORYS_FILE_NAME);
        if (config != null) {


            Document doc;
            InputStream in = null;
            try {
                in = config.getInputStream();
                doc = XMLUtil.parse(new InputSource(in), false, true, null, null);
                return findElement(doc.getDocumentElement(), elementName, namespace);
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
            return null;


        }
        return null;

    }

    private void putConfigurationFragment(final Element fragment) throws IllegalArgumentException {

        Document doc = null;
        FileObject config = baseDir.getFileObject(REPOSITORYS_FILE_NAME);

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
            doc.createComment("This document contains Local Task Repository informations");
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
                config = baseDir.createData(REPOSITORYS_FILE_NAME);
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
        
        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) l.item(i);
                if (name.equals(el.getLocalName()) &&
                        ((namespace == el.getNamespaceURI()) /*check both namespaces are null*/ || (namespace != null && namespace.equals(el.getNamespaceURI())))) {
                    return el;
                }
            }
        }
        return null;
    }
}
