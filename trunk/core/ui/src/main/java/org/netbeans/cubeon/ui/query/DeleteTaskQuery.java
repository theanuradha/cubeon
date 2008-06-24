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
package org.netbeans.cubeon.ui.query;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Anuradha
 */
public class DeleteTaskQuery extends AbstractAction {

    private TaskQuery query;

    public DeleteTaskQuery(TaskQuery query) {
        this.query = query;
        putValue(NAME, "Delete");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));//NOI18N
    }

    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(
                "Delete Query : " + query.getName() + " ?", "Task Query Delete",
                NotifyDescriptor.OK_CANCEL_OPTION);
        Object notify = DialogDisplayer.getDefault().notify(d);
        if (notify == NotifyDescriptor.OK_OPTION) {
            TaskRepository repository = query.getTaskRepository();
            TaskQuerySupportProvider provider = repository.getLookup().lookup(TaskQuerySupportProvider.class);
            provider.removeTaskQuery(query);
        }
    }
}
