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
package org.netbeans.cubeon.gcode.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public class GCodeComment {

    private String commentId;
    private String author;
    private String summary;
    private String comment;
    private String owner;
    private String status;
    private List<String> labels = new ArrayList<String>();
    private List<String> ccs = new ArrayList<String>();

    public GCodeComment(String commentId, String author) {
        this.commentId = commentId;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public boolean removeLabel(String label) {
        return labels.remove(label);
    }

    public boolean removeAllLabels(Collection<? extends String> labels) {
        return labels.removeAll(labels);
    }

    public boolean addAllLabels(Collection<? extends String> labels) {
        return this.labels.addAll(labels);
    }

    public boolean addLabel(String label) {
        return labels.add(label);
    }

    public List<String> getLabels() {
        return new ArrayList<String>(labels);
    }

    public boolean removeCc(String cc) {
        return ccs.remove(cc);
    }

    public boolean removeAllCcs(Collection<? extends String> ccs) {
        return ccs.removeAll(ccs);
    }

    public boolean addAllCcs(Collection<? extends String> ccs) {
        return this.ccs.addAll(ccs);
    }

    public boolean addCc(String cc) {
        return ccs.add(cc);
    }

    public List<String> getCcs() {
        return new ArrayList<String>(ccs);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GCodeComment other = (GCodeComment) obj;
        if ((this.commentId == null) ? (other.commentId != null) : !this.commentId.equals(other.commentId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.commentId != null ? this.commentId.hashCode() : 0);
        return hash;
    }
}