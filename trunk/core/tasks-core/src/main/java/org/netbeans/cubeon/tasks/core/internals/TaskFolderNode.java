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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.spi.TaskFolderActionsProvider;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.Presenter.Popup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TaskFolderNode extends AbstractNode {

    private TaskFolder folder;

    public TaskFolderNode(TaskFolder folder, Children children) {
        super(children, Lookups.fixed(folder));
        this.folder = folder;
        setDisplayName(folder.getName());
        setShortDescription(folder.getDescription());

    }

    @Override
    public Image getIcon(int i) {
        return NodeUtils.getTreeFolderIcon(false);
    }

    @Override
    public Image getOpenedIcon(int i) {
        return NodeUtils.getTreeFolderIcon(true);
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new NewActions(folder));
        actions.add(null);
        final List<TaskFolderActionsProvider> providers =
                new ArrayList<TaskFolderActionsProvider>(
                Lookup.getDefault().lookupAll(TaskFolderActionsProvider.class));
        boolean sepetatorAdded = false;
        for (TaskFolderActionsProvider tfap : providers) {
            Action[] as = tfap.getActions(folder);
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

    public  static class NewActions extends AbstractAction implements Presenter.Popup {
        private TaskFolder folder;
        public NewActions(TaskFolder folder) {
            this.folder=folder;
            putValue(NAME, NbBundle.getMessage(TaskFolderNode.class, "LBL_NEW"));
        }

        public void actionPerformed(ActionEvent e) {
        }

        public JMenuItem getPopupPresenter() {
            final JMenu menu = new JMenu(this);
            //lookup TaskFolderActionsProvider and sort with position attribute
            final List<TaskFolderActionsProvider> providers =
                    new ArrayList<TaskFolderActionsProvider>(
                    Lookup.getDefault().lookupAll(TaskFolderActionsProvider.class));

            Collections.sort(providers, new Comparator<TaskFolderActionsProvider>() {

                public int compare(TaskFolderActionsProvider o1,
                        TaskFolderActionsProvider o2) {
                    if (o1.getPosition() == o2.getPosition()) {
                        return 0;
                    }
                    if (o1.getPosition() > o2.getPosition()) {
                        return 1;
                    }
                    return -1;
                }
            });
            boolean sepetatorAdded = false;
            for (TaskFolderActionsProvider tfap : providers) {
                Action[] actions = tfap.getNewActions(folder);
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
