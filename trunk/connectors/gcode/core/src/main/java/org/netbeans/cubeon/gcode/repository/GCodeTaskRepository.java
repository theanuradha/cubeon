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
package org.netbeans.cubeon.gcode.repository;

import java.awt.Image;
import java.util.List;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.repository.RepositoryEventAdapter;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class GCodeTaskRepository implements TaskRepository {

    private final String id;
    private String name;
    private String description;
    private String project;
    private String user;
    private String password;
    private final GCodeTaskRepositoryProvider provider;
    private final Lookup lookup;
    private State state = State.INACTIVE;
    private final GCodeRepositoryExtension extension;
    public GCodeTaskRepository(GCodeTaskRepositoryProvider provider,
            String id, String name, String description) {
        this.provider = provider;
        this.id = id;
        this.name = name;
        this.description = description;
        extension = new GCodeRepositoryExtension(this);



        lookup = Lookups.fixed(this,  provider,extension );
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description != null ? description : name;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public Image getImage() {
        return ImageUtilities.loadImage("org/netbeans/cubeon/gcode/gcode-repository.png");
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

    public TaskElement createTaskElement(String summery, String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TaskElement getTaskElementById(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void persist(TaskElement element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void synchronize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public State getState() {
       return state;
    }

    public GCodeRepositoryExtension getNotifier() {
        return extension;
    }

    List<String> getTaskIds() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    FileObject getBaseDir() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void reconnect() throws GCodeException{
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void updateAttributes() throws GCodeException{
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
