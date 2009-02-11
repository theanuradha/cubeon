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
package org.netbeans.cubeon.local.actions;

import java.awt.EventQueue;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Anuradha
 */
public class DeleteTaskAction extends NodeAction {

    private static final long serialVersionUID = 8170584779537197106L;

    private DeleteTaskAction() {
        putValue(NAME, NbBundle.getMessage(DeleteTaskAction.class, "LBL_Delete_From_Repository"));

    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(DeleteTaskAction.class,
                "LBL_Delete_From_Repository_Dec",
                activatedNodes.length>1?"s":""),
                NotifyDescriptor.YES_NO_OPTION);
        Object notify = DialogDisplayer.getDefault().notify(d);

        if (notify == NotifyDescriptor.YES_OPTION) {
            for (Node node : activatedNodes) {
                final LocalTask element = node.getLookup().lookup(LocalTask.class);
                if (element != null) {
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            LocalTaskRepository repository = element.getTaskRepository();
                            repository.deleteTask(element);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            LocalTask element = node.getLookup().lookup(LocalTask.class);
            if (element == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return (String) getValue(NAME);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(DeleteTaskAction.class);
    }
}
