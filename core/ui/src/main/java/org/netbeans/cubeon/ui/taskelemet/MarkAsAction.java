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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskStatusProvider;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha
 */
public class MarkAsAction extends AbstractAction implements Menu, Popup {

    private TaskElement element;

    public MarkAsAction(TaskElement element) {
        this.element = element;
        putValue(NAME, NbBundle.getMessage(MarkAsAction.class, "LBL_Mark_As"));
    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuPresenter() {
        JMenu menuItem = new JMenu(this);
        TaskRepository taskRepository = element.getTaskRepository();
        TaskStatusProvider statusProvider = taskRepository.getLookup().lookup(TaskStatusProvider.class);
        for (TaskStatus status : statusProvider.getStatusList()) {
            menuItem.add(new SelectAction(status, status.equals(element.getStatus())));
        }
        return menuItem;
    }

    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    private class SelectAction extends AbstractAction {

        private TaskStatus status;

        public SelectAction(TaskStatus status, boolean selected) {
            this.status = status;
            putValue(NAME, status.getText());
            if (selected) {
                putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/selected.png")));

            }
            setEnabled(!selected);

        }

        public void actionPerformed(ActionEvent e) {
            element.setStatus(status);
            TaskRepository repository = element.getTaskRepository();
            repository.persist(element);
        }
    }
}
