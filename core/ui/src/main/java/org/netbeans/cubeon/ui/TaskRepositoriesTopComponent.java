/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.core.spi.RepositorysViewActionsProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;

/**
 * Top component which displays something.
 */
final class TaskRepositoriesTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static TaskRepositoriesTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/cubeon/ui/repositories.png";
    private static final String PREFERRED_ID = "TaskRepositoriesTopComponent";
    //node tree view
    private final BeanTreeView treeView = new BeanTreeView();
    private final transient ExplorerManager explorerManager = new ExplorerManager();

    private TaskRepositoriesTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(TaskRepositoriesTopComponent.class, "CTL_TaskRepositoriesTopComponent"));
        setToolTipText(NbBundle.getMessage(TaskRepositoriesTopComponent.class, "HINT_TaskRepositoriesTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        //hide root node
        treeView.setRootVisible(false);

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                refresh();
            }
        });
    }

    /**
     * 
     * load registered Task repositories
     * 
     */
    void refresh() {
        Children.Array array = new Children.Array();
        explorerManager.setRootContext(
                new AbstractNode(array) {

                    @Override
                    public Action[] getActions(boolean arg0) {
                        List<Action> actions = new ArrayList<Action>();
                        Collection<? extends RepositorysViewActionsProvider> actionsProviders =
                                Lookup.getDefault().lookupAll(RepositorysViewActionsProvider.class);

                        boolean seperatorAdded = false;
                        for (RepositorysViewActionsProvider rvap : actionsProviders) {
                            Action[] as = rvap.getActions();
                            for (Action action : as) {
                                if (action == null) {
                                    if (!seperatorAdded) {
                                        actions.add(action);
                                        seperatorAdded = true;
                                    }
                                    continue;
                                }
                                seperatorAdded = false;
                                actions.add(action);
                            }
                        }


                        return actions.toArray(new Action[0]);

                    }
                });

        //lookup CubeonContext
        CubeonContext cubeonContext = Lookup.getDefault().lookup(CubeonContext.class);
        assert cubeonContext != null : "CubeonContext can't be null";

        //lookup TaskRepositoryHandler
        TaskRepositoryHandler repositoryHandler = cubeonContext.getLookup().lookup(TaskRepositoryHandler.class);
        assert repositoryHandler != null : "TaskRepositoryHandler can't be null";

        List<TaskRepository> repositorys = repositoryHandler.getTaskRepositorys();
        Collections.sort(repositorys, new Comparator<TaskRepository>() {

            public int compare(TaskRepository o1, TaskRepository o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);
        for (TaskRepository tr : repositorys) {
            //get task repository lookup and find node from it
            Node repositoryNode = factory.createTaskRepositoryNode(tr, true);
            //repository node can not be null
            assert repositoryNode != null;
            array.add(new Node[]{repositoryNode});
        }

        //if no repositories found show empty       
        if (repositorys.isEmpty()) {
            array.add(new Node[]{createEmptyNode()});
        }
    }

    /**
     * "< Empty >"
     * this Node will show if no TaskRepositories found
     */
    private Node createEmptyNode() {
        return new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int arg0) {
                return ImageUtilities.loadImage("org/netbeans/cubeon/ui/repository.png", true);
            }

            @Override
            public String getDisplayName() {
                return NbBundle.getMessage(TaskRepositoriesTopComponent.class, "LBL_Empty_Repositories");
            }

            @Override
            public String getShortDescription() {
                return super.getShortDescription();
            //TODO add better description
            }

            /**Override to remove actions */
            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{};
            }
        };
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = treeView;

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized TaskRepositoriesTopComponent getDefault() {
        if (instance == null) {
            instance = new TaskRepositoriesTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the TaskRepositorysTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized TaskRepositoriesTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(TaskRepositoriesTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof TaskRepositoriesTopComponent) {
            return (TaskRepositoriesTopComponent) win;
        }
        Logger.getLogger(TaskRepositoriesTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
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
            return TaskRepositoriesTopComponent.getDefault();
        }
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
