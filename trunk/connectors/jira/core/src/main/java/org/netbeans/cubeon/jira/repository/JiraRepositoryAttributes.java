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
package org.netbeans.cubeon.jira.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.jira.repository.attributes.JiraProject;

/**
 *
 * @author Anuradha
 */
public class JiraRepositoryAttributes {

    private List<JiraProject> projects = new ArrayList<JiraProject>();
    private List<JiraFilter> filters = new ArrayList<JiraFilter>();

    public List<JiraProject> getProjects() {
        return new ArrayList<JiraProject>(projects);
    }

    public void setProjects(List<JiraProject> projects) {
        this.projects = new ArrayList<JiraProject>(projects);
    }

    public JiraProject getProjectById(String id) {
        for (JiraProject project : projects) {
            if (project.getId().equals(id)) {
                return project;
            }
        }
        return null;
    }
    //--------------------------filters-------------------------

    public List<JiraFilter> getFilters() {
        return new ArrayList<JiraFilter>(filters);
    }

    public void setFilters(List<JiraFilter> projects) {
        this.filters = new ArrayList<JiraFilter>(projects);
    }

    public JiraFilter geFilterById(String id) {
        for (JiraFilter filter : filters) {
            if (filter.getId().equals(id)) {
                return filter;
            }
        }
        return null;
    }
}
