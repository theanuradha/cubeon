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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.cubeon.jira.repository.JiraKeys;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.JiraRepositoryAttributes;
import org.netbeans.cubeon.jira.repository.JiraTaskPriorityProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskResolutionProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskType;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.jira.tasks.actions.OpenInBrowserTaskAction;
import org.netbeans.cubeon.jira.tasks.actions.OpenTaskHistoryAction;
import org.netbeans.cubeon.jira.tasks.actions.SubmitTaskAction;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskEditorPanels extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private JiraTask task;
    private JiraAction defaultStatus;
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
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
    ItemListener actionitemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        loadAction((JiraAction) cmbActions.getSelectedItem());
                    }
                });
            }
        }
    };
    ListSelectionListener listSelectionListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {

            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    fireChangeEvent();
                }
            });
        }
    };
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final OpenTaskHistoryAction openTaskHistoryAction;
    private final SubmitTaskAction submitTaskAction;

    /** Creates new form TaskEditorUI */
    public JiraTaskEditorPanels(JiraTask jiraTask) {
        this.task = jiraTask;
        initComponents();
        openInBrowserTaskAction = new OpenInBrowserTaskAction(jiraTask);
        openTaskHistoryAction = new OpenTaskHistoryAction(jiraTask);
        submitTaskAction = new SubmitTaskAction(jiraTask);
        refresh();
    }

    private void loadAttributes(JiraTaskRepository taskRepository) {

        JiraRepositoryAttributes attributes = taskRepository.getRepositoryAttributes();

        cmbProject.removeAllItems();
        List<JiraProject> projects = attributes.getProjects();
        for (JiraProject project : projects) {
            cmbProject.addItem(project);
        }
        if (task.getProject() != null) {
            cmbProject.setSelectedItem(task.getProject());
            loadProject(task.getProject());
            selectItems(lstFixVersion, task.getFixVersions());
            selectItems(lstAffectVersion, task.getAffectedVersions());
            selectItems(lstComponents, task.getComponents());
        } else {
            cmbProject.setSelectedIndex(-1);
        }
        cmbPriority.removeAllItems();

        JiraTaskPriorityProvider jtpp = taskRepository.getJiraTaskPriorityProvider();
        for (TaskPriority priority : jtpp.getTaskPriorities()) {
            cmbPriority.addItem(priority);
        }
        cmbPriority.setSelectedItem(task.getPriority());



        cmbType.removeAllItems();

        JiraTaskTypeProvider jttp = taskRepository.getJiraTaskTypeProvider();
        for (JiraTaskType type : jttp.getJiraTaskTypes()) {
            if (!type.isSubTask() && task.getProject().isTypesSupported(type)) {
                cmbType.addItem(type);
            }
        }
        if (task.getType() != null && jttp.getTaskTypeById(task.getType().getId()).isSubTask()) {
            cmbType.removeAllItems();
            cmbType.addItem(task.getType());
            cmbType.setEnabled(false);
        }
        cmbType.setSelectedItem(task.getType());

        JiraTaskResolutionProvider jtrp = taskRepository.getJiraTaskResolutionProvider();
        cmbResolution.removeAllItems();
        for (TaskResolution resolution : jtrp.getTaskResolutiones()) {

            cmbResolution.addItem(resolution);
        }
        if (task.getResolution() == null) {
            cmbResolution.setSelectedIndex(-1);
        } else {
            cmbResolution.setSelectedItem(task.getResolution());
        }
        cmbActions.removeAllItems();
        List<JiraAction> actions = task.getActions();
        defaultStatus = new JiraAction("##", "Leave as " + (task.getStatus() != null ? task.getStatus().getText() : "Local Task"));
        cmbActions.addItem(defaultStatus);
        for (JiraAction action : actions) {
            cmbActions.addItem(action);
        }
        if (task.getAction() != null) {
            cmbActions.setSelectedItem(task.getAction());
        } else {
            cmbActions.setSelectedItem(defaultStatus);
        }


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
        list.ensureIndexIsVisible(list.getSelectedIndex());
    }

    private void loadAction(JiraAction action) {
        if (action == null || action.equals(defaultStatus)) {
            cmbResolution.setEnabled(false);
            if (task.getResolution() == null) {
                cmbResolution.setSelectedIndex(-1);
            } else {
                cmbResolution.setSelectedItem(task.getResolution());
                //if task completed disable assignee
                txtAssignee.setEditable(false);
            }

        } else {
            List<String> filedIds = action.getFiledIds();
            if (filedIds.contains(JiraKeys.RESOLUTION)) {
                cmbResolution.setEnabled(true);
                if (cmbResolution.getSelectedIndex() == -1 && cmbResolution.getItemCount() > 0) {
                    cmbResolution.setSelectedIndex(0);
                }
            } else {
                cmbResolution.setEnabled(false);
                cmbResolution.setSelectedIndex(-1);
            }
            txtAssignee.setEditable(filedIds.contains(JiraKeys.ASSIGNEE));
            lstFixVersion.setEnabled(filedIds.contains(JiraKeys.FIX_VERSIONS));
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

    public JiraTask save() {

        if (!task.getPriority().equals(cmbPriority.getSelectedItem())) {
            task.setPriority((TaskPriority) cmbPriority.getSelectedItem());
        }
        if (!txtAssignee.getText().trim().equals(task.getAssignee())) {
            task.setAssignee(txtAssignee.getText().trim());
        }

        task.setEnvironment(txtEnvironment.getText().trim());
        if (!task.getType().equals(cmbType.getSelectedItem())) {
            task.setType((TaskType) cmbType.getSelectedItem());
        }
        if (task.getResolution() == null || !task.getResolution().equals(cmbResolution.getSelectedItem())) {
            task.setResolution((TaskResolution) cmbResolution.getSelectedItem());
        }
        if (task.getProject() == null || !task.getProject().equals(cmbProject.getSelectedItem())) {
            task.setProject((JiraProject) cmbProject.getSelectedItem());
        }

        Object action = cmbActions.getSelectedItem();
        if (action == null || !action.equals(defaultStatus)) {
            task.setAction((JiraAction) action);
        }
        List<JiraProject.Component> components = new ArrayList<JiraProject.Component>();
        Object[] selectedValues = lstComponents.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Component) {
                components.add((Component) object);
            }
        }
        task.setComponents(components);
        List<JiraProject.Version> affectedVersions = new ArrayList<JiraProject.Version>();
        selectedValues = lstAffectVersion.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                affectedVersions.add((Version) object);
            }
        }
        task.setAffectedVersions(affectedVersions);
        List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
        selectedValues = lstFixVersion.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                fixVersions.add((Version) object);
            }
        }
        task.setFixVersions(fixVersions);
        //set as modified if already or actuvaly modified 
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }
        task.getTaskRepository().persist(task);
        submitTaskAction.setEnabled(task.isModifiedFlag());

        return task;
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
        modifiedFlag.set(true);
        submitTaskAction.setEnabled(true);
    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                openTaskHistoryAction,
                submitTaskAction);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlActionAndPeople = new javax.swing.JPanel();
        lblResolution = new javax.swing.JLabel();
        cmbActions = new javax.swing.JComboBox();
        lblReportedBy = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAssignee = new javax.swing.JTextField();
        lblAction = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbResolution = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        cmbPriority = new javax.swing.JComboBox();
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

        pnlActionAndPeople.setBackground(new java.awt.Color(255, 255, 255));

        lblResolution.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblResolution.text")); // NOI18N

        lblReportedBy.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblReportedBy.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.jLabel3.text","-")); // NOI18N

        lblAction.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "TaskEditorUI.lblStatus.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.jLabel1.text","-")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlActionAndPeopleLayout = new org.jdesktop.layout.GroupLayout(pnlActionAndPeople);
        pnlActionAndPeople.setLayout(pnlActionAndPeopleLayout);
        pnlActionAndPeopleLayout.setHorizontalGroup(
            pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActionAndPeopleLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblAction, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblResolution, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .add(10, 10, 10)
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(cmbResolution, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cmbActions, 0, 156, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblReportedBy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtAssignee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        pnlActionAndPeopleLayout.setVerticalGroup(
            pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActionAndPeopleLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAction)
                    .add(cmbActions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(lblReportedBy))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlActionAndPeopleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblResolution)
                    .add(cmbResolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(txtAssignee, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setBackground(new java.awt.Color(255, 255, 255));
        setName(NbBundle.getMessage(JiraTaskEditorPanels.class, "LBL_Primary_Details","-")); // NOI18N

        lblPriority.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        lblType.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "TaskEditorUI.lblType.text")); // NOI18N

        lblProject.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblProject.text")); // NOI18N

        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        lblEnvironment.setForeground(new java.awt.Color(51, 51, 51));
        lblEnvironment.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblEnvironment.text")); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(txtEnvironment);

        lblAffects.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblAffects.text")); // NOI18N

        lblFixVersion.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblFixVersion.text")); // NOI18N

        jScrollPane2.setViewportView(lstAffectVersion);

        jScrollPane3.setViewportView(lstFixVersion);

        lblComponents.setText(NbBundle.getMessage(JiraTaskEditorPanels.class, "JiraTaskEditorPanels.lblComponents.text")); // NOI18N

        jScrollPane4.setViewportView(lstComponents);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblFixVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblAffects, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblComponents, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblPriority, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblProject, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .add(lblEnvironment, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, cmbProject, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, cmbType, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, cmbPriority, 0, 174, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 320, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProject)
                    .add(cmbProject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblType)
                    .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPriority)
                    .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblComponents)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblAffects)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblFixVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblEnvironment)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(16, 16, 16))
        );

        layout.linkSize(new java.awt.Component[] {jScrollPane2, jScrollPane3}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProjectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbActions;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JComboBox cmbResolution;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAction;
    private javax.swing.JLabel lblAffects;
    private javax.swing.JLabel lblComponents;
    private javax.swing.JLabel lblEnvironment;
    private javax.swing.JLabel lblFixVersion;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblType;
    private javax.swing.JList lstAffectVersion;
    private javax.swing.JList lstComponents;
    private javax.swing.JList lstFixVersion;
    private javax.swing.JPanel pnlActionAndPeople;
    private javax.swing.JTextField txtAssignee;
    private javax.swing.JEditorPane txtEnvironment;
    // End of variables declaration//GEN-END:variables

    public void refresh() {

        txtAssignee.getDocument().removeDocumentListener(documentListener);

        txtEnvironment.getDocument().removeDocumentListener(documentListener);

        cmbPriority.removeItemListener(itemListener);
        cmbActions.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);
        cmbProject.removeItemListener(itemListener);
        cmbProject.removeItemListener(projectitemListener);
        cmbActions.removeItemListener(actionitemListener);
        cmbResolution.removeItemListener(itemListener);


        lstAffectVersion.getSelectionModel().removeListSelectionListener(listSelectionListener);
        lstFixVersion.getSelectionModel().removeListSelectionListener(listSelectionListener);
        lstComponents.getSelectionModel().removeListSelectionListener(listSelectionListener);



        txtEnvironment.setText(task.getEnvironment());
        lblReportedBy.setText(task.getReporter());
        txtAssignee.setText(task.getAssignee());

        JiraTaskRepository taskRepository = task.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        loadAttributes(taskRepository);


        txtAssignee.getDocument().addDocumentListener(documentListener);

        txtEnvironment.getDocument().addDocumentListener(documentListener);

        cmbPriority.addItemListener(itemListener);
        cmbActions.addItemListener(itemListener);
        cmbType.addItemListener(itemListener);
        cmbProject.addItemListener(itemListener);
        cmbProject.addItemListener(projectitemListener);
        cmbActions.addItemListener(actionitemListener);
        cmbResolution.addItemListener(itemListener);


        lstAffectVersion.getSelectionModel().addListSelectionListener(listSelectionListener);
        lstFixVersion.getSelectionModel().addListSelectionListener(listSelectionListener);
        lstComponents.getSelectionModel().addListSelectionListener(listSelectionListener);




        if (task.isLocal()) {
            cmbActions.setEnabled(false);
            cmbResolution.setEnabled(false);
            lstFixVersion.setEnabled(false);

        } else {
            cmbProject.setEnabled(false);
            cmbActions.setEnabled(true);

            validateFiledsForEdit(task);


        }

        loadAction(task.getAction());
        openInBrowserTaskAction.setEnabled(!task.isLocal());
        openTaskHistoryAction.setEnabled(!task.isLocal());
        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
    }

    JiraTask getJiraTask() {
        return task;
    }

    JComponent getActionAndPeoplePanel() {
        return pnlActionAndPeople;
    }
    JComponent getAttributesPanel() {
        return this;
    }

    private void validateFiledsForEdit(JiraTask jiraTask) {
        List<String> editFieldIds = jiraTask.getEditFieldIds();

        txtAssignee.setEditable(editFieldIds.contains(JiraKeys.ASSIGNEE));
        lstComponents.setEnabled(editFieldIds.contains(JiraKeys.COMPONENTS));
        lstAffectVersion.setEnabled(editFieldIds.contains(JiraKeys.VERSIONS));
        lstFixVersion.setEnabled(editFieldIds.contains(JiraKeys.FIX_VERSIONS));
        txtEnvironment.setEnabled(editFieldIds.contains(JiraKeys.ENVIRONMENT));
        cmbPriority.setEnabled(editFieldIds.contains(JiraKeys.PRIORITY));
        cmbType.setEnabled(editFieldIds.contains(JiraKeys.TYPE));
    }

    public DocumentListener getDocumentListener() {
        return documentListener;
    }

}
