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
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketAction.Operation;
import org.netbeans.cubeon.trac.api.TicketChange;
import org.netbeans.cubeon.trac.api.TicketChange.FieldChange;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import static org.netbeans.cubeon.trac.api.TracKeys.*;

/**
 *
 * @author Anuradha G
 */
class TaskPersistenceHandler {
    // add 'tag_' to prevent overlap with trac custom fields

    private static final String FILESYSTEM_FILE_TAG = "repository.xml"; //NOI18N
    private static final String TAG_OPERATIONS = "operations";
    private static final String TAG_OPERATION = "operation";
    private static final String TAG_INPUT_OPTIONS = "input_options";
    private static final String TAG_INPUT_OPTION = "input_option";
    private static final String TAG_OPTION = "option";
    private static final String TAG_DEFAULT = "default";
    private static final String TAG_ROOT = "tasks";//NOI18N
    private static final String TAG_REPOSITORY = "repository";//NOI18N
    private static final String TAG_ID = "id";//NOI18N
    private static final String TAG_LABEL = "lable";//NOI18N
    private static final String TAG_HINT = "hint";//NOI18N
    private static final String TAG_TICKET_ID = "tag_ticket_id";//NOI18N
    private static final String TAG_TASKS = "tasks";//NOI18N
    private static final String TAG_NEXT_ID = "next";//NOI18N
    private static final String TAG_TASK = "task";//NOI18N
    private static final String TAG_NEW_COMMENT = "tag_comment";//NOI18N
    private static final String TAG_COMMENTS = "comments";//NOI18N
    private static final String TAG_COMMENT = "comment";//NOI18N
    private static final String TAG_TEXT = "text";//NOI18N
    private static final String TAG_LOCAL = "tag_local";//NOI18N
    private static final String TAG_ACTIONS = "actions";//NOI18N
    private static final String TAG_ACTION = "action";//NOI18N
    private static final String TAG_MODIFIED_FLAG = "tag_modified_flag";//NOI18N
    private static final String TAG_CHANGES = "changes";//NOI18N
    private static final String TAG_CHANGE = "change";//NOI18N
    private static final String TAG_TIME = "time";//NOI18N
    private static final String TAG_FIELD = "field";//NOI18N
    private static final String TAG_NEW = "new";//NOI18N
    private static final String TAG_OLD = "old";//NOI18N
    private static final String TAG_AUTHOR = "author";//NOI18N
    //----------------------------------------------------
    private TracTaskRepository taskRepository;
    private final FileObject baseDir;
    private FileObject tasksFolder;
    private static final Object LOCK = new Object();

    TaskPersistenceHandler(TracTaskRepository repository, FileObject parent, String foldername) {
        this.taskRepository = repository;
        this.baseDir = parent;

        tasksFolder = parent.getFileObject(foldername);
        if (tasksFolder == null) {
            try {
                tasksFolder = parent.createFolder(foldername);
            } catch (IOException ex) {
                Logger.getLogger(TaskPersistenceHandler.class.getName()).log(Level.WARNING, ex.getMessage());
            }
        }
        assert tasksFolder != null;

    }

    List<String> getTaskIds() {
        List<String> ids = new ArrayList<String>();
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_TASKS);
            //check tasksElement null and create element
            if (tasksElement == null) {
                tasksElement = document.createElement(TAG_TASKS);
                root.appendChild(tasksElement);
            }

            NodeList taskNodes =
                    tasksElement.getElementsByTagName(TAG_TASK);

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    ids.add(id);
                }
            }

        }
        return ids;
    }

    private void readTracTask(Element element, TracTask tracTask) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String nodeName = item.getNodeName();
            String nodeValue = item.getNodeValue();
            tracTask.put(nodeName, nodeValue);
        }
        String cdate = element.getAttribute(CREATED_DATE);
        String udate = element.getAttribute(UPDATED_DATE);
        try {
            tracTask.setCreatedDate(new Long(cdate));
            tracTask.setUpdatedDate(new Long(udate));
        } catch (NumberFormatException nfe) {
            Logger.getLogger(TaskPersistenceHandler.class.getName()).
                    warning(nfe.getMessage());
        }
    }

    TracTask getTaskElementById(String id) {
        Document taskDocument = getTaskDocument(id);
        Element rootElement = getRootElement(taskDocument);
        Element element = findElement(rootElement, TAG_TASK);
        if (element != null) {
            String ticketId = element.getAttribute(TAG_TICKET_ID);
            String name = element.getAttribute(SUMMARY);
            String description = element.getAttribute(DESCRIPTION);

            String localTag = element.getAttribute(TAG_LOCAL);
            boolean local = false;
            if (localTag != null) {
                local = Boolean.parseBoolean(localTag);
            }
            String modifiedTag = element.getAttribute(TAG_MODIFIED_FLAG);
            boolean modified = false;
            if (modifiedTag != null) {
                modified = Boolean.parseBoolean(modifiedTag);
            }



            String newcomment = element.getAttribute(TAG_NEW_COMMENT);

            //read actions
            String selectedAction = element.getAttribute(TAG_ACTION);

            Element actionsElement = findElement(element, TAG_ACTIONS);
            NodeList actionssNodeList = actionsElement.getElementsByTagName(TAG_ACTION);
            List<TicketAction> actions = new ArrayList<TicketAction>();
            for (int i = 0; i < actionssNodeList.getLength(); i++) {
                Node actionNode = actionssNodeList.item(i);
                if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element actionElement = (Element) actionNode;
                    String aId = actionElement.getAttribute(TAG_ID);
                    TicketAction action = new TicketAction(aId);
                    action.setLabel(actionElement.getAttribute(TAG_LABEL));
                    action.setHint(actionElement.getAttribute(TAG_HINT));
                    Element operationsElement = findElement(actionElement, TAG_OPERATIONS);
                    if (operationsElement != null) {
                        NodeList operationsNodeList = operationsElement.getElementsByTagName(TAG_OPERATION);
                        for (int j = 0; j < operationsNodeList.getLength(); j++) {
                            Node operationNode = operationsNodeList.item(j);
                            if (operationNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element operationElement = (Element) operationNode;
                                String oId = operationElement.getAttribute(TAG_ID);
                                action.addOperation(new Operation(oId));

                            }
                        }
                    }
                    Element inputOptionsElement = findElement(actionElement, TAG_INPUT_OPTIONS);
                    if (inputOptionsElement !=null) {
                        NodeList inputOptionsNodeList = inputOptionsElement.getElementsByTagName(TAG_INPUT_OPTION);
                        for (int j = 0; j < inputOptionsNodeList.getLength(); j++) {
                            Node inputOptionNode = inputOptionsNodeList.item(j);
                            if (inputOptionNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element inputOptionElement = (Element) inputOptionNode;
                                String oId = inputOptionElement.getAttribute(TAG_ID);
                                TicketAction.InputOption inputOption =new TicketAction.InputOption(oId);
                                inputOption.setDefaultValue(inputOptionElement.getAttribute(TAG_DEFAULT));
                                NodeList optionsNodeList = inputOptionElement.getElementsByTagName(TAG_OPTION);
                                for (int k = 0; k < optionsNodeList.getLength(); k++) {
                                    Node optionNode = inputOptionsNodeList.item(k);
                                    if (optionNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element optionElement = (Element) optionNode;
                                        inputOption.addOption(optionElement.getAttribute(TAG_ID));
                                    }
                                }
                                action.addInputOption(inputOption);
                            }
                        }
                    }
                    actions.add(action);
                }
            }

            //read changes
            List<TicketChange> changes = new ArrayList<TicketChange>();
            Element commentsElement = findElement(element, TAG_COMMENTS);
            if (commentsElement != null) {
                NodeList commentsNodeList = commentsElement.getElementsByTagName(TAG_COMMENT);

                for (int i = 0; i < commentsNodeList.getLength(); i++) {
                    Node commentNode = commentsNodeList.item(i);
                    if (commentNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element commentElement = (Element) commentNode;

                        String time = commentElement.getAttribute(TAG_TIME);
                        String author = commentElement.getAttribute(TAG_AUTHOR);
                        String comment = commentElement.getAttribute(TAG_TEXT);
                        TicketChange tc = new TicketChange(Long.parseLong(time), author, comment);
                        Element changesElement = findElement(commentElement, TAG_CHANGES);
                        if (changesElement != null) {
                            NodeList changesNodeList = changesElement.getElementsByTagName(TAG_CHANGE);
                            for (int j = 0; j < changesNodeList.getLength(); j++) {
                                Node changesNode = changesNodeList.item(j);
                                if (changesNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element changeElement = (Element) changesNode;
                                    String field = changeElement.getAttribute(TAG_FIELD);
                                    String oldValue = changeElement.getAttribute(TAG_OLD);
                                    String newValue = changeElement.getAttribute(TAG_NEW);
                                    tc.addFieldChange(new FieldChange(field, oldValue, newValue));
                                }
                            }
                        }

                        changes.add(tc);
                    }
                }
            }

            TracTask tracTask = new TracTask(taskRepository, id,
                    Integer.parseInt(ticketId), name, description);
            readTracTask(element, tracTask);
            tracTask.setLocal(local);
            tracTask.setModifiedFlag(modified);




            tracTask.setActions(actions);
            for (TicketAction ticketAction : actions) {
                if (ticketAction.getName().equals(selectedAction)) {
                    tracTask.setAction(ticketAction);
                    break;
                }
            }

            tracTask.setNewComment(newcomment);

            tracTask.setTicketChanges(changes);

            return tracTask;

        }
        return null;
    }

    TracTask getTracTaskById(String id) {
        Document taskDocument = getTaskDocument(id);
        Element rootElement = getRootElement(taskDocument);
        Element element = findElement(rootElement, TAG_TASK);
        if (element != null) {
            String ticketId = element.getAttribute(TAG_TICKET_ID);
            String name = element.getAttribute(SUMMARY);
            String description = element.getAttribute(DESCRIPTION);


            TracTask tracTask = new TracTask(taskRepository, id,
                    Integer.parseInt(ticketId), name, description);
            readTracTask(element, tracTask);


            return tracTask;

        }
        return null;
    }

    void vaidate(TaskElement element) {
        //do validations changes here
    }

    private void persist(TracTask task, Document document, Element taskElement) {
        taskElement.setAttribute(TAG_REPOSITORY, task.getTaskRepository().getId());
        taskElement.setAttribute(TAG_ID, task.getId());
        taskElement.setAttribute(TAG_TICKET_ID, String.valueOf(task.getTicketId()));
        taskElement.setAttribute(SUMMARY, task.getName());
        taskElement.setAttribute(DESCRIPTION, task.getDescription());
        Set<Entry<String, String>> entrySet = task.entrySet();
        for (Entry<String, String> entry : entrySet) {
            taskElement.setAttribute(entry.getKey(), entry.getValue());
        }




        taskElement.setAttribute(CREATED_DATE, String.valueOf(task.getCreatedDate()));



        taskElement.setAttribute(UPDATED_DATE, String.valueOf(task.getUpdatedDate()));


    }

    void persist(TracTask task) {
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_TASKS);
            //check tasksElement null and create element
            if (tasksElement == null) {
                tasksElement = document.createElement(TAG_TASKS);
                root.appendChild(tasksElement);
            }
            Element taskElement = null;


            NodeList taskNodes =
                    tasksElement.getElementsByTagName(TAG_TASK);

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    if (task.getId().equals(id)) {
                        taskElement = element;
                        break;
                    }
                }
            }
            if (taskElement == null) {
                taskElement = document.createElement(TAG_TASK);
                tasksElement.appendChild(taskElement);
                taskElement.setAttribute(TAG_ID, task.getId());
            }


            save(document);


            //create task xml
            document = getTaskDocument(task.getId());
            root = getRootElement(document);
            taskElement = findElement(root, TAG_TASK);
            //check tasksElement null and create element
            if (taskElement == null) {
                taskElement = document.createElement(TAG_TASK);
                root.appendChild(taskElement);
            }


            //persist Trac ticket
            persist(task, document, taskElement);

            if (task.isLocal()) {
                taskElement.setAttribute(TAG_LOCAL, String.valueOf(task.isLocal()));
            } else {
                taskElement.removeAttribute(TAG_LOCAL);
            }
            if (task.isModifiedFlag()) {
                taskElement.setAttribute(TAG_MODIFIED_FLAG, String.valueOf(task.isModifiedFlag()));
            } else {
                taskElement.removeAttribute(TAG_MODIFIED_FLAG);
            }





            if (task.getNewComment() != null) {
                taskElement.setAttribute(TAG_NEW_COMMENT, task.getNewComment());
            } else {
                taskElement.removeAttribute(TAG_NEW_COMMENT);
            }

            //actions
            List<TicketAction> actions = task.getActions();
            Element actionsElement = getEmptyElement(document, taskElement, TAG_ACTIONS);
            for (TicketAction action : actions) {
                Element actionElement = document.createElement(TAG_ACTION);
                actionsElement.appendChild(actionElement);
                actionElement.setAttribute(TAG_ID, action.getName());
                if (action.getLabel() != null) {
                    actionElement.setAttribute(TAG_LABEL, action.getLabel());
                }
                if (action.getHint() != null) {
                    actionElement.setAttribute(TAG_HINT, action.getHint());
                }
                List<Operation> operations = action.getOperations();
                Element operationsElement = getEmptyElement(document, actionElement,
                        TAG_OPERATIONS);
                for (Operation operation : operations) {
                    Element operationElement = document.createElement(TAG_OPERATION);
                    operationsElement.appendChild(operationElement);
                    operationElement.setAttribute(TAG_ID, operation.getName());
                }
                Element inputOptionsElement = getEmptyElement(document, actionElement,
                        TAG_INPUT_OPTIONS);
                for (TicketAction.InputOption inputOption : action.getInputOptions()) {
                    Element inputOptionElement = document.createElement(TAG_INPUT_OPTION);
                    inputOptionsElement.appendChild(inputOptionElement);
                    inputOptionElement.setAttribute(TAG_ID, inputOption.getField());
                    inputOptionElement.setAttribute(TAG_DEFAULT, inputOption.getDefaultValue());
                    for (String option : inputOption.getOptions()) {
                        Element optionElement = document.createElement(TAG_OPTION);
                        inputOptionElement.appendChild(optionElement);
                        optionElement.setAttribute(TAG_ID, option);
                    }
                }
            }

            List<TicketChange> ticketChanges = task.getTicketChanges();
            Element commentsElement = getEmptyElement(document, taskElement, TAG_COMMENTS);
            for (TicketChange ticketChange : ticketChanges) {
                Element element = document.createElement(TAG_COMMENT);
                commentsElement.appendChild(element);
                element.setAttribute(TAG_TIME, String.valueOf(ticketChange.getTime()));
                element.setAttribute(TAG_AUTHOR, ticketChange.getAuthor());
                element.setAttribute(TAG_TEXT, ticketChange.getComment());
                Element changesElement = getEmptyElement(document, element, TAG_CHANGES);
                List<FieldChange> fieldChanges = ticketChange.getFieldChanges();
                for (FieldChange fieldChange : fieldChanges) {
                    Element changeElement = document.createElement(TAG_CHANGE);
                    changesElement.appendChild(changeElement);
                    changeElement.setAttribute(TAG_FIELD, fieldChange.getField());
                    changeElement.setAttribute(TAG_OLD, fieldChange.getOldValue());
                    changeElement.setAttribute(TAG_NEW, fieldChange.getNewValuve());
                }


            }



            saveTask(document, task.getId());
        }
    }

    private Element getEmptyElement(Document document, Element root, String tag) {
        Element element = findElement(root, tag);
        if (element != null) {
            root.removeChild(element);
        }
        element = document.createElement(tag);
        root.appendChild(element);
        return element;

    }

    void removeTaskElement(TaskElement te) {
        synchronized (LOCK) {

            Document document = getDocument();
            Element root = getRootElement(document);
            Element tasksElement = findElement(root, TAG_TASKS);
            Element taskElement = null;

            NodeList taskNodes =
                    tasksElement.getElementsByTagName(TAG_TASK);

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute(TAG_ID);
                    if (te.getId().equals(id)) {
                        taskElement = element;
                        break;
                    }
                }
            }
            assert taskElement != null;

            tasksElement.removeChild(taskElement);

            save(document);

            FileObject taskFo = tasksFolder.getFileObject(te.getId() + ".xml");//NOI18N
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
        String id = taskRepository.getId().toUpperCase();
        synchronized (LOCK) {
            Document document = getDocument();
            Element root = getRootElement(document);
            Element nextElement = findElement(root, TAG_NEXT_ID);
            int nextID = 0;
            if (nextElement == null) {
                nextElement = document.createElement(TAG_NEXT_ID);
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
            doc = XMLUtil.createDocument(TAG_ROOT, null, null, null);

        }
        return doc;
    }

    private Document getTaskDocument(String tag) {
        final FileObject config = tasksFolder.getFileObject(tag + ".xml");//NOI18N
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

    private void saveTask(Document doc, String tag) {

        FileObject config = tasksFolder.getFileObject(tag + ".xml");//NOI18N

        FileLock lck = null;
        OutputStream out = null;
        try {
            if (config == null) {

                config = tasksFolder.createData(tag + ".xml");//NOI18N
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
