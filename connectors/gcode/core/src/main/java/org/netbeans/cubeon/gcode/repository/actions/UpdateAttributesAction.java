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
package org.netbeans.cubeon.gcode.repository.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.Notifier.NotifierReference;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository.State;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Anuradha
 */
public class UpdateAttributesAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final GCodeTaskRepository repository;
    private final Notifier<RepositoryEventAdapter> extension;
    private final NotifierReference<RepositoryEventAdapter> notifierReference;

    public UpdateAttributesAction(GCodeTaskRepository repository) {
        this.repository = repository;
        putValue(NAME, NbBundle.getMessage(UpdateAttributesAction.class, "LBL_Update_Attributes"));
        setEnabled(repository.getState() == TaskRepository.State.ACTIVE);
        extension = repository.getNotifier();
        notifierReference=extension.add(new RepositoryEventAdapter() {

            @Override
            public synchronized void stateChanged(State state) {
                setEnabled(state == TaskRepository.State.ACTIVE);
            }
        });



    }

    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                repository.updateAttributes();
            }
        });
    }

    
}
