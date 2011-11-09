/*
 * Copyright 2011 Anuradha.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.cubeon.resources.bridge;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.cubeon.context.api.TaskContext;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                connectToWindowManager();
            }
        });
    }

    private void connectToWindowManager() {
        final PropertyChangeListener activationListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {



                if (TopComponent.Registry.PROP_TC_OPENED.equals(evt.getPropertyName())) {
                    TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
                    DataObject dataObj = tc.getLookup().lookup(DataObject.class);
                    if (dataObj != null) {
                        TaskContextManager contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
                        TaskContext taskContext = contextManager.getActiveTaskContext();
                        if (taskContext != null) {

                            OtherResourceSet resourceSet = taskContext.getLookup().lookup(OtherResourceSet.class);

                            OtherResource resource = OtherResourceProvider.toResource(dataObj);
                            if (resource != null && !resourceSet.contains(resource)) {
                                resourceSet.addTaskResource(resource);
                            }
                        }
                    }
                }

            }
        };
        TaskContextManager contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
        TaskContext taskContext = contextManager.getActiveTaskContext();
        if (taskContext != null) {
            //this will pick up files opening and add to active 
            WindowManager.getDefault().getRegistry().addPropertyChangeListener(activationListener);
        }
        CubeonContext lookup = Lookup.getDefault().lookup(CubeonContext.class);
        lookup.addContextListener(new CubeonContextListener() {

            public void taskActivated(TaskElement element) {
                WindowManager.getDefault().getRegistry().addPropertyChangeListener(activationListener);
            }

            public void taskDeactivated(TaskElement element) {
               WindowManager.getDefault().getRegistry().removePropertyChangeListener(activationListener);
            }
        });
    }
}
