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
package org.netbeans.cubeon.trac.internals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.netbeans.cubeon.trac.api.TicketComponent;
import org.netbeans.cubeon.trac.api.TicketMilestone;
import org.netbeans.cubeon.trac.api.TicketPriority;
import org.netbeans.cubeon.trac.api.TicketResolution;
import org.netbeans.cubeon.trac.api.TicketSeverity;
import org.netbeans.cubeon.trac.api.TicketStatus;
import org.netbeans.cubeon.trac.api.TicketType;
import org.netbeans.cubeon.trac.api.TicketVersion;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracSession;

/**
 *
 * @author Anuradha G
 */
public class XmlRpcTracSession implements TracSession {

    private final XmlRpcClient client;

    /**
     * Create XmlRpcTracSession for trac 
     * @param url
     * @param user
     * @param password
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    XmlRpcTracSession(String url, String user, String password) throws TracException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //append "/login/xmlrpc" to url 
            config.setServerURL(new URL(url + "/login/xmlrpc"));//NOI18N
            //user name and pass to trac
            //TODO may be need to annnonymous 
            config.setBasicUserName(user);
            config.setBasicPassword(password);
            client = new XmlRpcClient();
            client.setConfig(config);
            // validate connitivity and api version
            /**
             * Returns a list with three elements.
             * First element is the epoch (0=Trac 0.10, 1=Trac 0.11 or higher).
             * Second element is the major version number, third is the minor.
             * Changes to the major version indicate API breaking changes,
             * while minor version changes are simple additions, bug fixes, etc.
             */
            Object[] versionInfo = (Object[]) client.execute("system.getAPIVersion()",//NOI18N
                    new Object[0]);
        //TODO validate trac version using versionInfo
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        } catch (MalformedURLException ex) {
            throw new TracException(ex);
        }
    }

    /**
     * Create Multi call Element.
     * @param methodName
     * @param parameters
     * @return 
     */
    private Map<String, Object> _createMultiCallElement(String methodName, Object... parameters) {
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("methodName", methodName);//NOI18N
        table.put("params", parameters);//NOI18N
        return table;
    }

    /**
     * Get All TicketTypes on remote server
     * @return Trac TicketTypes
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketType> getTicketTypes() throws TracException {
        List<TicketType> ticketTypes = new ArrayList<TicketType>();
        try {
            //get a list of all ticket type names.
            Object[] types = (Object[]) client.execute("ticket.type.getAll",//NOI18N 
                    new Object[0]);
            for (Object typeName : types) {
                //get a ticket type.
                String typeId = (String) client.execute("ticket.type.get",//NOI18N
                        new Object[]{typeName});
                //create and TicketType 
                ticketTypes.add(new TicketType(typeId, (String) typeName));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketTypes;
    }

    /**
     * Get All TicketPriorities on remote server
     * @return Trac TicketPriority
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketPriority> getTicketPriorities() throws TracException {
        List<TicketPriority> ticketPriorities = new ArrayList<TicketPriority>();
        try {
            //get a list of all ticket priority names.
            Object[] priorities = (Object[]) client.execute("ticket.priority.getAll",//NOI18N
                    new Object[0]);
            for (Object name : priorities) {
                //get a ticket priority.
                String prioritiesId = (String) client.execute("ticket.priority.get",//NOI18N
                        new Object[]{name});
                //create and TicketPriority 
                ticketPriorities.add(new TicketPriority(prioritiesId, (String) name));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketPriorities;
    }

    /**
     * Get All TicketComponents on remote server
     * @return Trac TicketComponent
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketComponent> getTicketComponents() throws TracException {
        List<TicketComponent> ticketComponents = new ArrayList<TicketComponent>();
        try {
            //get a list of all ticket component names.
            Object[] components = (Object[]) client.execute("ticket.component.getAll",//NOI18N
                    new Object[0]);
            for (Object name : components) {
                //get a ticket component.
                HashMap map = (HashMap) client.execute("ticket.component.get",//NOI18N
                        new Object[]{name});
                //create and TicketComponent 
                //{description, name, owner}

                ticketComponents.add(new TicketComponent((String) map.get("name"),//NOI18N
                        (String) map.get("description"),//NOI18N
                        (String) map.get("owner")));//NOI18N
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketComponents;
    }

    /**
     * Get All TicketVersions on remote server
     * @return Trac TicketVersion
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketVersion> getTicketVersions() throws TracException {
        List<TicketVersion> ticketVersions = new ArrayList<TicketVersion>();
        try {
            //get a list of all ticket version names.
            Object[] versions = (Object[]) client.execute("ticket.version.getAll",//NOI18N
                    new Object[0]);
            for (Object name : versions) {
                //get a ticket version.
                HashMap map = (HashMap) client.execute("ticket.version.get",//NOI18N
                        new Object[]{name});
                //create and TicketComponent 
                // {description=, name, due, completed}
                ticketVersions.add(new TicketVersion((String) map.get("name"),//NOI18N
                        (String) map.get("description")));//NOI18N
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketVersions;
    }

    /**
     * Get All TicketSeverities on remote server
     * @return Trac TicketSeverity
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketSeverity> getTicketSeverities() throws TracException {
        List<TicketSeverity> ticketSeverities = new ArrayList<TicketSeverity>();
        try {
            //get a list of all ticket severity names.
            Object[] severities = (Object[]) client.execute("ticket.severity.getAll",//NOI18N
                    new Object[0]);
            for (Object name : severities) {
                //get a ticket severity.
                String id = (String) client.execute("ticket.severity.get",//NOI18N
                        new Object[]{name});
                //create and TicketSeverity 
                ticketSeverities.add(new TicketSeverity(id, (String) name));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketSeverities;
    }

    /**
     * Get All TicketMilestones on remote server
     * @return Trac TicketMilestone
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketMilestone> getTicketMilestones() throws TracException {
        List<TicketMilestone> ticketMilestones = new ArrayList<TicketMilestone>();
        try {
            //get a list of all ticket milestone names.
            Object[] milestones = (Object[]) client.execute("ticket.milestone.getAll",//NOI18N
                    new Object[0]);
            for (Object name : milestones) {
                //get a ticket milestone.
                HashMap map = (HashMap) client.execute("ticket.milestone.get",//NOI18N
                        new Object[]{name});
                //create and TicketMilestone 
                //{description, name, due, completed}

                ticketMilestones.add(new TicketMilestone((String) map.get("name"), (String) map.get("description")));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketMilestones;
    }

    /**
     * Get All TicketResolutions on remote server
     * @return Trac TicketResolution
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketResolution> getTicketResolutions() throws TracException {
        List<TicketResolution> ticketResolutions = new ArrayList<TicketResolution>();
        try {
            //get a list of all ticket resolution names.
            Object[] resolutions = (Object[]) client.execute("ticket.resolution.getAll",//NOI18N
                    new Object[0]);
            for (Object name : resolutions) {
                //get a ticket resolution.
                String id = (String) client.execute("ticket.resolution.get",//NOI18N
                        new Object[]{name});
                //create and TicketResolution
                ticketResolutions.add(new TicketResolution(id, (String) name));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketResolutions;
    }

    /**
     * Get All TicketStatuses on remote server
     * @return Trac TicketStatus
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketStatus> getTicketStatuses() throws TracException {
        List<TicketStatus> ticketStatuses = new ArrayList<TicketStatus>();
        try {
            //get a list of all ticket status names.
            Object[] statuses = (Object[]) client.execute("ticket.status.getAll",//NOI18N
                    new Object[0]);
            for (Object name : statuses) {
                //get a ticket status.
                String id = (String) client.execute("ticket.status.get",//NOI18N
                        new Object[]{name});
                //create and TicketStatus
                ticketStatuses.add(new TicketStatus(id, (String) name));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketStatuses;
    }
}
