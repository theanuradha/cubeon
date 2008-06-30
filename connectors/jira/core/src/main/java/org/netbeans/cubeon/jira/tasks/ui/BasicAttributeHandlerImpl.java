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
package org.netbeans.cubeon.jira.tasks.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.jira.repository.JiraRepositoryAttributes;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.util.NbBundle;

/**
 *
 * @author  Anuradha G
 */
public class BasicAttributeHandlerImpl extends javax.swing.JPanel implements TaskEditorProvider.BasicAttributeHandler {

    private JiraTask jiraTask;
    private JiraTaskRepository jiraTaskRepository;

    /** Creates new form BasicAttributeHandlerImpl */
    private BasicAttributeHandlerImpl() {
        initComponents();
    }

    public BasicAttributeHandlerImpl(TaskElement taskElement) {
        this();
        jiraTask = taskElement.getLookup().lookup(JiraTask.class);
        assert jiraTask != null;
        jiraTaskRepository = jiraTask.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        assert jiraTaskRepository != null;
        JiraRepositoryAttributes attributes = jiraTaskRepository.getRepositoryAttributes();
        for (JiraProject project : attributes.getProjects()) {

            cmbProjects.addItem(project);
        }
        if (cmbProjects.getItemCount() > 0) {
            cmbProjects.setSelectedIndex(0);
        }
        
        JiraTaskTypeProvider typeProvider = jiraTaskRepository.getJiraTaskTypeProvider();
        for (TaskType taskType : typeProvider.getTaskTypes()) {
            cmbTypes.addItem(taskType);

        }
        if (cmbTypes.getItemCount() > 0) {
            cmbTypes.setSelectedIndex(0);
        }
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

        //to-do  return txtOutline.getText().trim().length() != 0;
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

    public JComponent getComponent() {
        return this;
    }

    public TaskElement getTaskElement() {

        jiraTask.setName(txtOutline.getText().trim());
        jiraTask.setDescription(txtDescription.getText().trim());
        jiraTask.setType((TaskType) cmbTypes.getSelectedItem());
        return jiraTask;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblOutline = new javax.swing.JLabel();
        txtOutline = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        cmbProjects = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbTypes = new javax.swing.JComboBox();

        lblOutline.setText(NbBundle.getMessage(BasicAttributeHandlerImpl.class, "BasicAttributeHandlerImpl.lblOutline.text")); // NOI18N

        lblDescription.setText(NbBundle.getMessage(BasicAttributeHandlerImpl.class, "BasicAttributeHandlerImpl.lblDescription.text")); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        jLabel1.setText(NbBundle.getMessage(BasicAttributeHandlerImpl.class, "BasicAttributeHandlerImpl.jLabel1.text","-")); // NOI18N

        jLabel2.setText(NbBundle.getMessage(BasicAttributeHandlerImpl.class, "BasicAttributeHandlerImpl.jLabel2.text","-")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cmbProjects, 0, 181, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                .add(38, 38, 38))
                            .add(layout.createSequentialGroup()
                                .add(cmbTypes, 0, 181, Short.MAX_VALUE)
                                .addContainerGap())))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                        .add(11, 11, 11))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cmbProjects, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cmbTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblOutline)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbProjects;
    private javax.swing.JComboBox cmbTypes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblOutline;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtOutline;
    // End of variables declaration//GEN-END:variables
}