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

/**
 *
 * @author Anuradha
 */
public class GCodeQuery {

    private String publishedMin;
    private String publishedMax;
    private String updatedMin;
    private String updatedMax;
    private int startIndex = 1;
    private int maxResults;
    private String owner;
    private String reporter;
    private String status;
    private String label;
    private String cannedQuery;

    public final String DATE_FORMAT = ("MM-dd-yyy");
    public GCodeQuery() {
    }

    public String getCannedQuery() {
        return cannedQuery;
    }

    public void setCannedQuery(String cannedQuery) {
        this.cannedQuery = cannedQuery;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPublishedMax() {
        return publishedMax;
    }

    public void setPublishedMax(String publishedMax) {
        this.publishedMax = publishedMax;
    }

    public String getPublishedMin() {
        return publishedMin;
    }

    public void setPublishedMin(String publishedMin) {
        this.publishedMin = publishedMin;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

 
    public String getUpdatedMax() {
        return updatedMax;
    }

    public void setUpdatedMax(String updatedMax) {
        this.updatedMax = updatedMax;
    }

    public String getUpdatedMin() {
        return updatedMin;
    }

    public void setUpdatedMin(String updatedMin) {
        this.updatedMin = updatedMin;
    }
}
