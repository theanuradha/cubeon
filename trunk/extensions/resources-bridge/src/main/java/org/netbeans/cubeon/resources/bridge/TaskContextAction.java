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
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextManager;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class TaskContextAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1L;
    private final Lookup context;
    private final OtherResourceSet resourceSet;
    private final OtherResource resource;
    private boolean remove;

    public TaskContextAction() {
        context = Utilities.actionsGlobalContext();

        resourceSet = null;
        resource = null;
        putValue(NAME, NbBundle.getMessage(TaskContextAction.class, "CTL_Add_to_Context"));
    }

    public TaskContextAction(Lookup context) {
        TaskContextManager contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
        TaskContext taskContext = contextManager.getActiveTaskContext();
        this.context = context;
        if(context==null){
            context = Utilities.actionsGlobalContext();
        }
        if (taskContext != null) {
            resourceSet = taskContext.getLookup().lookup(OtherResourceSet.class);

            resource = toResource(context);
            setEnabled(resource != null);
            if (resource != null && resourceSet.contains(resource)) {
                remove = true;
                putValue(NAME, NbBundle.getMessage(TaskContextAction.class, "CTL_Remove_from_Context"));
            } else {
                putValue(NAME, NbBundle.getMessage(TaskContextAction.class, "CTL_Add_to_Context"));
            }
        } else {
            resource = null;
            resourceSet = null;
            putValue(NAME, NbBundle.getMessage(TaskContextAction.class, "CTL_Add_to_Context"));
        }

    }

    @Override
    public boolean isEnabled() {
        return resourceSet != null && resource != null;
    }

    public void actionPerformed(ActionEvent e) {
        if (remove) {
            resourceSet.remove(resource);
        } else {
            resourceSet.addTaskResource(resource);
        }
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new TaskContextAction(arg0);
    }

    private static OtherResource toResource(Lookup l) {
        DataObject dataObject = l.lookup(DataObject.class);
        if (dataObject != null) {
            FileObject primaryFile = dataObject.getPrimaryFile();
            File toFile = FileUtil.toFile(primaryFile);
            if (toFile != null) {
                String path = toFile.getAbsolutePath();
                return new OtherResource(path);
            }
        }
        return null;
    }
}