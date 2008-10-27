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
package org.netbeans.cubeon.trac.repository.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.utils.TracExceptionHandler;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Anuradha
 */
public class ReconnectAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private TracTaskRepository repository;

    public ReconnectAction(TracTaskRepository repository) {
        this.repository = repository;
        putValue(NAME, NbBundle.getMessage(ReconnectAction.class, "LBL_Reconnect_Repository"));
    }

    public void actionPerformed(ActionEvent e) {

        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                try {
                    repository.reconnect();
                } catch (TracException ex) {
                    TracExceptionHandler.notify(ex);
                }
            }
        });

    }
}
