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
package org.netbeans.cubeon.bugzilla.core.repository.ui;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.bugzilla.api.BugzillaClient;
import org.netbeans.cubeon.bugzilla.api.MixedModeBugzillaClientImpl;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepository;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepositoryProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepositoryType.ConfigurationHandler;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Configuration handler is responisble for all repository configuration related tasks
 * like repository adding, connection verification.
 * 
 * @author radoslaw.holewa
 */
public class ConfigurationHandlerImpl extends javax.swing.JPanel implements ConfigurationHandler {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private BugzillaTaskRepositoryProvider repositoryProvider;
    private BugzillaTaskRepository repository;
    private boolean valid = false;

    /** Creates new form ConfigurationHandler */
    public ConfigurationHandlerImpl() {
        initComponents();
        jProgressBar1.setVisible(false);
    }

    /**
     * One-argument constructor, it creates instance of configuration handler and initializes task repository provider.
     *
     * @param bugzillaTaskRepositoryProvider - task repository provider which will be used to manage repository.
     */
    public ConfigurationHandlerImpl(BugzillaTaskRepositoryProvider bugzillaTaskRepositoryProvider) {
        this();
        this.repositoryProvider = bugzillaTaskRepositoryProvider;
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
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtUiserId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        btnValidate = new javax.swing.JButton();
        lblNotify = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(441, 393));

        lblName.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblName.text")); // NOI18N

        lblRepo_id.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblRepo_id.text")); // NOI18N

        jLabel2.setForeground(javax.swing.UIManager.getDefaults().getColor("Label.disabledForeground"));
        jLabel2.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel2.text", new Object[] {})); // NOI18N

        jLabel1.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel1.text","-")); // NOI18N

        lblUserID.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblUserID.text","-")); // NOI18N

        lblPassword.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.lblPassword.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "ConfigurationHandlerImpl.jLabel3.text")); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(146, 14));

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
                    .addComponent(btnValidate)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRepo_id, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUserID)
                        .addGap(25, 25, 25))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUiserId, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(328, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNotify, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRepo_id)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUiserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lblNotify, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnValidate)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        try {
            URLDisplayer.getDefault().
                    showURL(new URL(
                    "http://code.google.com/p/cubeon/wiki/BugzillaSupport"));//NOI18N
        } catch (MalformedURLException ex) {
            //ignore
        }
}//GEN-LAST:event_jLabel3MouseClicked

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed
        jProgressBar1.setVisible(true);
        btnValidate.setEnabled(false);
        valid = false;
        lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Validting"));
        lblNotify.setForeground(Color.blue);
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                try {
                    String url = txtId.getText().trim();
                    BugzillaClient client = new MixedModeBugzillaClientImpl(url, txtUiserId.getText().trim(), new String(txtPassword.getPassword()));
                    lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Configuration_Valid"));
                    lblNotify.setForeground(Color.blue);
                    valid = true;
                } catch (BugzillaException ex) {
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

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setTaskRepository(TaskRepository taskRepository) {
        repository = taskRepository.getLookup().lookup(BugzillaTaskRepository.class);
        if (repository != null) {
            txtId.setText(repository.getUrl());
            txtName.setText(taskRepository.getName());
            txtUiserId.setText(repository.getUsername());
            txtPassword.setText(repository.getPassword());

        } else {
            txtName.requestFocus();
        }
    }

    /**
     * {@inheritDoc}
     */
    public TaskRepository getTaskRepository() {
        if (repository == null) {
            //generate unique repository ID, the best solution is to get creation-time-related value
            String uniqueRepoId = String.valueOf(new Date().getTime());
            repository = new BugzillaTaskRepository(repositoryProvider, uniqueRepoId, txtName.getText().trim().toLowerCase(), txtName.getText().trim(), txtName.getText().trim());
        }
        repository.setName(txtName.getText().trim());
        repository.setDescription(txtName.getText().trim());
        repository.setUsername(txtUiserId.getText().trim());
        repository.setPassword(new String(txtPassword.getPassword()));
        String url = txtId.getText().trim();
        repository.setUrl(url);
        return repository;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidConfiguration() {
        if ((!valid) && lblNotify.getText().trim().length() == 0) {
            lblNotify.setText(NbBundle.getMessage(ConfigurationHandlerImpl.class, "LBL_Please_Validate_Configuration"));
            lblNotify.setForeground(Color.blue);
        }
        return valid;
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return this;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
