/*
 *  Copyright 2008 Tomas Knappek.
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
package org.netbeans.cubeon.javanet.ui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.kohsuke.jnt.JNProject;
import org.kohsuke.jnt.JavaNet;
import org.kohsuke.jnt.ProcessingException;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepositoryProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * @author Anuradha G
 * @author Tomas Knappek
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {

    private boolean _valid = false;
    private final Set<ChangeListener> _listeners = new HashSet<ChangeListener>(1);
    private JavanetTaskRepository _taskRepo = null;
    private JavanetTaskRepositoryProvider _taskRepoProvider = null;

    /** 
     * Creates new form ConfigurationHandlerImpl
     */
    public ConfigurationHandlerImpl() {
        initComponents();
        prbProgress.setVisible(false);
    }

    public ConfigurationHandlerImpl(JavanetTaskRepositoryProvider taskRepositoryProvider) {
        this();
        _taskRepoProvider = taskRepositoryProvider;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtUserId = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblUserID = new javax.swing.JLabel();
        prbProgress = new javax.swing.JProgressBar();
        btnValidate = new javax.swing.JButton();
        lblNotify = new javax.swing.JLabel();

        lblProjectName.setText(org.openide.util.NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblProjectName.text")); // NOI18N

        txtProjectName.setText(org.openide.util.NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.txtProjectName.text")); // NOI18N
        txtProjectName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNameActionPerformed(evt);
            }
        });

        jLabel1.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel1.text","-")); // NOI18N

        lblPassword.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblPassword.text","-")); // NOI18N

        lblUserID.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblUserID.text","-")); // NOI18N

        prbProgress.setIndeterminate(true);
        prbProgress.setPreferredSize(new java.awt.Dimension(146, 14));

        btnValidate.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.btnValidate.text","-")); // NOI18N
        btnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidateActionPerformed(evt);
            }
        });

        lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblNotify.text","-")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProjectName, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                            .addComponent(lblProjectName))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(143, 143, 143))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblUserID)
                                .addGap(26, 26, 26))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPassword)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPassword)
                            .addComponent(txtUserId, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                        .addContainerGap(157, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(prbProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(13, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnValidate)
                        .addContainerGap(261, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNotify, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(16, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProjectName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addGap(4, 4, 4)
                .addComponent(lblNotify, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prbProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnValidate)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtProjectNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNameActionPerformed

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed

        prbProgress.setVisible(true);
        btnValidate.setEnabled(false);
        _valid = false;
        lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Validating"));
        lblNotify.setForeground(Color.blue);
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                try {
                    String username = txtUserId.getText();
                    String password = new String(txtPassword.getPassword());
                    JavaNet javaNet = JavaNet.connect(username, password);

                    JNProject jnProject = javaNet.getProject(txtProjectName.getText());

                    lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Valid"));
                    lblNotify.setForeground(Color.blue);

                    _taskRepo = new JavanetTaskRepository(_taskRepoProvider, javaNet, jnProject, username, password);
                    
                    _valid = true;
                } catch (ProcessingException ex) {
                    lblNotify.setText("<html>" + ex.getMessage() + "</html>");
                    lblNotify.setForeground(Color.RED);
                    _valid = false;
                } finally {
                    prbProgress.setVisible(false);
                    btnValidate.setEnabled(true);
                    fireChangeEvent();
                }
            }
        });
    }//GEN-LAST:event_btnValidateActionPerformed

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (_listeners) {
            it = new HashSet<ChangeListener>(_listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblNotify;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel lblUserID;
    private javax.swing.JProgressBar prbProgress;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtUserId;
    // End of variables declaration//GEN-END:variables

    public void addChangeListener(ChangeListener changeListener) {
        synchronized (_listeners) {
            _listeners.add(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (_listeners) {
            _listeners.remove(changeListener);
        }
    }

    public boolean isValidConfiguration() {
        if ((!_valid) && lblNotify.getText().trim().length() == 0) {
            lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Please_Validate_Configuration"));
            lblNotify.setForeground(Color.blue);
        }

        return _valid;
    }

    public JComponent getComponent() {
        return this;
    }

    public void setTaskRepository(TaskRepository taskRepository) {

        _taskRepo = taskRepository.getLookup().lookup(JavanetTaskRepository.class);
        if (_taskRepo != null) {
            txtProjectName.setText(_taskRepo.getName());
            txtUserId.setText(_taskRepo.getUserName());
            txtPassword.setText(_taskRepo.getPassword());

        } else {
            txtProjectName.requestFocus();
        }

    }

    public TaskRepository getTaskRepository() {
        return _taskRepo;
    }

}
