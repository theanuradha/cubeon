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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResource;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
public class OtherResource implements TaskResource {

    private final String path;
    private final String projectId;

    public OtherResource(String path) {
        this(null, path);
    }

    public OtherResource(String projectId, String path) {
        this.path = path;
        this.projectId = projectId;
    }

    private DataObject getDataObject() {
        DataObject dataObject = null;

        try {
            FileObject fileObject = null;
            if (projectId == null) {
                File file = new File(path);
                file = FileUtil.normalizeFile(file);
                fileObject = FileUtil.toFileObject(file);

            } else {
                Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
                for (Project project : openProjects) {
                    ProjectInformation pi = project.getLookup().lookup(ProjectInformation.class);
                    if (pi.getName().equals(projectId)) {
                         fileObject = project.getProjectDirectory().getFileObject(path);
                        break;
                    }
                }
            }
            if (fileObject != null) {

                dataObject = DataObject.find(fileObject);
            }


        } catch (DataObjectNotFoundException ex) {

            Logger.getLogger(OtherResource.class.getName()).log(Level.FINE, "Missing : {0}", path);//NOI18N
        }
        return dataObject;
    }

    public String getPath() {
        return path;
    }

    public String getProjectId() {
        return projectId;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OtherResource other = (OtherResource) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        if ((this.projectId == null) ? (other.projectId != null) : !this.projectId.equals(other.projectId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 53 * hash + (this.projectId != null ? this.projectId.hashCode() : 0);
        return hash;
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
                        final OtherResourceSet resourceSet = taskContext.getLookup().lookup(OtherResourceSet.class);
                        if (resourceSet != null && resourceSet.contains(OtherResource.this)) {
                            return new Action[]{new AbstractAction(NbBundle.getMessage(OtherResource.class, "LBL_remove_Missing_class_path")) {

                                    public void actionPerformed(ActionEvent e) {
                                        resourceSet.remove(OtherResource.this);
                                    }
                                }};
                        }
                    }


                    return super.getActions(context);
                }
            };
            missing.setDisplayName(path);
            missing.setShortDescription(NbBundle.getMessage(OtherResource.class, "LBL_Missing_class_path", path));
            return missing;
        }
    }
}
