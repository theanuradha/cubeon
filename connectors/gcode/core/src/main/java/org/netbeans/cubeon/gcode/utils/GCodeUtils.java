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
import java.util.Arrays;
import java.util.List;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.api.GCodeIssueUpdate;
import org.netbeans.cubeon.gcode.api.GCodeSession;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class GCodeUtils {

    public static final String PRIORITY_TAG = "Priority-";
    public static final String TYPE_TAG = "Type-";
    public static final String COMPONENT_TAG = "Component-";
    public static final String OS_TAG = "OpSys-";
    public static final String MILESTONE_TAG = "Milestone-";

    private GCodeUtils() {
    }

    public static TaskType getTaskType(GCodeTask codeTask) {
        String tagLable = getTagLable(TYPE_TAG, codeTask);
        if (tagLable != null) {
            return new TaskType(codeTask.getTaskRepository(),
                    tagLable, _getTagValue(tagLable));
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
                    tagLable, _getTagValue(tagLable));
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
            if (label.startsWith(tag)) {
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
        codeTask.setReportedBy(codeIssue.getReportedBy());
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
                || (cachedTask.getStatus() != null && !cachedTask.getStatus().equals(issue.getStatus()))) {
            task.setStatus(issue.getStatus());
        }

        if ((cachedTask.getSummary() == null && issue.getSummary() != null)
                || (cachedTask.getSummary() != null && !cachedTask.getSummary().equals(issue.getSummary()))) {
            task.setSummary(issue.getSummary());
        }

        if ((cachedTask.getDescription() == null && issue.getDescription() != null)
                || (cachedTask.getDescription() != null && !cachedTask.getDescription().equals(issue.getDescription()))) {
            task.setDescription(issue.getDescription());
        }

        if ((cachedTask.getOwner() == null && issue.getOwner() != null)
                || (cachedTask.getOwner() != null && !cachedTask.getOwner().equals(issue.getOwner()))) {
            task.setOwner(issue.getOwner());
        }

        List<String> labels = task.getLabels();

        List<String> remoteLabels = issue.getLabels();
        remoteLabels.removeAll(cachedTask.getLabels());
        List<String> _getAvailableTags = _getAvailableTags(remoteLabels);
        //remove same tags in local and remote
        labels = _getFillteredLabels(labels, _getAvailableTags);
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

    public static List<String> getLabelTags() {

        return Arrays.asList(PRIORITY_TAG, COMPONENT_TAG, OS_TAG, TYPE_TAG, MILESTONE_TAG);
    }

    public static List<String> getLimitLabelTags() {

        return Arrays.asList(PRIORITY_TAG, TYPE_TAG, MILESTONE_TAG);
    }

    private static String _getTagValue(String lable) {
        int tagIndex = lable.indexOf('-');
        if (tagIndex > -1 && lable.length() > 1) {
            return lable.substring(++tagIndex);
        }
        return lable;
    }

    private static List<String> _getFillteredLabels(List<String> labels, List<String> fillteredTags) {
        List<String> fillteredLabels = new ArrayList<String>();
        OUTER:
        for (String label : labels) {
            for (String tag : fillteredTags) {
                if (label.startsWith(tag)) {
                    continue OUTER;
                }
            }
            fillteredLabels.add(label);
        }
        return fillteredLabels;
    }

    private static List<String> _getAvailableTags(List<String> labels) {
        List<String> tags = new ArrayList<String>();
        for (String label : labels) {
            for (String tag : getLimitLabelTags()) {
                if (label.startsWith(tag)) {
                    tags.add(tag);
                    break;
                }
            }
        }
        return tags;
    }

    public static void createIssue(GCodeTaskRepository repository, GCodeTask task) throws GCodeException {
        if (task.isLocal()) {
            String old = task.getId();
            GCodeSession session = repository.getSession();
            task.setReportedBy(repository.getUser());
            GCodeIssue createdIssue = session.createIssue(task, true);

            repository.getTaskPersistenceHandler().remove(old);
            task.setLocal(false);
            toCodeTask(task, createdIssue);
            repository.persist(task);
            repository.getQuerySupport().getOutgoingQuery().removeTaskId(old);
            //notify about task id changed
            repository.getNotifier().fireIdChanged(old, task.getId());
        }
    }

    public static GCodeIssueUpdate getIssueUpdate(GCodeTaskRepository repository, GCodeTask task) {
        GCodeIssueUpdate update = new GCodeIssueUpdate(task.getId(), repository.getUser());
        update.setComment(task.getNewComment()!=null && task.getNewComment().length()>0 ? task.getNewComment(): "-");
        GCodeTask cachedTask = repository.getTaskPersistenceHandler().getCachedGCodeTask(task.getId());
        if (cachedTask == null || task.getOwner() == null || !task.getOwner().equals(cachedTask.getOwner())) {
            update.setOwner(task.getOwner());
        }
        if (cachedTask == null || task.getStatus() == null || !task.getStatus().equals(cachedTask.getStatus())) {
            update.setStatus(task.getStatus());
        }
        if (cachedTask == null || task.getSummary() == null || !task.getSummary().equals(cachedTask.getSummary())) {
            update.setSummary(task.getSummary());
        }
        if (cachedTask != null) {
            List<String> labels = task.getLabels();
            List<String> cachedLabels = cachedTask.getLabels();
            List<String> removedLabels = new ArrayList<String>(cachedLabels);
            removedLabels.removeAll(labels);
            List<String> addLabels = new ArrayList<String>(labels);
            addLabels.removeAll(cachedLabels);

            for (String label : removedLabels) {
                update.addLabel("-" + label);
            }
            update.addAllLabels(addLabels);

            List<String> ccs = task.getCcs();
            List<String> cachedCcs = cachedTask.getCcs();
            List<String> removedCcs = new ArrayList<String>(cachedCcs);
            removedCcs.removeAll(ccs);
            List<String> addCcs = new ArrayList<String>(ccs);
            addCcs.removeAll(cachedCcs);

            for (String cc : removedCcs) {
                update.addCc("-" + cc);
            }
            update.addAllCcs(addCcs);

        } else {
            update.addAllCcs(task.getCcs());
            update.addAllLabels(task.getLabels());
        }
        return update;
    }
}
