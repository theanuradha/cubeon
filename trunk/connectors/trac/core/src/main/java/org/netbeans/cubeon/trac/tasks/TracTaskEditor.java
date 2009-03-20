/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.trac.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.components.ComponentBuilder;
import org.netbeans.cubeon.common.ui.components.ComponentContainer;
import org.netbeans.cubeon.common.ui.components.TextEditorUI;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketAction.Operation;
import org.netbeans.cubeon.trac.api.TicketField;
import org.netbeans.cubeon.trac.repository.TracRepositoryAttributes;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.repository.TracTaskResolutionProvider;
import org.netbeans.cubeon.trac.tasks.actions.OpenInBrowserTaskAction;
import org.netbeans.cubeon.trac.tasks.actions.SubmitTaskAction;
import org.openide.util.NbBundle;
import static org.netbeans.cubeon.trac.api.TracKeys.*;

/**
 *
 * @author Anuradha
 */
public class TracTaskEditor {

    private static final String LEAVE = "leave";//NOI18N
    private final TracTask task;
    private final TaskEditor editor;
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private static final String EMPTY = "";
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
    private ComponentBuilder builder = new ComponentBuilder() {

        @Override
        public void valueChanged() {
            fireChangeEvent();
        }
    };
    private ComponentContainer arrributesContainer = new ComponentContainer();
    private ComponentContainer actionsContainer = new ComponentContainer();
    private final TextEditorUI descriptionComponent = new TextEditorUI();
    private final TextEditorUI newCommentComponent = new TextEditorUI();
    //default trac fields
    private JComboBox actions;
    private JComboBox component;
    private JComboBox milestone;
    private JComboBox priority;
    private JComboBox resolution;
    private JComboBox severity;
    private JComboBox type;
    private JComboBox version;
    private JLabel reportedBy;
    private JTextField assignee;
    private JTextField cc;
    private JTextField keyWord;
    //custom field support
    private List<CustomFieldSupport> customFieldSupports = new ArrayList<CustomFieldSupport>();
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public TracTaskEditor(TaskEditor editor, TracTask task) {
        this.editor = editor;
        this.task = task;
        openInBrowserTaskAction = new OpenInBrowserTaskAction(task);
        submitTaskAction = new SubmitTaskAction(task);
        editor.addSummaryDocumentListener(builder.getDocumentListener());
        descriptionComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        newCommentComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        buildComponents();
    }

    private void buildComponents() {
        //build attributes
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Component")), component = builder.createComboBox(), new JLabel());

        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Milestone")), milestone = builder.createComboBox());
        //move to next row
        arrributesContainer.nextSection();

        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Type")), type = builder.createComboBox(), new JLabel());

        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Version")), version = builder.createComboBox());
        //move to next row
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Priority")), priority = builder.createComboBox(), new JLabel());

        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Severity")), severity = builder.createComboBox());

        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Keyword")), keyWord = builder.createTextField());

        arrributesContainer.nextSection();
        buildCustomComponents(arrributesContainer);
        //build actions and people
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Action")), actions = builder.createComboBox(), new JLabel());

        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Reported_By")), reportedBy = builder.createLabelField(" - "));
        //move to next row
        actionsContainer.nextSection();
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Resolution")), resolution = builder.createComboBox(), new JLabel());

        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_Assignee")), assignee = builder.createTextField());
        //move to next row
        actionsContainer.nextSection();
        //add space

        actionsContainer.addComponentGroup(
                builder.createLabel(""), builder.createEmptyField(), new JLabel());
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(TracTaskEditor.class,
                "LBL_CC")), cc = builder.createTextField());

    }

    private void buildCustomComponents(ComponentContainer container) {
        List<TicketField> customFields = getCustomFields();

        int index = 0;
        for (TicketField ticketField : customFields) {
            JComponent customEditor = null;
            if (ticketField.getType().equals("text")) {
                customEditor = builder.createTextField();
            } else if (ticketField.getType().equals("select") || ticketField.getType().equals("radio")) {
                customEditor = builder.createComboBox();
            } else if (ticketField.getType().equals("checkbox")) {
                customEditor = builder.createCheckbox();
            }

            if (customEditor != null) {
                CustomFieldSupport cfs = new CustomFieldSupport(ticketField, customEditor);
                customFieldSupports.add(cfs);
                if (index == 0) {
                    container.addComponentGroup(
                            builder.createLabel(ticketField.getLabel()),
                            customEditor,
                            new JLabel());

                } else {
                    container.addComponentGroup(
                            builder.createLabel(ticketField.getLabel()),
                            customEditor);
                }

                index++;
                //move to next row
                if (index > 1) {
                    container.nextSection();
                    index = 0;
                }
            }
        }

    }

    public void refresh() {
        //desable notify
        builder.setNotifyMode(false);
        loadDates();
        editor.setStatus(
                task.isLocal()
                ? NbBundle.getMessage(TracAttributeHandler.class, "LBL_Local")
                : task.get(STATUS));
        editor.setSummaryText(task.getName());




        descriptionComponent.setText(task.getDescription());
        descriptionComponent.setEditable(!task.isLocal());

        newCommentComponent.setText(task.getNewComment());



        reportedBy.setText(task.get(REPORTER));
        assignee.setText(task.get(OWNER));

        cc.setText(task.get(CC));
        keyWord.setText(task.get(KEYWORDS));

        //set ui to deafulrt
        assignee.setEditable(false);
        resolution.setEnabled(false);
        TracTaskRepository tracRepository = task.getTracRepository();
        TracRepositoryAttributes attributes = tracRepository.getRepositoryAttributes();
        actions.removeAllItems();
        resolution.removeAllItems();
        //load actions if ticket is not local task
        if (task.isLocal()) {
            actions.setEnabled(false);

        } else {
            actions.setEnabled(true);
            List<TicketAction> ticketActions = task.getActions();
            for (TicketAction ticketAction : ticketActions) {
                actions.addItem(ticketAction);
            }
            TicketAction selectedAction = task.getAction();
            if (selectedAction != null) {
                actions.setSelectedItem(selectedAction);
                loadAction(selectedAction);
            } else {
                TicketAction leaveAction = null;
                for (TicketAction ticketAction : ticketActions) {
                    //try to find leave option
                    if (ticketAction.getName().equals(LEAVE)) {
                        leaveAction = ticketAction;
                        break;
                    }
                }
                if (leaveAction != null) {
                    actions.setSelectedItem(leaveAction);
                } else {
                    actions.setSelectedIndex(-1);
                }

            }
            TicketField resolutionField = attributes.getTicketFiledByName(RESOLUTION);
            assert resolutionField != null;
            List<String> options = resolutionField.getOptions();
            TracTaskResolutionProvider provider = tracRepository.getResolutionProvider();
            for (String option : options) {
                TaskResolution taskResolution = provider.getTaskResolutionById(option);
                if (taskResolution != null) {
                    resolution.addItem(taskResolution);
                }
            }
            TaskResolution taskResolution = task.getResolution();
            if (taskResolution != null) {
                resolution.setSelectedItem(taskResolution);
            } else {
                resolution.setSelectedIndex(-1);
            }
        }
        //component field
        TicketField componentField = attributes.getTicketFiledByName(COMPONENT);
        if (componentField != null) {

            String ticketComponent = task.get(COMPONENT);
            _loadCombos(component, componentField.getOptions(),
                    componentField.isOptional(), ticketComponent);
        } else {
            component.setEnabled(false);
        }

        //type field
        TicketField typeField = attributes.getTicketFiledByName(TYPE);
        if (typeField != null) {

            String ticketType = task.get(TYPE);
            _loadCombos(type, typeField.getOptions(),
                    typeField.isOptional(), ticketType);
        } else {
            type.setEnabled(false);
        }
        //priority field
        TicketField priorityField = attributes.getTicketFiledByName(PRIORITY);
        if (priorityField != null) {

            String ticketriority = task.get(PRIORITY);
            _loadCombos(priority, priorityField.getOptions(),
                    priorityField.isOptional(), ticketriority);
        } else {
            priority.setEnabled(false);
        }
        //severity field
        TicketField severityField = attributes.getTicketFiledByName(SEVERITY);
        if (severityField != null) {

            String ticketSeverity = task.get(SEVERITY);
            _loadCombos(severity, severityField.getOptions(),
                    severityField.isOptional(), ticketSeverity);
        } else {
            severity.setEnabled(false);
        }

        //milestone field
        TicketField milestoneField = attributes.getTicketFiledByName(MILESTONE);
        if (milestoneField != null) {
            String ticketMilestone = task.get(MILESTONE);
            _loadCombos(milestone, milestoneField.getOptions(),
                    milestoneField.isOptional(), ticketMilestone);
        } else {
            milestone.setEnabled(false);
        }
        //milestone field
        TicketField versionField = attributes.getTicketFiledByName(VERSION);
        if (versionField != null) {
            String ticketVersion = task.get(VERSION);
            _loadCombos(version, versionField.getOptions(),
                    versionField.isOptional(), ticketVersion);
        } else {
            version.setEnabled(false);
        }
        for (CustomFieldSupport customFieldSupport : customFieldSupports) {
            customFieldSupport.refresh(task);
        }

        //enable notify
        builder.setNotifyMode(true);
        openInBrowserTaskAction.setEnabled(!task.isLocal());
        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
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

    private void loadAction(TicketAction ticketAction) {
        if (ticketAction != null) {
            List<Operation> operations = ticketAction.getOperations();
            for (Operation operation : operations) {
                processOperation(operation);
            }
        }
    }

    public TracTask read() {
        if (!task.getName().equals(editor.getSummaryText())) {
            task.setSummary(editor.getSummaryText());
        }
        task.setDescription(descriptionComponent.getText().trim());
        task.setNewComment(newCommentComponent.getText().trim());
        task.put(CC, cc.getText().trim());
        task.put(KEYWORDS, keyWord.getText().trim());
        task.put(TYPE, getSelectedValve(type));
        task.put(PRIORITY, getSelectedValve(priority));
        task.put(COMPONENT, getSelectedValve(component));
        task.put(SEVERITY, getSelectedValve(severity));
        task.put(VERSION, getSelectedValve(version));
        task.put(MILESTONE, getSelectedValve(milestone));
        String ticketAssignee = assignee.getText().trim();
        if (task.isLocal()) {
            task.put(OWNER, ticketAssignee.length() > 0 ? ticketAssignee : null);
        } else {
            task.setAction((TicketAction) actions.getSelectedItem());
            task.setResolution((TaskResolution) resolution.getSelectedItem());
        }
        //set as modified if already or actuvaly modified
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }
        for (CustomFieldSupport customFieldSupport : customFieldSupports) {
            customFieldSupport.read(task);
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

    private String getSelectedValve(JComboBox comboBox) {
        Object selected = comboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }

    private void loadDates() {

        if (task.getCreatedDate() != 0) {
            editor.setCreatedDate(new Date(task.getCreatedDate()));
        }
        if (task.getUpdatedDate() != 0) {
            editor.setUpdatedDate(new Date(task.getUpdatedDate()));
        }

    }

    private void processOperation(Operation operation) {
        //TODO Extranlze process Operations to support any operation
        if (operation.getName().equals("set_resolution")) {//NOI18N
            resolution.setEnabled(true);
        } else if (operation.getName().equals("set_owner")) {//NOI18N
            assignee.setEditable(true);
        } else if (operation.getName().equals("set_owner_to_self")) {//NOI18N
            assignee.setText(task.getTaskRepository().getUserName());
        } else if (operation.getName().equals("del_resolution")) {//NOI18N
            resolution.setSelectedIndex(-1);
        }
    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                submitTaskAction);
    }

    public JComponent getActionAndPeoplePanel() {
        return actionsContainer;
    }

    public JComponent getAttributesPanel() {
        return arrributesContainer;
    }

    public TextEditorUI getNewCommentComponent() {
        return newCommentComponent;
    }

    public TextEditorUI getDescriptionComponent() {
        return descriptionComponent;
    }

    private List<TicketField> getCustomFields() {
        List<TicketField> customFields = new ArrayList<TicketField>();
        List<String> defaultList = Arrays.asList(
                COMPONENT, CC, TYPE, PRIORITY,
                VERSION, RESOLUTION,
                SUMMARY, STATUS,
                DESCRIPTION, OWNER, REPORTER,
                SEVERITY, MILESTONE, KEYWORDS);
        TracTaskRepository tracRepository = task.getTracRepository();
        TracRepositoryAttributes attributes = tracRepository.getRepositoryAttributes();
        List<TicketField> fields = attributes.getTicketFields();
        for (TicketField ticketField : fields) {
            if (!defaultList.contains(ticketField.getName())) {
                customFields.add(ticketField);
            }
        }
        return customFields;
    }

    private class CustomFieldSupport {

        private final TicketField field;
        private JComponent component;

        private CustomFieldSupport(TicketField field, JComponent component) {
            this.field = field;
            this.component = component;
        }

        private void read(TracTask task) {
            if (field.getType().equals("text")) {
                JTextField textField = (JTextField) component;
                task.put(field.getName(), textField.getText().trim());
            } else if (field.getType().equals("select") || field.getType().equals("radio")) {
                JComboBox comboBox = (JComboBox) component;
                task.put(field.getName(), getSelectedValve(comboBox));
            } else if (field.getType().equals("checkbox")) {
                JCheckBox checkBox = (JCheckBox) component;
                task.put(field.getName(), checkBox.isSelected() ? "1" : "0");
            }
        }

        private void refresh(TracTask task) {
            if (field.getType().equals("text")) {
                JTextField textField = (JTextField) component;
                textField.setText(task.get(field.getName()));
            } else if (field.getType().equals("select") || field.getType().equals("radio")) {
                JComboBox comboBox = (JComboBox) component;
                String ticketComponent = task.get(field.getName());
                _loadCombos(comboBox, field.getOptions(),
                        field.isOptional(), ticketComponent);

            } else if (field.getType().equals("checkbox")) {
                JCheckBox checkBox = (JCheckBox) component;
                checkBox.setSelected("1".equals(task.get(field.getName())));
            }
        }
    }
}
