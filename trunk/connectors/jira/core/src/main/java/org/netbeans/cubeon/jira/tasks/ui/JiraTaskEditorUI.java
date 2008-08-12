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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
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
public class JiraTaskEditorUI extends javax.swing.JPanel {

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private JiraTask jiraTask;
    private JiraAction defaultStatus;
    private final JiraCommentsEditor commentsEditor;
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
    public JiraTaskEditorUI(JiraTask jiraTask) {
        this.jiraTask = jiraTask;
        initComponents();
        commentsEditor = new JiraCommentsEditor(this);

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
        for (TaskPriority priority : jtpp.getTaskPriorities()) {
            cmbPriority.addItem(priority);
        }
        cmbPriority.setSelectedItem(jiraTask.getPriority());

        String message = NbBundle.getMessage(JiraTaskEditorUI.class,
                "JiraTaskEditorUI.lblStatus.text",
                jiraTask.getStatus() == null ? "Local" : jiraTask.getStatus());
        lblStatus.setText(message);

        cmbType.removeAllItems();

        JiraTaskTypeProvider jttp = taskRepository.getJiraTaskTypeProvider();
        for (JiraTaskType type : jttp.getJiraTaskTypes()) {
            if (!type.isSubTask() && jiraTask.getProject().isTypesSupported(type)) {
                cmbType.addItem(type);
            }
        }
        if (jiraTask.getType() != null && jttp.getTaskTypeById(jiraTask.getType().getId()).isSubTask()) {
            cmbType.removeAllItems();
            cmbType.addItem(jiraTask.getType());
            cmbType.setEnabled(false);
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
        cmbActions.removeAllItems();
        List<JiraAction> actions = jiraTask.getActions();
        defaultStatus = new JiraAction("##", "Leave as " + (jiraTask.getStatus() != null ? jiraTask.getStatus().getText() : "Local Task"));
        cmbActions.addItem(defaultStatus);
        for (JiraAction action : actions) {
            cmbActions.addItem(action);
        }
        if (jiraTask.getAction() != null) {
            cmbActions.setSelectedItem(jiraTask.getAction());
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
            if (jiraTask.getResolution() == null) {
                cmbResolution.setSelectedIndex(-1);
            } else {
                cmbResolution.setSelectedItem(jiraTask.getResolution());
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
        if (!txtOutline.getText().trim().equals(jiraTask.getName())) {
            jiraTask.setName(txtOutline.getText().trim());
        }
        if (!jiraTask.getPriority().equals(cmbPriority.getSelectedItem())) {
            jiraTask.setPriority((TaskPriority) cmbPriority.getSelectedItem());
        }
        if (!txtAssignee.getText().trim().equals(jiraTask.getAssignee())) {
            jiraTask.setAssignee(txtAssignee.getText().trim());
        }
        if (jiraTask.getDescription()!=null && !jiraTask.getDescription().equals(txtDescription.getText().trim())) {
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
        jiraTask.setNewComment(commentsEditor.getNewComment());
        Object action = cmbActions.getSelectedItem();
        if (action == null || !action.equals(defaultStatus)) {
            jiraTask.setAction((JiraAction) action);
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
        //set as modified if already or actuvaly modified 
        if (jiraTask.isModifiedFlag() || modifiedFlag.get()) {
            jiraTask.setModifiedFlag(true);
        }
        jiraTask.getTaskRepository().persist(jiraTask);
        submitTaskAction.setEnabled(jiraTask.isModifiedFlag());
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
        modifiedFlag.set(true);
        submitTaskAction.setEnabled(true);
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

        lblPriority = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        cmbResolution = new javax.swing.JComboBox();
        lblResolution = new javax.swing.JLabel();
        txtOutline = new javax.swing.JTextField();
        cmbActions = new javax.swing.JComboBox();
        lblDesription = new javax.swing.JLabel();
        lblAction = new javax.swing.JLabel();
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
        lblDesription2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblReportedBy = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAssignee = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setName(NbBundle.getMessage(JiraTaskEditorUI.class, "LBL_Primary_Details","-")); // NOI18N

        lblPriority.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblPriority.text")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setMinimumSize(new java.awt.Dimension(23, 66));
        spDescription.setPreferredSize(new java.awt.Dimension(108, 88));

        txtDescription.setMinimumSize(new java.awt.Dimension(106, 80));
        spDescription.setViewportView(txtDescription);

        lblResolution.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblResolution.text")); // NOI18N

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblDesription.text")); // NOI18N

        lblAction.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "TaskEditorUI.lblStatus.text")); // NOI18N

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

        lblDesription1.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription1.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription1.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblDesription1.text")); // NOI18N

        lblDesription2.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription2.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription2.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblDesription2.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.jLabel1.text","-")); // NOI18N

        lblReportedBy.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblReportedBy.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.jLabel3.text","-")); // NOI18N

        lblStatus.setForeground(new java.awt.Color(102, 102, 102));
        lblStatus.setText(NbBundle.getMessage(JiraTaskEditorUI.class, "JiraTaskEditorUI.lblStatus.text","-")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtOutline, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                    .add(lblAttributes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(lblProject, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                                .add(lblType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(lblPriority, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(lblEnvironment, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(0, 0, 0)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(cmbPriority, 0, 149, Short.MAX_VALUE)
                                    .add(cmbProject, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(cmbType, 0, 104, Short.MAX_VALUE))
                                .add(18, 18, 18)
                                .add(lblComponents, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane4, 0, 0, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(lblFixVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblAffects, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(jScrollPane3, 0, 0, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblDesription1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 318, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblResolution)
                                    .add(lblAction))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cmbActions, 0, 187, Short.MAX_VALUE)
                                    .add(cmbResolution, 0, 187, Short.MAX_VALUE))
                                .add(91, 91, 91)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblDesription2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel1)
                                    .add(jLabel3))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(txtAssignee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                        .add(152, 152, 152))
                                    .add(lblReportedBy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)))))
                    .add(layout.createSequentialGroup()
                        .add(lblCreated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 239, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblUpdated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lblDesription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(txtOutline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUpdated)
                    .add(lblCreated)
                    .add(lblStatus))
                .add(7, 7, 7)
                .add(lblAttributes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblAffects)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblProject)
                            .add(lblComponents)
                            .add(cmbProject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblType)
                            .add(cmbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cmbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblPriority)))
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblFixVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .add(lblEnvironment)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(lblDesription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDesription1)
                    .add(lblDesription2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAction)
                    .add(jLabel1)
                    .add(lblReportedBy)
                    .add(cmbActions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblResolution)
                    .add(cmbResolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(txtAssignee, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JLabel lblAttributes;
    private javax.swing.JLabel lblComponents;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblDesription1;
    private javax.swing.JLabel lblDesription2;
    private javax.swing.JLabel lblEnvironment;
    private javax.swing.JLabel lblFixVersion;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JList lstAffectVersion;
    private javax.swing.JList lstComponents;
    private javax.swing.JList lstFixVersion;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextField txtAssignee;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JEditorPane txtEnvironment;
    private javax.swing.JTextField txtOutline;
    // End of variables declaration//GEN-END:variables

    public void refresh() {
        txtOutline.getDocument().removeDocumentListener(documentListener);
        txtAssignee.getDocument().removeDocumentListener(documentListener);
        txtDescription.getDocument().removeDocumentListener(documentListener);
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


        txtOutline.setText(jiraTask.getName());
        txtDescription.setText(jiraTask.getDescription());
        txtEnvironment.setText(jiraTask.getEnvironment());
        lblReportedBy.setText(jiraTask.getReporter());
        txtAssignee.setText(jiraTask.getAssignee());
        loadDates(jiraTask);
        JiraTaskRepository taskRepository = jiraTask.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        loadAttributes(taskRepository);
        commentsEditor.refresh();
        txtOutline.getDocument().addDocumentListener(documentListener);
        txtAssignee.getDocument().addDocumentListener(documentListener);
        txtDescription.getDocument().addDocumentListener(documentListener);
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




        if (jiraTask.isLocal()) {
            cmbActions.setEnabled(false);
            cmbResolution.setEnabled(false);
            lstFixVersion.setEnabled(false);

        } else {
            cmbProject.setEnabled(false);
            cmbActions.setEnabled(true);

            validateFiledsForEdit(jiraTask);


        }

        loadAction(jiraTask.getAction());
        openInBrowserTaskAction.setEnabled(!jiraTask.isLocal());
        openTaskHistoryAction.setEnabled(!jiraTask.isLocal());
        submitTaskAction.setEnabled(jiraTask.isModifiedFlag());
        modifiedFlag.set(false);
    }

    JiraTask getJiraTask() {
        return jiraTask;
    }

    private void validateFiledsForEdit(JiraTask jiraTask) {
        List<String> editFieldIds = jiraTask.getEditFieldIds();

        txtAssignee.setEditable(editFieldIds.contains(JiraKeys.ASSIGNEE));
        txtDescription.setEditable(editFieldIds.contains(JiraKeys.DESCRIPTION));
        txtOutline.setEditable(editFieldIds.contains(JiraKeys.SUMMERY));
        lstComponents.setEnabled(editFieldIds.contains(JiraKeys.COMPONENTS));
        lstAffectVersion.setEnabled(editFieldIds.contains(JiraKeys.VERSIONS));
        lstFixVersion.setEnabled(editFieldIds.contains(JiraKeys.FIX_VERSIONS));
        txtEnvironment.setEnabled(editFieldIds.contains(JiraKeys.ENVIRONMENT));
        cmbPriority.setEnabled(editFieldIds.contains(JiraKeys.PRIORITY));
        cmbType.setEnabled(editFieldIds.contains(JiraKeys.TYPE));
    }

    JiraCommentsEditor getCommentsEditor() {
        return commentsEditor;
    }
}
