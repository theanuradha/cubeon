/*
 *  Copyright 2008 Cube°n Team.
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
package org.netbeans.cubeon.ui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.netbeans.cubeon.ui.taskfolder.DeleteTaskFolderAction;
import org.netbeans.cubeon.ui.taskfolder.RefreshTaskFolderAction;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;

/**
 * Top component which displays Tasks
 */
public final class TaskExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static TaskExplorerTopComponent instance;
    private static final String SELECTED_VIEW = "SELECTED_VIEW";//NOI18N
    private static final String PREFERRED_ID = "TaskExplorerTopComponent";//NOI18N
    private static final long serialVersionUID = 3076553315734088248L;
    private final Preferences preferences = NbPreferences.forModule(TaskExplorerTopComponent.class);
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/cubeon/ui/task_explorer.png";
    private final BeanTreeView taskTreeView = new BeanTreeView();
    private final JPopupMenu viewMenu = new JPopupMenu();
    private final transient ExplorerManager explorerManager = new ExplorerManager();
    private TaskNodeView selectedView;

    private TaskExplorerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(TaskExplorerTopComponent.class, "CTL_TaskExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(TaskExplorerTopComponent.class, "HINT_TaskExplorerTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        taskTreeView.setRootVisible(false);
        unloadViewMenu();
        //Task Explorer - remove task keyboard shortcu
        ActionMap actionMap = getActionMap();

        actionMap.put("delete", new RemoveAction());//NOi18N
        associateLookup(ExplorerUtils.createLookup(explorerManager, actionMap));

        viewMenu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                loadViewMenu();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                unloadViewMenu();
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                unloadViewMenu();
            }
        });
        //issue 78
        goBackToRoot.setVisible(false);


    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void unloadViewMenu() {
        viewMenu.removeAll();
        viewMenu.addSeparator();
    }

    private void loadViewMenu() {
        viewMenu.removeAll();
        Collection<? extends TaskNodeView> views = Lookup.getDefault().lookupAll(TaskNodeView.class);
        for (final TaskNodeView view : views) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(new AbstractAction(view.getName()) {

                public void actionPerformed(ActionEvent e) {
                    selectView(view);
                }
            });
            item.setSelected(view.equals(selectedView));
            item.setEnabled(views.size() > 1);
            viewMenu.add(item);
        }

    }

    synchronized void selectView(final TaskNodeView view) {
        if (!view.equals(selectedView)) {
            selectedView = view;

            preferences.put(SELECTED_VIEW, view.getId());
            final Node node = view.getRootContext();
            explorerManager.setRootContext(node);
            expand();
        }
    }

    public void goInto(TaskFolder folder) {
        if (selectedView == null) {
            loadView();
        }

        Children children = selectedView.getRootContext().getChildren();
        final Node[] nodes = children.getNodes();
        for (Node node : nodes) {
            TaskFolder tf = node.getLookup().lookup(TaskFolder.class);
            if (folder.equals(tf)) {
                explorerManager.setRootContext(node);
                break;
            }
        }
        goBackToRoot.setVisible(true);
        goBackToRoot.setAction(new GoBack());
        taskTreeView.setRootVisible(true);
    }

    public void goToRoot() {
        if (selectedView == null) {
            loadView();
        }
        explorerManager.setRootContext(selectedView.getRootContext());
        expand();
        goBackToRoot.setVisible(false);
        taskTreeView.setRootVisible(false);
        goBackToRoot.setAction(null);
    }

    public void expand() {
        Task task = RequestProcessor.getDefault().create(new Runnable() {

            public void run() {
                Children children = explorerManager.getRootContext().getChildren();
                final Node[] nodes = children.getNodes();
                if (nodes.length > 0) {
                    Node n = nodes[0];
                    try {
                        explorerManager.setExploredContextAndSelection(n, new Node[]{n});
                    } catch (PropertyVetoException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        });
        task.schedule(200);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane treeView = taskTreeView;
        mainToolbarHolder = new javax.swing.JPanel();
        mainToolBar = new javax.swing.JToolBar();
        newTask = new javax.swing.JButton();
        refresh = new javax.swing.JButton();
        taskView = DropDownButtonFactory.createDropDownButton((new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/ui/view.png"))), viewMenu);
        javax.swing.JToolBar.Separator sep = new javax.swing.JToolBar.Separator();
        goBackToRoot = new javax.swing.JButton();
        subToolbar = new javax.swing.JToolBar();
        downMenu = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        treeView.setBorder(null);
        add(treeView, java.awt.BorderLayout.CENTER);

        mainToolbarHolder.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(137, 140, 149)));
        mainToolbarHolder.setLayout(new java.awt.BorderLayout());

        mainToolBar.setBorder(null);
        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);

        newTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/new_task.png"))); // NOI18N
        newTask.setFocusable(false);
        newTask.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newTask.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTaskActionPerformed(evt);
            }
        });
        mainToolBar.add(newTask);

        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/refresh.png"))); // NOI18N
        refresh.setFocusable(false);
        refresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });
        mainToolBar.add(refresh);

        taskView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/view.png"))); // NOI18N
        taskView.setFocusable(false);
        taskView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        taskView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        taskView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskViewActionPerformed(evt);
            }
        });
        mainToolBar.add(taskView);
        mainToolBar.add(sep);

        goBackToRoot.setFocusable(false);
        goBackToRoot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goBackToRoot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(goBackToRoot);

        mainToolbarHolder.add(mainToolBar, java.awt.BorderLayout.CENTER);

        subToolbar.setBorder(null);
        subToolbar.setFloatable(false);
        subToolbar.setRollover(true);

        downMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/down.gif"))); // NOI18N
        downMenu.setFocusable(false);
        downMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downMenu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        downMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downMenuActionPerformed(evt);
            }
        });
        subToolbar.add(downMenu);

        mainToolbarHolder.add(subToolbar, java.awt.BorderLayout.EAST);

        add(mainToolbarHolder, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

private void newTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTaskActionPerformed
    new NewTaskWizardAction("").actionPerformed(evt);
}//GEN-LAST:event_newTaskActionPerformed

private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
    TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
    new RefreshTaskFolderAction(fileSystem.getRootTaskFolder()).actionPerformed(evt);
}//GEN-LAST:event_refreshActionPerformed

private void downMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downMenuActionPerformed
    Node node = explorerManager.getRootContext();
    JPopupMenu contextMenu = node.getContextMenu();
    contextMenu.show(downMenu, 0, downMenu.getHeight());
}//GEN-LAST:event_downMenuActionPerformed

private void taskViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskViewActionPerformed
    viewMenu.show(taskView, 0, taskView.getHeight());
}//GEN-LAST:event_taskViewActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downMenu;
    private javax.swing.JButton goBackToRoot;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JPanel mainToolbarHolder;
    private javax.swing.JButton newTask;
    private javax.swing.JButton refresh;
    private javax.swing.JToolBar subToolbar;
    private javax.swing.JButton taskView;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized TaskExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new TaskExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the TaskExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized TaskExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(TaskExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof TaskExplorerTopComponent) {
            return (TaskExplorerTopComponent) win;
        }
        Logger.getLogger(TaskExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                loadView();
            }
        });
    }

    private void loadView() {
        if (selectedView == null) {
            Collection<? extends TaskNodeView> views = Lookup.getDefault().lookupAll(TaskNodeView.class);
            String selctedId = preferences.get(SELECTED_VIEW, null);

            for (TaskNodeView view : views) {
                if (selctedId == null || view.getId().equals(selctedId)) {
                    selectView(view);
                    break;
                }
            }

            if (selectedView == null && views.size() > 0) {
                //previse view is no longer available so set null on preferences and re call loadView()
                preferences.put(SELECTED_VIEW, null);
                loadViewMenu();
            }
        }

    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return TaskExplorerTopComponent.getDefault();
        }
    }

    private class Context extends AbstractAction {

        private static final long serialVersionUID = 5527829264450966761L;
        private final TaskContextManager contextManager;

        private Context() {
            contextManager = Lookup.getDefault().lookup(TaskContextManager.class);
            if (contextManager.getContextView().equals(selectedView)) {
                putValue(SHORT_DESCRIPTION, NbBundle.getMessage(TaskExplorerTopComponent.class, "LBL_Hide_Task_Context"));
                putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/ui/focus_off.png")));
            } else {
                putValue(SHORT_DESCRIPTION, NbBundle.getMessage(TaskExplorerTopComponent.class, "LBL_Show_Task_Context"));
                putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/ui/focus_on.png")));
            }



        }

        public void actionPerformed(ActionEvent e) {
            if (contextManager.getContextView().equals(selectedView)) {
                TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                selectView(fileSystem.getFilesystemView());
            } else {
                selectView(contextManager.getContextView());
            }


        }
    }

    private class GoBack extends AbstractAction {

        private static final long serialVersionUID = 4293205969173010442L;

        public GoBack() {
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(TaskExplorerTopComponent.class, "LBL_Go_Back_ To_ Root"));
            Image image = NodeUtils.getTreeFolderIcon(false);
            Image badge = ImageUtilities.loadImage("org/netbeans/cubeon/ui/goBack.png");
            Image mergeImages = ImageUtilities.mergeImages(image, badge, 7, 0);
            putValue(SMALL_ICON, new ImageIcon(mergeImages));
        }

        public void actionPerformed(ActionEvent e) {

            goToRoot();
        }
    }

    private class RemoveAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            Node[] selectedNodes = explorerManager.getSelectedNodes();
            TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
            Set<TaskFolder> deletedFolders = new HashSet<TaskFolder>();
            Set<TaskFolder> refreshFolders = new HashSet<TaskFolder>();
            for (Node node : selectedNodes) {
                TaskFolder container = node.getLookup().lookup(TaskFolder.class);
                TaskElement element = node.getLookup().lookup(TaskElement.class);
                //if element null and folder not null delete folder
                if (container != null && element == null &&
                        !container.equals(fileSystem.getDefaultFolder())) {
                    new DeleteTaskFolderAction(container).actionPerformed(e);
                    deletedFolders.add(container);
                    //if both not null remove task element from task folder
                } else if (container != null && element != null) {

                    if (element != null && !deletedFolders.contains(container)) {

                        fileSystem.removeTaskElement(container, element);
                        refreshFolders.add(container);

                        //if Shift down move task to Default Folder
                        if (e.getModifiers() == KeyEvent.SHIFT_DOWN_MASK &&
                                !container.equals(fileSystem.getDefaultFolder())) {
                            fileSystem.addTaskElement(fileSystem.getDefaultFolder(), element);
                            refreshFolders.add(fileSystem.getDefaultFolder());

                        }
                    }

                }
            }
            refreshFolders.removeAll(deletedFolders);
            for (TaskFolder taskFolder : refreshFolders) {
                TaskFolderRefreshable oldTfr = taskFolder.getLookup().lookup(TaskFolderRefreshable.class);
                if (oldTfr != null) {
                    oldTfr.refreshNode();
                }
            }
        }
    }
}
