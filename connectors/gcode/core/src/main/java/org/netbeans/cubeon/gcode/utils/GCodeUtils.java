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

import java.util.ArrayList;
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

    public static GCodeTask toCodeTask(GCodeTaskRepository repository, GCodeIssue codeIssue) {
        GCodeTask codeTask = new GCodeTask(repository, codeIssue.getId(),
                codeIssue.getSummary(), codeIssue.getDescription());
        return toCodeTask(codeTask, codeIssue);
    }

    public static GCodeTask toCodeTask(GCodeTask codeTask, GCodeIssue codeIssue) {
        codeTask.setId(codeIssue.getId());
        codeTask.setSummary(codeIssue.getSummary());
        codeTask.setDescription(codeIssue.getDescription());
        codeTask.setCreatedDate(codeIssue.getCreatedDate());
        codeTask.setUpdatedDate(codeIssue.getUpdatedDate());
        codeTask.setStatus(codeIssue.getStatus());
        codeTask.setState(codeIssue.getState());
        codeTask.setStars(codeIssue.getStars());
        codeTask.setOwner(codeIssue.getOwner());
        codeTask.setCcs(codeIssue.getCcs());
        codeTask.setLabels(codeIssue.getLabels());
        codeTask.setComments(codeIssue.getComments());
        return codeTask;
    }

    public static void maregeToTask(GCodeTaskRepository repository, GCodeIssue issue, GCodeTask cachedTask, GCodeTask task) {
        if (cachedTask == null) {
            cachedTask = toCodeTask(repository, issue);
            toCodeTask(cachedTask, task);
        }

        task.setStars(issue.getStars());
        task.setState(issue.getState());
        task.setReportedBy(issue.getReportedBy());

        if ((cachedTask.getStatus() == null && issue.getStatus() != null)
                || !cachedTask.getStatus().equals(issue.getStatus())) {
            task.setStatus(issue.getStatus());
        }

        if ((cachedTask.getSummary() == null && issue.getSummary() != null)
                || !cachedTask.getSummary().equals(issue.getSummary())) {
            task.setSummary(issue.getSummary());
        }

        if ((cachedTask.getDescription() == null && issue.getDescription() != null)
                || !cachedTask.getDescription().equals(issue.getDescription())) {
            task.setDescription(issue.getDescription());
        }

        if ((cachedTask.getOwner() == null && issue.getOwner() != null)
                || !cachedTask.getOwner().equals(issue.getOwner())) {
            task.setOwner(issue.getOwner());
        }

        List<String> labels = task.getLabels();
        
        List<String> remoteLabels = issue.getLabels();
        remoteLabels.removeAll(cachedTask.getLabels());
        
        //remove same tags in local and remote
        labels = _getFillteredLabels(labels, _getLabelTags(remoteLabels));
        //add new labels from remote
        labels.addAll(remoteLabels);
        task.setLabels(labels);        
        List<String> ccs = task.getCcs();
        List<String> remoteCcs = issue.getCcs();
        remoteCcs.removeAll(cachedTask.getCcs());
        ccs.addAll(remoteCcs);
        task.setCcs(ccs);

        //put created and updated date
        task.setCreatedDate(issue.getCreatedDate());
        task.setUpdatedDate(issue.getUpdatedDate());
        task.setId(issue.getId());
        task.setComments(issue.getComments());
    }

    private static List<String> _getLabelTags(List<String> labels) {
        List<String> tags = new ArrayList<String>();
        for (String label : labels) {
            int indexOf = label.indexOf("-");
            if (indexOf != -1) {
                tags.add(label.substring(0, indexOf));
            }
        }
        return tags;
    }
    
    private static List<String> _getFillteredLabels(List<String> labels, List<String> fillteredTags){
        List<String> fillteredLabels = new ArrayList<String>();
        for (String label : labels) {
            int indexOf = label.indexOf("-");
            if (indexOf != -1) {
                String tag = label.substring(0, indexOf);
                if(fillteredTags.contains(tag)){
                    continue;
                }
                fillteredLabels.add(label);
            }
        }
        return fillteredLabels;
    }
}
