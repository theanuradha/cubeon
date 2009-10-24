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

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author Anuradha
 */
public class TaskInfo implements JSONAware {

    private String id;
    private String summary;
    private String description;
    private String status;
    private String state;
    private String owner;
    private String reportedBy;
    private long createdDate = 0;
    private long updatedDate = 0;
    private List<String> labels = new ArrayList<String>();
    private List<String> ccs = new ArrayList<String>();
    private List<TaskCommentInfo> comments = new ArrayList<TaskCommentInfo>();
    private int stars;
    private boolean local;
    private boolean modifiedFlag;
    private String newComment;

    public String toJSONString() {
        JSONObject jsono = new JSONObject();
        jsono.put("id", id);
        jsono.put("summary", summary);
        jsono.put("description", description);
        jsono.put("status", status);
        jsono.put("state", state);
        jsono.put("owner", owner);
        jsono.put("reported-by", reportedBy);
        jsono.put("created-date", createdDate);
        jsono.put("updated-date", updatedDate);
        jsono.put("labels", labels);
        jsono.put("ccs", ccs);
        jsono.put("comments", comments);
        jsono.put("stars", stars);
        jsono.put("local", local);
        jsono.put("modified-flag", modifiedFlag);
        jsono.put("new-comment", newComment);
        return jsono.toJSONString();
    }

    static TaskInfo toTaskInfo(JSONObject taskJs) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.id = (String) taskJs.get("id");
        taskInfo.summary = (String) taskJs.get("summary");
        taskInfo.description = (String) taskJs.get("description");
        taskInfo.status = (String) taskJs.get("status");
        taskInfo.state = (String) taskJs.get("state");
        taskInfo.owner = (String) taskJs.get("owner");
        taskInfo.reportedBy = (String) taskJs.get("reported-by");
        taskInfo.createdDate = JSONUtils.getLongValue(taskJs, "created-date");
        taskInfo.updatedDate = JSONUtils.getLongValue(taskJs, "updated-date");
        taskInfo.stars = JSONUtils.getIntValue(taskJs, "stars");
        taskInfo.newComment = (String) taskJs.get("newComment");
        taskInfo.local = JSONUtils.getBooleanValue(taskJs, "local");
        taskInfo.modifiedFlag = JSONUtils.getBooleanValue(taskJs, "modified-flag");

        taskInfo.ccs = JSONUtils.getStrings(taskJs, "ccs");
        taskInfo.labels = JSONUtils.getStrings(taskJs, "labels");
        Object comments = taskJs.get("comments");
        if(comments instanceof JSONArray){
            taskInfo.comments = TaskCommentInfo.toTaskCommentInfos((JSONArray)comments);
        }


        return taskInfo;
    }

    public List<String> getCcs() {
        return ccs;
    }

    public void setCcs(List<String> ccs) {
        this.ccs = ccs;
    }

    public List<TaskCommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<TaskCommentInfo> comments) {
        this.comments = comments;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isModifiedFlag() {
        return modifiedFlag;
    }

    public void setModifiedFlag(boolean modifiedFlag) {
        this.modifiedFlag = modifiedFlag;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }
}
