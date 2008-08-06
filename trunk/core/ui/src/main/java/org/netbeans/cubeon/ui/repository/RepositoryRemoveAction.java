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
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.RepositorysViewRefreshable;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.netbeans.cubeon.ui.taskfolder.RefreshTaskFolderAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class RepositoryRemoveAction extends AbstractAction {

    private TaskRepository repository;

    public RepositoryRemoveAction(TaskRepository repository) {
        this.repository = repository;
        putValue(NAME, NbBundle.getMessage(RepositoryRemoveAction.class, "LBL_Delete"));//NOI18N
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));//NOI18N
        setEnabled(repository.getState() != TaskRepository.State.SYNCHRONIZING);
    }

    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(NbBundle.getMessage(
                RepositoryRemoveAction.class, "LBL_Delete_Confirmation", repository.getName()),
                NotifyDescriptor.OK_CANCEL_OPTION);
        Object notify = DialogDisplayer.getDefault().notify(d);
        if (notify == NotifyDescriptor.OK_OPTION) {
            TaskRepositoryType type = repository.getLookup().lookup(TaskRepositoryType.class);
            type.removeRepository(repository);
            Collection<? extends RepositorysViewRefreshable> refreshables =
                    Lookup.getDefault().lookupAll(RepositorysViewRefreshable.class);
            for (RepositorysViewRefreshable rvr : refreshables) {
                rvr.refreshContent();
            }
            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            new RefreshTaskFolderAction(fileSystem.getRootTaskFolder()).actionPerformed(e);
        }
    }
}
