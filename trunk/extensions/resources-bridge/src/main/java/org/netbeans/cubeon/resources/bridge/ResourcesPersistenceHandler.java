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
package org.netbeans.cubeon.resources.bridge;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.context.api.TaskContextHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Anuradha G
 */
class ResourcesPersistenceHandler {


    private static final String TAG_ID = "id";
    private static final String TAG_RESOURCES_PATHS = "resources-paths";
    private static final String TAG_PATH = "path";
    private static final String TAG_PROJECT_ID = "project-id";
    private final TaskContextHandler contextHandler;

    ResourcesPersistenceHandler(TaskContextHandler contextHandler) {
        this.contextHandler = contextHandler;

    }

    void remove(final OtherResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_RESOURCES_PATHS);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_RESOURCES_PATHS);
                    root.appendChild(tasksElement);
                }


                NodeList taskNodes =
                        tasksElement.getElementsByTagName(TAG_PATH);
                Element trelement = null;
                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String pid = element.getAttribute(TAG_PROJECT_ID);

                        if (resource.getPath().equals(id) && (
                                (pid==null && resource.getProjectId()==null) ||
                                (pid!=null && pid.equals(resource.getProjectId()))
                                )) {
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

    void add(final OtherResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_RESOURCES_PATHS);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_RESOURCES_PATHS);
                    root.appendChild(tasksElement);
                }
                Element taskElement = document.createElement(TAG_PATH);
                tasksElement.appendChild(taskElement);
                taskElement.setAttribute(TAG_ID, resource.getPath());
                if(resource.getProjectId()!=null){
                    taskElement.setAttribute(TAG_PROJECT_ID, resource.getProjectId());
                }

                contextHandler.saveContextDocument(document);
            }
        });
    }

    List<OtherResource> refresh() {
        final List<OtherResource> resources = new ArrayList<OtherResource>();

        contextHandler.getMutex().readAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_RESOURCES_PATHS);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_RESOURCES_PATHS);
                    root.appendChild(tasksElement);
                }


                NodeList taskNodes =
                        tasksElement.getElementsByTagName(TAG_PATH);
                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                        String pid = element.getAttribute(TAG_PROJECT_ID);

                        resources.add(new OtherResource(pid,id));


                    }
                }
            }
        });


        return resources;
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
