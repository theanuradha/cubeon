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
import org.netbeans.cubeon.jira.tasks.JiraTask;

/**
 *
 * @author Anuradha
 */
public class JiraUtils {

    public static RemoteFieldValue[] changedFieldValues(RemoteIssue issue,JiraTask task) {
        List<RemoteFieldValue> fieldValues = new ArrayList<RemoteFieldValue>();
        String description = task.getDescription();
        if(!issue.getDescription().equals(description)){
           fieldValues.add(new RemoteFieldValue("description", new String[]{description}));
        }

        String environment = task.getEnvironment();
        if(!issue.getEnvironment().equals(environment)){
           fieldValues.add(new RemoteFieldValue("environment", new String[]{environment}));
        }


        return fieldValues.toArray(new RemoteFieldValue[fieldValues.size()]);
    }
}
