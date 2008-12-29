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

package org.netbeans.cubeon.bugzilla.core.repository;

import java.util.List;

/**
 * Bugzilla repository attributes retreived from remote Bugzilla repository.
 *
 * @author radoslaw.holewa
 */
public interface BugzillaRepositoryAttributes {

    /**
     * Returns list of all available statuses.
     *
     * @return - list of statuses
     */
    public List<String> getStatuses();

    /**
     * Returns list of all available statuses for closed bug.
     *
     * @return - list of statuses
     */
    public List<String> getClosedStatuses();

    /**
     * Returns list of all available statuses for opened bug.
     *
     * @return - list of statuses
     */
    public List<String> getOpenStatuses();

    /**
     * Returns list of all available resolutions.
     *
     * @return - list of resolutions
     */
    public List<String> getResolutions();

    /**
     * Returns list of all available keywords.
     *
     * @return - list of keywords
     */
    public List<String> getKeywords();

    /**
     * Returns list of all available platforms.
     *
     * @return - list of platforms
     */
    public List<String> getPlatforms();

    /**
     * Returns list of all available operating systems.
     *
     * @return - list of operating systems
     */
    public List<String> getOperatingSystems();

    /**
     * Returns list of all available priorities.
     *
     * @return - list of priorities
     */
    public List<String> getPriorities();

    /**
     * Returns list of all available severities.
     *
     * @return - list of severities
     */
    public List<String> getSeverities();

    /**
     * Returns list of all available products.
     *
     * @return - list of products
     */
    public List<String> getProducts();

    /**
     * Returns default status for new bug.
     *
     * @return - default status
     */
    public String getDefaultStatus();

    /**
     * Returns default severity for new bug.
     *
     * @return - default severity
     */
    public String getDefaultSeverity();

    /**
     * Returns default resolution for new bug.
     *
     * @return - default resoulution
     */
    public String getDefaultResolution();

    /**
     * Returns default priority for new bug.
     *
     * @return - default priority
     */
    public String getDefaultPriority();

    /**
     * Returns default product for new bug.
     *
     * @return - default product
     */
    public String getDefaultProduct();

    /**
     * Returns default operating system for new bug.
     *
     * @return - default operating system
     */
    public String getDefaultOperatingSystem();

}
