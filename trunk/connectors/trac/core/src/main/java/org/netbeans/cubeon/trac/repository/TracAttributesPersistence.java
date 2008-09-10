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
package org.netbeans.cubeon.trac.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskSeverity;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.netbeans.cubeon.trac.api.TicketComponent;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.api.TicketMilestone;
import org.netbeans.cubeon.trac.api.TicketPriority;
import org.netbeans.cubeon.trac.api.TicketResolution;
import org.netbeans.cubeon.trac.api.TicketSeverity;
import org.netbeans.cubeon.trac.api.TicketStatus;
import org.netbeans.cubeon.trac.api.TicketType;
import org.netbeans.cubeon.trac.api.TicketVersion;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracSession;
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
public class TracAttributesPersistence {

    private static final String TAG_COMPLETED = "completed";
    private static final String TAG_MILESTONE = "milestone";
    private static final String TAG_MILESTONES = "milestones";
    private static final String TAG_OPTIONAL = "optional";
    private static final String TAG_OPTIONS = "options";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_SEVERITIES = "severities";
    private static final String TAG_SEVERITY = "severity";
    private static final String TAG_UNASSIGNED_ISSUES = "unassigned_issues";//NOI18N
    private static final String TAG_USER_MANAGMENT = "user_managment";//NOI18N
    private static final String TAG_VALUE = "value";
    private static final String TAG_WATCHING = "watching";//NOI18N
    private static final String FILESYSTEM_FILE_TAG = "attributes.xml"; //NOI18N
    private static final String TAG_ATTACHMENTS = "attachments";//NOI18N
    private static final String TAG_ISSUE_LINKING = "Issue_linking";//NOI18N
    private static final String TAG_ROOT = "attributes";//NOI18N
    private static final String TAG_PRIORITIES = "priorites";//NOI18N
    private static final String TAG_PROJECTS = "projects";//NOI18N
    private static final String TAG_FILTERS = "filters";//NOI18N
    private static final String TAG_SUB_TASKS = "sub_tasks";//NOI18N
    private static final String TAG_TYPES = "types";//NOI18N
    private static final String TAG_COMPONENTS = "components";//NOI18N
    private static final String TAG_VERSIONS = "versions";//NOI18N
    private static final String TAG_STATUSES = "statuses";//NOI18N
    private static final String TAG_RESOLUTIONS = "resolutions";//NOI18N
    private static final String TAG_PRIORITY = "priority";//NOI18N
    private static final String TAG_PROJECT = "project";//NOI18N
    private static final String TAG_FIELDS = "fields";//NOI18N
    private static final String TAG_FIELD = "field";//NOI18N
    private static final String TAG_FILTER = "filter";//NOI18N
    private static final String TAG_TYPE = "type";//NOI18N
    private static final String TAG_COMPONENT = "component";//NOI18N
    private static final String TAG_VERSION = "version";//NOI18N
    private static final String TAG_STATUS = "status";//NOI18N
    private static final String TAG_RESOLUTION = "resolution";//NOI18N
    private static final String TAG_ID = "id";//NOI18N
    private static final String TAG_NAME = "name";//NOI18N
    private static final String TAG_SCEQUENCE = "scequence";//NOI18N
    private static final String TAG_RELEASED = "released";//NOI18N
    private static final String TAG_ARCHIVED = "archived";//NOI18N
    private static final String TAG_USERS = "users";//NOI18N
    private static final String TAG_USER = "user";//NOI18N
    private static final String TAG_SUB_TYPE = "subtype";//NOI18N
    private static final String TAG_DESCRIPTION = "description";//NOI18N
    private static final String TAG_LEAD = "lead";//NOI18N
    private static final String TAG_CONFIGURATIONS = "Configurations";//NOI18N
    private static final String TAG_VOTING = "voting";//NOI18N
    private static final String TAG_AUTHOR = "author";//NOI18N
    private final TracRepositoryAttributes attributes;
    private final FileObject baseDir;
    private final Object LOCK = new Object();
    private static Logger LOG = Logger.getLogger(TracRepositoryAttributes.class.getName());

    TracAttributesPersistence(TracRepositoryAttributes attributes, FileObject fileObject) {
        this.attributes = attributes;
        this.baseDir = fileObject;
    }

    void loadAttributes() {
        synchronized (LOCK) {

            Document document = getDocument(false);
            Element root = getRootElement(document);
            Element attributesElement = findElement(root, TAG_ROOT);
            if (attributesElement != null) {

                Element fieldsElement = findElement(attributesElement, TAG_FIELDS);
                NodeList fieldsNodeList = fieldsElement.getChildNodes();
                List<TicketField> ticketFields = new ArrayList<TicketField>();
                for (int i = 0; i < fieldsNodeList.getLength(); i++) {

                    Node node = fieldsNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        //read field type
                        String type = element.getAttribute(TAG_TYPE);
                        //read field optional 
                        String optional = element.getAttribute(TAG_OPTIONAL);
                        String value = element.getAttribute(TAG_VALUE);
                        //if valuve empty assign to null
                        value = (value == null || value.trim().length() == 0)
                                ? null : value;
                        String optionsTag = element.getAttribute(TAG_OPTIONS);
                        //extract options from options tag
                        List<String> options = getStringsByTag(optionsTag);
                        TicketField ticketField =
                                new TicketField(id, name, value, type,
                                Boolean.parseBoolean(optional), options);

                        ticketFields.add(ticketField);
                    }
                }
                //load ticket fields to attributes
                attributes.setTicketFields(ticketFields);

                //read components
                Element componentsElement = findElement(attributesElement,
                        TAG_COMPONENTS);
                NodeList componentsNodeList = componentsElement.getChildNodes();
                List<TicketComponent> ticketComponents = new ArrayList<TicketComponent>();
                for (int i = 0; i < componentsNodeList.getLength(); i++) {

                    Node node = componentsNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String name = element.getAttribute(TAG_NAME);
                        String description = element.getAttribute(TAG_DESCRIPTION);
                        String owner = element.getAttribute(TAG_OWNER);
                        ticketComponents.add(new TicketComponent(name,
                                description, owner));
                    }
                }
                //load ticket comonents to attributes
                attributes.setTicketComponents(ticketComponents);

                //read types
                Element typesElement = findElement(attributesElement,
                        TAG_TYPES);
                NodeList typesNodeList = typesElement.getChildNodes();
                List<TaskType> taskTypes = new ArrayList<TaskType>();
                for (int i = 0; i < typesNodeList.getLength(); i++) {

                    Node node = typesNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        taskTypes.add(new TaskType(
                                attributes.getRepository(), id, name));
                    }
                }
                // load ticket types to attributes
                attributes.getRepository().getTypeProvider().
                        setTaskTypes(taskTypes);

                Element proritiesElement = findElement(attributesElement,
                        TAG_PRIORITIES);
                NodeList prioritiesNodeList = proritiesElement.getChildNodes();
                List<TaskPriority> taskPriorities = new ArrayList<TaskPriority>();
                for (int i = 0; i < prioritiesNodeList.getLength(); i++) {

                    Node node = prioritiesNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        taskPriorities.add(new TaskPriority(
                                attributes.getRepository(), id, name));
                    }
                }
                //load ticket priorities to attributes
                attributes.getRepository().getPriorityProvider().
                        setPriorities(taskPriorities);

                Element statusesElement = findElement(attributesElement,
                        TAG_STATUSES);
                NodeList statusesNodeList = statusesElement.getChildNodes();
                List<TaskStatus> taskStatuses = new ArrayList<TaskStatus>();
                for (int i = 0; i < statusesNodeList.getLength(); i++) {

                    Node node = statusesNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        taskStatuses.add(new TaskStatus(
                                attributes.getRepository(), id, name));
                    }
                }
                attributes.getRepository().getStatusProvider().
                        setStatuses(taskStatuses);

                Element resolutionsElement = findElement(attributesElement,
                        TAG_RESOLUTIONS);
                NodeList reslutionsNodeList = resolutionsElement.getChildNodes();
                List<TaskResolution> resolutions = new ArrayList<TaskResolution>();
                for (int i = 0; i < reslutionsNodeList.getLength(); i++) {

                    Node node = reslutionsNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        resolutions.add(new TaskResolution(
                                attributes.getRepository(), id, name));
                    }
                }
                attributes.getRepository().getResolutionProvider().
                        setTaskResolutions(resolutions);

                Element severitiesElement = findElement(attributesElement,
                        TAG_SEVERITIES);
                NodeList severitiesNodeList = severitiesElement.getChildNodes();
                List<TaskSeverity> severities = new ArrayList<TaskSeverity>();
                for (int i = 0; i < severitiesNodeList.getLength(); i++) {

                    Node node = severitiesNodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String name = element.getAttribute(TAG_NAME);
                        severities.add(new TaskSeverity(
                                attributes.getRepository(), id, name));
                    }
                }
                attributes.getRepository().getSeverityProvider().
                        setTaskSeverities(severities);
            }
        }
    }

    void refresh(ProgressHandle progressHandle) throws TracException {

        synchronized (LOCK) {
            final TracTaskRepository repository = attributes.getRepository();
            //get connection to Trac Session 
            TracSession session = repository.getSession();
            if (session == null) {
                return;
            }
            Document document = getDocument(true);
            Element root = getRootElement(document);
            Element attributesElement = findElement(root, TAG_ROOT);

            if (attributesElement == null) {
                attributesElement = document.createElement(TAG_ROOT);
                root.appendChild(attributesElement);
            }
            progressHandle.start();
            progressHandle.switchToIndeterminate();

            LOG.info("read ticket fields from remote server");//NOI18N
            //read ticket fields from remote server
            List<TicketField> fileds = session.getTicketFields();
            //create empty fields element to add child filed elements
            Element fieldsElement = getEmptyElement(document,
                    attributesElement, TAG_FIELDS);
            //write Field information to xml doc
            for (TicketField ticketFiled : fileds) {
                Element fieldElement = document.createElement(TAG_FIELD);
                fieldsElement.appendChild(fieldElement);
                //*** put field name as ID
                fieldElement.setAttribute(TAG_ID, ticketFiled.getName());
                //put field label as Name
                fieldElement.setAttribute(TAG_NAME, ticketFiled.getLabel());
                //put field type 
                fieldElement.setAttribute(TAG_TYPE, ticketFiled.getType());
                //put field is optional or not 
                fieldElement.setAttribute(TAG_OPTIONAL,
                        String.valueOf(ticketFiled.isOptional()));
                //filed default value (may be null)
                String value = ticketFiled.getValue();
                //validate is field default value null, if null ignore
                if (value != null) {
                    fieldElement.setAttribute(TAG_VALUE, value);
                }
                //ticket field supported options 
                List<String> options = ticketFiled.getOptions();
                //encode options to ':' seperated String
                String tagedOptions = getTagedString(options);
                fieldElement.setAttribute(TAG_OPTIONS, tagedOptions);
            }
            //read ticket components from remoe server
            List<TicketComponent> ticketComponents = session.getTicketComponents();
            //create empty fields element to add child filed elements
            Element componentsElement = getEmptyElement(document,
                    attributesElement, TAG_COMPONENTS);
            for (TicketComponent component : ticketComponents) {
                Element componentElement = document.createElement(TAG_COMPONENT);
                componentsElement.appendChild(componentElement);
                //put component attibutes 
                componentElement.setAttribute(TAG_NAME, component.getName());
                componentElement.setAttribute(TAG_DESCRIPTION, component.getDescription());
                componentElement.setAttribute(TAG_OWNER, component.getOwner());
            }
            //read ticket types form remote server
            List<TicketType> ticketTypes = session.getTicketTypes();
            Element typesElement = getEmptyElement(document,
                    attributesElement, TAG_TYPES);
            for (TicketType ticketType : ticketTypes) {
                Element typeElement = document.createElement(TAG_TYPE);
                typesElement.appendChild(typeElement);
                //put ticket type attributess
                typeElement.setAttribute(TAG_ID, ticketType.getName());
                typeElement.setAttribute(TAG_NAME, ticketType.getName());

            }
            //read ticket priorities from remote server
            List<TicketPriority> ticketPriorities = session.getTicketPriorities();
            Element prioritiesElement = getEmptyElement(document,
                    attributesElement, TAG_PRIORITIES);
            for (TicketPriority priority : ticketPriorities) {
                Element priorityElement = document.createElement(TAG_PRIORITY);
                prioritiesElement.appendChild(priorityElement);
                //put ticket priority attributess
                priorityElement.setAttribute(TAG_ID, priority.getName());
                priorityElement.setAttribute(TAG_NAME, priority.getName());
            }
            //read ticket statuses from remote server
            List<TicketStatus> statuses = session.getTicketStatuses();
            Element statusesElement = getEmptyElement(document,
                    attributesElement, TAG_STATUSES);
            for (TicketStatus status : statuses) {
                Element statusElement = document.createElement(TAG_STATUS);
                statusesElement.appendChild(statusElement);
                //put ticket status attributess
                statusElement.setAttribute(TAG_ID, status.getName());
                statusElement.setAttribute(TAG_NAME, status.getName());
            }
            //read ticket resolutions from remote server
            List<TicketResolution> resolutions = session.getTicketResolutions();
            Element resolutionsElement = getEmptyElement(document,
                    attributesElement, TAG_RESOLUTIONS);
            for (TicketResolution resolution : resolutions) {
                Element resolutionElement = document.createElement(TAG_RESOLUTION);
                resolutionsElement.appendChild(resolutionElement);
                //put ticket resolution attributess
                resolutionElement.setAttribute(TAG_ID, resolution.getName());
                resolutionElement.setAttribute(TAG_NAME, resolution.getName());
            }
            //read ticket severities from remote server
            List<TicketSeverity> ticketSeverities = session.getTicketSeverities();
            Element severitiesElement = getEmptyElement(document,
                    attributesElement, TAG_SEVERITIES);
            for (TicketSeverity severity : ticketSeverities) {
                Element severityElement = document.createElement(TAG_SEVERITY);
                severitiesElement.appendChild(severityElement);
                //put ticket severity attributess
                severityElement.setAttribute(TAG_ID, severity.getName());
                severityElement.setAttribute(TAG_NAME, severity.getName());
            }
            //read ticket versions from remote server
            List<TicketVersion> ticketVersions = session.getTicketVersions();
            Element versionsElement = getEmptyElement(document,
                    attributesElement, TAG_VERSIONS);
            for (TicketVersion version : ticketVersions) {
                Element versionElement = document.createElement(TAG_VERSION);
                versionsElement.appendChild(versionElement);
                //put ticket version attributess
                versionElement.setAttribute(TAG_NAME, version.getName());
                versionElement.setAttribute(TAG_DESCRIPTION, version.getDescription());

            }
            //read ticket Milestones from remote server 
            List<TicketMilestone> ticketMilestones = session.getTicketMilestones();
            Element milestonesElement = getEmptyElement(document,
                    attributesElement, TAG_MILESTONES);
            for (TicketMilestone milestone : ticketMilestones) {
                Element milestoneElement = document.createElement(TAG_MILESTONE);
                milestonesElement.appendChild(milestoneElement);
                //put ticket milestone attributess
                milestoneElement.setAttribute(TAG_NAME, milestone.getName());
                milestoneElement.setAttribute(TAG_DESCRIPTION, milestone.getDescription());
                milestoneElement.setAttribute(TAG_COMPLETED,
                        String.valueOf(milestone.isCompleted()));
            }
            //save document to attributes.xml at repository path
            save(document);
        }
    }

    static String getTagedString(List<String> strings) {
        StringBuffer buffer = new StringBuffer();

        //build string string by '|'
        for (String key : strings) {
            buffer.append(key).append("|");//NOI18N
        }
        return buffer.toString();
    }

    static List<String> getStringsByTag(String tag) {
        List<String> strings = new ArrayList<String>();
        // Tokeniz Strings tag by '|'
        StringTokenizer tokenizer = new StringTokenizer(tag, "|");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            //add only valid tag and omit empty tokens
            if (token.trim().length() > 0) {
                strings.add(token);
            }
        }
        return strings;
    }

    /******************************************** XML Related ***********************************/
    private Element getEmptyElement(Document document, Element root, String tag) {
        Element element = findElement(root, tag);
        if (element != null) {
            root.removeChild(element);
        }
        element = document.createElement(tag);
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
            doc = XMLUtil.createDocument(TAG_ROOT, null, null, null);

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
