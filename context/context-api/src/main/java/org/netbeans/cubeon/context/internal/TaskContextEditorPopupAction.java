/*
 *  Copyright 2010 Anuradha.
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
package org.netbeans.cubeon.context.internal;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.context.spi.TaskContextActionsProvider;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha
 */
public class TaskContextEditorPopupAction extends AbstractAction implements ContextAwareAction, Popup {

    private final Lookup context;

    public TaskContextEditorPopupAction() {
        context = Utilities.actionsGlobalContext();
        putValue(NAME, NbBundle.getMessage(TaskContextEditorPopupAction.class, "LBL_TaskContextAction"));
    }

    public TaskContextEditorPopupAction(Lookup context) {
        this.context = context;
        putValue(NAME, NbBundle.getMessage(TaskContextEditorPopupAction.class, "LBL_TaskContextAction"));
    }

    public void actionPerformed(ActionEvent e) {
        //donothing
    }

    public JMenuItem getPopupPresenter() {

        JMenu menuItem = new JMenu(this);
        Collection<? extends TaskContextActionsProvider> actionsProviders =
                Lookup.getDefault().lookupAll(TaskContextActionsProvider.class);
        boolean addedSepatator = false;
        for (TaskContextActionsProvider taskContextActionsProvider : actionsProviders) {
            Action[] editorPopupActions = taskContextActionsProvider.getEditorPopupActions(context);
            for (Action action : editorPopupActions) {
                if (action == null) {
                    if (!addedSepatator) {
                        addedSepatator = true;
                        menuItem.addSeparator();

                    }
                    continue;

                }
                if (action instanceof Popup) {
                    menuItem.add(((Popup) action).getPopupPresenter());
                } else {
                    menuItem.add(action);
                }
                addedSepatator = false;
            }
        }
        return menuItem;
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new TaskContextEditorPopupAction(actionContext);
    }
}
