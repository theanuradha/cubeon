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
package org.netbeans.cubeon.bugzilla.api;

import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.post.queries.BaseQuery;

import java.util.Map;
import java.util.List;

import org.netbeans.cubeon.bugzilla.api.model.NewBug;

/**
 * Moxed-mode Bugzilla client interface.
 * Class which will implement this interface will be 100% compatible
 * with the version 3.2 of Bugzilla client.
 *
 * @author radoslaw.holewa
 */
public interface BugzillaClient {

    /**
     * Invokes login operation.
     *
     * @param user     - username
     * @param password - user password
     * @return - map with parameters for eg. id of user
     * @throws BugzillaException - throws exception in case of any problems during login operation
     */
    Map doLogin( String user, String password ) throws BugzillaException;

    /**
     * Returns bug details.
     *
     * @param bugId - bug id
     * @return - bug details
     * @throws BugzillaException - throws exception in case of any problems during getting bug details
     */
    BugDetails getBugDetails( Integer bugId ) throws BugzillaException;

    /**
     * Returns Bugzilla remote repository specific values like products, varsions etc.
     *
     * @return - object with repository-specific attributes
     * @throws BugzillaException - throws exception in case there were any errors during
     *                           attributes retrieving
     */
    RepositoryAttributes getRepositoryAttributes() throws BugzillaException;

    /**
     * Returns list of bugs retrieved using given query.
     *
     * @param query - query used to retrieve list of bugs
     * @return - list of bugs
     * @throws BugzillaException - throws exception in case there were any errors during
     *                           bugs list retrieving
     */
    List<BugSummary> queryForBugs( BaseQuery query ) throws BugzillaException;

    /**
     * Creates new bug in Bugzilla repository.
     *
     * @param bug - the bug to add
     * @return - id of newly created bug
     * @throws BugzillaException - throws exception in case there are any errors during bug adding
     */
    Integer createBug( NewBug bug ) throws BugzillaException;

    /**
     * Returns URL for bug with given id.
     *
     * @param id - task id
     * @return - task URL
     */
    String getBugUrl( String id );
}
