/*
 *  Copyright 2008 Tomas Knappek.
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
package org.netbeans.cubeon.javanet.tasks;

import java.awt.Image;
import java.net.URL;
import org.kohsuke.jnt.JNIssue;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTask implements TaskElement {

    JNIssue _jnIssue = null;
    JavanetTaskRepository _repo = null;

    public JavanetTask(JavanetTaskRepository repo, JNIssue jnIssue) {
        _jnIssue = jnIssue;
        _repo = repo;
    }

    public String getId() {
        String ret = null;
        if (_jnIssue != null) {
            ret = Integer.toString(_jnIssue.getId());
        }

        return ret;
    }

    public String getName() {
        if (_jnIssue != null) {
            return _jnIssue.getShortDescription();
        } else {
            return null;
        }
    }

    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JavanetTaskRepository getTaskRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, _repo);
    }

    public boolean isCompleted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Image getImage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isModified() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void synchronize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Notifier<TaskElementChangeAdapter> getNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
