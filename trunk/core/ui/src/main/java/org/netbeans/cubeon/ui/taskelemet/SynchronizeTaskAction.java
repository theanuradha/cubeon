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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Anuradha
 */
public class SynchronizeTaskAction extends NodeAction {

    private SynchronizeTaskAction() {

        putValue(NAME, NbBundle.getMessage(SynchronizeTaskAction.class, "LBL_Synchronize"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(SynchronizeTaskAction.class, "LBL_Synchronize"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/refresh.png")));
    }

    public static Action createSynchronizeTaskAction(final TaskElement element) {
        AbstractAction action = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                RequestProcessor.getDefault().post(new Runnable() {

                    public void run() {

                        setEnabled(false);
                        ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing Tasks ");
                        handle.start();
                        handle.switchToIndeterminate();


                        if (element != null) {
                            sync(handle, element);
                        }

                        handle.finish();
                        setEnabled(true);
                    }
                });
            }
        };
        action.putValue(NAME, NbBundle.getMessage(SynchronizeTaskAction.class, "LBL_Synchronize"));
        action.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(SynchronizeTaskAction.class, "LBL_Synchronize"));
        action.putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/refresh.png")));
        return action;
    }

    @Override
    protected void performAction(final Node[] activatedNodes) {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {


                ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing Tasks ");
                handle.start();
                handle.switchToIndeterminate();
                for (Node node : activatedNodes) {
                    TaskElement task = node.getLookup().lookup(TaskElement.class);
                    if (task != null) {
                        sync(handle, task);
                    }
                }
                handle.finish();

            }
        });



    }

    private static void sync(ProgressHandle handle, TaskElement task) {
        handle.setDisplayName("Synchronizing : " + task.getId());
        handle.progress(task.getDisplayName());
        task.synchronize();
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
        return new HelpCtx(SynchronizeTaskAction.class);
    }
}
