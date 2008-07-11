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
package org.netbeans.cubeon.jira.repository;

import com.dolby.jira.net.soap.jira.RemoteComponent;
import com.dolby.jira.net.soap.jira.RemoteField;
import com.dolby.jira.net.soap.jira.RemoteFieldValue;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import com.dolby.jira.net.soap.jira.RemoteNamedObject;
import com.dolby.jira.net.soap.jira.RemoteVersion;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.netbeans.cubeon.jira.remote.JiraException;
import org.netbeans.cubeon.jira.remote.JiraSession;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class JiraUtils {

    public static RemoteFieldValue[] changedFieldValues(RemoteIssue issue, JiraTask task) {
        List<RemoteFieldValue> fieldValues = new ArrayList<RemoteFieldValue>();
        String description = task.getDescription();
        if (!issue.getDescription().equals(description)) {
            fieldValues.add(new RemoteFieldValue("description", new String[]{description}));
        }

        String environment = task.getEnvironment();
        if (issue.getEnvironment() == null || !issue.getEnvironment().equals(environment)) {
            fieldValues.add(new RemoteFieldValue("environment", new String[]{environment}));
        }
        String name = task.getName();
        if (!name.equals(issue.getSummary())) {
            fieldValues.add(new RemoteFieldValue("summary", new String[]{name}));
        }
        TaskType type = task.getType();
        if (type != null && !type.getId().equals(issue.getType())) {
            fieldValues.add(new RemoteFieldValue("issuetype", new String[]{type.getId()}));
        }
        TaskPriority priority = task.getPriority();
        if (priority != null && !priority.getId().equals(issue.getPriority())) {
            fieldValues.add(new RemoteFieldValue("priority", new String[]{priority.getId()}));
        }
        if (task.getAssignee() == null || !task.getAssignee().equals(issue.getAssignee())) {
            fieldValues.add(new RemoteFieldValue("assignee", new String[]{task.getAssignee()}));
        }
//        TaskResolution resolution = task.getResolution();
//        if (resolution != null && !resolution.getId().equals(issue.getResolution())) {
//            fieldValues.add(new RemoteFieldValue("resolution", new String[]{resolution.getId()}));
//        }

        if (issue.getResolution() == null) {
            List<Component> components = task.getComponents();
            List<String> componentIds = new ArrayList<String>();

            for (Component component : components) {
                componentIds.add(component.getId());
            }
            fieldValues.add(new RemoteFieldValue("components",
                    componentIds.toArray(new String[componentIds.size()])));
//----------------------------
            List<Version> affectedVersions = task.getAffectedVersions();
            List<String> affectedVersionIds = new ArrayList<String>();

            for (Version version : affectedVersions) {
                affectedVersionIds.add(version.getId());
            }

            fieldValues.add(new RemoteFieldValue("versions",
                    affectedVersionIds.toArray(new String[affectedVersionIds.size()])));
//----------------------------
            List<Version> fixVersions = task.getFixVersions();
            List<String> fixVersionsIds = new ArrayList<String>();

            for (Version version : fixVersions) {
                fixVersionsIds.add(version.getId());
            }

            fieldValues.add(new RemoteFieldValue("fixVersions",
                    fixVersionsIds.toArray(new String[fixVersionsIds.size()])));
        }
        return fieldValues.toArray(new RemoteFieldValue[fieldValues.size()]);
    }

    public static RemoteFieldValue[] changedFieldValuesForAction(JiraAction action, RemoteIssue issue, JiraTask task) {
        List<RemoteFieldValue> fieldValues = new ArrayList<RemoteFieldValue>();


        TaskResolution resolution = task.getResolution();
        List<String> filedIds = action.getFiledIds();
        if (filedIds.contains("resolution") && resolution != null && !resolution.getId().equals(issue.getResolution())) {
            fieldValues.add(new RemoteFieldValue("resolution", new String[]{resolution.getId()}));
        }
        if (filedIds.contains("assignee")) {
            fieldValues.add(new RemoteFieldValue("assignee", new String[]{task.getAssignee()}));
        }
        if (filedIds.contains("fixVersions")) {
            List<Version> fixVersions = task.getFixVersions();
            List<String> fixVersionsIds = new ArrayList<String>();

            for (Version version : fixVersions) {
                fixVersionsIds.add(version.getId());
            }

            fieldValues.add(new RemoteFieldValue("fixVersions",
                    fixVersionsIds.toArray(new String[fixVersionsIds.size()])));
        }
        return fieldValues.toArray(new RemoteFieldValue[fieldValues.size()]);
    }

    public static TaskElement createTaskElement(JiraTaskRepository repository,
            JiraTask jiraTask) throws JiraException {
        String old = jiraTask.getId();
        TaskType prefedTaskType = repository.getJiraTaskTypeProvider().getPrefedTaskType();
        TaskPriority prefredPriority = repository.getJiraTaskPriorityProvider().getPrefredPriority();
        JiraProject prefredProject = repository.getPrefredProject();

        JiraSession js = repository.getSession();
        RemoteIssue issue = new RemoteIssue();
        issue.setSummary(jiraTask.getName());
        issue.setDescription(jiraTask.getDescription());
        issue.setProject(jiraTask.getProject() == null ? prefredProject.getId() : jiraTask.getProject().getId());
        issue.setReporter(repository.getUserName());
        issue.setType(jiraTask.getType() == null ? prefedTaskType.getId() : jiraTask.getType().getId());
        issue.setPriority(jiraTask.getPriority() == null ? prefredPriority.getId() : jiraTask.getPriority().getId());
        issue.setEnvironment(jiraTask.getEnvironment());
        List<Version> affectedVersions = jiraTask.getAffectedVersions();
        RemoteVersion[] versions = new RemoteVersion[affectedVersions.size()];
        for (int i = 0; i < affectedVersions.size(); i++) {
            Version version = affectedVersions.get(i);
            RemoteVersion rv = new RemoteVersion();
            rv.setId(version.getId());
            versions[i] = rv;
        }
        issue.setAffectsVersions(versions);
        issue = js.createTask(issue);


        jiraTask.setUrlString(repository.getURL() + "/browse/" + issue.getKey());//NOI18N
        jiraTask.setId(issue.getKey());
        jiraTask.setLocal(false);
        JiraUtils.maregeToTask(repository, issue, jiraTask);
        repository.persist(jiraTask);
        repository.getExtension().fireIdChanged(old, jiraTask.getId());
        return jiraTask;


    }

    public static void maregeToTask(JiraTaskRepository repository, RemoteIssue issue, JiraTask jiraTask) throws JiraException {
        jiraTask.setName(issue.getSummary());
        jiraTask.setDescription(issue.getDescription());
        jiraTask.setEnvironment(issue.getEnvironment());
        JiraProject project = repository.getRepositoryAttributes().
                getProjectById(issue.getProject());
        jiraTask.setProject(project);
        jiraTask.setType(repository.getJiraTaskTypeProvider().getTaskTypeById(issue.getType()));
        jiraTask.setPriority(repository.getJiraTaskPriorityProvider().getTaskPriorityById(issue.getPriority()));

        if (jiraTask.getStatus() == null ||
                !issue.getStatus().equals(jiraTask.getStatus().getId())) {
            jiraTask.setAction(null);
            jiraTask.setResolution(repository.getJiraTaskResolutionProvider().getTaskResolutionById(issue.getResolution()));

            jiraTask.setStatus(repository.getJiraTaskStatusProvider().getTaskStatusById(issue.getStatus()));
        }

        jiraTask.setReporter(issue.getReporter());
        jiraTask.setAssignee(issue.getAssignee());

        //----------------------------------------------------------------------
        RemoteComponent[] components = issue.getComponents();
        List<JiraProject.Component> cs = new ArrayList<JiraProject.Component>();
        for (RemoteComponent rc : components) {
            Component component = project.getComponentById(rc.getId());
            if (component != null) {
                cs.add(component);
            }
        }
        jiraTask.setComponents(cs);

        //----------------------------------------------------------------------
        RemoteVersion[] affectsRemoteVersions = issue.getAffectsVersions();
        List<JiraProject.Version> affectsVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : affectsRemoteVersions) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                affectsVersions.add(version);
            }
        }
        jiraTask.setAffectedVersions(affectsVersions);
        //----------------------------------------------------------------------
        RemoteVersion[] rvs = issue.getFixVersions();
        List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : rvs) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                fixVersions.add(version);
            }
        }
        jiraTask.setFixVersions(fixVersions);
        //----------------------------------------------------------------------

        Calendar created = issue.getCreated();
        if (created != null) {
            jiraTask.setCreated(created.getTime());
        }
        Calendar updated = issue.getUpdated();
        if (updated != null) {
            jiraTask.setUpdated(updated.getTime());
        }

        List<JiraAction> actions = new ArrayList<JiraAction>();
        RemoteNamedObject[] availableActions = repository.getSession().
                getAvailableActions(issue.getKey());
        for (RemoteNamedObject rno : availableActions) {
            JiraAction action = new JiraAction(rno.getId(), rno.getName());
            RemoteField[] fields = repository.getSession().
                    getFieldsForAction(issue.getKey(), rno.getId());
            for (RemoteField rf : fields) {
                action.addFiled(rf.getId());
            }
            actions.add(action);
        }
        jiraTask.setActions(actions);

    }
}
