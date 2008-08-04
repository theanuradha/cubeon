/*
 *  Copyright 2008 Cube°n Team.
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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.CubeonContextListener;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.WeakSet;
import org.openide.util.lookup.Lookups;
import static org.netbeans.cubeon.tasks.core.internals.CorePreferences.getPreferences;

/**
 *
 * @author Anuradha G
 */
public class CubeonContextImpl implements CubeonContext {

    private static final String ACTIVE = "active";
    private TaskElement active;
    private final Set<CubeonContextListener> listeners = new HashSet<CubeonContextListener>();
    private final static Logger LOG = Logger.getLogger(CubeonContext.class.getName());

    public CubeonContextImpl() {
        //read activated task from Preferences and activate it
        String activeTag = getPreferences().get(ACTIVE, null);
        if (activeTag != null && activeTag.contains("|")) {
            StringTokenizer tokenizer = new StringTokenizer(activeTag, "|");
            String repo = tokenizer.nextToken();
            TaskRepositoryHandler repositoryHandler =
                    new TaskRepositoryHandlerImpl();
            TaskRepository repository = repositoryHandler.getTaskRepositoryById(repo);
            if (repository != null) {
                setActive(repository.getTaskElementById(tokenizer.nextToken()));
            }
        }

    }

    public Lookup getLookup() {
        return Lookups.fixed(
                /*Task Repository Handler Implementation*/
                new TaskRepositoryHandlerImpl());
    }

    public void setActive(TaskElement task) {
        //read old actived task
        TaskElement oldTask = this.active;

        this.active = task;
        //notify task deactivate
        if (oldTask != null) {
            fireTaskDeactivated(oldTask);
        }
        //notify task activate
        if (task != null) {
            fireTaskActivated(task);
        }
        //persiste activated task
        if (task != null) {
            getPreferences().put(ACTIVE, task.getTaskRepository().getId() + "|" + task.getId());
        } else {
            getPreferences().remove(ACTIVE);
        }
    }

    public final void addContextListener(CubeonContextListener l) {

        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeContextListener(CubeonContextListener l) {

        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    final void fireTaskActivated(TaskElement element) {
        LOG.info(new StringBuffer("Task Activated :").append(element.getDisplayName()).toString());
        Iterator<CubeonContextListener> it;
        synchronized (listeners) {
            it = new HashSet<CubeonContextListener>(listeners).iterator();
        }
        LOG.fine("Listeners :" + listeners.size());
        while (it.hasNext()) {
            it.next().taskActivated(element);
        }

    }

    final void fireTaskDeactivated(TaskElement element) {
        LOG.info(new StringBuffer("Task Deactivated :").append(element.getDisplayName()).toString());
        Iterator<CubeonContextListener> it;
        synchronized (listeners) {
            it = new HashSet<CubeonContextListener>(listeners).iterator();
        }
        LOG.fine("Listeners :" + listeners.size());
        while (it.hasNext()) {
            it.next().taskDeactivated(element);
        }

    }

    public TaskElement getActive() {
        return active;
    }
}
