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
 * TracTaskEditorPanels.java
 *
 * Created on Sep 7, 2008, 8:58:15 PM
 */
package org.netbeans.cubeon.trac.tasks.ui;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketAction.Operation;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.repository.TracRepositoryAttributes;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.repository.TracTaskResolutionProvider;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.netbeans.cubeon.trac.tasks.actions.OpenInBrowserTaskAction;
import org.netbeans.cubeon.trac.tasks.actions.SubmitTaskAction;
import org.openide.util.NbBundle;
import static org.netbeans.cubeon.trac.api.TracKeys.*;

/**
 *
 * @author Anuradha
 */
public class TracTaskEditorPanels extends javax.swing.JPanel {

    private static final long serialVersionUID = -7550167448688050066L;
    private static final String LEAVE = "leave";//NOI18N
    private final TracTask task;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private static final String EMPTY = "";
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
    private final TracCommentsEditor commentsEditor;
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
                        loadAction((TicketAction) cmbActions.getSelectedItem());
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

    /** Creates new form TracTaskEditorPanels */
    public TracTaskEditorPanels(TracTask task) {
        this.task = task;
        initComponents();
        commentsEditor = new TracCommentsEditor(this);
        openInBrowserTaskAction = new OpenInBrowserTaskAction(task);
        submitTaskAction = new SubmitTaskAction(task);

    }

    public DocumentListener getDocumentListener() {
        return documentListener;
    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                submitTaskAction);
    }

    public void refresh() {

        txtAssignee.getDocument().removeDocumentListener(documentListener);

        txtKeyWord.getDocument().removeDocumentListener(documentListener);
        txtCc.getDocument().removeDocumentListener(documentListener);

        cmbPriority.removeItemListener(itemListener);
        cmbActions.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);
        cmbComponent.removeItemListener(itemListener);
        cmbMilestone.removeItemListener(itemListener);
        cmbVersion.removeItemListener(itemListener);
        cmbSeverity.removeItemListener(itemListener);
        cmbActions.removeItemListener(actionitemListener);
        cmbResolution.removeItemListener(itemListener);


        commentsEditor.refresh();

        lblReportedBy.setText(task.get(REPORTER));
        txtAssignee.setText(task.get(OWNER));

        txtCc.setText(task.get(TracKeys.CC));
        txtKeyWord.setText(task.get(TracKeys.KEYWORDS));

        loadAttibutes();


        txtAssignee.getDocument().addDocumentListener(documentListener);

        txtKeyWord.getDocument().addDocumentListener(documentListener);
        txtCc.getDocument().addDocumentListener(documentListener);

        cmbPriority.addItemListener(itemListener);
        cmbActions.addItemListener(itemListener);
        cmbType.addItemListener(itemListener);
        cmbComponent.addItemListener(itemListener);
        cmbMilestone.addItemListener(itemListener);
        cmbVersion.addItemListener(itemListener);
        cmbSeverity.addItemListener(itemListener);
        cmbActions.addItemListener(actionitemListener);
        cmbResolution.addItemListener(itemListener);


        openInBrowserTaskAction.setEnabled(!task.isLocal());
        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
    }

    private String getSelectedValve(JComboBox comboBox) {
        Object selected = comboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }

    public TracTask save() {

        task.put(CC, txtCc.getText().trim());
        task.put(KEYWORDS, txtKeyWord.getText().trim());
        task.put(TYPE, getSelectedValve(cmbType));
        task.put(PRIORITY, getSelectedValve(cmbPriority));
        task.put(COMPONENT, getSelectedValve(cmbComponent));
        task.put(SEVERITY, getSelectedValve(cmbSeverity));
        task.put(VERSION, getSelectedValve(cmbVersion));
        task.put(MILESTONE, getSelectedValve(cmbMilestone));
        String assignee = txtAssignee.getText().trim();
        if (task.isLocal()) {
            task.put(OWNER, assignee.length() > 0 ? assignee : null);
        } else {
            task.setAction((TicketAction) cmbActions.getSelectedItem());
            task.setResolution((TaskResolution) cmbResolution.getSelectedItem());
        }
        //set as modified if already or actuvaly modified
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }
        submitTaskAction.setEnabled(task.isModifiedFlag());
        task.getTaskRepository().persist(task);
        task.setNewComment(commentsEditor.getNewComment());
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

    private void loadAttibutes() {
        //set ui to deafulrt
        defaultUI();
        TracTaskRepository tracRepository = task.getTracRepository();
        TracRepositoryAttributes attributes = tracRepository.getRepositoryAttributes();
        cmbActions.removeAllItems();
        cmbResolution.removeAllItems();
        //load actions if ticket is not local task
        if (task.isLocal()) {
            cmbActions.setEnabled(false);

        } else {
            cmbActions.setEnabled(true);
            List<TicketAction> actions = task.getActions();
            for (TicketAction action : actions) {
                cmbActions.addItem(action);
            }
            TicketAction selectedAction = task.getAction();
            if (selectedAction != null) {
                cmbActions.setSelectedItem(selectedAction);
                loadAction(selectedAction);
            } else {
                TicketAction leaveAction = null;
                for (TicketAction ticketAction : actions) {
                    //try to find leave option
                    if (ticketAction.getName().equals(LEAVE)) {
                        leaveAction = ticketAction;
                        break;
                    }
                }
                if (leaveAction != null) {
                    cmbActions.setSelectedItem(leaveAction);
                } else {
                    cmbActions.setSelectedIndex(-1);
                }

            }
            TicketField resolutionField = attributes.getTicketFiledByName(RESOLUTION);
            assert resolutionField != null;
            List<String> options = resolutionField.getOptions();
            TracTaskResolutionProvider provider = tracRepository.getResolutionProvider();
            for (String option : options) {
                TaskResolution resolution = provider.getTaskResolutionById(option);
                if (resolution != null) {
                    cmbResolution.addItem(resolution);
                }
            }
            TaskResolution resolution = task.getResolution();
            if (resolution != null) {
                cmbResolution.setSelectedItem(resolution);
            } else {
                cmbResolution.setSelectedIndex(-1);
            }
        }
        //component field
        TicketField componentField = attributes.getTicketFiledByName(COMPONENT);
        if (componentField != null) {

            String component = task.get(COMPONENT);
            _loadCombos(cmbComponent, componentField.getOptions(),
                    componentField.isOptional(), component);
        } else {
            cmbComponent.setEnabled(false);
        }

        //type field
        TicketField typeField = attributes.getTicketFiledByName(TYPE);
        if (typeField != null) {

            String type = task.get(TYPE);
            _loadCombos(cmbType, typeField.getOptions(),
                    typeField.isOptional(), type);
        } else {
            cmbType.setEnabled(false);
        }
        //priority field
        TicketField priorityField = attributes.getTicketFiledByName(PRIORITY);
        if (priorityField != null) {

            String priority = task.get(PRIORITY);
            _loadCombos(cmbPriority, priorityField.getOptions(),
                    priorityField.isOptional(), priority);
        } else {
            cmbPriority.setEnabled(false);
        }

        //severity field
        TicketField severityField = attributes.getTicketFiledByName(SEVERITY);
        if (severityField != null) {

            String severity = task.get(SEVERITY);
            _loadCombos(cmbSeverity, severityField.getOptions(),
                    severityField.isOptional(), severity);
        } else {
            cmbSeverity.setEnabled(false);
        }

        //milestone field
        TicketField milestoneField = attributes.getTicketFiledByName(MILESTONE);
        if (milestoneField != null) {
            String milestone = task.get(MILESTONE);
            _loadCombos(cmbMilestone, milestoneField.getOptions(),
                    milestoneField.isOptional(), milestone);
        } else {
            cmbMilestone.setEnabled(false);
        }
        //milestone field
        TicketField versionField = attributes.getTicketFiledByName(VERSION);
        if (versionField != null) {
            String version = task.get(VERSION);
            _loadCombos(cmbVersion, versionField.getOptions(),
                    versionField.isOptional(), version);
        } else {
            cmbVersion.setEnabled(false);
        }


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

    public TracTask getTask() {
        return task;
    }

    public void defaultUI() {
        txtAssignee.setEditable(false);
        cmbResolution.setEnabled(false);
    }

    private void loadAction(TicketAction ticketAction) {
        if (ticketAction != null) {
            List<Operation> operations = ticketAction.getOperations();
            for (Operation operation : operations) {
                processOperation(operation);
            }
        }
    }

    public TracCommentsEditor getCommentsEditor() {
        return commentsEditor;
    }

    private void processOperation(Operation operation) {
        //TODO Extranlze process Operations to support any operation
        if (operation.getName().equals("set_resolution")) {//NOI18N
            cmbResolution.setEnabled(true);
        } else if (operation.getName().equals("set_owner")) {//NOI18N
            txtAssignee.setEditable(true);
        } else if (operation.getName().equals("set_owner_to_self")) {//NOI18N
            txtAssignee.setText(task.getTaskRepository().getUserName());
        } else if (operation.getName().equals("del_resolution")) {//NOI18N
            cmbResolution.setSelectedIndex(-1);
        }
    }

    public JComponent getActionAndPeoplePanel() {
        return this;
    }

    public JComponent getAttributesPanel() {
        return pnlAttributes;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAttributes = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblComponent = new javax.swing.JLabel();
        cmbComponent = new javax.swing.JComboBox();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        lblSeverity = new javax.swing.JLabel();
        cmbSeverity = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        lblMilestone = new javax.swing.JLabel();
        cmbMilestone = new javax.swing.JComboBox();
        lblVersion = new javax.swing.JLabel();
        cmbVersion = new javax.swing.JComboBox();
        lblKeyword = new javax.swing.JLabel();
        txtKeyWord = new javax.swing.JTextField();
        lblAction = new javax.swing.JLabel();
        cmbActions = new javax.swing.JComboBox();
        lblResolution = new javax.swing.JLabel();
        cmbResolution = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAssignee = new javax.swing.JTextField();
        lblReportedBy = new javax.swing.JLabel();
        lblCc = new javax.swing.JLabel();
        txtCc = new javax.swing.JTextField();

        pnlAttributes.setOpaque(false);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        lblComponent.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblComponent.text")); // NOI18N
        jPanel1.add(lblComponent);

        jPanel1.add(cmbComponent);

        lblType.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblType.text")); // NOI18N
        jPanel1.add(lblType);

        jPanel1.add(cmbType);

        lblPriority.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblPriority.text")); // NOI18N
        jPanel1.add(lblPriority);

        jPanel1.add(cmbPriority);

        lblSeverity.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblSeverity.text")); // NOI18N
        jPanel1.add(lblSeverity);

        jPanel1.add(cmbSeverity);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        lblMilestone.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblMilestone.text")); // NOI18N
        jPanel2.add(lblMilestone);

        jPanel2.add(cmbMilestone);

        lblVersion.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblVersion.text")); // NOI18N
        jPanel2.add(lblVersion);

        jPanel2.add(cmbVersion);

        lblKeyword.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblKeyword.text","-")); // NOI18N
        jPanel2.add(lblKeyword);
        jPanel2.add(txtKeyWord);

        org.jdesktop.layout.GroupLayout pnlAttributesLayout = new org.jdesktop.layout.GroupLayout(pnlAttributes);
        pnlAttributes.setLayout(pnlAttributesLayout);
        pnlAttributesLayout.setHorizontalGroup(
            pnlAttributesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 271, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAttributesLayout.setVerticalGroup(
            pnlAttributesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAttributesLayout.createSequentialGroup()
                .add(pnlAttributesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlAttributesLayout.linkSize(new java.awt.Component[] {jPanel1, jPanel2}, org.jdesktop.layout.GroupLayout.VERTICAL);

        setName(NbBundle.getMessage(TracTaskEditorPanels.class, "LBL_Name")); // NOI18N
        setOpaque(false);

        lblAction.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblAction.text")); // NOI18N

        lblResolution.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblResolution.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.jLabel1.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.jLabel3.text","-")); // NOI18N

        lblReportedBy.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblReportedBy.text","-")); // NOI18N

        lblCc.setText(NbBundle.getMessage(TracTaskEditorPanels.class, "TracTaskEditorPanels.lblCc.text","-")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblResolution, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblAction, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(cmbActions, 0, 136, Short.MAX_VALUE)
                    .add(cmbResolution, 0, 136, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(lblCc))
                .add(25, 25, 25)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(lblReportedBy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(txtAssignee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .add(txtCc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAction)
                            .add(cmbActions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblResolution)
                            .add(cmbResolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
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
                            .add(txtCc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbActions;
    private javax.swing.JComboBox cmbComponent;
    private javax.swing.JComboBox cmbMilestone;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbResolution;
    private javax.swing.JComboBox cmbSeverity;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbVersion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAction;
    private javax.swing.JLabel lblCc;
    private javax.swing.JLabel lblComponent;
    private javax.swing.JLabel lblKeyword;
    private javax.swing.JLabel lblMilestone;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblSeverity;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel pnlAttributes;
    private javax.swing.JTextField txtAssignee;
    private javax.swing.JTextField txtCc;
    private javax.swing.JTextField txtKeyWord;
    // End of variables declaration//GEN-END:variables
}
