/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.internals;

import junit.framework.TestCase;
import org.netbeans.cubeon.gcode.api.GCodeIssue;

/**
 *
 * @author Anuradha
 */
public class GCodeSessionImplTest extends TestCase {

    private String user = null;
    private String password = null;

    public GCodeSessionImplTest(String testName) {
        super(testName);
    }

    public void testGetIssue() throws Exception {
        System.out.println("getIssue");
        GCodeSessionImpl instance = new GCodeSessionImpl("test-cubeon", user, password);
        int id = 3;
        GCodeIssue expResult = null;
        GCodeIssue result = instance.getIssue(id);
        printGCodeIssue(result);
    }

    public static void printGCodeIssue(GCodeIssue codeIssue) {
        System.out.println("Summary : " + codeIssue.getSummary());
        System.out.println("Description : " + codeIssue.getDescription());
        System.out.println("Report By : " + codeIssue.getReportedBy());
        System.out.println("State : " + codeIssue.getState());
        System.out.println("Status : " + codeIssue.getStatus());
        System.out.println("Owner : " + codeIssue.getOwner());
        System.out.println("Stars : " + codeIssue.getStars());
        System.out.println("Lables___________________________");
        for (String lable : codeIssue.getLables()) {
            System.out.println(lable);
        }
        System.out.println("_________________________________");
        System.out.print("Cc: ");
        for (String cc : codeIssue.getCcs()) {
            System.out.print(cc+", ");
        }
        System.out.println("");
    }
}
