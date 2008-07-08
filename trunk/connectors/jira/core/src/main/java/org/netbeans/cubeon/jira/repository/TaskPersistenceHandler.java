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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
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
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Anuradha G
 */
class TaskPersistenceHandler {

    private static final String FILESYSTEM_FILE_TAG = "repository.xml"; //NOI18N
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
    private static final String TAG_PROJECT = "project";
    private static final String TAG_ENVIRONMENT = "environment";
    private static final String TAG_RESOLUTION = "resolution";
    private static final String TAG_CREATED_DATE = "cdate";
    private static final String TAG_UPDATE_DATE = "udate";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_COMPONENTS = "components";
    private static final String TAG_AFFECT_VERSIONS = "affect_version";
    private static final String TAG_FIX_VERSIONS = "fix_version";
    private static final String TAG_COMPONENT = "component";
    private static final String TAG_VERSION = "version";
    private static final String TAG_LOCAL = "local";
    private static final String TAG_REPORTER = "reporter";
    private static final String TAG_ASSIGNEE = "assignee";
    private static final String TAG_ACTIONS = "actions";
    private static final String TAG_ACTION = "action";
    private static final String TAG_FIELDS = "fields";
    private static final String TAG_FIELD = "field";
    //----------------------------------------------------
    private JiraTaskRepository jiraTaskRepository;
    private final FileObject baseDir;
    private FileObject tasksFolder;
    private static final Object LOCK = new Object();

    TaskPersistenceHandler(JiraTaskRepository localTaskRepository, FileObject fileObject) {
        this.jiraTaskRepository = localTaskRepository;
        this.baseDir = fileObject;

        tasksFolder = fileObject.getFileObject("tasks");
        if (tasksFolder == null) {
            try {
                tasksFolder = fileObject.createFolder("tasks");
            } catch (IOException ex) {
                Logger.getLogger(TaskPersistenceHandler.class.getName()).log(Level.WARNING, ex.getMessage());
            }
        }
        assert tasksFolder != null;

    }

    JiraTask getTaskElementById(String id) {
        Document taskDocument = getTaskDocument(id);
        Element rootElement = getRootElement(taskDocument);
        Element element = findElement(rootElement, TAG_TASK, NAMESPACE);
        if (element != null) {
            JiraTaskPriorityProvider priorityProvider = jiraTaskRepository.getJiraTaskPriorityProvider();
            JiraTaskStatusProvider statusProvider = jiraTaskRepository.getJiraTaskStatusProvider();
            JiraTaskTypeProvider taskTypeProvider = jiraTaskRepository.getJiraTaskTypeProvider();
            JiraTaskResolutionProvider resolutionProvider = jiraTaskRepository.getJiraTaskResolutionProvider();
            Calendar calendar = Calendar.getInstance(Locale.getDefault());


            String name = element.getAttributeNS(NAMESPACE, TAG_NAME);
            String description = element.getAttributeNS(NAMESPACE, TAG_DESCRIPTION);
            String environment = element.getAttributeNS(NAMESPACE, TAG_ENVIRONMENT);
            //read priority
            String priority = element.getAttributeNS(NAMESPACE, TAG_PRIORITY);
            TaskPriority taskPriority = priorityProvider.getTaskPriorityById((priority));
            //read status
            String status = element.getAttributeNS(NAMESPACE, TAG_STATUS);
            TaskStatus taskStatus = statusProvider.getTaskStatusById(status);
            //read type
            String type = element.getAttributeNS(NAMESPACE, TAG_TYPE);
            TaskType taskType = taskTypeProvider.getTaskTypeById(type);

            String localTag = element.getAttributeNS(NAMESPACE, TAG_LOCAL);
            boolean local = false;
            if (localTag != null) {
                local = Boolean.parseBoolean(localTag);
            }
            //read resolution
            String resolution = element.getAttributeNS(NAMESPACE, TAG_RESOLUTION);
            TaskResolution taskResolution = null;
            if (resolution != null) {
                taskResolution = resolutionProvider.getTaskResolutionById(resolution);
            }
            //read resolution
            String projectId = element.getAttributeNS(NAMESPACE, TAG_PROJECT);
            JiraRepositoryAttributes attributes = jiraTaskRepository.getRepositoryAttributes();
            JiraProject project = attributes.getProjectById(projectId);

            String url = element.getAttributeNS(NAMESPACE, TAG_URL);

            Date createdDate = null;
            Date updatedDate = null;
            String created = element.getAttributeNS(NAMESPACE, TAG_CREATED_DATE);
            if (created != null && created.trim().length() != 0) {

                calendar.setTimeInMillis(Long.parseLong(created));
                createdDate = calendar.getTime();

            }
            String updated = element.getAttributeNS(NAMESPACE, TAG_UPDATE_DATE);
            if (updated != null && updated.trim().length() != 0) {

                calendar.setTimeInMillis(Long.parseLong(updated));
                updatedDate = calendar.getTime();

            }
            //load versions

            Element versionsElement = findElement(element, TAG_FIX_VERSIONS, NAMESPACE);
            NodeList versionsNodeList = versionsElement.getElementsByTagNameNS(NAMESPACE, TAG_VERSION);
            List<JiraProject.Version> fixVersions = new ArrayList<Version>();

            for (int i = 0; i < versionsNodeList.getLength(); i++) {
                Node node = versionsNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;

                    Version version = project.getVersionById(e.getTextContent());
                    if (version != null) {
                        fixVersions.add(version);
                    }
                }
            }



            versionsElement = findElement(element, TAG_AFFECT_VERSIONS, NAMESPACE);
            versionsNodeList = versionsElement.getElementsByTagNameNS(NAMESPACE, TAG_VERSION);
            List<JiraProject.Version> affectversions = new ArrayList<Version>();

            for (int i = 0; i < versionsNodeList.getLength(); i++) {
                Node node = versionsNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Version version = project.getVersionById(e.getTextContent());
                    if (version != null) {
                        affectversions.add(version);
                    }
                }
            }


            //load components
            Element componentsElement = findElement(element, TAG_COMPONENTS, NAMESPACE);
            NodeList componentsNodeList = componentsElement.getElementsByTagNameNS(NAMESPACE, TAG_COMPONENT);
            List<JiraProject.Component> components = new ArrayList<Component>();

            for (int i = 0; i < componentsNodeList.getLength(); i++) {
                Node node = componentsNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Component component = project.getComponentById(e.getTextContent());
                    if (component != null) {
                        components.add(component);
                    }
                }
            }


            String reporter = element.getAttributeNS(NAMESPACE, TAG_REPORTER);
            String assignee = element.getAttributeNS(NAMESPACE, TAG_ASSIGNEE);

            //read actions
            String action = element.getAttributeNS(NAMESPACE, TAG_ACTION);
            JiraAction selectedAction = null;
            Element actionsElement = findElement(element, TAG_ACTIONS, NAMESPACE);
            NodeList actionssNodeList = actionsElement.getElementsByTagNameNS(NAMESPACE, TAG_ACTION);
            List<JiraAction> actions = new ArrayList<JiraAction>();
            for (int i = 0; i < actionssNodeList.getLength(); i++) {
                Node node = actionssNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    String aId = e.getAttribute(TAG_ID);
                    String aName = e.getAttribute(TAG_NAME);
                    JiraAction jiraAction = new JiraAction(aId, aName);
                    if (selectedAction == null && aId.equals(action)) {
                        selectedAction = jiraAction;
                    }
                    Element fieldsElement = findElement(e, TAG_FIELDS, NAMESPACE);
                    NodeList fieldsNodeList = fieldsElement.getElementsByTagNameNS(NAMESPACE, TAG_FIELD);
                    for (int j = 0; j < fieldsNodeList.getLength(); j++) {
                        Node fnode = fieldsNodeList.item(j);
                        if (fnode.getNodeType() == Node.ELEMENT_NODE) {
                            Element fe = (Element) fnode;
                            String fid = fe.getTextContent();
                            jiraAction.addFiled(fid);
                        }
                    }
                    actions.add(jiraAction);
                }
            }



            JiraTask jiraTask = new JiraTask(id, name, description, jiraTaskRepository);
            jiraTask.setProject(project);
            jiraTask.setLocal(local);
            jiraTask.setEnvironment(environment);
            jiraTask.setPriority(taskPriority);
            jiraTask.setStatus(taskStatus);
            jiraTask.setType(taskType);
            jiraTask.setResolution(taskResolution);
            jiraTask.setUrlString(url);
            jiraTask.setCreated(createdDate);
            jiraTask.setUpdated(updatedDate);
            jiraTask.setComponents(components);
            jiraTask.setAffectedVersions(affectversions);
            jiraTask.setFixVersions(fixVersions);
            jiraTask.setReporter(reporter);
            jiraTask.setAssignee(assignee);
            jiraTask.getActionsProvider().setActions(actions);
            jiraTask.setAction(selectedAction);

            return jiraTask;

        }
        return null;
    }

    void vaidate(TaskElement element) {
        //do validations changes here
    }

    void persist(JiraTask task) {
        synchronized (LOCK) {
            //create task xml
            Document document = getTaskDocument(task.getId());
            Element root = getRootElement(document);
            Element taskElement = findElement(root, TAG_TASK, NAMESPACE);
            //check tasksElement null and create element
            if (taskElement == null) {
                taskElement = document.createElementNS(NAMESPACE, TAG_TASK);
                root.appendChild(taskElement);
            }

            taskElement.setAttributeNS(NAMESPACE, TAG_ID, task.getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_NAME, task.getName());
            taskElement.setAttributeNS(NAMESPACE, TAG_DESCRIPTION, task.getDescription());
            taskElement.setAttributeNS(NAMESPACE, TAG_REPOSITORY, task.getTaskRepository().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_PRIORITY, task.getPriority().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_STATUS, task.getStatus().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_TYPE, task.getType().getId());
            taskElement.setAttributeNS(NAMESPACE, TAG_PROJECT, task.getProject().getId());

            if (task.getReporter() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_REPORTER, task.getReporter());
            }
            if (task.getAssignee() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_ASSIGNEE, task.getAssignee());
            }

            if (task.isLocal()) {
                taskElement.setAttributeNS(NAMESPACE, TAG_LOCAL, String.valueOf(task.isLocal()));
            }

            if (task.getEnvironment() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_ENVIRONMENT, task.getEnvironment());
            }

            Element componentsElement = getEmptyElement(document, taskElement, TAG_COMPONENTS);
            List<Component> components = task.getComponents();
            for (Component component : components) {
                Text text = document.createTextNode(component.getId());
                Element element = document.createElement(TAG_COMPONENT);
                element.appendChild(text);
                componentsElement.appendChild(element);
            }

            List<Version> affectedVersions = task.getAffectedVersions();
            Element affectsVersion = getEmptyElement(document, taskElement, TAG_AFFECT_VERSIONS);
            for (Version version : affectedVersions) {
                Text text = document.createTextNode(version.getId());
                Element element = document.createElement(TAG_VERSION);
                element.appendChild(text);
                affectsVersion.appendChild(element);
            }

            List<Version> fixversions = task.getFixVersions();
            Element fixVersions = getEmptyElement(document, taskElement, TAG_FIX_VERSIONS);
            for (Version version : fixversions) {
                Text text = document.createTextNode(version.getId());
                Element element = document.createElement(TAG_VERSION);
                element.appendChild(text);
                fixVersions.appendChild(element);
            }


            if (task.getResolution() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_RESOLUTION, task.getResolution().getId());
            }

            if (task.getUrlString() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_URL, task.getUrlString());
            }

            if (task.getCreated() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_CREATED_DATE, String.valueOf(task.getCreated().getTime()));

            }
            if (task.getUpdated() != null) {
                taskElement.setAttributeNS(NAMESPACE, TAG_UPDATE_DATE, String.valueOf(task.getUpdated().getTime()));
            }
            //actions
            List<JiraAction> actions = task.getActions();
            Element actionsElement = getEmptyElement(document, taskElement, TAG_ACTIONS);
            for (JiraAction action : actions) {
                Element element = document.createElement(TAG_ACTION);
                actionsElement.appendChild(element);
                element.setAttribute(TAG_ID, action.getId());
                element.setAttribute(TAG_NAME, action.getName());
                Element fieldselement = document.createElement(TAG_FIELDS);
                element.appendChild(fieldselement);
                List<String> filedIds = action.getFiledIds();
                for (String id : filedIds) {
                    Text text = document.createTextNode(id);
                    Element field = document.createElement(TAG_FIELD);
                    field.appendChild(text);
                    fieldselement.appendChild(field);
                }
            }
            saveTask(document, task.getId());
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

    void removeTaskElement(TaskElement te) {
        synchronized (LOCK) {
            FileObject taskFo = tasksFolder.getFileObject(te.getId() + ".xml");
            if (taskFo != null) {
                try {
                    taskFo.delete();
                } catch (IOException ex) {
                    Logger.getLogger(TaskPersistenceHandler.class.getName()).log(Level.WARNING, ex.getMessage());
                }
            }
        }
    }

    String nextTaskId() {
        String id = jiraTaskRepository.getId().toUpperCase();
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

    private Document getDocument() {
        final FileObject config = baseDir.getFileObject(FILESYSTEM_FILE_TAG);
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

    private Document getTaskDocument(String tag) {
        final FileObject config = tasksFolder.getFileObject(tag + ".xml");
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

    private void saveTask(Document doc, String tag) {

        FileObject config = tasksFolder.getFileObject(tag + ".xml");

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {

                config = tasksFolder.createData(tag + ".xml");
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