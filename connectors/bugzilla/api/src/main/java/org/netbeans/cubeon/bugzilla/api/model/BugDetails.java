/*
 *  Copyright 2008 Anuradha.
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
package org.netbeans.cubeon.bugzilla.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains bug details data.
 *
 * @author radoslaw.holewa
 */
public class BugDetails {

    private Integer id;
    private Date creationDate;
    private String shortDescription;
    private Date deltaDate;
    private Boolean reporterAccessible;
    private Boolean ccListAccessible;
    private Integer classificationId;
    private String classification;
    private String product;
    private String component;
    private String version;
    private String reportPlatform;
    private String os;
    private String bugStatus;
    private String resolution;
    private String priority;
    private String severity;
    private String targetMilestone;
    private Integer votes;
    private Boolean everConfirmed;
    private User reporter;
    private User assignedTo;
    private List<String> cc = new ArrayList<String>();
    private List<LongDescription> longDescriptions = new ArrayList<LongDescription>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Date getDeltaDate() {
        return deltaDate;
    }

    public void setDeltaDate(Date deltaDate) {
        this.deltaDate = deltaDate;
    }

    public Boolean getReporterAccessible() {
        return reporterAccessible;
    }

    public void setReporterAccessible(Boolean reporterAccessible) {
        this.reporterAccessible = reporterAccessible;
    }

    public Boolean getCcListAccessible() {
        return ccListAccessible;
    }

    public void setCcListAccessible(Boolean ccListAccessible) {
        this.ccListAccessible = ccListAccessible;
    }

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReportPlatform() {
        return reportPlatform;
    }

    public void setReportPlatform(String reportPlatform) {
        this.reportPlatform = reportPlatform;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBugStatus() {
        return bugStatus;
    }

    public void setBugStatus(String bugStatus) {
        this.bugStatus = bugStatus;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTargetMilestone() {
        return targetMilestone;
    }

    public void setTargetMilestone(String targetMilestone) {
        this.targetMilestone = targetMilestone;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Boolean getEverConfirmed() {
        return everConfirmed;
    }

    public void setEverConfirmed(Boolean everConfirmed) {
        this.everConfirmed = everConfirmed;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<LongDescription> getLongDescriptions() {
        return longDescriptions;
    }

    public void setLongDescriptions(List<LongDescription> longDescriptions) {
        this.longDescriptions = longDescriptions;
    }
}
