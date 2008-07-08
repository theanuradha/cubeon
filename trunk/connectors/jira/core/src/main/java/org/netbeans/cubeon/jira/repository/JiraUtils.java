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

import com.dolby.jira.net.soap.jira.RemoteFieldValue;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.jira.repository.attributes.JiraAction;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Component;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject.Version;
import org.netbeans.cubeon.jira.tasks.JiraTask;
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
        if (!issue.getEnvironment().equals(environment)) {
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
        if (action.getFiledIds().contains("resolution") && resolution != null && !resolution.getId().equals(issue.getResolution())) {
            fieldValues.add(new RemoteFieldValue("resolution", new String[]{resolution.getId()}));
        }

        return fieldValues.toArray(new RemoteFieldValue[fieldValues.size()]);
    }
}
