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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.cubeon.javanet.repository.JavanetTaskAction;
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
                        //loadAction((TicketAction) cmbActions.getSelectedItem());
                        loadActionCombos();
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

    public JPanel getAttributesPanel() {
        return pnlAttributes;
    }

    public JPanel getActionsPanel() {
        return pnlActions;
    }

    public JPanel getCommentPanel() {
        return pnlComment;
    }

    public JComponent getDescription() {
        return spDescription;
    }

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
        txtDescription.getDocument().removeDocumentListener(documentListener);
        txtKeyWord.getDocument().removeDocumentListener(documentListener);
        txtCc.getDocument().removeDocumentListener(documentListener);
//
        cmbPriority.removeItemListener(itemListener);
        cmbAction.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);
        cmbComponent.removeItemListener(itemListener);
        cmbMilestone.removeItemListener(itemListener);
        cmbVersion.removeItemListener(itemListener);        
        cmbAction.removeItemListener(actionitemListener);
//        cmbResolution.removeItemListener(itemListener);
//
        
//commentsEditor.refresh();
//
        loadAttributes();
//
        txtDescription.getDocument().addDocumentListener(documentListener);
        txtKeyWord.getDocument().addDocumentListener(documentListener);
        txtCc.getDocument().addDocumentListener(documentListener);
//
        cmbPriority.addItemListener(itemListener);
        cmbAction.addItemListener(itemListener);
//        cmbType.addItemListener(itemListener);
        cmbComponent.addItemListener(itemListener);
        cmbMilestone.addItemListener(itemListener);
        cmbVersion.addItemListener(itemListener);       
        cmbAction.addItemListener(actionitemListener);
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

    private void loadActionCombos() {
        JavanetTaskAction selAction = (JavanetTaskAction) cmbAction.getSelectedItem();
        switch (selAction) {
            case CONFIRM_AND_REASSIGN:
            case REASSIGN:
                txtActionOption.setVisible(true);
                cmbActionOption.setVisible(false);
                break;
            case RESOLVE:
                txtActionOption.setVisible(false);
                cmbActionOption.setVisible(true);
                break;
            default:
                txtActionOption.setVisible(false);
                cmbActionOption.setVisible(false);
                break;
        }
    }

    private void loadAttributes() {
        //set ui to deafulrt
        defaultUI();
        cmbComponent.removeAllItems();

        String compo = null;
        String type = null;
        String prio = null;
        String assignedTo = null;
        String reporter = null;
        String desc = null;
        String status = null;
        String platform = null;
        String opSys = null;

        if (task != null) {
            compo = task.getComponent();
            type = task.getIssueType();
            prio = task.getPriority();
            assignedTo = task.getAssignedTo();
            reporter = task.getReporter();
            desc = task.getDescription();
            status = task.getStatus();
            platform = task.getPlatform();
            opSys = task.getOpSystem();
        }
        
        _loadCombos(cmbComponent, _repo.getComponents(), false, compo);        
        _loadCombos(cmbType, _repo.getIssueType(), false, type);
        _loadCombos(cmbPriority, _repo.getPriorities(), false, prio);
        _loadCombos(cmbPlatform, _repo.getPlatforms(), false, platform);
        _loadCombos(cmbOs, _repo.getSystems(), false, opSys);
        _loadCombos(cmbAction, _repo.getActions(task.getStatus()), false, JavanetTaskAction.DO_NOTHING);
        _loadCombos(cmbActionOption, _repo.getResolutions(), false, null);

        cmbActionOption.setVisible(false);
        txtActionOption.setVisible(false);

        lblAssignedTo.setText(assignedTo);
        lblReportedBy.setText(reporter);

        txtDescription.setText(desc);

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

    private void _loadCombos(JComboBox comboBox, List<? extends Object> options,
            boolean optional, Object selected) {
        comboBox.removeAllItems();
        if (options.isEmpty()) {
            comboBox.setEnabled(false);
        } else {
            comboBox.setEnabled(true);
            if (optional) {
                //if optional component add EMPTY tag to options
                comboBox.addItem(EMPTY);
                //if valuve optional and selected null set selected as EMPTY
                if (selected == null) {
                    selected = EMPTY;
                }
            }
            for (Object string : options) {
                comboBox.addItem(string);
            }
            if (selected == null || selected.equals(EMPTY)) {
                comboBox.setSelectedIndex(-1);
            } else {
                comboBox.setSelectedItem(selected);
            }
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

        spDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JEditorPane();
        pnlAttributes = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblComponent = new javax.swing.JLabel();
        cmbComponent = new javax.swing.JComboBox();
        lblSubComponent = new javax.swing.JLabel();
        cmbSubComponent = new javax.swing.JComboBox();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        lblPlatform = new javax.swing.JLabel();
        cmbPlatform = new javax.swing.JComboBox();
        lblOs = new javax.swing.JLabel();
        cmbOs = new javax.swing.JComboBox();
        lblVersion = new javax.swing.JLabel();
        cmbVersion = new javax.swing.JComboBox();
        lblMilestone = new javax.swing.JLabel();
        cmbMilestone = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblReportedBy = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblAssignedTo = new javax.swing.JLabel();
        lblCc = new javax.swing.JLabel();
        txtCc = new javax.swing.JTextField();
        lblKeyword = new javax.swing.JLabel();
        txtKeyWord = new javax.swing.JTextField();
        pnlActions = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cmbAction = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        cmbActionOption = new javax.swing.JComboBox();
        txtActionOption = new javax.swing.JTextField();
        pnlComment = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtComment = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));
        setName("null");

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setMinimumSize(new java.awt.Dimension(23, 66));
        spDescription.setPreferredSize(new java.awt.Dimension(108, 88));

        txtDescription.setEditable(false);
        txtDescription.setMinimumSize(new java.awt.Dimension(106, 80));
        spDescription.setViewportView(txtDescription);

        pnlAttributes.setLayout(new java.awt.GridLayout(1, 3, 3, 0));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(4, 2, 5, 2));

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

        pnlAttributes.add(jPanel1);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(4, 2, 3, 2));

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

        pnlAttributes.add(jPanel2);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridLayout(4, 2, 3, 2));

        jLabel1.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.jLabel1.text","-")); // NOI18N
        jPanel5.add(jLabel1);

        lblReportedBy.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblReportedBy.text","-")); // NOI18N
        jPanel5.add(lblReportedBy);

        jLabel3.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.jLabel3.text","-")); // NOI18N
        jPanel5.add(jLabel3);

        lblAssignedTo.setText(org.openide.util.NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblAssignedTo.text")); // NOI18N
        jPanel5.add(lblAssignedTo);

        lblCc.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblCc.text","-")); // NOI18N
        jPanel5.add(lblCc);
        jPanel5.add(txtCc);

        lblKeyword.setText(NbBundle.getMessage(JavanetTaskEditorUI.class, "JavanetTaskEditorUI.lblKeyword.text","-")); // NOI18N
        jPanel5.add(lblKeyword);
        jPanel5.add(txtKeyWord);

        pnlAttributes.add(jPanel5);

        jPanel4.setOpaque(false);

        cmbAction.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbActionItemStateChanged(evt);
            }
        });

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
                .add(cmbAction, 0, 262, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cmbAction, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlActionsLayout = new org.jdesktop.layout.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActionsLayout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(382, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActionsLayout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtComment.setColumns(20);
        txtComment.setRows(5);
        jScrollPane1.setViewportView(txtComment);

        org.jdesktop.layout.GroupLayout pnlCommentLayout = new org.jdesktop.layout.GroupLayout(pnlComment);
        pnlComment.setLayout(pnlCommentLayout);
        pnlCommentLayout.setHorizontalGroup(
            pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
        );
        pnlCommentLayout.setVerticalGroup(
            pnlCommentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCommentLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlActions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlComment, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
                    .add(pnlAttributes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(spDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAttributes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(29, 29, 29)
                .add(pnlActions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(44, 44, 44)
                .add(pnlComment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(38, 38, 38))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbComponentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbComponentItemStateChanged
        loadComponentDependentCombos();
    }//GEN-LAST:event_cmbComponentItemStateChanged

    private void cmbActionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbActionItemStateChanged
 
    }//GEN-LAST:event_cmbActionItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbAction;
    private javax.swing.JComboBox cmbActionOption;
    private javax.swing.JComboBox cmbComponent;
    private javax.swing.JComboBox cmbMilestone;
    private javax.swing.JComboBox cmbOs;
    private javax.swing.JComboBox cmbPlatform;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbSubComponent;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbVersion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAssignedTo;
    private javax.swing.JLabel lblCc;
    private javax.swing.JLabel lblComponent;
    private javax.swing.JLabel lblKeyword;
    private javax.swing.JLabel lblMilestone;
    private javax.swing.JLabel lblOs;
    private javax.swing.JLabel lblPlatform;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblSubComponent;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlAttributes;
    private javax.swing.JPanel pnlComment;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextField txtActionOption;
    private javax.swing.JTextField txtCc;
    private javax.swing.JTextArea txtComment;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtKeyWord;
    // End of variables declaration//GEN-END:variables
}
