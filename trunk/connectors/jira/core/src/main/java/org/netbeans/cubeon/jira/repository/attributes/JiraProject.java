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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class JiraProject {

    private final String id;
    private final String name;
    private final String description;
    private final String lead;
    private List<Component> components = new ArrayList<Component>(0);
    private List<Version> versions = new ArrayList<Version>(0);
    private List<JiraUser> users = new ArrayList<JiraUser>(0);
    private List<String> types = new ArrayList<String>(0);

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

    public List<Component> getComponents() {
        return new ArrayList<Component>(components);
    }

    public void setComponents(List<Component> components) {
        this.components = new ArrayList<Component>(components);
    }

    public Component getComponentById(String id) {
        for (Component component : components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }

    public List<Version> getVersions() {
        return new ArrayList<Version>(versions);
    }

    public void setVersions(List<Version> versions) {
        this.versions = new ArrayList<Version>(versions);
    }

    public Version getVersionById(String id) {
        for (Version version : versions) {
            if (version.getId().equals(id)) {
                return version;
            }
        }
        return null;
    }

    public List<JiraUser> getUsers() {
        return new ArrayList<JiraUser>(users);
    }

    public void setUsers(List<JiraUser> users) {
        this.users = new ArrayList<JiraUser>(users);
    }

    public boolean isTypesSupported(TaskType type) {
        return types.contains(type.getId());
    }

    public void setTypes(List<String> ids) {
        this.types = new ArrayList<String>(ids);
    }

    public JiraUser getUserById(String id) {
        for (JiraUser user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static class Version {

        private final String id;
        private final String name;

        public Version(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Version other = (Version) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }

    public static class Component {

        private final String id;
        private final String name;

        public Component(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Component other = (Component) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
