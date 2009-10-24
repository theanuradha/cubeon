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

import java.util.List;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class GCodeUtils {

    public static final String PRIORITY_TAG = "priority-";
    public static final String TYPE_TAG = "type-";

    private GCodeUtils() {
    }

    public static TaskType getTaskType(GCodeTask codeTask) {
        String tagLable = getTagLable(TYPE_TAG, codeTask);
        if (tagLable != null) {
            return new TaskType(codeTask.getTaskRepository(),
                    tagLable, tagLable.replace(TYPE_TAG, ""));
        }
        return null;
    }

    public static void setTaskType(GCodeTask codeTask, TaskType taskType) {
        String tagLable = getTagLable(TYPE_TAG, codeTask);
        if (tagLable != null) {
            codeTask.removeLabel(tagLable);
        }
        if (taskType != null) {
            codeTask.addLabel(taskType.getId());
        }
    }

    public static TaskPriority getTaskPriority(GCodeTask codeTask) {
        String tagLable = getTagLable(PRIORITY_TAG, codeTask);
        if (tagLable != null) {
            return new TaskPriority(codeTask.getTaskRepository(),
                    tagLable, tagLable.replace(PRIORITY_TAG, ""));
        }
        return null;
    }

    public static void setTaskPriority(GCodeTask codeTask, TaskPriority priority) {
        String tagLable = getTagLable(PRIORITY_TAG, codeTask);
        if (tagLable != null) {
            codeTask.removeLabel(tagLable);
        }
        if (priority != null) {
            codeTask.addLabel(priority.getId());
        }

    }

    private static String getTagLable(String tag, GCodeTask codeTask) {
        List<String> labels = codeTask.getLabels();
        for (String label : labels) {
            if (label.toLowerCase().startsWith(tag)) {
                return label;
            }
        }
        return null;
    }

    public static GCodeTask toCodeTask(GCodeTaskRepository repository,GCodeIssue codeIssue){
        GCodeTask codeTask = new GCodeTask(repository, codeIssue.getId(),
                codeIssue.getSummary(), codeIssue.getDescription());
        codeTask.setCreatedDate(codeIssue.getCreatedDate());
        codeTask.setUpdatedDate(codeIssue.getUpdatedDate());
        codeTask.setStatus(codeIssue.getStatus());
        codeTask.setState(codeIssue.getState());
        codeTask.setStars(codeIssue.getStars());
        codeTask.setOwner(codeIssue.getOwner());
        codeTask.addAllCcs(codeIssue.getCcs());
        codeTask.addAllLabels(codeIssue.getLabels());
        codeTask.addAllComments(codeIssue.getComments());
        return  codeTask;
    }
}
