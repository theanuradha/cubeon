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
package org.netbeans.cubeon.ui.repository;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.ui.repository.NewRepositoryWizardAction.WizardObject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class RepositoryEditAction extends AbstractAction {

    private TaskRepository repository;

    public RepositoryEditAction(TaskRepository repository) {
        this.repository = repository;
        putValue(NAME, NbBundle.getMessage(RepositoryEditAction.class, "LBL_Properties"));
        setEnabled(repository.getState()==TaskRepository.State.ACTIVE);
    }

    public void actionPerformed(ActionEvent e) {
        RepositorySettings settings = new RepositorySettings();
        TaskRepositoryType type = repository.getLookup().lookup(TaskRepositoryType.class);
        settings.setWizardObject(new WizardObject(type, repository));
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
            type.persistRepository(settings.getHandler().getTaskRepository());
        }
    }
}
