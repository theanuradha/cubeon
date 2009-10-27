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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.cubeon.gcode.api.GCodeComment;
import org.netbeans.cubeon.gcode.api.GCodeState;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.tasks.GCodeTask;

/**
 *
 * @author Anuradha
 */
public class TaskPersistence {

    private final File dir;
    private GCodeTaskRepository repository;
    private List<String> tasks = new ArrayList<String>();
    private int nextId;

    public TaskPersistence(File dir) {
        this.dir = dir;
        _loadTasks();
    }

    public TaskPersistence(File dir, GCodeTaskRepository repository) {
        this.dir = dir;
        this.repository = repository;
        _loadTasks();
    }

    public String nextId() {
        nextId++;
        _persistTasks();
        return String.valueOf(nextId);
    }

    public void remove(GCodeTask codeTask) {
        tasks.remove(codeTask.getId());
        File file = new File(dir, "tasks/" + codeTask.getId() + ".json");
        _remove(file);
        _persistTasks();
    }

    public void removeCache(GCodeTask codeTask) {
        File file = new File(dir, "cache/" + codeTask.getId() + ".json");
        _remove(file);
    }

    private GCodeTask _getGCodeTask(File file) {
        GCodeTask codeTask = null;
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(fileInputStream, "UTF8");
                JSONParser parser = new JSONParser();
                JSONObject jsono = (JSONObject) parser.parse(reader);
                if (jsono != null) {
                    JSONObject taskJs = (JSONObject) jsono.get("task");
                    TaskInfo taskInfo = TaskInfo.toTaskInfo(taskJs);
                    codeTask = toGCodeTask(taskInfo);
                }
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
                }
            }
        }
        return codeTask;
    }

    private void _remove(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public void persist(GCodeTask codeTask) {
        File file = new File(dir, "tasks/" + codeTask.getId() + ".json");
        _persist(codeTask, file);
        if (!tasks.contains(codeTask.getId())) {
            tasks.add(codeTask.getId());
        }
        _persistTasks();
    }

    public void persistCache(GCodeTask codeTask) {
        File file = new File(dir, "cache/" + codeTask.getId() + ".json");
        _persist(codeTask, file);
    }

    private void _persist(GCodeTask codeTask, File file) {
        TaskInfo taskInfo = toTaskInfo(codeTask);
        FileOutputStream fileOutputStream = null;
        try {
            JSONObject jsonRepos = new JSONObject();
            file.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(file);
            jsonRepos.put("task", taskInfo);
            jsonRepos.put("version", "1");
            Writer writer = new OutputStreamWriter(fileOutputStream, "UTF8");
            String json = new JsonIndenter(jsonRepos.toJSONString()).result();
            writer.write(json);
            writer.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(TaskPersistence.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TaskPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    public GCodeTask getGCodeTask(String id) {

        File file = new File(dir, "tasks/" + id + ".json");
        return _getGCodeTask(file);
    }

    public GCodeTask getCachedGCodeTask(String id) {

        File file = new File(dir, "cache/" + id + ".json");
        return _getGCodeTask(file);
    }

    private void _loadTasks() {
        File file = new File(dir, "repository.json");
        if (file.exists()) {

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(fileInputStream, "UTF8");
                JSONParser parser = new JSONParser();
                JSONObject jsono = (JSONObject) parser.parse(reader);
                if (jsono != null) {
                    nextId = JSONUtils.getIntValue(jsono, "next-id");
                    tasks = JSONUtils.getStrings(jsono, "tasks");
                }
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
                }
            }
        }
    }

    private void _persistTasks() {
        FileOutputStream fileOutputStream = null;
        try {
            JSONObject jsonRepos = new JSONObject();
            File file = new File(dir, "repository.json");
            file.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(file);
            jsonRepos.put("tasks", tasks);
            jsonRepos.put("next-id", nextId);
            jsonRepos.put("version", "1");
            Writer writer = new OutputStreamWriter(fileOutputStream, "UTF8");
            String json = new JsonIndenter(jsonRepos.toJSONString()).result();
            writer.write(json);
            writer.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(TaskPersistence.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TaskPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    private GCodeTask toGCodeTask(TaskInfo taskInfo) {
        GCodeTask codeTask = new GCodeTask(repository, taskInfo.getId(),
                taskInfo.getSummary(), taskInfo.getDescription());

        codeTask.setId(taskInfo.getId());
        codeTask.setSummary(taskInfo.getSummary());
        codeTask.setDescription(taskInfo.getDescription());
        codeTask.setReportedBy(taskInfo.getReportedBy());
        codeTask.setStatus(taskInfo.getStatus());
        codeTask.setState(GCodeState.valueOf(taskInfo.getState()));
        codeTask.setOwner(taskInfo.getOwner());
        codeTask.setCreatedDate(taskInfo.getCreatedDate());
        codeTask.setUpdatedDate(taskInfo.getUpdatedDate());
        codeTask.addAllLabels(taskInfo.getLabels());
        codeTask.addAllCcs(taskInfo.getCcs());
        codeTask.setStars(taskInfo.getStars());
        codeTask.setLocal(taskInfo.isLocal());
        codeTask.setModifiedFlag(taskInfo.isModifiedFlag());
        codeTask.setNewComment(taskInfo.getNewComment());
        List<GCodeComment> codeComments = new ArrayList<GCodeComment>();
        for (TaskCommentInfo taskCommentInfo : taskInfo.getComments()) {
            GCodeComment codeComment = new GCodeComment(taskCommentInfo.getCommentId(),
                    taskInfo.getReportedBy());
            codeComment.setComment(taskCommentInfo.getComment());
            codeComment.setCommentDate(taskCommentInfo.getCommentDate());
            codeComment.setAuthor(taskCommentInfo.getAuthor());
            codeComment.setSummary(taskCommentInfo.getSummary());
            codeComment.setStatus(taskCommentInfo.getStatus());
            codeComment.addAllLabels(taskCommentInfo.getLabels());
            codeComment.addAllCcs(taskCommentInfo.getCcs());

            codeComments.add(codeComment);
        }
        codeTask.addAllComments(codeComments);
        return codeTask;
    }

    private TaskInfo toTaskInfo(GCodeTask codeTask) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(codeTask.getId());
        taskInfo.setSummary(codeTask.getSummary());
        taskInfo.setDescription(codeTask.getDescription());
        taskInfo.setReportedBy(codeTask.getReportedBy());
        taskInfo.setStatus(codeTask.getStatus());
        taskInfo.setState(codeTask.getState().name());
        taskInfo.setOwner(codeTask.getOwner());
        taskInfo.setCreatedDate(codeTask.getCreatedDate());
        taskInfo.setUpdatedDate(codeTask.getUpdatedDate());
        taskInfo.setLabels(codeTask.getLabels());
        taskInfo.setCcs(codeTask.getCcs());
        taskInfo.setStars(codeTask.getStars());
        taskInfo.setLocal(codeTask.isLocal());
        taskInfo.setModifiedFlag(codeTask.isModifiedFlag());
        taskInfo.setNewComment(codeTask.getNewComment());
        List<TaskCommentInfo> commentInfos = new ArrayList<TaskCommentInfo>();

        for (GCodeComment comment : codeTask.getComments()) {
            TaskCommentInfo commentInfo = new TaskCommentInfo();
            commentInfo.setCommentId(comment.getCommentId());
            commentInfo.setAuthor(comment.getAuthor());
            commentInfo.setComment(comment.getComment());
            commentInfo.setCommentDate(comment.getCommentDate());
            commentInfo.setSummary(comment.getSummary());
            commentInfo.setStatus(comment.getStatus());
            commentInfo.setLabels(comment.getLabels());
            commentInfo.setCcs(comment.getCcs());
            commentInfos.add(commentInfo);
        }
        taskInfo.setComments(commentInfos);
        return taskInfo;
    }

    public List<String> getTaskIds() {
        return new ArrayList<String>(tasks);
    }
}
