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

import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JavaResource implements TaskResource {

    private String path;

    public JavaResource(String path) {
        this.path = path;
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
        DataObject dataObject = null;
        try {
            ClassPath cp = ClassPathSupport.createClassPath(GlobalPathRegistry.getDefault().getSourceRoots().toArray(new FileObject[0]));
            FileObject fileObject = cp.findResource(path);
            if (fileObject != null) {
                dataObject = DataObject.find(fileObject);
            }

        } catch (DataObjectNotFoundException ex) {

            Logger.getLogger(JavaResource.class.getName()).fine("Missing : " + path);
        }
        if (dataObject != null) {
            return dataObject.getNodeDelegate().cloneNode();
        } else {
            Node missing = new AbstractNode(Children.LEAF) {
            };
            missing.setDisplayName(path);
            missing.setShortDescription("Source not available in Class Path :" + path);
            return missing;
        }
    }
}
