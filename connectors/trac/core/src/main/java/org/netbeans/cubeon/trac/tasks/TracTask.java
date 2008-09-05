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
package org.netbeans.cubeon.trac.tasks;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.api.Ticket;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TracTask extends Ticket implements TaskElement {

    private TracTaskRepository taskRepository;

    public TracTask(TracTaskRepository taskRepository,
            int ticketId, String summary, String description) {
        super(ticketId, summary, description);
        this.taskRepository = taskRepository;
    }

    public TracTask(int id) {
        super(id);
    }

    public String getId() {
        return String.valueOf(getTicketId());
    }

    public String getName() {
        return getSummary();
    }

    public String getDisplayName() {
        return getId() + " : " + getName();
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public boolean isCompleted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Image getImage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUrlString() {
        return taskRepository.getURL() + "/browse/" + getId();
    }

    public URL getUrl() {

        try {
            return new URL(getUrlString());
        } catch (MalformedURLException ex) {
            //ignore
        }
        return null;
    }

    public void synchronize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
