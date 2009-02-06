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
package org.netbeans.cubeon.ui.taskelemet;

import java.awt.EventQueue;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Anuradha
 */
public class OpenAction extends NodeAction {

    private static final long serialVersionUID = 8170584779537197106L;
    
    private OpenAction() {
        putValue(NAME, NbBundle.getMessage(OpenAction.class, "LBL_Open"));

    }
    
    

    @Override
    protected void performAction(Node[] activatedNodes) {
        final TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);

         for (Node node : activatedNodes) {
            final TaskElement element = node.getLookup().lookup(TaskElement.class);
            if (element != null) {
               EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        factory.openTask(element);
                    }
                });
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            TaskElement element = node.getLookup().lookup(TaskElement.class);
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
        return new HelpCtx(OpenAction.class);
    }
}
