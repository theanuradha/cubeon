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
        txtUrl.setText(localTask.getUrlString());
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

        cmbType.removeAllItems();

        LocalTaskTypeProvider localTaskTypeProvider = taskRepository.getLocalTaskTypeProvider();
        for (TaskType type : localTaskTypeProvider.getTaskTypes()) {
            cmbType.addItem(type);
        }

        cmbType.setSelectedItem(localTask.getType());
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
        txtUrl.getDocument().addDocumentListener(documentListener);

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
        cmbType.addItemListener(itemListener);
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
        if (!localTask.getType().equals(cmbType.getSelectedItem())) {
            localTask.setType((TaskType) cmbType.getSelectedItem());
        }
        localTask.setUrlString(txtUrl.getText().trim());
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
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        UrlTools = new javax.swing.JToolBar();
        Open = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        lblPriority.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        lblStatus.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblStatus.text")); // NOI18N

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblDesription.text")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setViewportView(txtDescription);

        lblType.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblType.text")); // NOI18N

        lblUrl.setText(NbBundle.getMessage(TaskEditorUI.class, "TaskEditorUI.lblUrl.text")); // NOI18N

        UrlTools.setFloatable(false);
        UrlTools.setRollover(true);
        UrlTools.setOpaque(false);

        Open.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/local/web.png"))); // NOI18N
        Open.setFocusable(false);
        Open.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Open.setOpaque(false);
        Open.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        UrlTools.add(Open);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblDesription))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                    .add(lblUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(31, 31, 31)
                                        .add(lblPriority)
                                        .add(18, 18, 18)
                                        .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(29, 29, 29)
                                        .add(lblStatus)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 629, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(20, 20, 20)))
                .add(0, 0, 0))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(spDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 711, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {cmbPriority, cmbStatus, cmbType}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblType)
                            .add(lblPriority)
                            .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblStatus)
                            .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblUrl)
                            .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblDesription))
                    .add(layout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(103, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {cmbPriority, cmbStatus, cmbType}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Open;
    private javax.swing.JToolBar UrlTools;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtOutline;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
