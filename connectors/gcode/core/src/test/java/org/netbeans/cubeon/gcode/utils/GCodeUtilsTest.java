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
package org.netbeans.cubeon.gcode.utils;

import junit.framework.TestCase;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class GCodeUtilsTest extends TestCase {

    public GCodeUtilsTest(String testName) {
        super(testName);
    }

    public void testGetTaskType() {
        System.out.println("getTaskType");
        GCodeTask codeTask = makeNewTask();
        TaskType expResult = new TaskType(null, "Type-Other", "Other");
        TaskType result = GCodeUtils.getTaskType(codeTask);
        assertEquals(expResult, result);
        assertEquals(expResult.getText(), result.getText());
    }

    public void testSetTaskType() {
        System.out.println("setTaskType");
        GCodeTask codeTask = makeNewTask();
        TaskType taskType = new TaskType(null, "Type-UI", "UI");
        GCodeUtils.setTaskType(codeTask, taskType);
        TaskType result = GCodeUtils.getTaskType(codeTask);
        assertEquals(taskType, result);
        assertEquals(taskType.getText(), result.getText());
    }

    public void testGetTaskPriority() {
        System.out.println("getTaskPriority");
        GCodeTask codeTask = makeNewTask();
        TaskPriority expResult = new TaskPriority(null, "Priority-High", "High");
        TaskPriority result = GCodeUtils.getTaskPriority(codeTask);
        assertEquals(expResult, result);
        assertEquals(expResult.getText(), result.getText());

    }

    public void testSetTaskPriority() {
        System.out.println("setTaskPriority");
        GCodeTask codeTask = makeNewTask();
        TaskPriority priority = new TaskPriority(null, "Priority-Low", "Low");
        GCodeUtils.setTaskPriority(codeTask, priority);
        TaskPriority result = GCodeUtils.getTaskPriority(codeTask);
        assertEquals(priority, result);
        assertEquals(priority.getText(), result.getText());
    }


    public void testMaregeToTask() {
        System.out.println("maregeToTask");
        GCodeTaskRepository repository = null;
        GCodeIssue issue = makeNewIssue();
        GCodeTask cachedTask = makeNewTask();
        GCodeTask task = makeNewTask();
        task.removeLabel("Milestone-2009");
        task.removeLabel("Priority-High");
        task.removeCc("user");
        task.addCc("user-1");
        task.addLabel("Priority-Medium");
        task.setStatus("Started");
        
        GCodeUtils.maregeToTask(repository, issue, cachedTask, task);

        assertEquals("Started", task.getStatus());
        assertTrue(task.getLabels().contains("Priority-Low"));
        assertTrue(task.getCcs().contains("user-1"));
        assertTrue(task.getCcs().contains("user-2"));
        assertFalse(task.getLabels().contains("Priority-High"));
        assertFalse(task.getLabels().contains("Priority-Medium"));
        assertFalse(task.getLabels().contains("Milestone-2009"));
        assertFalse(task.getCcs().contains("user"));
    }

    protected GCodeTask makeNewTask() {
        GCodeTask entry = new GCodeTask(null, "test", "issue summary", "issue description");
        entry.setReportedBy("user");
        entry.setStatus(("New"));
        entry.addLabel(("Type-Other"));
        entry.addLabel(("Priority-High"));
        entry.addLabel(("Milestone-2009"));
        entry.addCc("user");
        return entry;
    }

    protected GCodeIssue makeNewIssue() {
        GCodeIssue entry = new GCodeIssue("test", "issue summary", "issue description");
        entry.setReportedBy("user");
        entry.setStatus(("New"));
        entry.addLabel(("Priority-Low"));
        entry.addLabel(("OpSys-All"));
        entry.addLabel(("Milestone-2009"));
        entry.addCc("user");
        entry.addCc("user-2");
        return entry;
    }
}
