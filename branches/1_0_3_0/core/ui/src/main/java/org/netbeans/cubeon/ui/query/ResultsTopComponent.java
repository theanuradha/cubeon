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
package org.netbeans.cubeon.ui.query;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.Notifier.NotifierReference;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final class ResultsTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static ResultsTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/cubeon/ui/query/results.png";
    private static final long serialVersionUID = 1L;
    private transient ExplorerManager explorerManager = new ExplorerManager();
    private static final String PREFERRED_ID = "ResultsTopComponent";
    private BeanTreeView treeView = new BeanTreeView();

    private ResultsTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ResultsTopComponent.class, "CTL_ResultsTopComponent"));
        setToolTipText(NbBundle.getMessage(ResultsTopComponent.class, "HINT_ResultsTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = treeView;

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized ResultsTopComponent getDefault() {
        if (instance == null) {
            instance = new ResultsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ResultsTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ResultsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ResultsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ResultsTopComponent) {
            return (ResultsTopComponent) win;
        }
        Logger.getLogger(ResultsTopComponent.class.getName()).warning(
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
        if (taskQuery == null) {
            explorerManager.setRootContext(_createNoQueryNode());
        }
    }

    private Node _createNoQueryNode() {
        return new EmptyNode("No task query seleted to show results");
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
            return ResultsTopComponent.getDefault();
        }
    }
    private volatile Task task;
    private TaskQuery taskQuery;
    private NotifierReference<TaskQueryEventAdapter> notifierReference;

    synchronized void showResults(final TaskQuery taskQuery) {
        if (taskQuery != null) {

            if (this.taskQuery != null && notifierReference != null) {
                this.taskQuery.getNotifier().remove(notifierReference);
                notifierReference = null;
            }
            final Children.Array array = new Children.Array();
            this.taskQuery = taskQuery;
            final ResultQueryNode queryNode = new ResultQueryNode(array, taskQuery);
            explorerManager.setRootContext(queryNode);

            notifierReference = taskQuery.getNotifier().add(new TaskQueryEventAdapter() {

                @Override
                public void atributesupdated() {
                    queryNode.setInfomations(taskQuery);
                }

                @Override
                public void querySynchronizing() {
                    queryNode.updateNodeTag("Synchronizing...");
                    array.remove(array.getNodes());
                }

                @Override
                public void taskAdded(TaskElement element) {
                    queryNode.updateNodeTag("Synchronizing...");
                    array.add(new Node[]{new TaskResultNode(element)});
                }

                @Override
                public void querySynchronized() {
                    if (task != null && !task.isFinished()) {
                        task.cancel();
                    }
                    task = RequestProcessor.getDefault().create(new Runnable() {

                        public void run() {
                            array.remove(array.getNodes());
                            loadQueries(array, queryNode);
                        }
                    });
                    task.run();
                }

                @Override
                public void removed() {
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            if (task != null && !task.isFinished()) {
                                task.cancel();
                            }
                            explorerManager.setRootContext(_createNoQueryNode());
                        }
                    });
                }
            });

            queryNode.updateNodeTag("Synchronizing...");
            loadQueries(array, queryNode);

        }
    }

    private void loadQueries(Children array, final ResultQueryNode queryNode) {

        List<TaskElement> elements = taskQuery.getTaskElements();
        Collections.sort(elements, new Comparator<TaskElement>() {

            public int compare(TaskElement o1, TaskElement o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });


        queryNode.updateNodeTag(elements.size() + " Tasks Found");
        for (TaskElement taskElement : elements) {
            array.add(new Node[]{new TaskResultNode(taskElement)});
        }

    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private static class EmptyNode extends AbstractNode {

        public EmptyNode(String name) {
            super(Children.LEAF);
            setName(name);
        }

        @Override
        public Action[] getActions(boolean arg0) {
            return new Action[]{};
        }

        @Override
        public Image getIcon(int arg0) {
            return Utilities.loadImage(ICON_PATH);
        }
    }
}