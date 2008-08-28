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

import java.util.List;
import junit.framework.TestCase;
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

/**
 *
 * @author Anuradha
 */
public class XmlRpcTracSessionTest extends TestCase {

    XmlRpcTracSession tracSession;

    public XmlRpcTracSessionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            tracSession = new XmlRpcTracSession("http://192.168.1.100:8000/argos-dev",
                    "anuradha", "a123");
        } catch (TracException tracException) {
            System.out.println(tracException.getMessage());
            System.out.println("XmlRpcTracSession : Connection Not found. " +
                    "Check you Test trac repo url,username,password");
            System.out.println("IGNORING XmlRpcTracSessionTest !!!");
        }
    }

    /**
     * Test of getTicketTypes method, of class XmlRpcTracSession.
     */
    public void testGetTicketTypes() throws Exception {
        System.out.println("getTicketTypes");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketType> result = instance.getTicketTypes();
        assertNotNull(result);

    }

    /**
     * Test of getTicketPriorities method, of class XmlRpcTracSession.
     */
    public void testGetTicketPriorities() throws Exception {
        System.out.println("getTicketPriorities");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketPriority> result = instance.getTicketPriorities();
        assertNotNull(result);
    }

    /**
     * Test of getTicketComponents method, of class XmlRpcTracSession.
     */
    public void testGetTicketComponents() throws Exception {
        System.out.println("getTicketComponents");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketComponent> result = instance.getTicketComponents();
        assertNotNull(result);
    }

    /**
     * Test of getTicketVersions method, of class XmlRpcTracSession.
     */
    public void testGetTicketVersions() throws Exception {
        System.out.println("getTicketVersions");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketVersion> result = instance.getTicketVersions();
        assertNotNull(result);
    }

    /**
     * Test of getTicketSeverities method, of class XmlRpcTracSession.
     */
    public void testGetTicketSeverities() throws Exception {
        System.out.println("getTicketSeverities");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketSeverity> result = instance.getTicketSeverities();
        assertNotNull(result);
    }

    /**
     * Test of getTicketMilestones method, of class XmlRpcTracSession.
     */
    public void testGetTicketMilestones() throws Exception {
        System.out.println("getTicketMilestones");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketMilestone> result = instance.getTicketMilestones();
        assertNotNull(result);
    }

    /**
     * Test of getTicketResolutions method, of class XmlRpcTracSession.
     */
    public void testGetTicketResolutions() throws Exception {
        System.out.println("getTicketResolutions");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketResolution> result = instance.getTicketResolutions();
        assertNotNull(result);
    }

    /**
     * Test of getTicketStatuses method, of class XmlRpcTracSession.
     */
    public void testGetTicketStatuses() throws Exception {
        System.out.println("getTicketStatuses");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {

            return;
        }
        List<TicketStatus> result = instance.getTicketStatuses();
        assertNotNull(result);
    }

    /**
     * Test of getTicketFileds method, of class XmlRpcTracSession.
     */
    public void testGetTicketFileds() throws Exception {
        System.out.println("getTicketFileds");
        XmlRpcTracSession instance = tracSession;
        if (tracSession == null) {
            return;
        }
        List<TicketFiled> result = instance.getTicketFileds();
        assertNotNull(result);
    }
}
