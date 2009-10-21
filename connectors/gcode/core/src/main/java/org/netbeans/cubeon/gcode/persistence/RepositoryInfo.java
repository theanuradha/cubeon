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
package org.netbeans.cubeon.gcode.persistence;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author Anuradha
 */
public class RepositoryInfo implements JSONAware {

    private String id;
    private String name;
    private String description;
    private String project;
    private String user;
    private String password;

    public RepositoryInfo() {
    }

    public RepositoryInfo(String id, String name, String description,
            String project, String user, String password) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.project = project;
        this.user = user;
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        obj.put("description", description);
        obj.put("project", project);
        obj.put("user", user);
        obj.put("password", password);
        return obj.toJSONString();
    }

    public static RepositoryInfo toRepositoryInfo(JSONObject jsonObject) {
        return new RepositoryInfo(
                (String) jsonObject.get("id"),
                (String) jsonObject.get("name"),
                (String) jsonObject.get("description"),
                (String) jsonObject.get("project"),
                (String) jsonObject.get("user"),
                (String) jsonObject.get("password"));
    }
}
