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
package org.netbeans.cubeon.api;

import java.util.List;
import junit.framework.TestCase;
import org.netbeans.cubeon.bugzilla.api.BugzillaClient;
import org.netbeans.cubeon.bugzilla.api.MixedModeBugzillaClientImpl;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaConnectionException;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.model.NewBug;
import org.netbeans.cubeon.bugzilla.api.post.queries.SpecificQuery;

/**
 * Test for MixedMode Bugzilla client.
 * In case there were any problems with connection test result will be skipped
 * and will not cause build failure.
 *
 * @author radoslaw.holewa
 */
public class MixedModeBugzillaClientTest extends TestCase {

    private BugzillaClient client;
    private static Integer bugId;

    public MixedModeBugzillaClientTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String repositoryUrl = "https://landfill.bugzilla.org/bugzilla-3.2-branch/";
        try {
            client = new MixedModeBugzillaClientImpl(repositoryUrl, "radoslaw.holewa@gmail.com", "testpassword");
        } catch (BugzillaException bugzillaException) {
            System.out.println(bugzillaException.getMessage());
            System.out.println("MixedModeBugzillaClientTest: Could not login to Bugzilla test repository ("
                    + repositoryUrl + ")");
        }
    }

    public void testQueryForBugs() throws Exception{
        if (client == null) {
            return;
        }
        SpecificQuery query = new SpecificQuery();
        query.setProduct("MxTest2");
        query.setContent("Sample");
        try {
            List<BugSummary> bugs = client.queryForBugs(query);
            assertNotNull("Returned bugs list is NULL", bugs);
        } catch (BugzillaConnectionException e) {
            System.out.println("Error while connecting to Bugzilla repository.");
            System.out.println("SKIPING TEST");
        }
        
    }

    public void testCreateBug() throws Exception {
        if (client == null) {
            return;
        }
        NewBug newBug = new NewBug();
        newBug.setProduct("MxTest2");
        newBug.setComponent("mxcompoentntest2");
        newBug.setSummary("Sample summary");
        newBug.setDescription("Sample description");
        newBug.setVersion("unspecified");
        newBug.setTargetMilestone("---");
        newBug.setOperatingSystem("Windows XP");
        newBug.setPlatform("PC");
        newBug.setPriority("P1");
        newBug.setSeverity("major");
        newBug.setAssignee("radoslaw.holewa@gmail.com");
        try {
            bugId = client.createBug(newBug);
            assertNotNull("Returned bug ID is NULL", bugId);
        } catch (BugzillaConnectionException e) {
            System.out.println("Error while connecting to Bugzilla repository.");
            System.out.println("SKIPING TEST");
        }
    }

    public void testGetBugDetails() throws Exception {
        if (client == null) {
            return;
        }
        try {
            BugDetails bug = client.getBugDetails(bugId);
            assertNotNull("Returned buf details is NULL", bug);
        } catch (BugzillaConnectionException e) {
            System.out.println("Error while connecting to Bugzilla repository.");
            System.out.println("SKIPING TEST");
        }
    }
}
