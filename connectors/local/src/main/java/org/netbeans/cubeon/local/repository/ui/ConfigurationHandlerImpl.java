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
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha G
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {

    private LocalTaskRepositoryProvider repositoryProvider;
    private LocalTaskRepository repository;

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
            repository = taskRepository.getLookup().lookup(LocalTaskRepository.class);
            txtName.setText(taskRepository.getName());
            txtDescription.setText(taskRepository.getDescription());

        }
    }

    public TaskRepository getTaskRepository() {
        if (repository != null) {
            repository.setName(txtName.getText().trim());
            repository.setDescription(txtDescription.getText().trim());
            return repository;
        }
        return new LocalTaskRepository(repositoryProvider,
                txtName.getText().trim().toLowerCase(),
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

        txtName = new javax.swing.JTextField();
        javax.swing.JScrollPane txtDescriptionContainer = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextPane();
        lblName = new javax.swing.JLabel();
        lblDecripion = new javax.swing.JLabel();

        txtDescriptionContainer.setViewportView(txtDescription);

        lblName.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblName.text")); // NOI18N

        lblDecripion.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblDecripion.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblDecripion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(txtDescriptionContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblName)
                .add(4, 4, 4)
                .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblDecripion)
                .addContainerGap(281, Short.MAX_VALUE))
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(73, 73, 73)
                    .add(txtDescriptionContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDecripion;
    private javax.swing.JLabel lblName;
    private javax.swing.JTextPane txtDescription;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
