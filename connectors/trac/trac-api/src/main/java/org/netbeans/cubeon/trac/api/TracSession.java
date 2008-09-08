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
package org.netbeans.cubeon.trac.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Anuradha
 */
public interface TracSession {

    /**
     * Get All TicketTypess on remote server
     * @return Trac TicketTypes
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketType> getTicketTypes() throws TracException;

    /**
     * Get All TicketPriorities on remote server
     * @return Trac TicketPriority
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketPriority> getTicketPriorities() throws TracException;

    /**
     * Get All TicketComponents on remote server
     * @return Trac TicketComponent
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketComponent> getTicketComponents() throws TracException;

    /**
     * Get All TicketVersions on remote server
     * @return Trac TicketVersion
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public List<TicketVersion> getTicketVersions() throws TracException;

    /**
     * Get All TicketSeverities on remote server
     * @return Trac TicketSeverity
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketSeverity> getTicketSeverities() throws TracException;

    /**
     * Get All TicketMilestones on remote server
     * @return Trac TicketMilestone
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketMilestone> getTicketMilestones() throws TracException;

    /**
     * Get All TicketResolutions on remote server
     * @return Trac TicketResolution
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketResolution> getTicketResolutions() throws TracException;

    /**
     * Get All TicketStatuses on remote server
     * @return Trac TicketStatus
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketStatus> getTicketStatuses() throws TracException;

    /**
     * Get All getTicketFields on remote server
     * @return Trac TicketField
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<TicketField> getTicketFields() throws TracException;

    /**
     * Get Ticket BY ticket Id
     * @param id ticket id
     * @return Ticket may be nul
     * @throws org.netbeans.cubeon.trac.api.TracExceptionl
     */
    Ticket getTicket(int id) throws TracException;

    /**
     *  Get list of tickets by Id's
     * @param ids tikect Ids
     * @return List Of tickets
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<Ticket> getTickets(int... ids) throws TracException;

    /**
     *
     * @param summary
     * @param description
     * @param attributes
     * @param notify
     * @return
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    Ticket createTicket(String summary, String description,
            Map<String, Object> attributes, boolean notify) throws TracException;

    /**
     * 
     * @param ticket
     * @return Updated Ticket
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    Ticket updateTicket(String comment, Ticket ticket, boolean notify) throws TracException;

    /**
     *
     * @param ticket
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    void deleteTicket(Ticket ticket) throws TracException;

    /**
     * Returns the actions that can be performed on the ticket.
     * @param id
     * @return
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<String> getTicketActions(int id) throws TracException;

    /**
     * Perform a ticket query, returning a list of ticket ID's.
     * @param query “query language” for specifying query filters. Basically, the filters are separated by ampersands (&). Each filter then consists of the ticket field name, an operator, and one or more values. More than one value are separated by a pipe (|), meaning that the filter matches any of the values.
     *
     * <br><b>The available operators are:</b></br>
     *  <br>=	 the field content exactly matches the one of the values
     *  <br>~=	 the field content contains one or more of the values
     *  <br>^=	 the field content starts with one of the values
     *  <br>$=	 the field content ends with one of the values
     *  <br>
     * <br><b>All of these operators can also be negated</b></br>
     *  <br>!=	 the field content matches none of the values
     *  <br>!~=	 the field content does not contain any of the values
     *  <br>!^=	 the field content does not start with any of the values
     *  <br>!$=	 the field content does not end with any of the values
     *  <br>
     *  <br<b> Ex: status!=closed&component=Test</b>
     * @return    a list of ticket ID's.
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    List<Integer> queryTickets(String query) throws TracException;
}
