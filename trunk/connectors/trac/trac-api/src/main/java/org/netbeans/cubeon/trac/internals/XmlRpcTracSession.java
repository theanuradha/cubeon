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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.api.TicketComponent;
import org.netbeans.cubeon.trac.api.TicketFiled;
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
    private XmlRpcClientConfigImpl config;

    /**
     * Create XmlRpcTracSession for trac 
     * @param url
     * @param user
     * @param password
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    XmlRpcTracSession(String url, String user, String password) throws TracException {
        try {
            config = new XmlRpcClientConfigImpl();
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
            Object[] versionInfo = (Object[]) client.execute("system.getAPIVersion",//NOI18N
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
                Object releaseObj = map.get("time");//NOI18N
                Date release = null;
                if (release instanceof Date) {
                    release = (Date) releaseObj;
                }
                ticketVersions.add(new TicketVersion((String) map.get("name"),//NOI18N
                        (String) map.get("description"),//NOI18N
                        release));
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
                Object dueObj = map.get("due");//NOI18N
                Date due = null;
                if (due instanceof Date) {
                    due = (Date) dueObj;
                }
                Object completedObj = map.get("completed");//NOI18N
                boolean completed = completedObj instanceof Boolean ? (Boolean) completedObj : false;
                ticketMilestones.add(new TicketMilestone((String) map.get("name"),//NOI18N
                        (String) map.get("description"), completed,
                        due));
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

    /**
     * Get All TicketFiled on remote server
     * @return Trac TicketFiled
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketFiled> getTicketFileds() throws TracException {
        List<TicketFiled> ticketFileds = new ArrayList<TicketFiled>();
        try {
            //Return a list of all ticket fields fields.
            Object[] milestones = (Object[]) client.execute("ticket.getTicketFields",//NOI18N
                    new Object[0]);
            for (Object o : milestones) {
                //get a ticket field info.
                HashMap map = (HashMap) o;

                //{optional, name, value, label, type, options}

                String name = (String) map.get("name");//NOI18N
                String label = (String) map.get("label");//NOI18N
                String value = (String) map.get("value");//NOI18N
                String type = (String) map.get("type");//NOI18N
                Boolean optional = (Boolean) map.get("optional");//NOI18N
                List<String> options = new ArrayList<String>();
                Object[] oOptions = (Object[]) map.get("options");//NOI18N
                if (oOptions != null) {
                    for (Object object : oOptions) {
                        options.add((String) object);
                    }
                }
                //converte to boolean 
                final boolean b = optional == null ? false : optional;
                //create and TicketFiled
                ticketFileds.add(new TicketFiled(name, label, value, type, b, options));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketFileds;
    }

    public Ticket getTicket(int id) throws TracException {
        try {
            Object[] result = (Object[]) client.execute("ticket.get",//NOI18N
                    new Object[]{id});

            if (result != null && result.length == 4) {

                return _extractTicket(result);
            }
            return null;
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    private Ticket _extractTicket(Object[] result) {
        Ticket ticket = null;

        if (result != null && result.length == 4) {
            int id = (Integer) result[0];
            ticket = new Ticket(id);
            Date cdate = (Date) result[1];
            Date udate = (Date) result[2];
            HashMap<String, Object> vaules = (HashMap<String, Object>) result[3];
            Set<Entry<String, Object>> entrySet = vaules.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                Object value = entry.getValue();
                if (value != null) {
                    ticket.put(entry.getKey(), String.valueOf(value));
                }
            }
            if (cdate != null) {
                ticket.setCreatedDate(cdate.getTime());
            }
            if (udate != null) {
                ticket.setUpdatedDate(udate.getTime());
            }
        }
        return ticket;
    }

    public List<Ticket> getTickets(int... ids) throws TracException {
        List<Ticket> tickets = new ArrayList<Ticket>(ids.length);
        try {
            Object[] ticketCalls = new Object[ids.length];
            int index = 0;
            for (int id : ids) {
                ticketCalls[index] = _createMultiCallElement("ticket.get",//NOI18N
                        new Object[]{id});
                index++;
            }
            Object[] objects = (Object[]) client.execute("system.multicall",//NOI18N
                    new Object[]{ticketCalls});
            for (Object object : objects) {
                Object[] result = (Object[]) object;
                for (Object o : result) {
                    if (o instanceof Object[]) {
                        Ticket ticket = _extractTicket((Object[]) o);
                        if (ticket != null) {
                            tickets.add(ticket);
                        }
                    }
                }

            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return tickets;
    }

    public Ticket createTicket(String summary, String description, Map<String, Object> attributes, boolean notify) throws TracException {
        Ticket ticket = null;
        try {
            Object execute = client.execute("ticket.create",//NOI18N
                    new Object[]{summary, description, attributes, notify});
            if (execute instanceof Integer) {
                ticket = getTicket((Integer) execute);
            }
            return ticket;
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    public Ticket updateTicket(String comment, Ticket ticket, boolean notify) throws TracException {

        try {
            Object execute = client.execute("ticket.update",//NOI18N
                    new Object[]{ticket.getId(), comment, ticket.getAttributes(), notify});
            if (execute instanceof Integer) {
                ticket = getTicket((Integer) execute);
            }
            return ticket;
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    public void deleteTicket(Ticket ticket) throws TracException {
        try {
            Object execute = client.execute("ticket.delete",//NOI18N
                    new Object[]{ticket.getId()});


        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    public List<String> getTicketActions(int id) throws TracException {
        List<String> actions = new ArrayList<String>();
        try {
            Object[] result = (Object[]) client.execute("ticket.getAvailableActions",//NOI18N
                    new Object[]{id});

            for (Object object : result) {
                actions.add((String) object);
            }

        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return actions;
    }
}
