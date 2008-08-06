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
package org.netbeans.cubeon.tasks.core.internals;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.spi.TaskExplorerViewActionsProvider;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
class RootFolder extends TaskFolderImpl {

    public RootFolder(String name,
            String description) {
        super(null, name, description, true);



        folderChildren = new TaskFolderChildren(this);

        folderNode = new AbstractNode(folderChildren.getChildren(), Lookups.singleton(this)) {

            @Override
            public Action[] getActions(boolean arg0) {
                List<Action> actions = new ArrayList<Action>();
                actions.add(new NewActions());
                actions.add(null);
                final List<TaskExplorerViewActionsProvider> providers =
                        new ArrayList<TaskExplorerViewActionsProvider>(
                        Lookup.getDefault().lookupAll(TaskExplorerViewActionsProvider.class));
                boolean sepetatorAdded = false;
                for (TaskExplorerViewActionsProvider tevap : providers) {
                    Action[] as = tevap.getActions();
                    for (Action action : as) {
                        //check null and addSeparator 
                        if (action == null) {
                            //check sepetatorAdd to prevent adding duplicate Separators 
                            if (!sepetatorAdded) {
                                //mark sepetatorAdd to true
                                sepetatorAdded = true;
                                actions.add(action);

                            }
                            continue;
                        }
                        actions.add(action);
                        sepetatorAdded = false;
                    }
                }
                return actions.toArray(new Action[0]);
            }
        };
    }

    @Override
    public void refreshNode() {
        folderChildren.refreshContent();
        for (TaskFolderImpl taskFolderImpl : taskFolders) {
            taskFolderImpl.refreshNode();
        }

    }


    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private class NewActions extends AbstractAction implements Presenter.Popup {

        public NewActions() {
            putValue(NAME, NbBundle.getMessage(RootFolder.class, "LBL_NEW"));
        }

        public void actionPerformed(ActionEvent e) {
        }

        public JMenuItem getPopupPresenter() {
            final JMenu menu = new JMenu(this);
            //lookup TaskExplorerViewActionsProvider and sort with position attribute
            final List<TaskExplorerViewActionsProvider> providers =
                    new ArrayList<TaskExplorerViewActionsProvider>(
                    Lookup.getDefault().lookupAll(TaskExplorerViewActionsProvider.class));

            boolean sepetatorAdded = false;
            for (TaskExplorerViewActionsProvider tevap : providers) {
                Action[] actions = tevap.getNewActions();
                for (Action action : actions) {
                    //check null and addSeparator 
                    if (action == null) {
                        //check sepetatorAdd to prevent adding duplicate Separators 
                        if (!sepetatorAdded) {
                            //mark sepetatorAdd to true
                            sepetatorAdded = true;
                            menu.addSeparator();

                        }
                        continue;
                    }
                    //mark sepetatorAdd to false
                    sepetatorAdded = false;
                    //check for Presenter.Popup
                    if (action instanceof Presenter.Popup) {
                        Presenter.Popup popup = (Popup) action;
                        menu.add(popup.getPopupPresenter());
                        continue;
                    }

                    menu.add(new JMenuItem(action));
                }
            }
            return menu;
        }
    }
}
