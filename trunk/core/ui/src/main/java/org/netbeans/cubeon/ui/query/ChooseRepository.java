/*
 * ChooseRepository.java
 *
 * Created on May 17, 2008, 11:20 AM
 */
package org.netbeans.cubeon.ui.query;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.core.api.TaskNodeFactory;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha G
 */
final class ChooseRepository extends javax.swing.JPanel implements ExplorerManager.Provider {

    private final BeanTreeView taskTreeView = new BeanTreeView();
    private final transient ExplorerManager explorerManager = new ExplorerManager();

    /** Creates new form ChooseRepository */
    ChooseRepository(final ChooseRepositoryWizard wizard, List<TaskRepository> repositorys) {
        initComponents();
        taskTreeView.setRootVisible(false);
        taskTreeView.setPopupAllowed(false);
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    wizard.fireChangeEvent();
                }
            }
        });
        loadRepositorys(repositorys);
    }

    @Override
    public String getName() {
        return "Select Repository";
    }

    public boolean isTaskRepositorySelected() {
        return getTaskRepository() != null;
    }

    TaskRepository getTaskRepository() {
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length > 0) {
            Node node = nodes[0];
            TaskRepository repository = node.getLookup().lookup(TaskRepository.class);
            if (repository.getState() == TaskRepository.State.ACTIVE) {
                return repository;
            }
        }
        return null;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void loadRepositorys(List<TaskRepository> repositorys) {

        Children.Array array = new Children.Array();

        Node[] nodes = new Node[repositorys.size()];
        TaskNodeFactory factory = Lookup.getDefault().lookup(TaskNodeFactory.class);
        for (int i = 0; i < repositorys.size(); i++) {
            final TaskRepository repository = repositorys.get(i);
            nodes[i] = factory.createTaskRepositoryNode(repository, false);
        }
        array.add(nodes);
        Node node = new AbstractNode(array) {

            @Override
            public String getName() {
                return "Repositories";//NOI18N
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[0];
            }
        };
        explorerManager.setRootContext(node);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblMainHeader = new javax.swing.JLabel();
        lblSubHeader = new javax.swing.JLabel();
        lblIcon = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JScrollPane scrPane = taskTreeView;

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));

        lblMainHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblMainHeader.setText(NbBundle.getMessage(ChooseRepository.class, "LBL_Repo")); // NOI18N

        lblSubHeader.setText(NbBundle.getMessage(ChooseRepository.class, "LBL_Repo_Dec")); // NOI18N

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/new_task.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout pnlHeaderLayout = new org.jdesktop.layout.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlHeaderLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblSubHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                    .add(lblMainHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                .add(45, 45, 45)
                .add(lblIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeaderLayout.createSequentialGroup()
                .add(pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblMainHeader)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(lblSubHeader))
                    .add(lblIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .add(8, 8, 8)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2))
        );

        scrPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("ComboBox.selectionBackground")));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(scrPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlHeader, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblMainHeader;
    private javax.swing.JLabel lblSubHeader;
    private javax.swing.JPanel pnlHeader;
    // End of variables declaration//GEN-END:variables
}
