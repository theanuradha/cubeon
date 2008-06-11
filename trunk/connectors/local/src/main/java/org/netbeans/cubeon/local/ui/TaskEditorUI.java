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

/*
 * TaskEditorUI.java
 *
 * Created on Jun 7, 2008, 4:49:17 PM
 */
package org.netbeans.cubeon.local.ui;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.local.repository.LocalTaskPriorityProvider;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.local.repository.LocalTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.netbeans.cubeon.tasks.spi.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.TaskPriority;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskType;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class TaskEditorUI extends javax.swing.JPanel implements EditorAttributeHandler {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private LocalTask localTask;

    /** Creates new form TaskEditorUI */
    public TaskEditorUI(LocalTask localTask) {
        this.localTask = localTask;
        initComponents();
        txtOutline.setText(localTask.getName());
        txtDescription.setText(localTask.getDescription());
        cmbPriority.removeAllItems();
        LocalTaskRepository taskRepository = localTask.getTaskRepository().getLookup().lookup(LocalTaskRepository.class);
        LocalTaskPriorityProvider ltpp = taskRepository.getLocalTaskPriorityProvider();
        for (TaskPriority priority : ltpp.getTaskPrioritys()) {
            cmbPriority.addItem(priority);
        }
        cmbPriority.setSelectedItem(localTask.getPriority());

        cmbStatus.removeAllItems();
        LocalTaskStatusProvider statusProvider = taskRepository.getLocalTaskStatusProvider();
        for (TaskStatus status : statusProvider.getStatusList()) {
            cmbStatus.addItem(status);
        }
        cmbStatus.setSelectedItem(localTask.getStatus());

        jComboBox1.removeAllItems();

        LocalTaskTypeProvider localTaskTypeProvider = taskRepository.getLocalTaskTypeProvider();
        for (TaskType type : localTaskTypeProvider.getTaskTypes()) {
            jComboBox1.addItem(type);
        }

        jComboBox1.setSelectedItem(localTask.getType());
        final DocumentListener documentListener = new DocumentListener() {

            public void insertUpdate(DocumentEvent arg0) {
                run();
            }

            public void removeUpdate(DocumentEvent arg0) {
                run();
            }

            public void changedUpdate(DocumentEvent arg0) {
                run();
            }

            private void run() {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        fireChangeEvent();
                    }
                });

            }
        };
        txtOutline.getDocument().addDocumentListener(documentListener);
        txtDescription.getDocument().addDocumentListener(documentListener);

        ItemListener itemListener = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            fireChangeEvent();
                        }
                    });
                }
            }
        };
        cmbPriority.addItemListener(itemListener);
        cmbStatus.addItemListener(itemListener);
        jComboBox1.addItemListener(itemListener);
    }

    @Override
    public String getName() {
        return localTask.getName();
    }

    public String getDisplayName() {
        return localTask.getName();
    }

    public String getShortDescription() {
        return localTask.getDescription();
    }

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

    public JComponent getComponent() {
        return this;
    }

    public TaskElement save() {
        if (!txtOutline.getText().trim().equals(localTask.getName())) {
            localTask.setName(txtOutline.getText().trim());
        }
        if (!localTask.getPriority().equals(cmbPriority.getSelectedItem())) {
            localTask.setPriority((TaskPriority) cmbPriority.getSelectedItem());
        }
        if (!localTask.getStatus().equals(cmbStatus.getSelectedItem())) {
            localTask.setStatus((TaskStatus) cmbStatus.getSelectedItem());
        }
        if (!localTask.getDescription().equals(txtDescription.getText().trim())) {
            localTask.setDescription(txtDescription.getText().trim());
        }
        if (!localTask.getType().equals(jComboBox1.getSelectedItem())) {
            localTask.setType((TaskType) jComboBox1.getSelectedItem());
        }
        localTask.getTaskRepository().persist(localTask);
        return localTask;
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

        txtOutline = new javax.swing.JTextField();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox();
        lblDesription = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(255, 255, 255));

        lblPriority.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        lblStatus.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblStatus.text")); // NOI18N

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblDesription.text")); // NOI18N

        spDescription.setViewportView(txtDescription);

        jLabel1.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(30, 30, 30)
                        .add(lblPriority)
                        .add(18, 18, 18)
                        .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(27, 27, 27)
                        .add(lblStatus)
                        .add(18, 18, 18)
                        .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                    .add(lblDesription)
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cmbPriority, cmbStatus, jComboBox1}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPriority)
                    .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblStatus)
                    .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(28, 28, 28)
                .add(lblDesription)
                .add(5, 5, 5)
                .add(spDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtOutline;
    // End of variables declaration//GEN-END:variables
}
