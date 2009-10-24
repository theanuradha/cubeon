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
package org.netbeans.cubeon.gcode.persistence;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.internals.GCodeSessionImpl;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.gcode.utils.GCodeUtils;

/**
 *
 * @author Anuradha
 */
public class TaskPersistenceTest extends TestCase {

    public TaskPersistenceTest(String testName) {
        super(testName);
    }

    public void testPersist() throws IOException {
        File file = File.createTempFile("TaskPersistenceTest", null);
        //create temp dir
        file = new File(file.getParentFile(), "TaskPersistence");
        file.mkdirs();
        System.out.println("TaskPersistenceTest");
        TaskPersistence persistence = new TaskPersistence(file);
        try {
            GCodeSessionImpl codeSessionImpl = new GCodeSessionImpl("test-cubeon", null, null);
            GCodeIssue issue = codeSessionImpl.getIssue("3");
            GCodeTask codeTask = GCodeUtils.toCodeTask(null, issue);
            persistence.persist(codeTask);
            System.out.println(new String(
                    RepositoryPersistenceTest.fileToByteArray(new File(file,
                    codeTask.getId() + ".json"))));
            GCodeTask result = persistence.getGCodeTask("3");
            assertEquals(codeTask.getId(), result.getId());
            assertEquals(codeTask.getSummary(), result.getSummary());
            assertEquals(codeTask.getDescription(), result.getDescription());
            assertEquals(codeTask.getStatus(), result.getStatus());
            assertEquals(codeTask.getReportedBy(), result.getReportedBy());
            assertEquals(codeTask.getStars(), result.getStars());
            assertEquals(codeTask.getState(), result.getState());
            assertEquals(codeTask.getCreatedDate(), result.getCreatedDate());
            assertEquals(codeTask.getUpdatedDate(), result.getUpdatedDate());
            assertEquals(codeTask.getNewComment(), result.getNewComment());
            assertEquals(codeTask.isLocal(), result.isLocal());
            assertEquals(codeTask.isModifiedFlag(), result.isModifiedFlag());
            assertTrue(codeTask.getLabels().containsAll(result.getLabels()));
            assertTrue(codeTask.getCcs().containsAll(result.getCcs()));
            assertTrue(codeTask.getComments().size()==result.getComments().size());
        } catch (GCodeException gCodeException) {
            //ignore
            System.out.println("TaskPersistenceTest ignored :" + gCodeException.getMessage());
        }
    }
}
