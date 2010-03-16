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
package org.netbeans.cubeon.javanet.tasks.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.javanet.tasks.JavanetTask;
import org.netbeans.cubeon.tasks.core.api.TaskEditorFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Anuradha
 */
public class SubmitTaskAction extends AbstractAction {
    private static final long serialVersionUID = -792527693541936426L;

    private JavanetTask task;

    public SubmitTaskAction(JavanetTask task) {
        this.task = task;
        putValue(NAME, NbBundle.getMessage(SubmitTaskAction.class, "LBL_Submit_Local_Changes"));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(SubmitTaskAction.class, "LBL_Submit_Local_Changes"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/javanet/task-submit.png")));
        setEnabled(task.isModified());
    }

    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                setEnabled(false);

                ProgressHandle handle = ProgressHandleFactory.createHandle(
                        NbBundle.getMessage(SubmitTaskAction.class, "LBL_Submiting",
                        task.getId()));
                handle.start();
                handle.switchToIndeterminate();
                TaskEditorFactory factory = Lookup.getDefault().lookup(TaskEditorFactory.class);
                factory.save(task);
                Submitable repository = task.getTaskRepository().getLookup().lookup(JavanetTaskRepository.class);

                repository.submit(task);
                
                factory.refresh(task);

                handle.finish();
                setEnabled(true);
            }
        });
    }
}
