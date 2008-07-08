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
package org.netbeans.cubeon.jira.remote;

import com.dolby.jira.net.soap.jira.JiraSoapService;
import com.dolby.jira.net.soap.jira.JiraSoapServiceServiceLocator;
import com.dolby.jira.net.soap.jira.RemoteAuthenticationException;
import com.dolby.jira.net.soap.jira.RemoteComponent;
import com.dolby.jira.net.soap.jira.RemoteConfiguration;
import com.dolby.jira.net.soap.jira.RemoteField;
import com.dolby.jira.net.soap.jira.RemoteFieldValue;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import com.dolby.jira.net.soap.jira.RemoteIssueType;
import com.dolby.jira.net.soap.jira.RemoteNamedObject;
import com.dolby.jira.net.soap.jira.RemotePriority;
import com.dolby.jira.net.soap.jira.RemoteProject;
import com.dolby.jira.net.soap.jira.RemoteResolution;
import com.dolby.jira.net.soap.jira.RemoteStatus;
import com.dolby.jira.net.soap.jira.RemoteVersion;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author Anuradha G
 */
public class JiraSession {

    private String token;
    private JiraSoapService service;

    public JiraSession(String url, String user, String pass) throws JiraException {
        try {


            JiraSoapServiceServiceLocator fJiraSoapServiceGetter = new JiraSoapServiceServiceLocator();

            String serverURL = url;
            String endPoint = "/rpc/soap/jirasoapservice-v2";
            fJiraSoapServiceGetter.setJirasoapserviceV2EndpointAddress(serverURL + endPoint);
            fJiraSoapServiceGetter.setMaintainSession(true);
            service = fJiraSoapServiceGetter.getJirasoapserviceV2();
            try {

                token = service.login(user, pass);
            } catch (RemoteAuthenticationException ex) {
                throw new JiraException(ex);
            } catch (com.dolby.jira.net.soap.jira.RemoteException ex) {
                throw new JiraException(ex);
            } catch (RemoteException ex) {
                throw new JiraException(ex);
            }

        } catch (ServiceException ex) {
            throw new JiraException(ex);
        }

    }

    public RemoteStatus[] getStatuses() throws JiraException {
        try {
            return service.getStatuses(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteResolution[] getResolutions() throws JiraException {
        try {
            return service.getResolutions(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemotePriority[] getPriorities() throws JiraException {
        try {
            return service.getPriorities(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteIssueType[] getIssueTypes() throws JiraException {
        try {
            return service.getIssueTypes(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteIssueType[] getIssueTypesForProject(String projectID) throws JiraException {
        try {
            return service.getIssueTypesForProject(token, projectID);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteConfiguration getConfiguration() throws JiraException {
        try {
            return service.getConfiguration(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteProject[] getProjects() throws JiraException {
        try {
            return service.getProjects(token);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteIssue createTask(RemoteIssue issue) throws JiraException {
        try {
            return service.createIssue(token, issue);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteIssue updateTask(String id, RemoteFieldValue[] fieldValues) throws JiraException {
        try {


            return service.updateIssue(token, id, fieldValues);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }

    }

    public RemoteIssue progressWorkflowAction(String issueId, String actionId, RemoteFieldValue[] arg3) throws JiraException {
        try {

            return service.progressWorkflowAction(token, issueId, actionId, arg3);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteIssue getIssue(String id) throws JiraException {
        try {
            return service.getIssue(token, id);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteField[] getFieldsForAction(String issueId, String actionId) throws JiraException {
        try {
            return service.getFieldsForAction(token, issueId, actionId);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteNamedObject[] getAvailableActions(String issueId) throws JiraException {
        try {
            return service.getAvailableActions(token, issueId);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteVersion[] getVersions(String projectID) throws JiraException {
        try {
            return service.getVersions(token, projectID);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }

    public RemoteComponent[] getComponents(String ProjectId) throws JiraException {
        try {
            return service.getComponents(token, ProjectId);
        } catch (Exception ex) {
            throw new JiraException(ex);
        }
    }
}
