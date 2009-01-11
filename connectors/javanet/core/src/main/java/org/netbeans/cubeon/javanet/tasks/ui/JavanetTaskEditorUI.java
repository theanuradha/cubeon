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
 * TracTaskEditorUI.java
 *
 * Created on Sep 7, 2008, 8:58:15 PM
 */
package org.netbeans.cubeon.javanet.tasks.ui;

import org.netbeans.cubeon.javanet.tasks.actions.SubmitTaskAction;
import org.netbeans.cubeon.javanet.tasks.actions.OpenInBrowserTaskAction;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.javanet.tasks.JavanetTask;
import org.openide.util.NbBundle;


/**
 *
 * @author Anuradha
 */
public class JavanetTaskEditorUI extends javax.swing.JPanel {

    private static final long serialVersionUID = -7550167448688050066L;
    private static final String LEAVE = "leave";//NOI18N
    private final JavanetTask task;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private static final String EMPTY = "";
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
    private JavanetTaskRepository _repo;

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
    ItemListener actionitemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
//                        loadAction((TicketAction) cmbActions.getSelectedItem());
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


    public JavanetTaskEditorUI(JavanetTask task) {
        this.task = task;
        this._repo = task.getTaskRepository();
        initComponents();
        openInBrowserTaskAction = new OpenInBrowserTaskAction(task);
        submitTaskAction = new SubmitTaskAction(task);
        refresh();
    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                submitTaskAction);
    }

    public void refresh() {
        txtSummary.getDocument().removeDocumentListener(documentListener);
        txtAssignee.getDocument().removeDocumentListener(documentListener);
        txtDescription.getDocument().removeDocumentListener(documentListener);
        txtKeyWord.getDocument().removeDocumentListener(documentListener);
        txtCc.getDocument().removeDocumentListener(documentListener);
//
        cmbPriority.removeItemListener(itemListener);
//        cmbActions.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);
        cmbComponent.removeItemListener(itemListener);
        cmbMilestone.removeItemListener(itemListener);
        cmbVersion.removeItemListener(itemListener);
        cmbSeverity.removeItemListener(itemListener);
//        cmbActions.removeItemListener(actionitemListener);
//        cmbResolution.removeItemListener(itemListener);
//
        loadDates();
//commentsEditor.refresh();
        txtSummary.setText(task.getName());
        Font boldFont=new Font(txtSummary.getFont().getName(),Font.BOLD,txtSummary.getFont().getSize());
        txtSummary.setFont(boldFont);
        txtDescription.setText(task.getDescription());                
//        lblReportedBy.setText(task.get(REPORTER));
        txtAssignee.setText(task.getAssignedTo());
        lblStatus.setText(task.getStatus());
        boldFont=new Font(lblStatus.getFont().getName(),Font.BOLD,lblStatus.getFont().getSize());
        lblStatus.setFont(boldFont);
//        txtCc.setText(task.get(TracKeys.CC));
//        txtKeyWord.setText(task.get(TracKeys.KEYWORDS));
//
        loadAttributes();
//
        txtSummary.getDocument().addDocumentListener(documentListener);
        txtAssignee.getDocument().addDocumentListener(documentListener);
        txtDescription.getDocument().addDocumentListener(documentListener);
        txtKeyWord.getDocument().addDocumentListener(documentListener);
        txtCc.getDocument().addDocumentListener(documentListener);
//
        cmbPriority.addItemListener(itemListener);
//        cmbActions.addItemListener(itemListener);
//        cmbType.addItemListener(itemListener);
        cmbComponent.addItemListener(itemListener);
        cmbMilestone.addItemListener(itemListener);
        cmbVersion.addItemListener(itemListener);
        cmbSeverity.addItemListener(itemListener);
//        cmbActions.addItemListener(actionitemListener);
//        cmbResolution.addItemListener(itemListener);
//
//
//        openInBrowserTaskAction.setEnabled(!task.isLocal());
//        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
    }

    private String getSelectedValve(JComboBox comboBox) {
        Object selected = comboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }

    public JavanetTask save() {
//        task.setSummary(txtSummary.getText().trim());
//        task.setDescription(txtDescription.getText().trim());
//        task.put(CC, txtCc.getText().trim());
//        task.put(KEYWORDS, txtKeyWord.getText().trim());
//        task.put(TYPE, getSelectedValve(cmbType));
//        task.put(PRIORITY, getSelectedValve(cmbPriority));
//        task.put(COMPONENT, getSelectedValve(cmbComponent));
//        task.put(SEVERITY, getSelectedValve(cmbSeverity));
//        task.put(VERSION, getSelectedValve(cmbVersion));
//        task.put(MILESTONE, getSelectedValve(cmbMilestone));
//        String assignee = txtAssignee.getText().trim();
//        if (task.isLocal()) {
//            task.put(OWNER, assignee.length() > 0 ? assignee : null);
//        } else {
//            task.setAction((TicketAction) cmbActions.getSelectedItem());
//            task.setResolution((TaskResolution) cmbResolution.getSelectedItem());
//        }
//        //set as modified if already or actuvaly modified
//        if (task.isModifiedFlag() || modifiedFlag.get()) {
//            task.setModifiedFlag(true);
//        }
//        submitTaskAction.setEnabled(task.isModifiedFlag());
//        task.getTaskRepository().persist(task);
//        task.setNewComment(commentsEditor.getNewComment());
//        return task;
        return null;
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

    private void loadAttributes() {
        //set ui to deafulrt
        defaultUI();
        cmbComponent.removeAllItems();
        String compo = task != null ? task.getComponent() : null;
        _loadCombos(cmbComponent, _repo.getComponents(), false, compo);

        String type = task != null ? task.getIssueType() : null;
        _loadCombos(cmbType, _repo.getIssueType(), false, type);

        String prio = task != null ? task.getPriority() : null;
        _loadCombos(cmbPriority, _repo.getPriorities(), false, prio);

        _loadCombos(cmbPlatform, _repo.getPlatforms(), false, null);

        _loadCombos(cmbOs, _repo.getSystems(), false, null);

//        JavanetTaskRepository tracRepository = task.getTracRepository();
//        TracRepositoryAttributes attributes = tracRepository.getRepositoryAttributes();
//        cmbActions.removeAllItems();
//        cmbResolution.removeAllItems();
//        //load actions if ticket is not local task
//        if (task.isLocal()) {
//            cmbActions.setEnabled(false);
//
//        } else {
//            cmbActions.setEnabled(true);
//            List<TicketAction> actions = task.getActions();
//            for (TicketAction action : actions) {
//                cmbActions.addItem(action);
//            }
//            TicketAction selectedAction = task.getAction();
//            if (selectedAction != null) {
//                cmbActions.setSelectedItem(selectedAction);
//                loadAction(selectedAction);
//            } else {
//                TicketAction leaveAction = null;
//                for (TicketAction ticketAction : actions) {
//                    //try to find leave option
//                    if (ticketAction.getName().equals(LEAVE)) {
//                        leaveAction = ticketAction;
//                        break;
//                    }
//                }
//                if (leaveAction != null) {
//                    cmbActions.setSelectedItem(leaveAction);
//                } else {
//                    cmbActions.setSelectedIndex(-1);
//                }
//
//            }
//            TicketField resolutionField = attributes.getTicketFiledByName(RESOLUTION);
//            assert resolutionField != null;
//            List<String> options = resolutionField.getOptions();
//            TracTaskResolutionProvider provider = tracRepository.getResolutionProvider();
//            for (String option : options) {
//                TaskResolution resolution = provider.getTaskResolutionById(option);
//                if (resolution != null) {
//                    cmbResolution.addItem(resolution);
//                }
//            }
//            TaskResolution resolution = task.getResolution();
//            if (resolution != null) {
//                cmbResolution.setSelectedItem(resolution);
//            } else {
//                cmbResolution.setSelectedIndex(-1);
//            }
//        }
//        //component field
//        TicketField componentField = attributes.getTicketFiledByName(COMPONENT);
//        if (componentField != null) {
//
//            String component = task.get(COMPONENT);
//            _loadCombos(cmbComponent, componentField.getOptions(),
//                    componentField.isOptional(), component);
//        } else {
//            cmbComponent.setEnabled(false);
//        }
//
//        //type field
//        TicketField typeField = attributes.getTicketFiledByName(TYPE);
//        if (typeField != null) {
//
//            String type = task.get(TYPE);
//            _loadCombos(cmbType, typeField.getOptions(),
//                    typeField.isOptional(), type);
//        } else {
//            cmbType.setEnabled(false);
//        }
//        //priority field
//        TicketField priorityField = attributes.getTicketFiledByName(PRIORITY);
//        if (priorityField != null) {
//
//            String priority = task.get(PRIORITY);
//            _loadCombos(cmbPriority, priorityField.getOptions(),
//                    priorityField.isOptional(), priority);
//        } else {
//            cmbPriority.setEnabled(false);
//        }
//
//        //severity field
//        TicketField severityField = attributes.getTicketFiledByName(SEVERITY);
//        if (severityField != null) {
//
//            String severity = task.get(SEVERITY);
//            _loadCombos(cmbSeverity, severityField.getOptions(),
//                    severityField.isOptional(), severity);
//        } else {
//            cmbSeverity.setEnabled(false);
//        }
//
//        //milestone field
//        TicketField milestoneField = attributes.getTicketFiledByName(MILESTONE);
//        if (milestoneField != null) {
//            String milestone = task.get(MILESTONE);
//            _loadCombos(cmbMilestone, milestoneField.getOptions(),
//                    milestoneField.isOptional(), milestone);
//        } else {
//            cmbMilestone.setEnabled(false);
//        }
//        //milestone field
//        TicketField versionField = attributes.getTicketFiledByName(VERSION);
//        if (versionField != null) {
//            String version = task.get(VERSION);
//            _loadCombos(cmbVersion, versionField.getOptions(),
//                    versionField.isOptional(), version);
//        } else {
//            cmbVersion.setEnabled(false);
//        }


    }

    private void _loadCombos(JComboBox comboBox, List<String> options,
            boolean optinal, String selected) {
        comboBox.removeAllItems();
        if (options.isEmpty()) {
            comboBox.setEnabled(false);
        } else {
            comboBox.setEnabled(true);
            if (optinal) {
                //if optional component add EMPTY tag to options
                comboBox.addItem(EMPTY);
                //if valuve optional and selected null set selected as EMPTY
                if (selected == null || selected.trim().length() == 0) {
                    selected = EMPTY;
                }
            }
            for (String string : options) {
                comboBox.addItem(string);
            }
            if (selected == null || selected.equals(EMPTY)) {
                comboBox.setSelectedIndex(-1);
            } else {
                comboBox.setSelectedItem(selected);
            }
        }
    }

    private void loadDates() {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (task.getCreationDate() != null) {
            String message = NbBundle.getMessage(JavanetTaskEditorUI.class,
                    "JavanetTaskEditorUI.lblCreated.text",
                    dateFormat.format(task.getCreationDate().getTime()));
            lblCreated.setText(message);
        }
        if (task.getLastModified() != null) {
            String message = NbBundle.getMessage(JavanetTaskEditorUI.class,
                    "JavanetTaskEditorUI.lblUpdated.text",
                    dateFormat.format(task.getLastModified().getTime()));
            lblUpdated.setText(message);;
        }

    }

    private void loadComponentDependentCombos() {
        String component = (String) cmbComponent.getSelectedItem();
        if (component != null) {
            String subCompo = task != null ? task.getSubComponent() : null;
            _loadCombos(cmbSubComponent, _repo.getSubComponents(component) , true, subCompo);

            String version = task != null ? task.getVersion() : null;
            _loadCombos(cmbVersion, _repo.getVersions(component), false, version);

            String milestone = task != null ? task.getMilestone() : null;
            _loadCombos(cmbMilestone, _repo.getMilestones(component), false, milestone);
        }
    }

    public JavanetTask getTask() {
        return task;
    }

    public void defaultUI() {
        txtAssignee.setEditable(false);
//        cmbResolution.setEnabled(false);
    }

//    private void loadAction(TicketAction ticketAction) {
//        if (ticketAction != null) {
//            List<Operation> operations = ticketAction.getOperations();
//            for (Operation operation : operations) {
//                processOperation(operation);
//            }
//        }
//    }


//    private void processOperation(Operation operation) {
//        //TODO Extranlze process Operations to support any operation
//        if (operation.getName().equals("set_resolution")) {//NOI18N
//            cmbResolution.setEnabled(true);
//        } else if (operation.getName().equals("set_owner")) {//NOI18N
//            txtAssignee.setEditable(true);
//        } else if (operation.getName().equals("set_owner_to_self")) {//NOI18N
//            txtAssignee.setText(task.getTaskRepository().getUserName());
//        } else if (operation.getName().equals("del_resolution")) {//NOI18N
//            cmbResolution.setSelectedIndex(-1);
//        }
//    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSummary = new javax.swing.JTextField();
        lblCreated = new javax.swing.JLabel();
        lblUpdated = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        lblDesription = new javax.swing.JLabel();
        lblAttributes = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblComponent = new javax.swing.JLabel();
        cmbComponent = new javax.swing.JComboBox();
        lblSubComponent = new javax.swing.JLabel();
        cmbSubComponent = new javax.swing.JComboBox();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        lblSeverity = new javax.swing.JLabel();
        cmbSeverity = new javax.swing.JComboBox();
        lblDesription2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAssignee = new javax.swing.JTextField();
        lblReportedBy = new javax.swing.JLabel();
        lblCc = new javax.swing.JLabel();
        txtCc = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblPlatform = new javax.swing.JLabel();
        cmbPlatform = new javax.swing.JComboBox();
        lblOs = new javax.swing.JLabel();
        cmbOs = new javax.swing.JComboBox();
        lblVersion = new javax.swing.JLabel();
        cmbVersion = new javax.swing.JComboBox();
        lblMilestone = new javax.swing.JLabel();
        cmbMilestone = new javax.swing.JComboBox();
        lblKeyword = new javax.swing.JLabel();
        txtKeyWord = new javax.swing.JTextField();
        lblDesription1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cmbAction = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        cmbActionOption = new javax.swing.JComboBox();
        txtActionOption = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setName("null");

        lblCreated.setForeground(new java.awt.Color(102, 102, 102));
        lblCreated.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblCreated.text","-")); // NOI18N

        lblUpdated.setForeground(new java.awt.Color(102, 102, 102));
        lblUpdated.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblUpdated.text","-")); // NOI18N

        lblStatus.setForeground(new java.awt.Color(102, 102, 102));
        lblStatus.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblStatus.text","-")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setMinimumSize(new java.awt.Dimension(23, 66));
        spDescription.setPreferredSize(new java.awt.Dimension(108, 88));

        txtDescription.setMinimumSize(new java.awt.Dimension(106, 80));
        spDescription.setViewportView(txtDescription);

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblDesription.text")); // NOI18N

        lblAttributes.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAttributes.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblAttributes.text")); // NOI18N

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(5, 2, 5, 5));

        lblComponent.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblComponent.text")); // NOI18N
        jPanel1.add(lblComponent);

        cmbComponent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbComponentItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbComponent);

        lblSubComponent.setText(org.openide.util.NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblSubComponent.text")); // NOI18N
        jPanel1.add(lblSubComponent);

        jPanel1.add(cmbSubComponent);

        lblType.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblType.text")); // NOI18N
        jPanel1.add(lblType);

        jPanel1.add(cmbType);

        lblPriority.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblPriority.text")); // NOI18N
        jPanel1.add(lblPriority);

        jPanel1.add(cmbPriority);

        lblSeverity.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblSeverity.text")); // NOI18N
        jPanel1.add(lblSeverity);

        jPanel1.add(cmbSeverity);

        lblDesription2.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription2.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription2.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblDesription2.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.jLabel1.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.jLabel3.text","-")); // NOI18N

        lblReportedBy.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblReportedBy.text","-")); // NOI18N

        lblCc.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblCc.text","-")); // NOI18N

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(5, 2, 5, 5));

        lblPlatform.setText(org.openide.util.NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblPlatform.text")); // NOI18N
        jPanel2.add(lblPlatform);

        jPanel2.add(cmbPlatform);

        lblOs.setText(org.openide.util.NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblOs.text")); // NOI18N
        jPanel2.add(lblOs);

        jPanel2.add(cmbOs);

        lblVersion.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblVersion.text")); // NOI18N
        jPanel2.add(lblVersion);

        jPanel2.add(cmbVersion);

        lblMilestone.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblMilestone.text")); // NOI18N
        jPanel2.add(lblMilestone);

        jPanel2.add(cmbMilestone);

        lblKeyword.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblKeyword.text","-")); // NOI18N
        jPanel2.add(lblKeyword);
        jPanel2.add(txtKeyWord);

        lblDesription1.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription1.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription1.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblDesription1.text")); // NOI18N

        jPanel4.setOpaque(false);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.add(cmbActionOption);

        txtActionOption.setText(org.openide.util.NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.txtActionOption.text")); // NOI18N
        jPanel3.add(txtActionOption);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(cmbAction, 0, 286, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cmbAction, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblCreated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 239, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblUpdated, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(txtSummary, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                    .add(lblDesription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                    .add(lblAttributes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 684, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createSequentialGroup()
                                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                .add(layout.createSequentialGroup()
                                    .add(lblDesription1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 149, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(317, 317, 317)))
                            .add(layout.createSequentialGroup()
                                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblDesription2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 313, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel1)
                                        .add(jLabel3)
                                        .add(lblCc))
                                    .add(103, 103, 103)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                            .add(9, 9, 9)
                                            .add(lblReportedBy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                                        .add(txtAssignee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .add(txtCc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(txtSummary, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUpdated)
                    .add(lblCreated)
                    .add(lblStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDesription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblAttributes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDesription1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblDesription2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(lblReportedBy))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(txtAssignee, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblCc)
                            .add(txtCc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbComponentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbComponentItemStateChanged
        loadComponentDependentCombos();
    }//GEN-LAST:event_cmbComponentItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbAction;
    private javax.swing.JComboBox cmbActionOption;
    private javax.swing.JComboBox cmbComponent;
    private javax.swing.JComboBox cmbMilestone;
    private javax.swing.JComboBox cmbOs;
    private javax.swing.JComboBox cmbPlatform;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbSeverity;
    private javax.swing.JComboBox cmbSubComponent;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbVersion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblAttributes;
    private javax.swing.JLabel lblCc;
    private javax.swing.JLabel lblComponent;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblDesription1;
    private javax.swing.JLabel lblDesription2;
    private javax.swing.JLabel lblKeyword;
    private javax.swing.JLabel lblMilestone;
    private javax.swing.JLabel lblOs;
    private javax.swing.JLabel lblPlatform;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblSeverity;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubComponent;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextField txtActionOption;
    private javax.swing.JTextField txtAssignee;
    private javax.swing.JTextField txtCc;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtKeyWord;
    private javax.swing.JTextField txtSummary;
    // End of variables declaration//GEN-END:variables
}
