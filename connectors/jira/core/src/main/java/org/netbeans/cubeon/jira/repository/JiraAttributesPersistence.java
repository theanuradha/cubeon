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
import com.dolby.jira.net.soap.jira.RemoteProjectRole;
import com.dolby.jira.net.soap.jira.RemoteProjectRoleActors;
import com.dolby.jira.net.soap.jira.RemoteResolution;
import com.dolby.jira.net.soap.jira.RemoteRoleActor;
import com.dolby.jira.net.soap.jira.RemoteStatus;
import com.dolby.jira.net.soap.jira.RemoteUser;
import com.dolby.jira.net.soap.jira.RemoteVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraUser;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
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
    private static final String TAG_USERS = "users";
    private static final String TAG_USER = "user";
    private static final String TAG_SUB_TYPE = "subtype";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LEAD = "lead";
    private static final String TAG_CONFIGURATIONS = "Configurations";
    private static final String TAG_VOTING = "voting";
    private static final String TAG_AUTHOR = "author";
    private final JiraRepositoryAttributes attributes;
    private final FileObject baseDir;
    private final Object LOCK = new Object();
    private static Logger LOG = Logger.getLogger(JiraAttributesPersistence.class.getName());

    JiraAttributesPersistence(JiraRepositoryAttributes attributes, FileObject fileObject) {
        this.attributes = attributes;
        this.baseDir = fileObject;
    }

    void refresh(ProgressHandle progressHandle) throws JiraException {

        synchronized (LOCK) {
            JiraSession session = attributes.getRepository().getSession();
            if (session == null) {
                return;
            }
            Document document = getDocument(true);
            Element root = getRootElement(document);
            Element attributesElement = findElement(root, TAG_ROOT, NAMESPACE);

            if (attributesElement == null) {
                attributesElement = document.createElement(TAG_ROOT);
                root.appendChild(attributesElement);
            }
            progressHandle.start();
            progressHandle.switchToIndeterminate();
            Element projectsElement = getEmptyElement(document, attributesElement, TAG_PROJECTS);

            RemoteProjectRole[] rprs = session.getRemoteProjectRoles();
            if (attributes.getRepository().getProjectKey() == null) {
                LOG.log(Level.INFO, "requsting projects");
                progressHandle.progress("Requsting Projects Information");

                RemoteProject[] projects = session.getProjects();
                LOG.log(Level.INFO, projects.length + " projects found");
                progressHandle.progress(projects.length + " Projects found");

                for (RemoteProject rp : projects) {
                    Element project = document.createElement(TAG_PROJECT);
                    projectsElement.appendChild(project);
                    _refreshProject(document, project, progressHandle, session, rprs, rp);
                }
            } else {
                String projectKey = attributes.getRepository().getProjectKey();
                boolean refreshedflag = false;
                List<JiraProject> projects = attributes.getProjects();
                for (JiraProject jiraProject : projects) {
                    LOG.log(Level.INFO, "requsting project : " + jiraProject.getId());
                    progressHandle.progress("Requsting Project : " + jiraProject.getId() + "Information");
                    RemoteProject rp = session.getProjectByKey(jiraProject.getId());
                    Element project = document.createElement(TAG_PROJECT);
                    projectsElement.appendChild(project);
                    _refreshProject(document, project, progressHandle, session, rprs, rp);
                    refreshedflag = refreshedflag || projectKey.equals(jiraProject.getId());
                }
                if (!refreshedflag) {
                    LOG.log(Level.INFO, "requsting project : " + projectKey);
                    progressHandle.progress("Requsting Project : " + projectKey + "Information");
                    RemoteProject rp = session.getProjectByKey(projectKey);
                    Element project = document.createElement(TAG_PROJECT);
                    projectsElement.appendChild(project);
                    _refreshProject(document, project, progressHandle, session, rprs, rp);
                }
            }

            RemotePriority[] priorities = session.getPriorities();
            Element prioritiesElement = getEmptyElement(document, attributesElement, TAG_PRIORITIES);
            for (RemotePriority rp : priorities) {
                Element priority = document.createElement(TAG_PRIORITY);
                prioritiesElement.appendChild(priority);
                priority.setAttribute(TAG_ID, rp.getId());
                priority.setAttribute(TAG_NAME, rp.getName());
            }

            //-----------------------------------------------------------------
            RemoteIssueType[] issueTypes = session.getIssueTypes();
            RemoteIssueType[] subissueTypes = session.getSubTaskIssueTypes();
            Element typesElement = getEmptyElement(document, attributesElement, TAG_TYPES);
            for (RemoteIssueType issueType : issueTypes) {
                Element type = document.createElement(TAG_TYPE);
                typesElement.appendChild(type);
                type.setAttribute(TAG_ID, issueType.getId());
                type.setAttribute(TAG_NAME, issueType.getName());
                type.setAttribute(TAG_SUB_TYPE, String.valueOf(issueType.isSubTask()));
            }
            for (RemoteIssueType issueType : subissueTypes) {
                Element type = document.createElement(TAG_TYPE);
                typesElement.appendChild(type);
                type.setAttribute(TAG_ID, issueType.getId());
                type.setAttribute(TAG_NAME, issueType.getName());
                type.setAttribute(TAG_SUB_TYPE, String.valueOf(issueType.isSubTask()));
            }

            //-----------------------------------------------------------------
            RemoteStatus[] statuses = session.getStatuses();
            Element statusesElement = getEmptyElement(document, attributesElement, TAG_STATUSES);
            for (RemoteStatus rs : statuses) {
                Element status = document.createElement(TAG_STATUS);
                statusesElement.appendChild(status);
                status.setAttribute(TAG_ID, rs.getId());
                status.setAttribute(TAG_NAME, rs.getName());
            }

            //-----------------------------------------------------------------
            RemoteResolution[] resolutions = session.getResolutions();
            Element resolutionsElement = getEmptyElement(document, attributesElement, TAG_RESOLUTIONS);
            for (RemoteResolution rr : resolutions) {
                Element resolution = document.createElement(TAG_RESOLUTION);
                resolutionsElement.appendChild(resolution);
                resolution.setAttribute(TAG_ID, rr.getId());
                resolution.setAttribute(TAG_NAME, rr.getName());
            }
            //-----------------------------------------------------------------
            _refreshFilters(session, document, attributesElement);
            //-----------------------------------------------------------------
            Element configurationsElement = getEmptyElement(document, attributesElement, TAG_CONFIGURATIONS);
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
            JiraSession session = attributes.getRepository().getSession();
            if (session == null) {
                return;
            }
            Document document = getDocument(false);
            Element root = getRootElement(document);
            Element attributesElement = findElement(root, TAG_ROOT, NAMESPACE);

            if (attributesElement == null) {
                attributesElement = document.createElement(TAG_ROOT);
                root.appendChild(attributesElement);
            }

            progressHandle.progress("Requsting Repository Filters");
            _refreshFilters(session, document, attributesElement);
            save(document);
        }

    }

    public void loadAttributes() {
        synchronized (LOCK) {

            Document document = getDocument(false);
            Element root = getRootElement(document);
            Element attributesElement = findElement(root, TAG_ROOT, NAMESPACE);


            if (attributesElement != null) {

                //-----------------------------------------
                Element taskpriorities = findElement(attributesElement, TAG_PRIORITIES, NAMESPACE);
                NodeList repositoryNodes = taskpriorities.getChildNodes();
                List<TaskPriority> prioriiesList = new ArrayList<TaskPriority>();
                for (int i = 0; i < repositoryNodes.getLength(); i++) {

                    Node node = repositoryNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        prioriiesList.add(new TaskPriority(attributes.getRepository(), id, name));

                    }
                }
                attributes.getRepository().getJiraTaskPriorityProvider().setPrioritys(prioriiesList);

                //-----------------------------------------
                Element taskTypes = findElement(attributesElement, TAG_TYPES, NAMESPACE);
                NodeList taskTypeNodes = taskTypes.getChildNodes();
                List<JiraTaskType> types = new ArrayList<JiraTaskType>();
                for (int i = 0; i < taskTypeNodes.getLength(); i++) {

                    Node node = taskTypeNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        String subtype = element.getAttribute(TAG_SUB_TYPE);

                        types.add(new JiraTaskType(attributes.getRepository(), id, name,
                                Boolean.parseBoolean(subtype)));

                    }
                }
                attributes.getRepository().getJiraTaskTypeProvider().setTaskTypes(types);
                //-----------------------------------------

                Element taskStatuses = findElement(attributesElement, TAG_STATUSES, NAMESPACE);
                NodeList taskStatusNodes = taskStatuses.getChildNodes();
                List<TaskStatus> statuses = new ArrayList<TaskStatus>();
                for (int i = 0; i < taskStatusNodes.getLength(); i++) {

                    Node node = taskStatusNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        statuses.add(new TaskStatus(attributes.getRepository(), id, name));

                    }
                }
                attributes.getRepository().getJiraTaskStatusProvider().setStatuses(statuses);

                //-----------------------------------------
                Element taskProjects = findElement(attributesElement, TAG_PROJECTS, NAMESPACE);
                NodeList taskProjectNodes = taskProjects.getChildNodes();
                List<JiraProject> projects = new ArrayList<JiraProject>();
                if (taskProjectNodes != null) {
                    for (int i = 0; i < taskProjectNodes.getLength(); i++) {

                        Node node = taskProjectNodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            projects.add(_readJiraProject(element));

                        }
                    }
                }
                attributes.setProjects(projects);
                //----------------------------------------
                _loadFilters(attributesElement);
                //-----------------------------------------
                Element taskResolution = findElement(attributesElement, TAG_RESOLUTIONS, NAMESPACE);
                NodeList taskResolutionNodes = taskResolution.getChildNodes();
                List<TaskResolution> resolutiones = new ArrayList<TaskResolution>();
                for (int i = 0; i < taskResolutionNodes.getLength(); i++) {

                    Node node = taskResolutionNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);

                        resolutiones.add(new TaskResolution(attributes.getRepository(), id, name));

                    }
                }
                attributes.getRepository().getJiraTaskResolutionProvider().setTaskResolutions(resolutiones);
            }
        }
    }

    void loadFilters() {
        Document document = getDocument(false);
        Element root = getRootElement(document);
        Element attributesElement = findElement(root, TAG_ROOT, NAMESPACE);


        if (attributesElement != null) {
            _loadFilters(attributesElement);
        }

    }

    JiraProject resolveJiraProject(String key) throws JiraException {
        ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Updating Project :" + key);
        return resolveJiraProject(key, progressHandle);
    }

    JiraProject resolveJiraProject(String key, ProgressHandle progressHandle) throws JiraException {

        progressHandle.start();
        progressHandle.switchToIndeterminate();
        try {

            JiraSession session = attributes.getRepository().getSession();
            RemoteProject project = session.getProjectByKey(key);
            synchronized (LOCK) {
                Document document = getDocument(true);
                Element root = getRootElement(document);
                Element attributesElement = findElement(root, TAG_ROOT, NAMESPACE);

                if (attributesElement == null) {
                    attributesElement = document.createElement(TAG_ROOT);
                    root.appendChild(attributesElement);
                }
                LOG.log(Level.INFO, "requsting project : " + attributes.getRepository().getProjectKey());
                progressHandle.progress("Requsting Project : " + attributes.getRepository().getProjectKey() + "Information");
                Element projectsElement = findElement(attributesElement, TAG_PROJECTS, NAMESPACE);
                if (projectsElement == null) {
                    attributesElement.appendChild((projectsElement = document.createElement(TAG_PROJECTS)));
                }
                Element projectElement = null;

                NodeList taskNodes =
                        projectsElement.getElementsByTagNameNS(NAMESPACE, TAG_PROJECT);

                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttributeNS(NAMESPACE, TAG_ID);
                        if (key.equals(id)) {
                            projectElement = element;
                            break;
                        }
                    }
                }

                if (projectElement == null) {
                    projectsElement.appendChild((projectElement =
                            document.createElement(TAG_PROJECT)));
                }
                RemoteProjectRole[] remoteProjectRoles = session.getRemoteProjectRoles();
                _refreshProject(document, projectElement, progressHandle, session,
                        remoteProjectRoles, project);
                JiraProject _readJiraProject = _readJiraProject(projectElement);
                save(document);
                return _readJiraProject;
            }
        } finally {
            progressHandle.finish();

        }


    }

    private void _refreshProject(Document document, Element project, ProgressHandle progressHandle, JiraSession session, RemoteProjectRole[] projectRoles, RemoteProject rp) throws JiraException {

        LOG.log(Level.INFO, "project :" + rp.getKey() + " : " + rp.getName());
        progressHandle.progress("Project :" + rp.getName() + " : " + rp.getName());

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
        Set<String> userIds = new HashSet<String>();
        Element usersElement = getEmptyElement(document, project, TAG_USERS);
        for (RemoteProjectRole remoteProjectRole : projectRoles) {


            LOG.log(Level.INFO, "ProjectRole : " + remoteProjectRole.getName() + " : " + remoteProjectRole.getDescription());
            RemoteProjectRoleActors actors = session.getProjectRoleActors(remoteProjectRole, rp);
            RemoteRoleActor[] roleActors = actors.getRoleActors();
            for (RemoteRoleActor rra : roleActors) {


                RemoteUser[] users = rra.getUsers();
                for (RemoteUser ru : users) {
                    if (userIds.contains(ru.getName())) {
                        continue;
                    }
                    userIds.add(ru.getName());
                    LOG.log(Level.INFO, "USER : " + ru.getName() + " : " + ru.getFullname());
                    Element userElement = document.createElement(TAG_USER);
                    usersElement.appendChild(userElement);
                    userElement.setAttribute(TAG_ID, ru.getName());
                    userElement.setAttribute(TAG_NAME, ru.getFullname());
                }
            }
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

    private void _loadFilters(Element attributesElement) {
        List<JiraFilter> filters = new ArrayList<JiraFilter>();
        Element taskfilter = findElement(attributesElement, TAG_FILTERS, NAMESPACE);
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

                    filters.add(new JiraFilter(attributes.getRepository(), id, name, description, author));

                }
            }

        }

        attributes.setFilters(filters);

    }

    private JiraProject _readJiraProject(Element element) {
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
        //load users

        Element users = findElement(element, TAG_USERS, NAMESPACE);
        if (users != null) {
            List<JiraUser> us = new ArrayList<JiraUser>();
            NodeList usersNodeList = users.getChildNodes();
            for (int j = 0; j < usersNodeList.getLength(); j++) {

                Node userNode = usersNodeList.item(j);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {

                    String uid = ((Element) userNode).getAttribute(TAG_ID);
                    String uname = ((Element) userNode).getAttribute(TAG_NAME);
                    us.add(new JiraUser(uid, uname));
                }
            }
            jiraProject.setUsers(us);
        }

        return jiraProject;
    }

    /******************************************** XML Related ***********************************/
    private Element getEmptyElement(Document document, Element root, String tag) {
        Element element = findElement(root, tag, NAMESPACE);
        if (element != null) {
            root.removeChild(element);
        }
        element = document.createElementNS(NAMESPACE, tag);
        root.appendChild(element);
        return element;

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
