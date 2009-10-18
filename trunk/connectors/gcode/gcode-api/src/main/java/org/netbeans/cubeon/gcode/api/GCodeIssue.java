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
public class GCodeIssue {

    private String id;
    private String summary;
    private String description;
    private String status;
    private GCodeState state;
    private String owner;
    private String reportedBy;
    private long createdDate = 0;
    private long updatedDate = 0;
    private List<String> lables = new ArrayList<String>();
    private List<String> ccs = new ArrayList<String>();
    private List<GCodeComment> comments = new ArrayList<GCodeComment>();
    private int stars;

    public GCodeIssue(String id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
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

    public GCodeState getState() {
        return state;
    }

    public void setState(GCodeState state) {
        this.state = state;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
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

    public boolean removeLable(String lable) {
        return lables.remove(lable);
    }

    public boolean removeAllLables(Collection<? extends String> lables) {
        return this.lables.removeAll(lables);
    }

    public boolean addAllLables(Collection<? extends String> lables) {
        return this.lables.addAll(lables);
    }

    public boolean addLable(String lable) {
        return lables.add(lable);
    }

    public List<String> getLables() {
        return new ArrayList<String>(lables);
    }

    public boolean removeCc(String cc) {
        return ccs.remove(cc);
    }

    public boolean removeAllCcs(Collection<? extends String> ccs) {
        return this.ccs.removeAll(ccs);
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

    public boolean removeComment(GCodeComment codeComment) {
        return comments.remove(codeComment);
    }

    public boolean removeAllComments(Collection<? extends GCodeComment> comments) {
        return this.comments.removeAll(comments);
    }

    public boolean addAllComments(Collection<? extends GCodeComment> codeComments) {
        return this.comments.addAll(codeComments);
    }

    public boolean addComment(GCodeComment comment) {
        return comments.add(comment);
    }

    public List<GCodeComment> getComments() {
        return new ArrayList<GCodeComment>(comments);
    }

    @Override
    public String toString() {
        return id + " - " + summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GCodeIssue other = (GCodeIssue) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

   
}
