/*
 * ChooseRepository.java
 *
 * Created on May 17, 2008, 11:20 AM
 */
package org.netbeans.cubeon.ui.taskelemet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha G
 */
final class ChooseRepository extends javax.swing.JPanel implements ExplorerManager.Provider {

    private final BeanTreeView taskTreeView = new BeanTreeView();
    private final transient ExplorerManager explorerManager = new ExplorerManager();
     
    /** Creates new form ChooseRepository */
    ChooseRepository(final ChooseRepositoryWizard wizard,List<TaskRepository> repositorys) {
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
        return "Step #1";
    }

    public boolean isTaskRepositorySelected() {
        return getTaskRepository() != null;
    }

    TaskRepository getTaskRepository() {
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length > 0) {
            Node node = nodes[0];
            TaskRepository repository = node.getLookup().lookup(TaskRepository.class);
            return repository;
        }
        return null;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void loadRepositorys(List<TaskRepository> repositorys) {
        
        Children.Array array = new Children.Array();

        Node[] nodes = new Node[repositorys.size()];

        for (int i = 0; i < repositorys.size(); i++) {
            final TaskRepository repository = repositorys.get(i);
            nodes[i] = repository.getLookup().lookup(Node.class);
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
        javax.swing.JScrollPane scrPane = taskTreeView;

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblMainHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMainHeader.setText(NbBundle.getMessage(ChooseRepository.class, "LBL_Repo")); // NOI18N

        lblSubHeader.setText(NbBundle.getMessage(ChooseRepository.class, "LBL_Repo_Dec")); // NOI18N

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/new_task.png"))); // NOI18N

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblSubHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                    .addComponent(lblMainHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addGap(45, 45, 45)
                .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblMainHeader)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblSubHeader))
                    .addComponent(lblIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addContainerGap())
        );

        scrPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("ComboBox.selectionBackground")));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblMainHeader;
    private javax.swing.JLabel lblSubHeader;
    private javax.swing.JPanel pnlHeader;
    // End of variables declaration//GEN-END:variables
}
