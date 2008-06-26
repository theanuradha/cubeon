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
package org.netbeans.cubeon.jira.repository.attributes;

/**
 *
 * @author Anuradha
 */
public class JiraProject {

    public final String id;
    public final String name;
    public final String description;
    public final String lead;

    public JiraProject(String id, String name, String description, String lead) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lead = lead;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getLead() {
        return lead;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
