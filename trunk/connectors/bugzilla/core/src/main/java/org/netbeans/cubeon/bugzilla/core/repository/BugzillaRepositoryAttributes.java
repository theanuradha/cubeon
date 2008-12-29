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
    
    public List<String> getStatuses();

    public List<String> getClosedStatuses();

    public List<String> getOpenStatuses();

    public List<String> getResolutions();

    public List<String> getKeywords();

    public List<String> getPlatforms();

    public List<String> getOperatingSystems();

    public List<String> getPriorities();

    public List<String> getSeverities();

    public List<String> getProducts();

    public String getDefaultStatus();

    public String getDefaultSeverity();

    public String getDefaultResolution();

    public String getDefaultPriority();

    public String getDefaultProduct();

    public String getDefaultOperationgSystem();

}
