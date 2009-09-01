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
import org.netbeans.cubeon.tasks.core.api.RepositoryUtils;
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
    private static final String TAG_ROOT = "jira-task-repository-configuration";//NOI18N
    private static final String TAG_REPOSITORIES = "repositories";//NOI18N
    private static final String TAG_REPOSITORY = "repository";//NOI18N
    private static final String TAG_ID = "id";//NOI18N
    private static final String TAG_USERID = "user";//NOI18N
    private static final String TAG_IGNORE_SSL = "ignore-ssl";//NOI18N
    private static final String TAG_VERSION = "version";//NOI18N
    private static final String VERSION = "1.0";//NOI18N
    private static final String TAG_URL = "url";//NOI18N
    private static final String TAG_PROJECT = "project";//NOI18N
    private static final String TAG_PASSWORD_HASH = "password";//NOI18N
    private static final String TAG_NAME = "name";//NOI18N
    private static final String TAG_DESCRIPTION = "description";//NOI18N
    private JiraTaskRepositoryProvider provider;
    private FileObject baseDir;

    JiraRepositoryPersistence(JiraTaskRepositoryProvider provider, FileObject baseDir) {
        this.provider = provider;
        this.baseDir = baseDir;
    }

    void addRepository(JiraTaskRepository repository) {
        Element repositoryElement = null;

        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES);
        if (repositorysElement == null) {
            Document doc = XMLUtil.createDocument(TAG_ROOT, null, null, null);
            repositorysElement = doc.createElement(TAG_REPOSITORIES);
        }

        NodeList repositoryNodes =
                repositorysElement.getElementsByTagName(TAG_REPOSITORY);

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
            repositoryElement = document.createElement(TAG_REPOSITORY);
            repositorysElement.appendChild(repositoryElement);
        }

        repositoryElement.setAttribute(TAG_ID, repository.getId());
        repositoryElement.setAttribute(TAG_VERSION, VERSION);
        repositoryElement.setAttribute(TAG_NAME, repository.getName());
        repositoryElement.setAttribute(TAG_DESCRIPTION, repository.getDescription());
        repositoryElement.setAttribute(TAG_USERID, repository.getUserName());
        repositoryElement.setAttribute(TAG_IGNORE_SSL, String.valueOf(repository.isIgnoreSSL()));
        repositoryElement.setAttribute(TAG_URL, repository.getURL());
        if (repository.getProjectKey() != null && repository.getProjectKey().trim().length() > 0) {
            repositoryElement.setAttribute(TAG_PROJECT, repository.getProjectKey());
        } else {
            repositoryElement.removeAttribute(TAG_PROJECT);
        }
        repositoryElement.setAttribute(TAG_PASSWORD_HASH, RepositoryUtils.encodePassword(repository.getUserName(),
                repository.getPassword()));

        putConfigurationFragment(repositorysElement);
    }

    void removeRepository(JiraTaskRepository repository) {
        Element repositoryElement = null;

        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES);
        if (repositorysElement == null) {
            return;
        }

        NodeList repositoryNodes =
                repositorysElement.getElementsByTagName(TAG_REPOSITORY);

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

        if (repositoryElement != null) {

            repositorysElement.removeChild(repositoryElement);
        }


        putConfigurationFragment(repositorysElement);
    }

    List<JiraTaskRepository> getJiraTaskRepositorys() {
        List<JiraTaskRepository> repositorys = new ArrayList<JiraTaskRepository>();
        List<JiraTaskRepository> markForUpdate = new ArrayList<JiraTaskRepository>();
        Element repositorysElement = getConfigurationFragment(TAG_REPOSITORIES);

        if (repositorysElement != null) {

            NodeList repositoryNodes =
                    repositorysElement.getElementsByTagName(TAG_REPOSITORY);

            for (int i = 0; i < repositoryNodes.getLength(); i++) {
                Node node = repositoryNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    String version = element.getAttribute(TAG_VERSION);
                    String name = element.getAttribute(TAG_NAME);
                    String description = element.getAttribute(TAG_DESCRIPTION);
                    String url = element.getAttribute(TAG_URL);
                    String user = element.getAttribute(TAG_USERID);
                    String ignoreSSL = element.hasAttribute(TAG_IGNORE_SSL)? element.getAttribute(TAG_IGNORE_SSL):null;
                    String project = element.getAttribute(TAG_PROJECT);

                    String password = element.getAttribute(TAG_PASSWORD_HASH);
                    JiraTaskRepository jiraTaskRepository;
                    //backwade compatibility for versions before <1.0
                    if (VERSION.equals(version)) {
                        password = RepositoryUtils.decodePassword(user, password);
                        jiraTaskRepository = new JiraTaskRepository(provider, id, name, description);
                    } else {
                        jiraTaskRepository = new JiraTaskRepository(provider, id, name, description);
                        markForUpdate.add(jiraTaskRepository);
                    }


                    jiraTaskRepository.setUserName(user);
                    jiraTaskRepository.setPassword(password);
                    jiraTaskRepository.setURL(url);
                    jiraTaskRepository.setIgnoreSSL(ignoreSSL==null || Boolean.valueOf(ignoreSSL));
                    if (project == null || project.trim().length() == 0) {
                        jiraTaskRepository.setProjectKey(null);
                    } else {
                        jiraTaskRepository.setProjectKey(project);
                    }
                    jiraTaskRepository.loadAttributes();
                    jiraTaskRepository.loadFilters();
                    repositorys.add(jiraTaskRepository);
                }
            }
        }
        //merge old versions before <1.0 with verstion tag and password encoding 
        for (JiraTaskRepository jiraTaskRepository : markForUpdate) {
            addRepository(jiraTaskRepository);
        }
        return repositorys;


    }

    //xml related
    private Element getConfigurationFragment(final String elementName) {

        final FileObject config = baseDir.getFileObject(REPOSITORYS_FILE_NAME);
        if (config != null) {


            Document doc;
            InputStream in = null;
            try {
                in = config.getInputStream();
                doc = XMLUtil.parse(new InputSource(in), false, true, null, null);
                return findElement(doc.getDocumentElement(), elementName);
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
            doc.createComment("This document contains Local Task Repository informations");//NOI18N
        }

        if (doc != null) {
            Element el = findElement(doc.getDocumentElement(), fragment.getNodeName());
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
