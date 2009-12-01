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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JavaResource implements TaskResource {

    private final String path;

    public JavaResource(String path) {
        this.path = path;
    }

    private DataObject getDataObject() {
        DataObject dataObject = null;
        try {


            FileObject fileObject = GlobalPathRegistry.getDefault().findResource(path);
            if (fileObject != null) {
                dataObject = DataObject.find(fileObject);
            }

        } catch (DataObjectNotFoundException ex) {

            Logger.getLogger(JavaResource.class.getName()).fine("Missing : " + path);//NOI18N
        }
        return dataObject;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return path;
    }

    public String getDescription() {
        return path;
    }

    public Node getNode() {
        return getResourceNode();
    }

    public void open() {
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    private Node getResourceNode() {
        DataObject dataObject = getDataObject();

        if (dataObject != null) {
            return dataObject.getNodeDelegate().cloneNode();
        } else {
            Node missing = new AbstractNode(Children.LEAF) {

                @Override
                public Action[] getActions(boolean context) {
                    TaskContextManager contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
                    TaskContext taskContext = contextManager.getActiveTaskContext();
                    if (taskContext != null) {
                        final JavaResourceSet resourceSet = taskContext.getLookup().lookup(JavaResourceSet.class);
                        if (resourceSet != null && resourceSet.contains(JavaResource.this)) {
                            return new Action[]{new AbstractAction(NbBundle.getMessage(JavaResource.class, "LBL_remove_Missing_class_path")) {

                                    public void actionPerformed(ActionEvent e) {
                                        resourceSet.remove(JavaResource.this);
                                    }
                                }};
                        }
                    }


                    return super.getActions(context);
                }
            };
            missing.setDisplayName(path);
            missing.setShortDescription(NbBundle.getMessage(JavaResource.class, "LBL_Missing_class_path", path));
            return missing;
        }
    }
}
