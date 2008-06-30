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
package org.netbeans.cubeon.jira.tasks.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.cubeon.jira.repository.JiraTaskPriorityProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskStatusProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskEditorUI extends javax.swing.JPanel implements EditorAttributeHandler {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private JiraTask jiraTask;

    /** Creates new form TaskEditorUI */
    public JiraTaskEditorUI(JiraTask jiraTask) {
        this.jiraTask = jiraTask;
        initComponents();
        txtOutline.setText(jiraTask.getName());
        txtDescription.setText(jiraTask.getDescription());
        txtUrl.setText(jiraTask.getUrlString());
        loadDates(jiraTask);
        cmbPriority.removeAllItems();
        JiraTaskRepository taskRepository = jiraTask.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        JiraTaskPriorityProvider jtpp = taskRepository.getJiraTaskPriorityProvider();
        for (TaskPriority priority : jtpp.getTaskPrioritys()) {
            cmbPriority.addItem(priority);
        }
        cmbPriority.setSelectedItem(jiraTask.getPriority());

        cmbStatus.removeAllItems();
        JiraTaskStatusProvider statusProvider = taskRepository.getJiraTaskStatusProvider();
        for (TaskStatus status : statusProvider.getStatusList()) {
            cmbStatus.addItem(status);
        }
        cmbStatus.setSelectedItem(jiraTask.getStatus());

        cmbType.removeAllItems();

        JiraTaskTypeProvider localTaskTypeProvider = taskRepository.getJiraTaskTypeProvider();
        for (TaskType type : localTaskTypeProvider.getTaskTypes()) {
            cmbType.addItem(type);
        }

        cmbType.setSelectedItem(jiraTask.getType());
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
        return jiraTask.getName();
    }

    public String getDisplayName() {
        return jiraTask.getId();
    }

    public String getShortDescription() {
        return jiraTask.getName();
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
        if (!txtOutline.getText().trim().equals(jiraTask.getName())) {
            jiraTask.setName(txtOutline.getText().trim());
        }
        if (!jiraTask.getPriority().equals(cmbPriority.getSelectedItem())) {
            jiraTask.setPriority((TaskPriority) cmbPriority.getSelectedItem());
        }
        if (!jiraTask.getStatus().equals(cmbStatus.getSelectedItem())) {
            jiraTask.setStatus((TaskStatus) cmbStatus.getSelectedItem());
        }
        if (!jiraTask.getDescription().equals(txtDescription.getText().trim())) {
            jiraTask.setDescription(txtDescription.getText().trim());
        }
        if (!jiraTask.getType().equals(cmbType.getSelectedItem())) {
            jiraTask.setType((TaskType) cmbType.getSelectedItem());
        }
        jiraTask.setUrlString(txtUrl.getText().trim());
        jiraTask.getTaskRepository().persist(jiraTask);
        loadDates(jiraTask);
        return jiraTask;
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

    private void loadDates(JiraTask jiraTask) {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (jiraTask.getCreated() != null) {
            String message = NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblCreated.text", dateFormat.format(jiraTask.getCreated()));
            lblCreated.setText(message);
        }
        if (jiraTask.getUpdated() != null) {
            String message = NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblUpdated.text", dateFormat.format(jiraTask.getUpdated()));
            lblUpdated.setText(message);
        }

    }

    private void showURL() {
        try {
            URLDisplayer.getDefault().showURL(new URL(txtUrl.getText()));

        } catch (MalformedURLException ex) {
            NotifyDescriptor d =
                    new NotifyDescriptor.Message(NbBundle.getMessage(JiraTaskEditorUI.class,
                    "LBL_Open_Url_Error",
                    txtUrl.getText()), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
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

        lblPriority = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        txtOutline = new javax.swing.JTextField();
        lblDesription = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        UrlTools = new javax.swing.JToolBar();
        Open =         new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent evt) {

                showURL();

            }
        });
        lblType = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        cmbType = new javax.swing.JComboBox();
        lblUrl = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        cmbStatus = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        lblCreated = new javax.swing.JLabel();
        lblUpdated = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        lblPriority.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setViewportView(txtDescription);

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblDesription.text")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.jLabel1.text")); // NOI18N

        UrlTools.setFloatable(false);
        UrlTools.setRollover(true);
        UrlTools.setOpaque(false);

        Open.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/cubeon/jira/web.png"))); // NOI18N
        Open.setFocusable(false);
        Open.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Open.setOpaque(false);
        Open.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        UrlTools.add(Open);

        lblType.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblType.text")); // NOI18N

        lblUrl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setForeground(new java.awt.Color(51, 51, 51));
        lblUrl.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblUrl.text")); // NOI18N

        lblStatus.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblStatus.text")); // NOI18N

        lblCreated.setForeground(new java.awt.Color(102, 102, 102));
        lblCreated.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblCreated.text","-")); // NOI18N

        lblUpdated.setForeground(new java.awt.Color(102, 102, 102));
        lblUpdated.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblUpdated.text","-")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblPriority)
                            .add(lblType))
                        .add(28, 28, 28)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(cmbPriority, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cmbType, 0, 116, Short.MAX_VALUE))
                        .add(50, 50, 50)
                        .add(lblStatus)
                        .add(18, 18, 18)
                        .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(txtUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(lblUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblDesription))
                    .add(layout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(lblCreated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 211, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblUpdated, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                        .add(218, 218, 218)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUpdated)
                    .add(lblCreated))
                .add(7, 7, 7)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblType)
                            .add(lblStatus)
                            .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblPriority))
                    .add(layout.createSequentialGroup()
                        .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(26, 26, 26)
                .add(lblUrl)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(lblDesription))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(UrlTools, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(5, 5, 5)
                .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Open;
    private javax.swing.JToolBar UrlTools;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtOutline;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
