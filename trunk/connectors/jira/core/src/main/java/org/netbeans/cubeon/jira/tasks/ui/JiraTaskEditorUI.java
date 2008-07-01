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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.cubeon.jira.repository.JiraRepositoryAttributes;
import org.netbeans.cubeon.jira.repository.JiraTaskPriorityProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskResolutionProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskStatusProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
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
        txtEnvironment.setText(jiraTask.getEnvironment());
        loadDates(jiraTask);
        loadAttributes();
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

        ItemListener projectitemListener = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            loadProject((JiraProject) cmbProject.getSelectedItem());
                        }
                    });
                }
            }
        };
        cmbPriority.addItemListener(itemListener);
        cmbStatus.addItemListener(itemListener);
        cmbType.addItemListener(itemListener);
        cmbProject.addItemListener(itemListener);
        cmbProject.addItemListener(projectitemListener);
        cmbResolution.addItemListener(itemListener);

    }

    private void loadAttributes() {
        JiraTaskRepository taskRepository = jiraTask.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        JiraRepositoryAttributes attributes = taskRepository.getRepositoryAttributes();

        cmbProject.removeAllItems();
        List<JiraProject> projects = attributes.getProjects();
        for (JiraProject project : projects) {
            cmbProject.addItem(project);
        }
        if (jiraTask.getProject() != null) {
            cmbProject.setSelectedItem(jiraTask.getProject());
            loadProject(jiraTask.getProject());
            selectItems(lstFixVersion, jiraTask.getFixVersions());
            selectItems(lstAffectVersion, jiraTask.getAffectedVersions());
            selectItems(lstComponents, jiraTask.getComponents());
        } else {
            cmbProject.setSelectedIndex(-1);
        }
        cmbPriority.removeAllItems();

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

        JiraTaskTypeProvider jttp = taskRepository.getJiraTaskTypeProvider();
        for (TaskType type : jttp.getTaskTypes()) {
            cmbType.addItem(type);
        }

        cmbType.setSelectedItem(jiraTask.getType());

        JiraTaskResolutionProvider jtrp = taskRepository.getJiraTaskResolutionProvider();
        cmbResolution.removeAllItems();
        for (TaskResolution resolution : jtrp.getTaskResolutiones()) {

            cmbResolution.addItem(resolution);
        }
        if (jiraTask.getResolution() == null) {
            cmbResolution.setSelectedIndex(-1);
        } else {
            cmbResolution.setSelectedItem(jiraTask.getResolution());
        }
    //cmbResolution.setSelectedItem(jiraTask.);
    }

    private void selectItems(JList list, List<? extends Object> objects) {

        DefaultListSelectionModel dlsm = (DefaultListSelectionModel) list.getSelectionModel();
        dlsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dlsm.setLeadAnchorNotificationEnabled(true);

        ListModel lm = list.getModel();
        if (lm instanceof DefaultListModel) {
            DefaultListModel model = (DefaultListModel) lm;

            for (Object object : objects) {
                int indexOf = model.indexOf(object);
                list.addSelectionInterval(indexOf, indexOf);
            }
        }
    }

    private void loadProject(JiraProject project) {
        //clear all first
        DefaultListModel componentModel = new DefaultListModel();
        DefaultListModel versionModel = new DefaultListModel();

        if (project != null) {
            List<Version> versions = project.getVersions();
            for (Version version : versions) {
                versionModel.addElement(version);
            }
            List<Component> components = project.getComponents();
            for (Component component : components) {
                componentModel.addElement(component);
            }
        }
        lstFixVersion.setModel(versionModel);
        lstAffectVersion.setModel(versionModel);
        lstComponents.setModel(componentModel);
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
        jiraTask.setEnvironment(txtEnvironment.getText().trim());
        if (!jiraTask.getType().equals(cmbType.getSelectedItem())) {
            jiraTask.setType((TaskType) cmbType.getSelectedItem());
        }
        if (jiraTask.getResolution() == null || !jiraTask.getResolution().equals(cmbResolution.getSelectedItem())) {
            jiraTask.setResolution((TaskResolution) cmbResolution.getSelectedItem());
        }
        if (jiraTask.getProject() == null || !jiraTask.getProject().equals(cmbProject.getSelectedItem())) {
            jiraTask.setProject((JiraProject) cmbProject.getSelectedItem());
        }
        List<JiraProject.Component> components = new ArrayList<JiraProject.Component>();
        Object[] selectedValues = lstComponents.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Component) {
                components.add((Component) object);
            }
        }
        jiraTask.setComponents(components);
        List<JiraProject.Version> affectedVersions = new ArrayList<JiraProject.Version>();
        selectedValues = lstAffectVersion.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                affectedVersions.add((Version) object);
            }
        }
        jiraTask.setAffectedVersions(affectedVersions);
        List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
        selectedValues = lstFixVersion.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                fixVersions.add((Version) object);
            }
        }
        jiraTask.setFixVersions(fixVersions);
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
        cmbResolution = new javax.swing.JComboBox();
        lblResolution = new javax.swing.JLabel();
        txtOutline = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox();
        lblDesription = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblAttributes = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        cmbPriority = new javax.swing.JComboBox();
        lblCreated = new javax.swing.JLabel();
        lblUpdated = new javax.swing.JLabel();
        lblProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        lblEnvironment = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEnvironment = new javax.swing.JEditorPane();
        lblAffects = new javax.swing.JLabel();
        lblFixVersion = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAffectVersion = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstFixVersion = new javax.swing.JList();
        lblComponents = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstComponents = new javax.swing.JList();
        lblDesription1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        lblPriority.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setViewportView(txtDescription);

        lblResolution.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblResolution.text")); // NOI18N

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblDesription.text")); // NOI18N

        lblStatus.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblStatus.text")); // NOI18N

        lblAttributes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAttributes.setForeground(new java.awt.Color(102, 102, 102));
        lblAttributes.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.jLabel1.text")); // NOI18N

        lblType.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblType.text")); // NOI18N

        lblCreated.setForeground(new java.awt.Color(102, 102, 102));
        lblCreated.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblCreated.text","-")); // NOI18N

        lblUpdated.setForeground(new java.awt.Color(102, 102, 102));
        lblUpdated.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblUpdated.text","-")); // NOI18N

        lblProject.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblProject.text")); // NOI18N

        lblEnvironment.setForeground(new java.awt.Color(51, 51, 51));
        lblEnvironment.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblEnvironment.text")); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(txtEnvironment);

        lblAffects.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblAffects.text")); // NOI18N

        lblFixVersion.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblFixVersion.text")); // NOI18N

        jScrollPane2.setViewportView(lstAffectVersion);

        jScrollPane3.setViewportView(lstFixVersion);

        lblComponents.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblComponents.text")); // NOI18N

        jScrollPane4.setViewportView(lstComponents);

        lblDesription1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription1.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription1.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblDesription1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                            .add(lblAttributes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblProject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblType)
                                    .add(lblPriority))
                                .add(40, 40, 40)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cmbProject, 0, 159, Short.MAX_VALUE)
                                    .add(cmbType, 0, 159, Short.MAX_VALUE)
                                    .add(cmbPriority, 0, 159, Short.MAX_VALUE))
                                .add(10, 10, 10)
                                .add(lblComponents)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(lblEnvironment)
                                .add(18, 18, 18)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblAffects)
                            .add(lblFixVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(8, 8, 8))
                    .add(layout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(lblCreated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblUpdated, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                            .add(lblDesription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblDesription1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(layout.createSequentialGroup()
                                        .add(lblResolution)
                                        .add(18, 18, 18)
                                        .add(cmbResolution, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .add(layout.createSequentialGroup()
                                        .add(lblStatus)
                                        .add(38, 38, 38)
                                        .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jScrollPane2, jScrollPane3}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCreated)
                    .add(lblUpdated))
                .add(7, 7, 7)
                .add(lblAttributes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(lblProject)
                                    .add(cmbProject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblComponents)
                                    .add(lblAffects))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblType))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblPriority)))
                            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblEnvironment)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(9, 9, 9))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblFixVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(36, 36, 36)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDesription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDesription1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStatus)
                    .add(cmbStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblResolution)
                    .add(cmbResolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(142, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {jScrollPane2, jScrollPane3}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JComboBox cmbResolution;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAffects;
    private javax.swing.JLabel lblAttributes;
    private javax.swing.JLabel lblComponents;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblDesription1;
    private javax.swing.JLabel lblEnvironment;
    private javax.swing.JLabel lblFixVersion;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JList lstAffectVersion;
    private javax.swing.JList lstComponents;
    private javax.swing.JList lstFixVersion;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JEditorPane txtEnvironment;
    private javax.swing.JTextField txtOutline;
    // End of variables declaration//GEN-END:variables
}
