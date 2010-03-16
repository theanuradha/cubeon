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
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.TaskFolderActionsProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.Presenter.Popup;
import org.openide.util.datatransfer.PasteType;
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
        Image image = NodeUtils.getTreeFolderIcon(false);
        if (folder.getTaskQuery() != null) {
            image = ImageUtilities.mergeImages(image,
                    ImageUtilities.loadImage("org/netbeans/cubeon/tasks/core/badge_query.png"), 14, 0);
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int i) {
        Image image = NodeUtils.getTreeFolderIcon(true);
        if (folder.getTaskQuery() != null) {
            image = ImageUtilities.mergeImages(image,
                    ImageUtilities.loadImage("org/netbeans/cubeon/tasks/core/badge_query.png"), 14, 0);
        }
        return image;
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new NewActions());
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
                        actions.add(null);

                    }
                    continue;
                }
                actions.add(action);
                sepetatorAdded = false;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public PasteType getDropType(final Transferable t, final int action, int index) {
        final Node[] ns = NodeTransfer.nodes(t,
                DnDConstants.ACTION_COPY_OR_MOVE+NodeTransfer.DND_MOVE );
        TaskElement element = null;
        boolean support = false;
        if (ns != null) {
            for (Node dropNode : ns) {
                element = dropNode.getLookup().lookup(TaskElement.class);
                if (element != null && !folder.contains(element)) {
                    support = true;
                    break;
                }
            }
        }
        if (support) {

            if (element != null && !folder.contains(element)) {
                return new PasteType() {

                    public Transferable paste() throws IOException {
                        TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                        TaskFolderImpl folderImpl = folder.getLookup().lookup(TaskFolderImpl.class);
                        Set<TaskFolderImpl> markToRefresh = new HashSet<TaskFolderImpl>();
                        for (Node dropNode : ns) {
                            TaskElement element = dropNode.getLookup().lookup(TaskElement.class);
                            if (element != null && !folder.contains(element)) {
                                if ((action & NodeTransfer.DND_MOVE) != 0) {
                                    TaskFolderImpl oldFolderImpl =
                                            dropNode.getParentNode().getLookup().lookup(TaskFolderImpl.class);
                                    if (oldFolderImpl != null) {
                                        fileSystem.removeTaskElement(oldFolderImpl, element);
                                        //add to refresh 
                                        markToRefresh.add(oldFolderImpl);

                                    }
                                }
                                if (folderImpl != null) {
                                    fileSystem.addTaskElement(folderImpl, element);
                                    //add to refresh
                                    markToRefresh.add(folderImpl);
                                }
                            }
                        }
                        //refresh changed folders
                        for (TaskFolderImpl taskFolderImpl : markToRefresh) {
                            taskFolderImpl.refreshContent();
                        }
                        return null;
                    }
                };
            }
        }
        return null;
    }

    private class NewActions extends AbstractAction implements Presenter.Popup {

        private static final long serialVersionUID = 7150861423568497818L;

        public NewActions() {
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

    @Override
    public String getHtmlDisplayName() {
        return getDisplayName();
    }

    void refreshIcon() {
        fireIconChange();
    }
}
