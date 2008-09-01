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
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.cubeon.trac.api.TicketComponent;
import org.netbeans.cubeon.trac.api.TicketFiled;
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
            List<TicketFiled> fileds = session.getTicketFileds();
            //create empty fields element to add child filed elements
            Element fieldsElement = getEmptyElement(document,
                    attributesElement, TAG_FIELDS);
            //write Field information to xml doc
            for (TicketFiled ticketFiled : fileds) {
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
                String tagedOptions = _getTagedOptions(options);
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
                typeElement.setAttribute(TAG_ID, ticketType.getId());
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
                priorityElement.setAttribute(TAG_ID, priority.getId());
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
                statusElement.setAttribute(TAG_ID, status.getId());
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
                resolutionElement.setAttribute(TAG_ID, resolution.getId());
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
                severityElement.setAttribute(TAG_ID, severity.getId());
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

    private String _getTagedOptions(List<String> options) {
        StringBuffer buffer = new StringBuffer();
        for (String key : options) {
            buffer.append(key).append(":");//NOI18N
        }
        return buffer.toString();
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
