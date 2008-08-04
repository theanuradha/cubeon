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
package org.netbeans.cubeon.java.bridge;

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

    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_ID = "id";
    private static final String TAG_JAVA_PATHS = "java-paths";
    private static final String TAG_PATH = "path";
    private final TaskContextHandler contextHandler;

    ResourcesPersistenceHandler(TaskContextHandler contextHandler) {
        this.contextHandler = contextHandler;

    }

    void remove(final JavaResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_JAVA_PATHS, null);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_JAVA_PATHS);
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
                       
                        if (resource.getPath().equals(id)) {
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

    void add(final JavaResource resource) {
        contextHandler.getMutex().writeAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_JAVA_PATHS, null);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_JAVA_PATHS);
                    root.appendChild(tasksElement);
                }
                Element taskElement = document.createElement(TAG_PATH);
                tasksElement.appendChild(taskElement);
                taskElement.setAttribute(TAG_ID, resource.getPath());
            
                contextHandler.saveContextDocument(document);
            }
        });
    }

    List<JavaResource> refresh() {
        final List<JavaResource> resources = new ArrayList<JavaResource>();
        
        contextHandler.getMutex().readAccess(new Runnable() {

            public void run() {
                Document document = contextHandler.getContextDocument();
                Element root = contextHandler.getRootElement(document);


                Element tasksElement = findElement(root, TAG_JAVA_PATHS, null);
                //check foldersElement null and create element
                if (tasksElement == null) {
                    tasksElement = document.createElement(TAG_JAVA_PATHS);
                    root.appendChild(tasksElement);
                }


                NodeList taskNodes =
                        tasksElement.getElementsByTagName(TAG_PATH);
                for (int i = 0; i < taskNodes.getLength(); i++) {
                    Node node = taskNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getAttribute(TAG_ID);
                       
                          resources.add(new JavaResource(id));
                        

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
