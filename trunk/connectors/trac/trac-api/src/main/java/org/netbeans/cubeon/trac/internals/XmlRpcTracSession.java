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
import java.util.Collection;
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
import org.netbeans.cubeon.trac.api.TicketAction;
import org.netbeans.cubeon.trac.api.TicketChange;
import org.netbeans.cubeon.trac.api.TicketComponent;
import org.netbeans.cubeon.trac.api.TicketField;
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
    private final int epochVersion;
    private final int majorVersion;
    private final int minorVersion;

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
            //check closing '/' is avaiable
            if (!url.trim().endsWith("/") || !url.trim().endsWith("\\")) {
                url += "/";
            }
            //append "/login/xmlrpc" to url
            config.setServerURL(new URL(url + "login/xmlrpc"));//NOI18N
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
             epochVersion = (Integer) versionInfo[0];
             majorVersion = (Integer) versionInfo[1];
             minorVersion = (Integer) versionInfo[2];
          
           
            //validate xmlrpc version
            if(majorVersion<5){
              throw  new TracException("xmlrpc plug-in version not supported:" +
                      " Please Use  cube'n patched XmlRpcPlugin." +
                      "For more information refer tp " +
                      "http://code.google.com/p/cubeon/wiki/GSTracRepository");
            }
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
     * Get All TicketField on remote server
     * @return Trac TicketField
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketField> getTicketFields() throws TracException {
        List<TicketField> ticketFields = new ArrayList<TicketField>();
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
                //create and TicketField
                ticketFields.add(new TicketField(name, label, value, type, b, options));
            }
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ticketFields;
    }

    public Ticket getTicket(int id) throws TracException {
        try {
            Object[] ticketCalls = new Object[2];
            ticketCalls[0] = _createMultiCallElement("ticket.get",//NOI18N
                    new Object[]{id});
            ticketCalls[1] = _createMultiCallElement("ticket.changeLog",//NOI18N
                    new Object[]{id});
            Object[] objects = (Object[]) client.execute("system.multicall",//NOI18N
                    new Object[]{ticketCalls});
            if (objects.length == 2) {
                Ticket ticket = _extractTicket((Object[]) ((Object[]) objects[0])[0]);
                if (ticket != null) {
                    Object[] changes = (Object[]) ((Object[]) objects[1])[0];

                    _extractTicketChanges(ticket, changes);

                }
                return ticket;
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
            HashMap<String, Object> values = (HashMap<String, Object>) result[3];
            //trac 0.12 remove time and changetime from vaules
            values.remove("time");
            values.remove("changetime");
            Set<Entry<String, Object>> entrySet = values.entrySet();
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

    private void _extractTicketChanges(Ticket ticket, Object[] changes) {
        List<TicketChange> ticketChanges = new ArrayList<TicketChange>();
        for (Object change : changes) {
            Object[] result = (Object[]) change;
            //time, author, field, old, new, permanent
            final long time = ((Date) result[0]).getTime();
            final String author = (String) result[1];
            TicketChange tc = new TicketChange(time, author);
            int indexOf = ticketChanges.indexOf(tc);
            if (indexOf != -1) {
                tc = ticketChanges.get(indexOf);
            } else {
                ticketChanges.add(tc);
            }

            final String field = (String) result[2];
            final String oldValue = (String) result[3];
            final String newValue = (String) result[4];
            if (field.equals("comment")) {//NOI18N
                tc.setComment(newValue);
            } else {
                tc.addFieldChange(new TicketChange.FieldChange(field, oldValue, newValue));
            }
        }
        ticket.setTicketChanges(ticketChanges);
    }

    public List<Ticket> getTickets(int... ids) throws TracException {
        List<Ticket> tickets = new ArrayList<Ticket>(ids.length);
        try {
            Object[] ticketCalls = new Object[ids.length * 2];
            int index = 0;
            for (int id : ids) {
                ticketCalls[index] = _createMultiCallElement("ticket.get",//NOI18N
                        new Object[]{id});
                index++;
                ticketCalls[index] = _createMultiCallElement("ticket.changeLog",//NOI18N
                        new Object[]{id});
                index++;
            }
            Object[] objects = (Object[]) client.execute("system.multicall",//NOI18N
                    new Object[]{ticketCalls});

            for (int i = 0; i < objects.length; i++) {
                Object[] ticketResult = (Object[]) objects[i++];
                Object[] changesResult = (Object[]) ((Object[]) objects[i])[0];

                Ticket ticket = _extractTicket((Object[]) ticketResult[0]);
                if (ticket != null) {

                    _extractTicketChanges(ticket, changesResult);


                    tickets.add(ticket);
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
            client.execute("ticket.update",//NOI18N
                    new Object[]{ticket.getTicketId(), comment, ticket.getAttributes(), notify});


            return getTicket(ticket.getTicketId());


        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    public void deleteTicket(Ticket ticket) throws TracException {
        try {
            client.execute("ticket.delete",//NOI18N
                    new Object[]{ticket.getTicketId()});


        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
    }

    public List<TicketAction> getTicketActions(int id) throws TracException {
        List<TicketAction> actions = new ArrayList<TicketAction>();
        try {
            final String methodID;
            //check version id
            if(majorVersion<6){
               methodID="ticket.getAvailableActions";//NOI18N
            }else {
               methodID="ticket.getAvailableCustomActions";//NOI18N
            }
            HashMap result = (HashMap) client.execute(methodID,
                    new Object[]{id});
            Collection maps = result.values();
            for (Object object : maps) {
                HashMap hashMap = (HashMap) object;
                String name = (String) hashMap.get("name");//NOI18N
                TicketAction action = new TicketAction(name);
                Object[] operations = (Object[]) hashMap.get("operations");//NOI18N
                for (Object operation : operations) {
                    action.addOperation(new TicketAction.Operation((String) operation));
                }
                actions.add(action);
            }

        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return actions;
    }

    public List<Integer> queryTickets(String query) throws TracException {
        List<Integer> ids = new ArrayList<Integer>();
        try {
            Object[] result = (Object[]) client.execute("ticket.query",//NOI18N
                    new Object[]{query});

            for (Object object : result) {
                ids.add((Integer) object);
            }

        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return ids;
    }

    public Ticket executeAction(TicketAction action, String comment, Ticket ticket, boolean notify) throws TracException {
        try {
            client.execute("ticket.executeAction",
                    new Object[]{ticket.getTicketId(), action.getName(),
                        comment, ticket.getAttributes(), notify});

        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        }
        return getTicket(ticket.getTicketId());
    }
}
