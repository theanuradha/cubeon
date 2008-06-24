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

import java.awt.Image;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraTaskRepository implements TaskRepository {

    private final JiraTaskRepositoryProvider provider;
    private final String id;
    private String name;
    private String description;
    private String url;
    //----------------------------
    private String userName;
    private String password;
    //----------------------------
    private final JiraRepositoryExtension extension;

    public JiraTaskRepository(JiraTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        extension=new JiraRepositoryExtension(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this,
                provider,extension);
    }

    public Image getImage() {
        return Utilities.loadImage("org/netbeans/cubeon/jira/repository/jira-repository.png");
    }

    public void validate(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public TaskElement createTaskElement() {
        throw new UnsupportedOperationException();
    }

    public List<TaskElement> getTaskElements() {
        throw new UnsupportedOperationException();
    }

    public TaskElement getTaskElementById(String id) {
        throw new UnsupportedOperationException();
    }

    public void persist(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public void reset(TaskElement element) {
        throw new UnsupportedOperationException();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public JiraTaskRepositoryProvider getProvider() {
        return provider;
    }

    public JiraRepositoryExtension getExtension() {
        return extension;
    }



}