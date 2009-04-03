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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.TaskEditor;
import org.netbeans.cubeon.common.ui.components.ComponentBuilder;
import org.netbeans.cubeon.common.ui.components.ComponentContainer;
import org.netbeans.cubeon.common.ui.components.TextEditorUI;
import org.netbeans.cubeon.jira.repository.JiraKeys;
import org.netbeans.cubeon.jira.repository.JiraRepositoryAttributes;
import org.netbeans.cubeon.jira.repository.JiraTaskPriorityProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.JiraTaskResolutionProvider;
import org.netbeans.cubeon.jira.repository.JiraTaskType;
import org.netbeans.cubeon.jira.repository.JiraTaskTypeProvider;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
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
import static org.openide.util.NbBundle.*;

/**
 *
 * @author Anuradha
 */
public class JiraTaskEditor {

    private JiraTask task;
    private TaskEditor editor;
    private AtomicBoolean modifiedFlag = new AtomicBoolean(false);
    private JiraAction defaultStatus;
    private final OpenInBrowserTaskAction openInBrowserTaskAction;
    private final SubmitTaskAction submitTaskAction;
    private final OpenTaskHistoryAction openTaskHistoryAction;
    private ComponentBuilder builder = new ComponentBuilder() {

        @Override
        public void valueChanged() {
            fireChangeEvent();
        }
    };
    private final ItemListener projectitemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && builder.isNotifyMode()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        loadProject((JiraProject) project.getSelectedItem());
                    }
                });
            }
        }
    };
    private final ItemListener actionitemListener = new ItemListener() {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && builder.isNotifyMode()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        loadAction((JiraAction) actions.getSelectedItem());
                    }
                });
            }
        }
    };
    private ComponentContainer arrributesContainer = new ComponentContainer();
    private ComponentContainer actionsContainer = new ComponentContainer();
    private final TextEditorUI descriptionComponent = new TextEditorUI();
    private final TextEditorUI newCommentComponent = new TextEditorUI();
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    //fields
    private JList affectVersions;
    private JList components;
    private JList fixVersions;
    private JTextField assignee;
    private JEditorPane environment;
    private JComboBox actions;
    private JComboBox priority;
    private JComboBox project;
    private JComboBox resolution;
    private JComboBox type;
    private JLabel reportedBy;

    public JiraTaskEditor(JiraTask task, TaskEditor editor) {
        this.task = task;
        this.editor = editor;
        openInBrowserTaskAction = new OpenInBrowserTaskAction(task);
        submitTaskAction = new SubmitTaskAction(task);
        openTaskHistoryAction = new OpenTaskHistoryAction(task);
        editor.addSummaryDocumentListener(builder.getDocumentListener());
        descriptionComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        newCommentComponent.getDocument().addDocumentListener(builder.getDocumentListener());
        buildComponents();
    }

    public void refresh() {
        //desable notify
        builder.setNotifyMode(false);
        if (task.getCreated() != null) {
            editor.setCreatedDate((task.getCreated()));
        }
        if (task.getUpdated() != null) {
            editor.setUpdatedDate((task.getUpdated()));
        }
        editor.setStatus(
                task.isLocal()
                ? getMessage(JiraAttributeHandler.class, "LBL_Local")
                : task.getStatus().getText());
        editor.setSummaryText(task.getName());

        descriptionComponent.setText(task.getDescription());
        newCommentComponent.setEditable(!task.isLocal());

        newCommentComponent.setText(task.getNewComment());
        environment.setText(task.getEnvironment());
        reportedBy.setText(task.getReporter());
        assignee.setText(task.getAssignee());

        JiraTaskRepository taskRepository = task.getTaskRepository().getLookup().lookup(JiraTaskRepository.class);
        JiraRepositoryAttributes attributes = taskRepository.getRepositoryAttributes();

        project.removeAllItems();
        List<JiraProject> projects = attributes.getProjects();
        for (JiraProject jiraProject : projects) {
            project.addItem(jiraProject);
        }
        if (task.getProject() != null) {
            project.setSelectedItem(task.getProject());
            loadProject(task.getProject());
            selectItems(fixVersions, task.getFixVersions());
            selectItems(affectVersions, task.getAffectedVersions());
            selectItems(components, task.getComponents());
        } else {
            project.setSelectedIndex(-1);
        }
        priority.removeAllItems();

        JiraTaskPriorityProvider jtpp = taskRepository.getJiraTaskPriorityProvider();
        for (TaskPriority taskPriority : jtpp.getTaskPriorities()) {
            priority.addItem(taskPriority);
        }
        priority.setSelectedItem(task.getPriority());



        type.removeAllItems();

        JiraTaskTypeProvider jttp = taskRepository.getJiraTaskTypeProvider();
        for (JiraTaskType taskType : jttp.getJiraTaskTypes()) {
            if (!taskType.isSubTask() && task.getProject().isTypesSupported(taskType)) {
                type.addItem(taskType);
            }
        }
        if (task.getType() != null && jttp.getTaskTypeById(task.getType().getId()).isSubTask()) {
            type.removeAllItems();
            type.addItem(task.getType());
            type.setEnabled(false);
        }
        type.setSelectedItem(task.getType());

        JiraTaskResolutionProvider jtrp = taskRepository.getJiraTaskResolutionProvider();
        resolution.removeAllItems();
        for (TaskResolution taskResolution : jtrp.getTaskResolutiones()) {

            resolution.addItem(taskResolution);
        }
        if (task.getResolution() == null) {
            resolution.setSelectedIndex(-1);
        } else {
            resolution.setSelectedItem(task.getResolution());
        }
        actions.removeAllItems();
        List<JiraAction> jpActions = task.getActions();
        defaultStatus = new JiraAction("##", "Leave as " + (task.getStatus() != null ? task.getStatus().getText() : "Local Task"));
        actions.addItem(defaultStatus);
        for (JiraAction action : jpActions) {
            actions.addItem(action);
        }
        if (task.getAction() != null) {
            actions.setSelectedItem(task.getAction());
        } else {
            actions.setSelectedItem(defaultStatus);
        }

        //enable notify
        if (task.isLocal()) {
            actions.setEnabled(false);
            resolution.setEnabled(false);
            fixVersions.setEnabled(false);

        } else {
            project.setEnabled(false);
            actions.setEnabled(true);

            validateFiledsForEdit(task);


        }

        loadAction(task.getAction());
        openInBrowserTaskAction.setEnabled(!task.isLocal());
        openTaskHistoryAction.setEnabled(!task.isLocal());
        submitTaskAction.setEnabled(task.isModifiedFlag());
        modifiedFlag.set(false);
        builder.setNotifyMode(true);
    }

    public JiraTask read() {
        if (!task.getName().equals(editor.getSummaryText())) {
            task.setName(editor.getSummaryText());
        }
        task.setDescription(descriptionComponent.getText().trim());
        task.setNewComment(newCommentComponent.getText().trim());

        if (!task.getPriority().equals(priority.getSelectedItem())) {
            task.setPriority((TaskPriority) priority.getSelectedItem());
        }
        if (!assignee.getText().trim().equals(task.getAssignee())) {
            task.setAssignee(assignee.getText().trim());
        }

        task.setEnvironment(environment.getText().trim());
        if (!task.getType().equals(type.getSelectedItem())) {
            task.setType((TaskType) type.getSelectedItem());
        }
        if (task.getResolution() == null || !task.getResolution().equals(resolution.getSelectedItem())) {
            task.setResolution((TaskResolution) resolution.getSelectedItem());
        }
        if (task.getProject() == null || !task.getProject().equals(project.getSelectedItem())) {
            task.setProject((JiraProject) project.getSelectedItem());
        }

        Object action = actions.getSelectedItem();
        if (action == null || !action.equals(defaultStatus)) {
            task.setAction((JiraAction) action);
        }
        List<JiraProject.Component> jpComponents = new ArrayList<JiraProject.Component>();
        Object[] selectedValues = components.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Component) {
                jpComponents.add((Component) object);
            }
        }
        task.setComponents(jpComponents);
        List<JiraProject.Version> jpAffectedVersions = new ArrayList<JiraProject.Version>();
        selectedValues = affectVersions.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                jpAffectedVersions.add((Version) object);
            }
        }
        task.setAffectedVersions(jpAffectedVersions);
        List<JiraProject.Version> jpFixVersions = new ArrayList<JiraProject.Version>();
        selectedValues = fixVersions.getSelectedValues();
        for (Object object : selectedValues) {
            if (object instanceof Version) {
                jpFixVersions.add((Version) object);
            }
        }
        task.setFixVersions(jpFixVersions);
        //set as modified if already or actuvaly modified
        if (task.isModifiedFlag() || modifiedFlag.get()) {
            task.setModifiedFlag(true);
        }

        submitTaskAction.setEnabled(task.isModifiedFlag());
        task.getTaskRepository().persist(task);

        return task;
    }

    private void buildComponents() {
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Project")), project = builder.createComboBox(), new JLabel());
        project.addItemListener(projectitemListener);
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Type")), type = builder.createComboBox(), new JLabel());
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Priority")), priority = builder.createComboBox(), new JLabel());
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Components")), builder.addToScrollPane(components = builder.createListBox()), new JLabel());
        arrributesContainer.nextSection();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Affects_Versions")), builder.addToScrollPane(affectVersions = builder.createListBox()), new JLabel());
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Fix_Versions")), builder.addToScrollPane(fixVersions = builder.createListBox()), new JLabel());
        arrributesContainer.nextSection();
         environment= builder.createEditorField();
        arrributesContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Environment")),  builder.addToScrollPane(environment), new JLabel());
        arrributesContainer.nextSection();
        //build actions and people
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Action")), actions = builder.createComboBox(), new JLabel());
        actions.addItemListener(actionitemListener);
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Reported_By")), reportedBy = builder.createLabelField(" - "));
        //move to next row
        actionsContainer.nextSection();
        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Resolution")), resolution = builder.createComboBox(), new JLabel());

        actionsContainer.addComponentGroup(
                builder.createLabel(NbBundle.getMessage(JiraTaskEditor.class,
                "LBL_Assignee")), assignee = builder.createTextField());
        //move to next row
        actionsContainer.nextSection();
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
            resolution.setEnabled(false);
            if (task.getResolution() == null) {
                resolution.setSelectedIndex(-1);
            } else {
                resolution.setSelectedItem(task.getResolution());
                //if task completed disable assignee
                assignee.setEditable(false);
            }

        } else {
            List<String> filedIds = action.getFiledIds();
            if (filedIds.contains(JiraKeys.RESOLUTION)) {
                resolution.setEnabled(true);
                if (resolution.getSelectedIndex() == -1 && resolution.getItemCount() > 0) {
                    resolution.setSelectedIndex(0);
                }
            } else {
                resolution.setEnabled(false);
                resolution.setSelectedIndex(-1);
            }
            assignee.setEditable(filedIds.contains(JiraKeys.ASSIGNEE));
            fixVersions.setEnabled(filedIds.contains(JiraKeys.FIX_VERSIONS));
        }
    }

    private void loadProject(JiraProject project) {
        //clear all first
        DefaultListModel componentModel = new DefaultListModel();
        DefaultListModel versionModel = new DefaultListModel();

        if (project != null) {
            List<Version> jpVersions = project.getVersions();
            for (Version version : jpVersions) {
                versionModel.addElement(version);
            }
            List<Component> jpComponents = project.getComponents();
            for (Component component : jpComponents) {
                componentModel.addElement(component);
            }
        }
        fixVersions.setModel(versionModel);
        affectVersions.setModel(versionModel);
        components.setModel(componentModel);
    }

    private void validateFiledsForEdit(JiraTask jiraTask) {
        List<String> editFieldIds = jiraTask.getEditFieldIds();

        assignee.setEditable(editFieldIds.contains(JiraKeys.ASSIGNEE));
        components.setEnabled(editFieldIds.contains(JiraKeys.COMPONENTS));
        affectVersions.setEnabled(editFieldIds.contains(JiraKeys.VERSIONS));
        fixVersions.setEnabled(editFieldIds.contains(JiraKeys.FIX_VERSIONS));
        environment.setEnabled(editFieldIds.contains(JiraKeys.ENVIRONMENT));
        priority.setEnabled(editFieldIds.contains(JiraKeys.PRIORITY));
        type.setEnabled(editFieldIds.contains(JiraKeys.TYPE));
    }

    public List<Action> getActions() {
        return Arrays.<Action>asList(
                openInBrowserTaskAction,
                openTaskHistoryAction,
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
}
