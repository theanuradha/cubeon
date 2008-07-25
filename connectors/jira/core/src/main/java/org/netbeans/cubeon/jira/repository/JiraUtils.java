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

import com.dolby.jira.net.soap.jira.RemoteComment;
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
import org.netbeans.cubeon.jira.repository.attributes.JiraComment;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraRemoteTask;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import org.netbeans.cubeon.tasks.spi.task.TaskType;
import static org.netbeans.cubeon.jira.repository.JiraKeys.*;

/**
 *
 * @author Anuradha
 */
public class JiraUtils {

    public static RemoteFieldValue[] changedFieldValues(JiraRemoteTask remoteTask, JiraTask task) {
        List<RemoteFieldValue> fieldValues = new ArrayList<RemoteFieldValue>();
        String description = task.getDescription();
        if (!remoteTask.getDescription().equals(description)) {
            fieldValues.add(new RemoteFieldValue(DESCRIPTION, new String[]{description}));
        }

        String environment = task.getEnvironment();
        if (remoteTask.getEnvironment() == null || !remoteTask.getEnvironment().equals(environment)) {
            fieldValues.add(new RemoteFieldValue(ENVIRONMENT, new String[]{environment}));
        }
        String name = task.getName();
        if (!name.equals(remoteTask.getName())) {
            fieldValues.add(new RemoteFieldValue(SUMMERY, new String[]{name}));
        }
        TaskType type = task.getType();
        if (type != null && !type.equals(remoteTask.getType())) {
            fieldValues.add(new RemoteFieldValue(TYPE, new String[]{type.getId()}));
        }
        TaskPriority priority = task.getPriority();
        if (priority != null && !priority.equals(remoteTask.getPriority())) {
            fieldValues.add(new RemoteFieldValue(PRIORITY, new String[]{priority.getId()}));
        }
        if (task.getAssignee() == null || !task.getAssignee().equals(remoteTask.getAssignee())) {
            fieldValues.add(new RemoteFieldValue(ASSIGNEE, new String[]{task.getAssignee()}));
        }


        List<Component> components = task.getComponents();
        List<String> componentIds = new ArrayList<String>();

        for (Component component : components) {
            componentIds.add(component.getId());
        }
        fieldValues.add(new RemoteFieldValue(COMPONENTS,
                componentIds.toArray(new String[componentIds.size()])));
//----------------------------
        List<Version> affectedVersions = task.getAffectedVersions();
        List<String> affectedVersionIds = new ArrayList<String>();

        for (Version version : affectedVersions) {
            affectedVersionIds.add(version.getId());
        }

        fieldValues.add(new RemoteFieldValue(VERSIONS,
                affectedVersionIds.toArray(new String[affectedVersionIds.size()])));
//----------------------------
        List<Version> fixVersions = task.getFixVersions();
        List<String> fixVersionsIds = new ArrayList<String>();

        for (Version version : fixVersions) {
            fixVersionsIds.add(version.getId());
        }

        fieldValues.add(new RemoteFieldValue(FIX_VERSIONS,
                fixVersionsIds.toArray(new String[fixVersionsIds.size()])));

        return fieldValues.toArray(new RemoteFieldValue[fieldValues.size()]);
    }

    public static RemoteFieldValue[] changedFieldValuesForAction(JiraAction action, JiraRemoteTask remoteTask, JiraTask task) {
        List<RemoteFieldValue> fieldValues = new ArrayList<RemoteFieldValue>();


        TaskResolution resolution = task.getResolution();
        List<String> filedIds = action.getFiledIds();
        if (filedIds.contains(RESOLUTION) && resolution != null && !resolution.equals(remoteTask.getResolution())) {
            fieldValues.add(new RemoteFieldValue(RESOLUTION, new String[]{resolution.getId()}));
        }
        if (filedIds.contains(ASSIGNEE)) {
            fieldValues.add(new RemoteFieldValue(ASSIGNEE, new String[]{task.getAssignee()}));
        }
        if (filedIds.contains(FIX_VERSIONS)) {
            List<Version> fixVersions = task.getFixVersions();
            List<String> fixVersionsIds = new ArrayList<String>();

            for (Version version : fixVersions) {
                fixVersionsIds.add(version.getId());
            }

            fieldValues.add(new RemoteFieldValue(FIX_VERSIONS,
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
        issue.setAssignee(jiraTask.getAssignee());
        issue.setType(jiraTask.getType() == null ? prefedTaskType.getId() : jiraTask.getType().getId());
        issue.setPriority(jiraTask.getPriority() == null ? prefredPriority.getId() : jiraTask.getPriority().getId());
        issue.setEnvironment(jiraTask.getEnvironment());
        List<Component> components = jiraTask.getComponents();
        List<RemoteComponent> remoteComponents = new ArrayList<RemoteComponent>();

        for (Component component : components) {
            remoteComponents.add(new RemoteComponent(component.getId(), component.getName()));
        }

        issue.setComponents(remoteComponents.toArray(new RemoteComponent[remoteComponents.size()]));
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

        repository.remove(jiraTask);
        jiraTask.setUrlString(repository.getURL() + "/browse/" + issue.getKey());//NOI18N
        jiraTask.setId(issue.getKey());
        jiraTask.setLocal(false);
        JiraRemoteTask remoteTask = repository.getJiraRemoteTaskCache(issue.getKey());

        JiraUtils.maregeToTask(repository, issue, remoteTask, jiraTask);

        repository.cache(issueToTask(repository, issue));
        repository.persist(jiraTask);
        repository.getExtension().fireIdChanged(old, jiraTask.getId());
        return jiraTask;


    }

    public static void remoteToTask(JiraTaskRepository repository, JiraRemoteTask remoteTask, JiraTask jiraTask) {
        jiraTask.setName(remoteTask.getName());
        jiraTask.setDescription(remoteTask.getDescription());
        jiraTask.setEnvironment(remoteTask.getEnvironment());
        jiraTask.setProject(remoteTask.getProject());
        jiraTask.setType(remoteTask.getType());
        jiraTask.setPriority(remoteTask.getPriority());

        jiraTask.setResolution(remoteTask.getResolution());
        jiraTask.setStatus(remoteTask.getStatus());
        jiraTask.setReporter(remoteTask.getReporter());
        jiraTask.setAssignee(remoteTask.getAssignee());
        //----------------------------------------------------------------------
        jiraTask.setComponents(remoteTask.getComponents());
        //----------------------------------------------------------------------
        jiraTask.setAffectedVersions(remoteTask.getAffectedVersions());
        //----------------------------------------------------------------------
        jiraTask.setFixVersions(remoteTask.getFixVersions());
        //----------------------------------------------------------------------
        jiraTask.setCreated(remoteTask.getCreated());
        jiraTask.setUpdated(remoteTask.getUpdated());
        jiraTask.setComments(remoteTask.getComments());
    }

    public static void maregeToTask(JiraTaskRepository repository, RemoteIssue issue, JiraRemoteTask remoteTask, JiraTask jiraTask) throws JiraException {
        if (remoteTask == null) {
            remoteTask = issueToTask(repository, issue);
        }

        if (!remoteTask.getName().equals(issue.getSummary())) {
            jiraTask.setName(issue.getSummary());
        }
        if (!remoteTask.getDescription().equals(issue.getDescription())) {
            jiraTask.setDescription(issue.getDescription());
        }
        if (!(remoteTask.getEnvironment() == null && (issue.getEnvironment()) == null/*check both null*/) || !remoteTask.getEnvironment().equals(issue.getEnvironment())) {
            jiraTask.setEnvironment(issue.getEnvironment());
        }
        if (!remoteTask.getProject().getId().equals(issue.getProject())) {
            jiraTask.setProject(repository.getRepositoryAttributes().getProjectById(issue.getProject()));
        }
        if (!remoteTask.getType().getId().equals(issue.getType())) {
            jiraTask.setType(repository.getJiraTaskTypeProvider().getTaskTypeById(issue.getType()));
        }
        if (!remoteTask.getPriority().getId().equals(issue.getPriority())) {
            jiraTask.setPriority(repository.getJiraTaskPriorityProvider().getTaskPriorityById(issue.getPriority()));
        }

        if (remoteTask.getStatus() == null ||
                !remoteTask.getStatus().getId().equals(issue.getStatus())) {
            jiraTask.setAction(null);
            jiraTask.setResolution(repository.getJiraTaskResolutionProvider().getTaskResolutionById(issue.getResolution()));

            jiraTask.setStatus(repository.getJiraTaskStatusProvider().getTaskStatusById(issue.getStatus()));
        }
        if (!remoteTask.getReporter().equals(issue.getReporter())) {
            jiraTask.setReporter(issue.getReporter());
        }
        if (!(remoteTask.getAssignee() == null && issue.getAssignee() == null/*check both null*/) || !remoteTask.getAssignee().equals(issue.getAssignee())) {
            jiraTask.setAssignee(issue.getAssignee());
        }
        //----------------------------------------------------------------------
        if (isComponentsChanged(remoteTask.getComponents(), issue.getComponents())) {
            RemoteComponent[] components = issue.getComponents();
            List<JiraProject.Component> cs = new ArrayList<JiraProject.Component>();
            for (RemoteComponent rc : components) {
                Component component = jiraTask.getProject().getComponentById(rc.getId());
                if (component != null) {
                    cs.add(component);
                }
            }
            jiraTask.setComponents(cs);
        }
        //----------------------------------------------------------------------
        if (isVersionsChanged(remoteTask.getAffectedVersions(), issue.getAffectsVersions())) {
            RemoteVersion[] affectsRemoteVersions = issue.getAffectsVersions();
            List<JiraProject.Version> affectsVersions = new ArrayList<JiraProject.Version>();
            for (RemoteVersion rv : affectsRemoteVersions) {
                Version version = jiraTask.getProject().getVersionById(rv.getId());
                if (version != null) {
                    affectsVersions.add(version);
                }
            }
            jiraTask.setAffectedVersions(affectsVersions);
        }
        //----------------------------------------------------------------------
        if (isVersionsChanged(remoteTask.getFixVersions(), issue.getFixVersions())) {
            RemoteVersion[] rvs = issue.getFixVersions();
            List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
            for (RemoteVersion rv : rvs) {
                Version version = jiraTask.getProject().getVersionById(rv.getId());
                if (version != null) {
                    fixVersions.add(version);
                }
            }
            jiraTask.setFixVersions(fixVersions);
        }
        //----------------------------------------------------------------------

        Calendar created = issue.getCreated();
        if (created != null) {
            jiraTask.setCreated(created.getTime());
        }
        Calendar updated = issue.getUpdated();
        if (updated != null) {
            jiraTask.setUpdated(updated.getTime());
        }
        RemoteComment[] comments = repository.getSession().getComments(jiraTask.getId());
        List<JiraComment> jiraComments = new ArrayList<JiraComment>();
        for (RemoteComment comment : comments) {
            JiraComment jiraComment = new JiraComment(comment.getId());
            jiraComment.setAuthor(comment.getAuthor());

            jiraComment.setBody(comment.getBody());
            jiraComment.setCreated(comment.getCreated().getTime());

            jiraComment.setUpdateAuthor(comment.getUpdateAuthor());
            if (comment.getUpdated() != null) {
                jiraComment.setUpdated(comment.getUpdated().getTime());
            }
            jiraComments.add(jiraComment);
        }
        jiraTask.setComments(jiraComments);
        readWorkFlow(repository, jiraTask);


    }

    public static void readWorkFlow(JiraTaskRepository repository, JiraTask jiraTask) throws JiraException {
        List<JiraAction> actions = new ArrayList<JiraAction>();
        RemoteNamedObject[] availableActions = repository.getSession().
                getAvailableActions(jiraTask.getId());
        for (RemoteNamedObject rno : availableActions) {
            JiraAction action = new JiraAction(rno.getId(), rno.getName());
            RemoteField[] fields = repository.getSession().
                    getFieldsForAction(jiraTask.getId(), rno.getId());
            for (RemoteField rf : fields) {
                action.addFiled(rf.getId());
            }
            actions.add(action);
        }
        jiraTask.setActions(actions);


        List<String> editFieldIds = new ArrayList<String>();
        RemoteField[] fieldsForEdit = repository.getSession().getFieldsForEdit(jiraTask.getId());
        for (RemoteField rf : fieldsForEdit) {
            editFieldIds.add(rf.getId());
        }
        jiraTask.setEditFieldIds(editFieldIds);
    }

    public static JiraRemoteTask issueToTask(JiraTaskRepository repository, RemoteIssue issue) throws JiraException {
        JiraRemoteTask remoteTask = new JiraRemoteTask(repository, issue.getKey(), issue.getSummary(), issue.getDescription());

        remoteTask.setEnvironment(issue.getEnvironment());
        JiraProject project = repository.getRepositoryAttributes().
                getProjectById(issue.getProject());
        remoteTask.setProject(project);
        remoteTask.setType(repository.getJiraTaskTypeProvider().getTaskTypeById(issue.getType()));
        remoteTask.setPriority(repository.getJiraTaskPriorityProvider().getTaskPriorityById(issue.getPriority()));


        remoteTask.setResolution(repository.getJiraTaskResolutionProvider().getTaskResolutionById(issue.getResolution()));

        remoteTask.setStatus(repository.getJiraTaskStatusProvider().getTaskStatusById(issue.getStatus()));


        remoteTask.setReporter(issue.getReporter());
        remoteTask.setAssignee(issue.getAssignee());

        //----------------------------------------------------------------------
        RemoteComponent[] components = issue.getComponents();
        List<JiraProject.Component> cs = new ArrayList<JiraProject.Component>();
        for (RemoteComponent rc : components) {
            Component component = project.getComponentById(rc.getId());
            if (component != null) {
                cs.add(component);
            }
        }
        remoteTask.setComponents(cs);

        //----------------------------------------------------------------------
        RemoteVersion[] affectsRemoteVersions = issue.getAffectsVersions();
        List<JiraProject.Version> affectsVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : affectsRemoteVersions) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                affectsVersions.add(version);
            }
        }
        remoteTask.setAffectedVersions(affectsVersions);
        //----------------------------------------------------------------------
        RemoteVersion[] rvs = issue.getFixVersions();
        List<JiraProject.Version> fixVersions = new ArrayList<JiraProject.Version>();
        for (RemoteVersion rv : rvs) {
            Version version = project.getVersionById(rv.getId());
            if (version != null) {
                fixVersions.add(version);
            }
        }
        remoteTask.setFixVersions(fixVersions);
        //----------------------------------------------------------------------

        Calendar created = issue.getCreated();
        if (created != null) {
            remoteTask.setCreated(created.getTime());
        }
        Calendar updated = issue.getUpdated();
        if (updated != null) {
            remoteTask.setUpdated(updated.getTime());
        }


        RemoteComment[] comments = repository.getSession().getComments(issue.getKey());
        List<JiraComment> jiraComments = new ArrayList<JiraComment>();
        for (RemoteComment comment : comments) {
            JiraComment jiraComment = new JiraComment(comment.getId());
            jiraComment.setAuthor(comment.getAuthor());

            jiraComment.setBody(comment.getBody());
            jiraComment.setCreated(comment.getCreated().getTime());

            jiraComment.setUpdateAuthor(comment.getUpdateAuthor());
            if (comment.getUpdated() != null) {
                jiraComment.setUpdated(comment.getUpdated().getTime());
            }
            jiraComments.add(jiraComment);
        }
        remoteTask.setComments(jiraComments);



        return remoteTask;
    }

    private static boolean isComponentsChanged(List<Component> coms, RemoteComponent[] ids) {
        List<Component> components = new ArrayList<Component>(coms);
        List<String> componentIds = new ArrayList<String>(ids.length);
        for (RemoteComponent rc : ids) {
            componentIds.add(rc.getId());
        }
        for (Component component : components) {

            if (!componentIds.remove(component.getId())) {
                return true;
            }
        }

        return componentIds.size() > 0;
    }

    private static boolean isVersionsChanged(List<JiraProject.Version> vs, RemoteVersion[] ids) {
        List<Version> versions = new ArrayList<Version>(vs);
        List<String> versionIds = new ArrayList<String>(ids.length);
        for (RemoteVersion version : ids) {
            versionIds.add(version.getId());
        }
        for (Version version : versions) {

            if (!versionIds.remove(version.getId())) {
                return true;
            }
        }

        return versionIds.size() > 0;
    }
}


