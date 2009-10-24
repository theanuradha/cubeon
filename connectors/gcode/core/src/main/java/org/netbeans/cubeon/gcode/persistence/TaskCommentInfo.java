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
public class TaskCommentInfo implements JSONAware {

    private String commentId;
    private String author;
    private String summary;
    private String comment;
    private String owner;
    private String status;
    private long commentDate;
    private List<String> labels = new ArrayList<String>();
    private List<String> ccs = new ArrayList<String>();

    public String toJSONString() {
        JSONObject jsono = new JSONObject();
        jsono.put("comment-id", commentId);
        jsono.put("author", author);
        jsono.put("summary", summary);
        jsono.put("comment", comment);
        jsono.put("comment-date", commentDate);
        jsono.put("owner", owner);
        jsono.put("status", status);
        jsono.put("labels", labels);
        jsono.put("ccs", ccs);

        return jsono.toJSONString();
    }

    static List<TaskCommentInfo> toTaskCommentInfos(JSONArray jSONArray) {
        List<TaskCommentInfo> commentInfos = new ArrayList<TaskCommentInfo>();
        for (Object object : jSONArray) {
           if(object instanceof JSONObject){
               commentInfos.add(toTaskCommentInfo((JSONObject) object));
           }
        }
        return commentInfos;
    }

    static TaskCommentInfo toTaskCommentInfo(JSONObject jsono) {
        TaskCommentInfo commentInfo = new TaskCommentInfo();
        commentInfo.commentId = (String) jsono.get("comment-id");
        commentInfo.author = (String) jsono.get("author");
        commentInfo.summary = (String) jsono.get("summary");
        commentInfo.comment = (String) jsono.get("comment");
        commentInfo.status = (String) jsono.get("status");
        commentInfo.owner = (String) jsono.get("owner");
        commentInfo.commentDate = JSONUtils.getLongValue(jsono, "comment-date");
        commentInfo.ccs = JSONUtils.getStrings(jsono, "ccs");
        commentInfo.labels = JSONUtils.getStrings(jsono, "labels");
        return commentInfo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getCcs() {
        return ccs;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }

    public void setCcs(List<String> ccs) {
        this.ccs = ccs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
}
