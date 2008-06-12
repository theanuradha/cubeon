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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
import org.netbeans.cubeon.ui.taskelemet.NewTaskWizardAction;
import org.netbeans.cubeon.ui.taskfolder.RefreshTaskFolderAction;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;

/**
 * Top component which displays Tasks
 */
public final class TaskExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static TaskExplorerTopComponent instance;
    private static final String SELECTED_VIEW = "SELECTED_VIEW";
    private static final String PREFERRED_ID = "TaskExplorerTopComponent";
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
        setIcon(Utilities.loadImage(ICON_PATH, true));
        taskTreeView.setRootVisible(false);
        unloadViewMenu();
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

    private synchronized void selectView(final TaskNodeView view) {
        selectedView = view;
        preferences.put(SELECTED_VIEW, view.getId());
        final Node node = view.createRootContext();
        explorerManager.setRootContext(node);
        expand();
    }

    public void expand() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {

                taskTreeView.setAutoscrolls(false);
                Children children = explorerManager.getRootContext().getChildren();
                final Node[] nodes = children.getNodes();
                for (Node n : nodes) {
                    taskTreeView.expandNode(n);
                }

                taskTreeView.setAutoscrolls(true);

            }
        });
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
        taskView = DropDownButtonFactory.createDropDownButton((new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/view.png"))), viewMenu);
        javax.swing.JToolBar.Separator sep = new javax.swing.JToolBar.Separator();
        focas = new javax.swing.JButton();
        subToolbar = new javax.swing.JToolBar();
        downMenu = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        treeView.setBorder(null);
        add(treeView, java.awt.BorderLayout.CENTER);

        mainToolbarHolder.setLayout(new java.awt.BorderLayout());

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
        mainToolBar.add(taskView);
        mainToolBar.add(sep);

        focas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/focus_off.png"))); // NOI18N
        focas.setFocusable(false);
        focas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        focas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(focas);

        mainToolbarHolder.add(mainToolBar, java.awt.BorderLayout.CENTER);

        subToolbar.setFloatable(false);
        subToolbar.setRollover(true);

        downMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/down.gif"))); // NOI18N
        downMenu.setFocusable(false);
        downMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downMenu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        subToolbar.add(downMenu);

        mainToolbarHolder.add(subToolbar, java.awt.BorderLayout.EAST);
        mainToolbarHolder.add(jSeparator1, java.awt.BorderLayout.SOUTH);

        add(mainToolbarHolder, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

private void newTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTaskActionPerformed
    new NewTaskWizardAction("").actionPerformed(evt);
}//GEN-LAST:event_newTaskActionPerformed

private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
    TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
    new RefreshTaskFolderAction(fileSystem.getRootTaskFolder()).actionPerformed(evt);
}//GEN-LAST:event_refreshActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downMenu;
    private javax.swing.JButton focas;
    private javax.swing.JSeparator jSeparator1;
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
        EventQueue.invokeLater(new Runnable() {

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
}
