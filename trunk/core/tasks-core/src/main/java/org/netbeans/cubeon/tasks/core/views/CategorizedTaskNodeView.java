/*
 *  Copyright 2008 Cube°n Team.
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
package org.netbeans.cubeon.tasks.core.views;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.RefreshableChildren;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.internals.TaskFolderNode;
import org.netbeans.cubeon.tasks.core.spi.TaskExplorerViewActionsProvider;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class CategorizedTaskNodeView implements TaskNodeView {

    public String getId() {
        return "CategorizedTaskNodeView";
    }

    public String getName() {
        return NbBundle.getMessage(CategorizedTaskNodeView.class, "LBL_Categorized_Name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CategorizedTaskNodeView.class, "LBL_Categorized_Description");
    }

    public Node createRootContext() {

        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        assert fileSystem!=null;
        final TaskFolder folder = fileSystem.getRootTaskFolder();
        RefreshableChildren rc = folder.getLookup().lookup(RefreshableChildren.class);
        return new AbstractNode(rc.getChildren(), Lookups.singleton(folder)) {

            @Override
            public Action[] getActions(boolean arg0) {
                List<Action> actions = new ArrayList<Action>();
                actions.add(new TaskFolderNode.NewActions(folder));
                actions.add(null);
                final List<TaskExplorerViewActionsProvider> providers =
                        new ArrayList<TaskExplorerViewActionsProvider>(
                        Lookup.getDefault().lookupAll(TaskExplorerViewActionsProvider.class));
                boolean sepetatorAdded = false;
                for (TaskExplorerViewActionsProvider tevap : providers) {
                    Action[] as = tevap.getActions();
                    for (Action action : as) {
                        //check null and addSeparator 
                        if (action == null) {
                            //check sepetatorAdd to prevent adding duplicate Separators 
                            if (!sepetatorAdded) {
                                //mark sepetatorAdd to true
                                sepetatorAdded = true;
                                actions.add(action);

                            }
                            continue;
                        }
                        actions.add(action);
                        sepetatorAdded = false;
                    }
                }
                return actions.toArray(new Action[0]);
            }
        };
    }
}
