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
package org.netbeans.cubeon.trac.tasks.ui;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.api.TracKeys;
import org.netbeans.cubeon.trac.repository.TracRepositoryAttributes;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.netbeans.cubeon.trac.tasks.actions.OpenInBrowserTaskAction;
import org.netbeans.cubeon.trac.tasks.actions.SubmitTaskAction;
import org.openide.util.NbBundle;
import static org.netbeans.cubeon.trac.api.TracKeys.*;

/**
 *
 * @author Anuradha
 */
public class TracTaskEditorUI extends javax.swing.JPanel {

    private static final long serialVersionUID = -7550167448688050066L;
    private static final String LEAVE = "leave";//NOI18N
    private final TracTask task;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private static final String EMPTY = "";
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
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
                        //loadAction((JiraAction) cmbActions.getSelectedItem());
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

    /** Creates new form TracTaskEditorUI */
    public TracTaskEditorUI(TracTask task) {
        this.task = task;
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

        cmbPriority.removeItemListener(itemListener);
        cmbActions.removeItemListener(itemListener);
        cmbType.removeItemListener(itemListener);
        cmbComponent.removeItemListener(itemListener);
        cmbMilestone.removeItemListener(itemListener);
        cmbVersion.removeItemListener(itemListener);
        cmbSeverity.removeItemListener(itemListener);
        cmbActions.removeItemListener(actionitemListener);
        cmbResolution.removeItemListener(itemListener);

        loadDates();
        txtSummary.setText(task.getSummary());
        txtDescription.setText(task.getDescription());
        lblReportedBy.setText(task.get(REPORTER));
        txtAssignee.setText(task.get(OWNER));
        lblStatus.setText(NbBundle.getMessage(TracTaskEditorUI.class,
                "TracTaskEditorUI.lblStatus.text",
                task.isLocal()
                ? NbBundle.getMessage(TracTaskEditorUI.class, "LBL_Local")
                : task.get(STATUS))); // NOI18N
        txtCc.setText(task.get(TracKeys.CC));
        txtKeyWord.setText(task.get(TracKeys.KEYWORDS));

        loadAttibutes();

        txtSummary.getDocument().addDocumentListener(documentListener);
        txtAssignee.getDocument().addDocumentListener(documentListener);
        txtDescription.getDocument().addDocumentListener(documentListener);
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
        task.setSummary(txtSummary.getText().trim());
        task.setDescription(txtDescription.getText().trim());
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
            //TODO hadel action and resulution
        }
        //set as modified if already or actuvaly modified
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }
        submitTaskAction.setEnabled(task.isModifiedFlag());
        task.getTaskRepository().persist(task);
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
            for (String option : options) {
                cmbResolution.addItem(option);
            }
            TaskResolution resolution = task.getResolution();
            if (resolution != null) {
                cmbResolution.setSelectedItem(resolution.getId());
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

    private void loadDates() {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (task.getCreatedDate() != 0) {
            String message = NbBundle.getMessage(TracTaskEditorUI.class,
                    "TracTaskEditorUI.lblCreated.text",
                    dateFormat.format(new Date(task.getCreatedDate())));
            lblCreated.setText(message);
        }
        if (task.getUpdatedDate() != 0) {
            String message = NbBundle.getMessage(TracTaskEditorUI.class,
                    "TracTaskEditorUI.lblUpdated.text",
                    dateFormat.format(new Date(task.getUpdatedDate())));
            lblUpdated.setText(message);
        }

    }

    public TracTask getTask() {
        return task;
    }

    public void defaultUI() {
        txtAssignee.setEditable(false);
        cmbResolution.setEnabled(false);
    }

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
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        lblSeverity = new javax.swing.JLabel();
        cmbSeverity = new javax.swing.JComboBox();
        lblDesription1 = new javax.swing.JLabel();
        lblAction = new javax.swing.JLabel();
        cmbActions = new javax.swing.JComboBox();
        lblResolution = new javax.swing.JLabel();
        cmbResolution = new javax.swing.JComboBox();
        lblDesription2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAssignee = new javax.swing.JTextField();
        lblReportedBy = new javax.swing.JLabel();
        lblCc = new javax.swing.JLabel();
        txtCc = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblMilestone = new javax.swing.JLabel();
        cmbMilestone = new javax.swing.JComboBox();
        lblVersion = new javax.swing.JLabel();
        cmbVersion = new javax.swing.JComboBox();
        lblKeyword = new javax.swing.JLabel();
        txtKeyWord = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        lblCreated.setForeground(new java.awt.Color(102, 102, 102));
        lblCreated.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblCreated.text","-")); // NOI18N

        lblUpdated.setForeground(new java.awt.Color(102, 102, 102));
        lblUpdated.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblUpdated.text","-")); // NOI18N

        lblStatus.setForeground(new java.awt.Color(102, 102, 102));
        lblStatus.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblStatus.text","-")); // NOI18N

        spDescription.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spDescription.setMinimumSize(new java.awt.Dimension(23, 66));
        spDescription.setPreferredSize(new java.awt.Dimension(108, 88));

        txtDescription.setMinimumSize(new java.awt.Dimension(106, 80));
        spDescription.setViewportView(txtDescription);

        lblDesription.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblDesription.text")); // NOI18N

        lblAttributes.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAttributes.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblAttributes.text")); // NOI18N

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        lblComponent.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblComponent.text")); // NOI18N
        jPanel1.add(lblComponent);

        jPanel1.add(cmbComponent);

        lblType.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblType.text")); // NOI18N
        jPanel1.add(lblType);

        jPanel1.add(cmbType);

        lblPriority.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblPriority.text")); // NOI18N
        jPanel1.add(lblPriority);

        jPanel1.add(cmbPriority);

        lblSeverity.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblSeverity.text")); // NOI18N
        jPanel1.add(lblSeverity);

        jPanel1.add(cmbSeverity);

        lblDesription1.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDesription1.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription1.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblDesription1.text")); // NOI18N

        lblAction.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblAction.text")); // NOI18N

        lblResolution.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblResolution.text")); // NOI18N

        lblDesription2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesription2.setForeground(new java.awt.Color(51, 51, 51));
        lblDesription2.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblDesription2.text")); // NOI18N

        jLabel1.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.jLabel1.text","-")); // NOI18N

        jLabel3.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.jLabel3.text","-")); // NOI18N

        lblReportedBy.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblReportedBy.text","-")); // NOI18N

        lblCc.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblCc.text","-")); // NOI18N

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        lblMilestone.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblMilestone.text")); // NOI18N
        jPanel2.add(lblMilestone);

        jPanel2.add(cmbMilestone);

        lblVersion.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblVersion.text")); // NOI18N
        jPanel2.add(lblVersion);

        jPanel2.add(cmbVersion);

        lblKeyword.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblKeyword.text","-")); // NOI18N
        jPanel2.add(lblKeyword);
        jPanel2.add(txtKeyWord);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUpdated, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSummary, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                    .addComponent(spDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                    .addComponent(lblDesription, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                    .addComponent(lblAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblAction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblResolution, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cmbResolution, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbActions, 0, 250, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addComponent(lblDesription1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(lblCc))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblReportedBy, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                    .addComponent(txtAssignee, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                    .addComponent(txtCc, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addComponent(lblDesription2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUpdated)
                    .addComponent(lblCreated)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDesription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAttributes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDesription1)
                    .addComponent(lblDesription2))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblReportedBy))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtAssignee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCc)
                            .addComponent(txtCc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblAction)
                            .addComponent(cmbActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblResolution)
                            .addComponent(cmbResolution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
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
    private javax.swing.JLabel lblAttributes;
    private javax.swing.JLabel lblCc;
    private javax.swing.JLabel lblComponent;
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblDesription;
    private javax.swing.JLabel lblDesription1;
    private javax.swing.JLabel lblDesription2;
    private javax.swing.JLabel lblKeyword;
    private javax.swing.JLabel lblMilestone;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblReportedBy;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblSeverity;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextField txtAssignee;
    private javax.swing.JTextField txtCc;
    private javax.swing.JEditorPane txtDescription;
    private javax.swing.JTextField txtKeyWord;
    private javax.swing.JTextField txtSummary;
    // End of variables declaration//GEN-END:variables
}
