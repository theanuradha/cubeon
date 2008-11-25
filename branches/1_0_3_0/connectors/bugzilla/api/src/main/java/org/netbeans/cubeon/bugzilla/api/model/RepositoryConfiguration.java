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
import java.util.List;

/**
 * Contains information about Bugzilla remote repository specific values like available products,
 * versions etc.
 *
 * @author radoslaw.holewa
 */
public class RepositoryConfiguration {

   /**
    * Bugzilla statuses, this list contains all available statuses.
    */
    private List<String> statuses = new ArrayList<String>();

   /**
    * This list contains statuses that might be set to closed bugs.
    */
    private List<String> closedStatuses = new ArrayList<String>();

    /**
     * This list contains statuses that might be set to open bugs.
     */
    private List<String> openStatuses = new ArrayList<String>();

    /**
     * Available resolutions.
     */
    private List<String> resolutions = new ArrayList<String>();
  
    /**
     * Available keywords list.
     */
    private List<String> keywords = new ArrayList<String>();

    /**
     * Available platforms list. 
     */
    private List<String> platforms = new ArrayList<String>();

    /**
     * List of available operating systems.
     */
    private List<String> operatingSystems = new ArrayList<String>();

    /**
     * List of available priorities.
     */
    private List<String> priorities = new ArrayList<String>();

    /**
     * List of available severities.
     */
    private List<String> severities = new ArrayList<String>();

    /**
     * List of available products.
     */
    private List<String> products = new ArrayList<String>();

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public List<String> getClosedStatuses() {
        return closedStatuses;
    }

    public void setClosedStatuses(List<String> closedStatuses) {
        this.closedStatuses = closedStatuses;
    }

    public List<String> getOpenStatuses() {
        return openStatuses;
    }

    public void setOpenStatuses(List<String> openStatuses) {
        this.openStatuses = openStatuses;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<String> resolutions) {
        this.resolutions = resolutions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getOperatingSystems() {
        return operatingSystems;
    }

    public void setOperatingSystems(List<String> operatingSystems) {
        this.operatingSystems = operatingSystems;
    }

    public List<String> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<String> priorities) {
        this.priorities = priorities;
    }

    public List<String> getSeverities() {
        return severities;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
