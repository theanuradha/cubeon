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
import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;

/**
 * Bugzilla repository attributes wrapper class that implements 
 * BugzillaRepositor[yAttributes interface.
 *
 * @author radoslaw.holewa
 */
public class BugzillaRepositoryAttributesImpl implements BugzillaRepositoryAttributes {

    /**
     * Bugzilla repository attributtes.
     */
    private RepositoryAttributes repositoryAttributes;

    /**
     * {@inheritDoc}
     */
    public List<String> getStatuses() {
        return repositoryAttributes.getStatuses();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getClosedStatuses() {
        return repositoryAttributes.getClosedStatuses();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getOpenStatuses() {
        return repositoryAttributes.getOpenStatuses();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getResolutions() {
        return repositoryAttributes.getResolutions();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getKeywords() {
        return repositoryAttributes.getKeywords();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPlatforms() {
        return repositoryAttributes.getPlatforms();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getOperatingSystems() {
        return repositoryAttributes.getOperatingSystems();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPriorities() {
        return repositoryAttributes.getPriorities();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSeverities() {
        return repositoryAttributes.getSeverities();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getProducts() {
        return repositoryAttributes.getProducts();
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultSeverity() {
        return repositoryAttributes.getSeverities().contains("normal") ? "normal" : repositoryAttributes.getSeverities().get(0);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultStatus() {
        return repositoryAttributes.getOpenStatuses().contains("NEW") ? "NEW" : repositoryAttributes.getOpenStatuses().get(0);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultResolution() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultPriority() {
        return repositoryAttributes.getPriorities().get(0);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultProduct() {
        return repositoryAttributes.getProducts().get(0);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultOperatingSystem() {
        return repositoryAttributes.getProducts().contains("All") ? "All" : "";
    }
}
