/*
 * ChooseRepositoryProvider.java
 *
 * Created on May 17, 2008, 11:20 AM
 */
package org.netbeans.cubeon.ui.repository;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.JScrollPane;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Anuradha G
 */
final class ChooseRepositoryProvider extends javax.swing.JPanel implements ExplorerManager.Provider {

    private final BeanTreeView taskTreeView = new BeanTreeView();
    private final transient ExplorerManager explorerManager = new ExplorerManager();

    /** Creates new form ChooseRepositoryProvider */
    ChooseRepositoryProvider(final ChooseRepositoryWizard wizard) {
        initComponents();
        taskTreeView.setBorder(new JScrollPane().getBorder());
        taskTreeView.setRootVisible(false);
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    wizard.fireChangeEvent();
                }
            }
        });
        loadProviders();
    }

    @Override
    public String getName() {
        return "Choose Repository Type";
    }

    public boolean isTypeSelected() {
        return getTaskRepositoryType() != null;
    }

    TaskRepositoryType getTaskRepositoryType() {
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length > 0) {
            Node node = nodes[0];
            TaskRepositoryType type = node.getLookup().lookup(TaskRepositoryType.class);
            return type;
        }
        return null;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void loadProviders() {
        //lookup CubeonContext
        CubeonContext cubeonContext = Lookup.getDefault().lookup(CubeonContext.class);
        assert cubeonContext != null : "CubeonContext can't be null";
        //lookup TaskRepositoryHandler
        TaskRepositoryHandler repositoryHandler = cubeonContext.getLookup().lookup(TaskRepositoryHandler.class);

        List<TaskRepositoryType> providers = repositoryHandler.getTaskRepositoryTypes();
        Collections.sort(providers, new Comparator<TaskRepositoryType>() {

            public int compare(TaskRepositoryType o1, TaskRepositoryType o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Children.Array array = new Children.Array();

        Node[] nodes = new Node[providers.size()];

        for (int i = 0; i < providers.size(); i++) {
            final TaskRepositoryType trp = providers.get(i);
            nodes[i] = new AbstractNode(Children.LEAF, Lookups.singleton(trp)) {

                @Override
                public String getDisplayName() {
                    return trp.getName();
                }

                @Override
                public String getShortDescription() {
                    return trp.getDescription();
                }

                @Override
                public Image getIcon(int arg0) {
                    return trp.getImage();
                }

                @Override
                public Action[] getActions(boolean arg0) {
                    return new Action[0];
                }
            };
        }
        array.add(nodes);
        Node node = new AbstractNode(array) {

            @Override
            public String getName() {
                return "Providers";//NOI18N
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
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(137, 140, 149)));

        lblMainHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMainHeader.setText(NbBundle.getMessage(ChooseRepositoryProvider.class, "LBL_Repo_Provider")); // NOI18N

        lblSubHeader.setText(NbBundle.getMessage(ChooseRepositoryProvider.class, "LBL_Repo_Provider_Dec")); // NOI18N

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/ui/repository_add.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout pnlHeaderLayout = new org.jdesktop.layout.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlHeaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlHeaderLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblSubHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                    .add(lblMainHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .add(45, 45, 45)
                .add(lblIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                    .add(lblIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(12, 12, 12))
        );

        scrPane.setBorder(null);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(scrPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
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
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblMainHeader;
    private javax.swing.JLabel lblSubHeader;
    private javax.swing.JPanel pnlHeader;
    // End of variables declaration//GEN-END:variables
}
