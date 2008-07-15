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

import com.dolby.jira.net.soap.jira.RemoteComponent;
import com.dolby.jira.net.soap.jira.RemoteConfiguration;
import com.dolby.jira.net.soap.jira.RemoteFilter;
import com.dolby.jira.net.soap.jira.RemoteIssueType;
import com.dolby.jira.net.soap.jira.RemotePriority;
import com.dolby.jira.net.soap.jira.RemoteProject;
import com.dolby.jira.net.soap.jira.RemoteResolution;
import com.dolby.jira.net.soap.jira.RemoteStatus;
import com.dolby.jira.net.soap.jira.RemoteVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
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
class JiraAttributesPersistence {

    private static final String TAG_UNASSIGNED_ISSUES = "unassigned_issues";
    private static final String TAG_USER_MANAGMENT = "user_managment";
    private static final String TAG_WATCHING = "watching";
    private static final String FILESYSTEM_FILE_TAG = "attributes.xml"; //NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private static final String TAG_ATTACHMENTS = "attachments";
    private static final String TAG_ISSUE_LINKING = "Issue_linking";
    private static final String TAG_ROOT = "attributes";
    private static final String TAG_PRIORITIES = "priorites";
    private static final String TAG_PROJECTS = "projects";
    private static final String TAG_FILTERS = "filters";
    private static final String TAG_SUB_TASKS = "sub_tasks";
    private static final String TAG_TYPES = "types";
    private static final String TAG_COMPONENTS = "components";
    private static final String TAG_VERSIONS = "versions";
    private static final String TAG_STATUSES = "statuses";
    private static final String TAG_RESOLUTIONS = "resolutions";
    private static final String TAG_PRIORITY = "priority";
    private static final String TAG_PROJECT = "project";
    private static final String TAG_FILTER = "filter";
    private static final String TAG_TYPE = "type";
    private static final String TAG_COMPONENT = "component";
    private static final String TAG_VERSION = "version";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESOLUTION = "resolution";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LEAD = "lead";
    private static final String TAG_CONFIGURATIONS = "Configurations";
    private static final String TAG_VOTING = "voting";
    private static final String TAG_AUTHOR = "author";
    private final JiraTaskRepository repository;
    private final FileObject baseDir;
    private final Object LOCK = new Object();
    private static Logger LOG = Logger.getLogger(JiraAttributesPersistence.class.getName());

    JiraAttributesPersistence(JiraTaskRepository jiraTaskRepository, FileObject fileObject) {
        this.repository = jiraTaskRepository;
        this.baseDir = fileObject;
    }

    void refresh(ProgressHandle progressHandle) throws JiraException {

        synchronized (LOCK) {
            JiraSession session = repository.getSession();
            if (session == null) {
                return;
            }
            Document document = getDocument(true);
            Element root = getRootElement(document);
            Element attributes = findElement(root, TAG_ROOT, NAMESPACE);

            if (attributes == null) {
                attributes = document.createElement(TAG_ROOT);
                root.appendChild(attributes);
            }
            progressHandle.start();
            progressHandle.switchToIndeterminate();

            LOG.log(Level.INFO, "requsting projects");
            progressHandle.progress("Requsting Projects Information");

            RemoteProject[] projects = session.getProjects();
            LOG.log(Level.INFO, projects.length + " projects found");
            progressHandle.progress(projects.length + " Projects found");
            Element projectsElement = getEmptyElement(document, attributes, TAG_PROJECTS);
            for (RemoteProject rp : projects) {
                LOG.log(Level.INFO, "project :" + rp.getKey() + " : " + rp.getName());
                progressHandle.progress("Project :" + rp.getName() + " : " + rp.getName());
                Element project = document.createElement(TAG_PROJECT);
                projectsElement.appendChild(project);
                project.setAttribute(TAG_ID, rp.getKey());
                project.setAttribute(TAG_NAME, rp.getName());
                project.setAttribute(TAG_DESCRIPTION, rp.getDescription());
                project.setAttribute(TAG_LEAD, rp.getLead());
                /*
                RemoteIssueType[] issueTypes = session.getIssueTypesForProject(rp.getId());
                Element types = document.createElement(TAG_TYPES);
                project.appendChild(types);
                for (RemoteIssueType rit : issueTypes) {
                Element type = document.createElement(TAG_TYPE);
                types.appendChild(type);
                type.setAttribute(TAG_ID, rit.getId());
                }*/
                LOG.log(Level.INFO, "requsting Components ");
                RemoteComponent[] remoteComponents = session.getComponents(rp.getKey());
                Element components = document.createElement(TAG_COMPONENTS);
                project.appendChild(components);
                for (RemoteComponent rc : remoteComponents) {
                    LOG.log(Level.INFO, "component :" + rc.getName());
                    Element component = document.createElement(TAG_COMPONENT);
                    components.appendChild(component);
                    component.setAttribute(TAG_ID, rc.getId());
                    component.setAttribute(TAG_NAME, rc.getName());
                }

                LOG.log(Level.INFO, "requsting versions ");
                RemoteVersion[] remoteVersions = session.getVersions(rp.getKey());
                Element versions = document.createElement(TAG_VERSIONS);
                project.appendChild(versions);
                for (RemoteVersion rv : remoteVersions) {
                    LOG.log(Level.INFO, "version :" + rv.getName());
                    Element version = document.createElement(TAG_VERSION);
                    versions.appendChild(version);
                    version.setAttribute(TAG_ID, rv.getId());
                    version.setAttribute(TAG_NAME, rv.getName());
                }
            }


            RemotePriority[] priorities = session.getPriorities();
            Element prioritiesElement = getEmptyElement(document, attributes, TAG_PRIORITIES);
            for (RemotePriority rp : priorities) {
                Element priority = document.createElement(TAG_PRIORITY);
                prioritiesElement.appendChild(priority);
                priority.setAttribute(TAG_ID, rp.getId());
                priority.setAttribute(TAG_NAME, rp.getName());
            }

            //-----------------------------------------------------------------
            RemoteIssueType[] issueTypes = session.getIssueTypes();
            Element typesElement = getEmptyElement(document, attributes, TAG_TYPES);
            for (RemoteIssueType issueType : issueTypes) {
                Element type = document.createElement(TAG_TYPE);
                typesElement.appendChild(type);
                type.setAttribute(TAG_ID, issueType.getId());
                type.setAttribute(TAG_NAME, issueType.getName());
            }

            //-----------------------------------------------------------------
            RemoteStatus[] statuses = session.getStatuses();
            Element statusesElement = getEmptyElement(document, attributes, TAG_STATUSES);
            for (RemoteStatus rs : statuses) {
                Element status = document.createElement(TAG_STATUS);
                statusesElement.appendChild(status);
                status.setAttribute(TAG_ID, rs.getId());
                status.setAttribute(TAG_NAME, rs.getName());
            }

            //-----------------------------------------------------------------
            RemoteResolution[] resolutions = session.getResolutions();
            Element resolutionsElement = getEmptyElement(document, attributes, TAG_RESOLUTIONS);
            for (RemoteResolution rr : resolutions) {
                Element resolution = document.createElement(TAG_RESOLUTION);
                resolutionsElement.appendChild(resolution);
                resolution.setAttribute(TAG_ID, rr.getId());
                resolution.setAttribute(TAG_NAME, rr.getName());
            }
            //-----------------------------------------------------------------
            _refreshFilters(session, document, attributes);
            //-----------------------------------------------------------------
            Element configurationsElement = getEmptyElement(document, attributes, TAG_CONFIGURATIONS);
            try {
                RemoteConfiguration configuration = session.getConfiguration();
                configurationsElement.setAttribute(TAG_ATTACHMENTS, String.valueOf(configuration.isAllowAttachments()));
                configurationsElement.setAttribute(TAG_USER_MANAGMENT, String.valueOf(configuration.isAllowExternalUserManagment()));
                configurationsElement.setAttribute(TAG_ISSUE_LINKING, String.valueOf(configuration.isAllowIssueLinking()));
                configurationsElement.setAttribute(TAG_SUB_TASKS, String.valueOf(configuration.isAllowSubTasks()));
                configurationsElement.setAttribute(TAG_UNASSIGNED_ISSUES, String.valueOf(configuration.isAllowUnassignedIssues()));
                configurationsElement.setAttribute(TAG_VOTING, String.valueOf(configuration.isAllowVoting()));
                configurationsElement.setAttribute(TAG_WATCHING, String.valueOf(configuration.isAllowWatching()));

            } catch (JiraException jiraException) {
                Logger.getLogger(JiraAttributesPersistence.class.getName()).log(Level.WARNING, jiraException.getMessage());
                configurationsElement.setAttribute(TAG_ATTACHMENTS, String.valueOf(false));
                configurationsElement.setAttribute(TAG_USER_MANAGMENT, String.valueOf(false));
                configurationsElement.setAttribute(TAG_ISSUE_LINKING, String.valueOf(false));
                configurationsElement.setAttribute(TAG_SUB_TASKS, String.valueOf(false));
                configurationsElement.setAttribute(TAG_UNASSIGNED_ISSUES, String.valueOf(false));
                configurationsElement.setAttribute(TAG_VOTING, String.valueOf(false));
                configurationsElement.setAttribute(TAG_WATCHING, String.valueOf(false));
            }
            save(document);
        }
    }

    void refreshFilters(ProgressHandle progressHandle) throws JiraException {
        synchronized (LOCK) {
            JiraSession session = repository.getSession();
            if (session == null) {
                return;
            }
            Document document = getDocument(false);
            Element root = getRootElement(document);
            Element attributes = findElement(root, TAG_ROOT, NAMESPACE);

            if (attributes == null) {
                attributes = document.createElement(TAG_ROOT);
                root.appendChild(attributes);
            }
            progressHandle.start();
            progressHandle.switchToIndeterminate();
            progressHandle.progress("Requsting Repository Filters");
            _refreshFilters(session, document, attributes);
            save(document);
        }

    }

    private void _refreshFilters(JiraSession session, Document document, Element attributes) throws JiraException {
        RemoteFilter[] savedFilters = session.getSavedFilters();
        Element filtersElement = getEmptyElement(document, attributes, TAG_FILTERS);
        for (RemoteFilter remoteFilter : savedFilters) {
            Element filter = document.createElement(TAG_FILTER);
            filtersElement.appendChild(filter);
            filter.setAttribute(TAG_ID, remoteFilter.getId());
            filter.setAttribute(TAG_NAME, remoteFilter.getName());
            filter.setAttribute(TAG_DESCRIPTION, remoteFilter.getDescription());
            filter.setAttribute(TAG_AUTHOR, remoteFilter.getAuthor());
        }

    }

    private Element getEmptyElement(Document document, Element root, String tag) {
        Element taskpriorities = findElement(root, tag, NAMESPACE);
        if (taskpriorities != null) {
            root.removeChild(taskpriorities);
        }
        taskpriorities = document.createElementNS(NAMESPACE, tag);
        root.appendChild(taskpriorities);
        return taskpriorities;

    }

    private Document getDocument(boolean newDoc) {

        final FileObject config = baseDir.getFileObject(FILESYSTEM_FILE_TAG);
        Document doc = null;
        if (!newDoc && config != null) {
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

        FileObject config = baseDir.getFileObject(FILESYSTEM_FILE_TAG);

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {

                config = baseDir.createData(FILESYSTEM_FILE_TAG);
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

    public void loadAttributes() {
        synchronized (LOCK) {

            Document document = getDocument(false);
            Element root = getRootElement(document);
            Element attributes = findElement(root, TAG_ROOT, NAMESPACE);


            if (attributes != null) {

                //-----------------------------------------
                Element taskpriorities = findElement(attributes, TAG_PRIORITIES, NAMESPACE);
                NodeList repositoryNodes = taskpriorities.getChildNodes();
                List<TaskPriority> prioriiesList = new ArrayList<TaskPriority>();
                for (int i = 0; i < repositoryNodes.getLength(); i++) {

                    Node node = repositoryNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        prioriiesList.add(new TaskPriority(repository, id, name));

                    }
                }
                repository.getJiraTaskPriorityProvider().setPrioritys(prioriiesList);

                //-----------------------------------------
                Element taskTypes = findElement(attributes, TAG_TYPES, NAMESPACE);
                NodeList taskTypeNodes = taskTypes.getChildNodes();
                List<TaskType> types = new ArrayList<TaskType>();
                for (int i = 0; i < taskTypeNodes.getLength(); i++) {

                    Node node = taskTypeNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        types.add(new TaskType(repository, id, name));

                    }
                }
                repository.getJiraTaskTypeProvider().setTaskTypes(types);
                //-----------------------------------------

                Element taskStatuses = findElement(attributes, TAG_STATUSES, NAMESPACE);
                NodeList taskStatusNodes = taskStatuses.getChildNodes();
                List<TaskStatus> statuses = new ArrayList<TaskStatus>();
                for (int i = 0; i < taskStatusNodes.getLength(); i++) {

                    Node node = taskStatusNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        statuses.add(new TaskStatus(repository, id, name));

                    }
                }
                repository.getJiraTaskStatusProvider().setStatuses(statuses);

                //-----------------------------------------
                Element taskProjects = findElement(attributes, TAG_PROJECTS, NAMESPACE);
                NodeList taskProjectNodes = taskProjects.getChildNodes();
                List<JiraProject> projects = new ArrayList<JiraProject>();
                for (int i = 0; i < taskProjectNodes.getLength(); i++) {

                    Node node = taskProjectNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        String description = element.getAttribute(TAG_DESCRIPTION);
                        String lead = element.getAttribute(TAG_LEAD);
                        JiraProject jiraProject = new JiraProject(id, name, description, lead);
                        //load components
                        Element components = findElement(element, TAG_COMPONENTS, NAMESPACE);
                        if (components != null) {
                            List<JiraProject.Component> cs = new ArrayList<JiraProject.Component>();
                            NodeList componentsNodes = components.getChildNodes();
                            for (int j = 0; j < componentsNodes.getLength(); j++) {

                                Node componentNode = componentsNodes.item(j);
                                if (componentNode.getNodeType() == Node.ELEMENT_NODE) {

                                    String cid = ((Element) componentNode).getAttribute(TAG_ID);
                                    String cname = ((Element) componentNode).getAttribute(TAG_NAME);
                                    cs.add(new JiraProject.Component(cid, cname));
                                }
                            }
                            jiraProject.setComponents(cs);
                        }
                        //load versions
                        Element versions = findElement(element, TAG_VERSIONS, NAMESPACE);
                        if (versions != null) {
                            List<JiraProject.Version> vs = new ArrayList<JiraProject.Version>();
                            NodeList versionNodes = versions.getChildNodes();
                            for (int j = 0; j < versionNodes.getLength(); j++) {

                                Node verstionNode = versionNodes.item(j);
                                if (verstionNode.getNodeType() == Node.ELEMENT_NODE) {

                                    String vid = ((Element) verstionNode).getAttribute(TAG_ID);
                                    String vname = ((Element) verstionNode).getAttribute(TAG_NAME);
                                    vs.add(new JiraProject.Version(vid, vname));
                                }
                            }
                            jiraProject.setVersions(vs);
                        }

                        projects.add(jiraProject);

                    }
                }
                repository.getRepositoryAttributes().setProjects(projects);
                //----------------------------------------
                _loadFilters(attributes);
                //-----------------------------------------
                Element taskResolution = findElement(attributes, TAG_RESOLUTIONS, NAMESPACE);
                NodeList taskResolutionNodes = taskResolution.getChildNodes();
                List<TaskResolution> resolutiones = new ArrayList<TaskResolution>();
                for (int i = 0; i < taskResolutionNodes.getLength(); i++) {

                    Node node = taskResolutionNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        resolutiones.add(new TaskResolution(repository, id, name));

                    }
                }
                repository.getJiraTaskResolutionProvider().setTaskResolutions(resolutiones);
            }
        }
    }

    void loadFilters() {
        Document document = getDocument(false);
        Element root = getRootElement(document);
        Element attributes = findElement(root, TAG_ROOT, NAMESPACE);


        if (attributes != null) {
            _loadFilters(attributes);
        }

    }

    private void _loadFilters(Element attributes) {
        List<JiraFilter> filters = new ArrayList<JiraFilter>();
        Element taskfilter = findElement(attributes, TAG_FILTERS, NAMESPACE);
        if (taskfilter != null) {
            NodeList filterNodes = taskfilter.getChildNodes();

            for (int i = 0; i < filterNodes.getLength(); i++) {

                Node node = filterNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    String name = element.getAttribute(TAG_NAME);
                    String description = element.getAttribute(TAG_DESCRIPTION);
                    String author = element.getAttribute(TAG_AUTHOR);

                    filters.add(new JiraFilter(repository, id, name, description,author));

                }
            }

        }

        repository.getRepositoryAttributes().setFilters(filters);

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
