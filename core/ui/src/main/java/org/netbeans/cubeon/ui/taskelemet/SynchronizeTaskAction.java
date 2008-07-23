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
import javax.swing.ImageIcon;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha
 */
public class SynchronizeTaskAction extends AbstractAction {

    private TaskElement task;

    public SynchronizeTaskAction(TaskElement task) {

        this.task = task;
        putValue(NAME, "Synchronize");
        putValue(SHORT_DESCRIPTION, "Synchronize Remote Repository");
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/refresh.png")));
    }

    public void actionPerformed(ActionEvent e) {

        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                setEnabled(false);

                ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing : " + task.getId());
                handle.start();
                handle.switchToIndeterminate();
                task.synchronize();
                handle.finish();
                setEnabled(true);
            }
        });
    }
}
