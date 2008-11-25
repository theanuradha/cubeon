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
package org.netbeans.cubeon.trac.repository;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Anuradha G
 */
public class TracAttributesPersistenceTest extends TestCase {

    public TracAttributesPersistenceTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetTagedString() throws Exception {
        System.out.println("getTagedString");
        List<String> strings = new ArrayList<String>();
        strings.add("accepted");
        strings.add("assigned");
        strings.add("closed");
        strings.add("new");
        strings.add("reopened");
        String result = TracAttributesPersistence.getTagedString(strings);
        String expexted = "accepted|assigned|closed|new|reopened|";
        assertEquals(expexted, result);
    }

    public void testGetStringsByTag() throws Exception {
        System.out.println("getStringsByTag");
        String tag = "accepted|assigned|closed|new|reopened|";
        List<String> result = TracAttributesPersistence.getStringsByTag(tag);
        List<String> expexted = new ArrayList<String>();
        expexted.add("accepted");
        expexted.add("assigned");
        expexted.add("closed");
        expexted.add("new");
        expexted.add("reopened");
        assertEquals(expexted, result);
    }
}
