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
package org.netbeans.cubeon.context.task.link;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.context.api.TaskContextHandler;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Anuradha G
 */
class ResourcesPersistenceHandler {

    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_TASK = "task";
    private final TaskContextHandler contextHandler;

    ResourcesPersistenceHandler(TaskContextHandler contextHandler) {
        this.contextHandler = contextHandler;

    }

    void remove(final TaskLinkResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_TASKS, null);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_TASKS);
                    root.appendChild(tasksElement);
                }


                NodeList taskNodes =
                        tasksElement.getElementsByTagName(TAG_TASK);
                Element trelement = null;
                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String repo = element.getAttribute(TAG_REPOSITORY);
                        if (resource.getElement().getId().equals(id) && resource.getElement().getTaskRepository().getId().equals(repo)) {
                            trelement = element;
                            break;
                        }

                    }
                }
                if (trelement != null) {
                    tasksElement.removeChild(trelement);
                    contextHandler.saveContextDocument(document);
                }
            }
        });
    }

    void add(final TaskLinkResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_TASKS, null);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_TASKS);
                    root.appendChild(tasksElement);
                }
                Element taskElement = document.createElement(TAG_TASK);
                tasksElement.appendChild(taskElement);
                taskElement.setAttribute(TAG_ID, resource.getElement().getId());
                taskElement.setAttribute(TAG_REPOSITORY, resource.getElement().
                        getTaskRepository().getId());
                contextHandler.saveContextDocument(document);
            }
        });
    }

    List<TaskLinkResource> refresh() {
        final List<TaskLinkResource> resources = new ArrayList<TaskLinkResource>();
        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        final TaskRepositoryHandler repositoryHandler = context.getLookup().
                lookup(TaskRepositoryHandler.class);
        contextHandler.getMutex().readAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_TASKS, null);
                //check foldersElement null and create element
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
                        String repo = element.getAttribute(TAG_REPOSITORY);
                        TaskRepository repository = repositoryHandler.getTaskRepositoryById(repo);
                        if (repository != null) {
                            TaskElement te = repository.getTaskElementById(id);
                            if (te != null) {
                                resources.add(new TaskLinkResource(te));
                            }
                        }

                    }
                }
            }
        });


        return resources;
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
