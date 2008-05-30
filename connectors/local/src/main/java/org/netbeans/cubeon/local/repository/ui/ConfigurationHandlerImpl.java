/*
 * ConfigurationHandlerImpl.java
 *
 * Created on May 19, 2008, 2:49 PM
 */
package org.netbeans.cubeon.local.repository.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskRepositoryProvider;
import org.netbeans.cubeon.tasks.spi.TaskRepository;
import org.netbeans.cubeon.tasks.spi.TaskRepositoryType.ConfigurationHandler;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha G
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {

    private LocalTaskRepositoryProvider repositoryProvider;

    /** Creates new form ConfigurationHandlerImpl */
    private ConfigurationHandlerImpl() {
        initComponents();
    }

    public ConfigurationHandlerImpl(LocalTaskRepositoryProvider repositoryProvider) {
        this();
        this.repositoryProvider = repositoryProvider;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        if (taskRepository != null) {
            txtId.setText(taskRepository.getId());
            txtName.setText(taskRepository.getName());
            txtDescription.setText(taskRepository.getDescription());
        }
    }

    public TaskRepository getTaskRepository() {
        return new LocalTaskRepository(repositoryProvider,txtId.getText().trim(),
                txtName.getText().trim(), txtDescription.getText().trim());
    }

    public JComponent getComponent() {
        return this;
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public boolean isValidConfiguration() {

        //to-do
        return true;
    }

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblRepo_id = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDecripion = new javax.swing.JLabel();
        scp = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextPane();

        lblRepo_id.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblRepo_id.text")); // NOI18N

        lblName.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblName.text")); // NOI18N

        lblDecripion.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblDecripion.text")); // NOI18N

        scp.setViewportView(txtDescription);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRepo_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDecripion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(scp, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRepo_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblDecripion, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scp, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDecripion;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRepo_id;
    private javax.swing.JScrollPane scp;
    private javax.swing.JTextPane txtDescription;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
