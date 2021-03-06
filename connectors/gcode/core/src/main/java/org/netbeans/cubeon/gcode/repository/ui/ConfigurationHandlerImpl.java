/*
 * ConfigurationHandlerImpl.java
 *
 * Created on May 19, 2008, 2:49 PM
 */
package org.netbeans.cubeon.gcode.repository.ui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.gcode.api.GCodeClient;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeSession;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepositoryProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Anuradha G
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {
    private static final long serialVersionUID = 3209605499584698376L;

    private GCodeTaskRepositoryProvider repositoryProvider;
    private GCodeTaskRepository repository;
    private boolean valid = false;

    /** Creates new form ConfigurationHandlerImpl */
    private ConfigurationHandlerImpl() {
        initComponents();
        jProgressBar1.setVisible(false);
    }

    public ConfigurationHandlerImpl(GCodeTaskRepositoryProvider repositoryProvider) {
        this();
        this.repositoryProvider = repositoryProvider;
    }

    public void setTaskRepository(TaskRepository taskRepository) {

        repository = taskRepository.getLookup().lookup(GCodeTaskRepository.class);
        if (repository != null) {
            txtId.setText(repository.getProject());
            txtName.setText(taskRepository.getName());
            txtUiserId.setText(repository.getUser());
            txtPassword.setText(repository.getPassword());

        }
        txtName.requestFocusInWindow();
    }

    public TaskRepository getTaskRepository() {
        if (repository == null) {
            repository = new GCodeTaskRepository(repositoryProvider, txtName.getText().trim().toLowerCase(), txtName.getText().trim(), txtName.getText().trim());
        }
        repository.setName(txtName.getText().trim());

        repository.setUser(txtUiserId.getText().trim());
        repository.setPassword(new String(txtPassword.getPassword()));
        String project = txtId.getText().trim();


        repository.setProject(project);
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
        if ((!valid) && lblNotify.getText().trim().length() == 0) {
            lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Please_Validate_Configuration"));
            lblNotify.setForeground(Color.blue);
        }

        return valid;
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

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblRepo_id = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblUserID = new javax.swing.JLabel();
        txtUiserId = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        lblNotify = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        btnValidate = new javax.swing.JButton();

        lblName.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblName.text")); // NOI18N

        lblRepo_id.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblRepo_id.text")); // NOI18N

        jLabel2.setForeground(javax.swing.UIManager.getDefaults().getColor("Label.disabledForeground"));
        jLabel2.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel2.text", new Object[] {})); // NOI18N

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(lblRepo_id, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(btnValidate)
                            .add(txtId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblUserID)
                                    .add(lblPassword))
                                .add(30, 30, 30)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(txtPassword)
                                    .add(txtUiserId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jLabel1))))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblUserID)
                        .add(txtUiserId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblPassword))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 80, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnValidate)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed

        jProgressBar1.setVisible(true);
        btnValidate.setEnabled(false);
        valid = false;
        lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Validting"));
        lblNotify.setForeground(Color.blue);
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                try {
                    String project = txtId.getText().trim();
                    GCodeSession session = Lookup.getDefault().lookup(GCodeClient.class).
                            createSession(project, txtUiserId.getText().trim(),
                            new String(txtPassword.getPassword()));



                    lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Valid"));
                    lblNotify.setForeground(Color.blue);
                    valid = true;
                } catch (GCodeException ex) {
                    lblNotify.setText("<html>" + ex.getMessage() + "</html>");
                    lblNotify.setForeground(Color.RED);
                    valid = false;
                } finally {
                    jProgressBar1.setVisible(false);
                    btnValidate.setEnabled(true);
                    fireChangeEvent();
                }
            }
        });

    }//GEN-LAST:event_btnValidateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
