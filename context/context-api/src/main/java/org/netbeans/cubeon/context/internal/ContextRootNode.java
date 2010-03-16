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
package org.netbeans.cubeon.context.internal;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.context.spi.TaskResouresProvider;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.core.api.TagNode;
import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.core.spi.TaskExplorerViewActionsProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children.Array;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Anuradha
 */
public class ContextRootNode extends AbstractNode {

    private final Array array;
    private final CubeonContext context;
    private final TaskContextManager contextManager;

    public ContextRootNode(Array array) {
        super(array);
        this.array = array;
        context = Lookup.getDefault().lookup(CubeonContext.class);
        contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
        context.addContextListener(new CubeonContextListener() {

            public void taskActivated(TaskElement element) {
                refresh();
            }

            public void taskDeactivated(TaskElement element) {
                refresh();
            }
        });
    }

    public void refresh() {
        array.remove(array.getNodes());
        TaskElement active = context.getActive();
        if (active != null) {
            List<TaskResouresProvider> providers = contextManager.getResouresProviders(active);
            TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);
            Node node = factory.createTaskElementNode(active, new ContextChildern(contextManager.getActiveTaskContext()), true);
            array.add(new Node[]{node});
        } else {
            Node activeNode = TagNode.createNode(NbBundle.getMessage(TaskContextManager.class, "LBL_ActivateNode"),
                    NbBundle.getMessage(TaskContextManager.class, "LBL_ActivateNode"),
                    ImageUtilities.loadImage("org/netbeans/cubeon/context/api/task.png"));
            array.add(new Node[]{activeNode});
        }

    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();

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
        return actions.toArray(new Action[actions.size()]);
    }
}
