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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class QueryEditAction extends AbstractAction {

    private TaskQuery query;

    public QueryEditAction(TaskQuery query) {
        this.query = query;
        putValue(NAME, NbBundle.getMessage(QueryEditAction.class, "LBL_Edit_Query"));
        TaskQuerySupportProvider provider = query.getTaskRepository().getLookup().lookup(TaskQuerySupportProvider.class);
        setEnabled(provider.canModify(query));
    }

    public void actionPerformed(ActionEvent e) {
        TaskQueryAttributes settings = new TaskQueryAttributes(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                //todo
            }
        });

        settings.setWizardObject(new NewQueryWizardAction.WizardObject(query, query.getTaskRepository()));
        DialogDescriptor dd = new DialogDescriptor(settings, settings.getName());
        dd.setClosingOptions(new Object[]{
                    DialogDescriptor.OK_OPTION,
                    DialogDescriptor.CANCEL_OPTION
                });
        dd.setOptions(new Object[]{
                    DialogDescriptor.OK_OPTION,
                    DialogDescriptor.CANCEL_OPTION
                });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (DialogDescriptor.OK_OPTION == ret) {
            TaskRepository repository = query.getTaskRepository();
            TaskQuerySupportProvider provider = repository.getLookup().lookup(TaskQuerySupportProvider.class);
            TaskQuery taskQuery = settings.getHandler().getTaskQuery();
            provider.modifyTaskQuery(taskQuery);
            taskQuery.synchronize();

        }
    }
}
