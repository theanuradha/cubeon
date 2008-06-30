/*
 * ConfigurationHandlerImpl.java
 *
 * Created on May 19, 2008, 2:49 PM
 */
package org.netbeans.cubeon.jira.repository.ui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskRepositoryProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Anuradha G
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {

    private JiraTaskRepositoryProvider repositoryProvider;
    private JiraTaskRepository repository;

    /** Creates new form ConfigurationHandlerImpl */
    private ConfigurationHandlerImpl() {
        initComponents();
        jProgressBar1.setVisible(false);
    }

    public ConfigurationHandlerImpl(JiraTaskRepositoryProvider repositoryProvider) {
        this();
        this.repositoryProvider = repositoryProvider;
    }

    public void setTaskRepository(TaskRepository taskRepository) {

        repository = taskRepository.getLookup().lookup(JiraTaskRepository.class);
        if (repository != null) {
            txtId.setText(repository.getURL());
            txtName.setText(taskRepository.getName());
            txtUiserId.setText(repository.getUserName());
            txtPassword.setText(repository.getPassword());
        } else {
            txtName.requestFocus();
        }

    }

    public TaskRepository getTaskRepository() {
        if (repository == null) {
            repository = new JiraTaskRepository(repositoryProvider, txtName.getText().trim().toLowerCase(), txtName.getText().trim(), txtName.getText().trim());
        }
        repository.setName(txtName.getText().trim());

        repository.setUserName(txtUiserId.getText().trim());
        repository.setPassword(new String(txtPassword.getPassword()));
        repository.setURL(txtId.getText().trim());
        return repository;
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

        txtId = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtUiserId = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblRepo_id = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblUserID = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblNotify = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        btnValidate = new javax.swing.JButton();

        lblName.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblName.text")); // NOI18N

        lblRepo_id.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblRepo_id.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel1.text","-")); // NOI18N

        lblUserID.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblUserID.text","-")); // NOI18N

        lblPassword.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblPassword.text","-")); // NOI18N

        jPanel1.setLayout(new java.awt.BorderLayout());

        lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblNotify.text","-")); // NOI18N
        jPanel1.add(lblNotify, java.awt.BorderLayout.CENTER);

        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(146, 14));
        jPanel1.add(jProgressBar1, java.awt.BorderLayout.SOUTH);

        btnValidate.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.btnValidate.text","-")); // NOI18N
        btnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidateActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(lblRepo_id, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(lblUserID)
                                .add(28, 28, 28))
                            .add(layout.createSequentialGroup()
                                .add(lblPassword)
                                .add(18, 18, 18)))
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtPassword)
                            .add(txtUiserId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                    .add(btnValidate)
                    .add(jLabel1)
                    .add(txtId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblRepo_id)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblUserID)
                        .add(txtUiserId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblPassword)
                            .add(txtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 90, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnValidate)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed

        jProgressBar1.setVisible(true);
        btnValidate.setEnabled(false);

        RequestProcessor.getDefault().post(new Runnable() {                                                

            public void run() {
                try {
                    new JiraSession(txtId.getText().trim(),
                            txtUiserId.getText().trim(), new String(txtPassword.getPassword()));
                    lblNotify.setText("Configuration valid.");                                           
                    lblNotify.setForeground(Color.blue);//GEN-HEADEREND:event_btnValidateActionPerformed
                } catch (JiraException ex) {
                    lblNotify.setText("<html>" + ex.getMessage() + "</html>");
                    lblNotify.setForeground(Color.RED);//GEN-LAST:event_btnValidateActionPerformed
                } finally {
                    jProgressBar1.setVisible(false);
                    btnValidate.setEnabled(true);
                }
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNotify;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRepo_id;
    private javax.swing.JLabel lblUserID;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUiserId;
    // End of variables declaration//GEN-END:variables
}
