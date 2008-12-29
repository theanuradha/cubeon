/*
 *  Copyright 2008 raho.
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
import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;

/**
 *
 * @author raho
 */
public class BugzillaRepositoryAttributesImpl implements BugzillaRepositoryAttributes {

    /**
     * Bugzilla repository attributtes.
     */
    private RepositoryAttributes repositoryAttributes;

    public List<String> getStatuses() {
        return repositoryAttributes.getStatuses();
    }

    public List<String> getClosedStatuses() {
        return repositoryAttributes.getClosedStatuses();
    }

    public List<String> getOpenStatuses() {
        return repositoryAttributes.getOpenStatuses();
    }

    public List<String> getResolutions() {
        return repositoryAttributes.getResolutions();
    }

    public List<String> getKeywords() {
        return repositoryAttributes.getKeywords();
    }

    public List<String> getPlatforms() {
        return repositoryAttributes.getPlatforms();
    }

    public List<String> getOperatingSystems() {
        return repositoryAttributes.getOperatingSystems();
    }

    public List<String> getPriorities() {
        return repositoryAttributes.getPriorities();
    }

    public List<String> getSeverities() {
        return repositoryAttributes.getSeverities();
    }

    public List<String> getProducts() {
        return repositoryAttributes.getProducts();
    }

    public String getDefaultSeverity() {
        return repositoryAttributes.getSeverities().contains("normal") ? "normal" : repositoryAttributes.getSeverities().get(0);
    }

    public String getDefaultStatus() {
        return repositoryAttributes.getOpenStatuses().contains("NEW") ? "NEW" : repositoryAttributes.getOpenStatuses().get(0);
    }

    public String getDefaultResolution() {
        return "";
    }

    public String getDefaultPriority() {
        return repositoryAttributes.getPriorities().get(0);
    }

    public String getDefaultProduct() {
        return repositoryAttributes.getProducts().get(0);
    }

    public String getDefaultOperationgSystem() {
        return repositoryAttributes.getProducts().contains("All") ? "All" : "";
    }
}
