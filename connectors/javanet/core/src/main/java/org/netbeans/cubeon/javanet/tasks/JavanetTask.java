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
import java.util.Calendar;
import java.util.List;
import org.kohsuke.jnt.IssueStatus;
import org.kohsuke.jnt.IssueType;
import org.kohsuke.jnt.JNIssue;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskElementChangeAdapter;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetTask implements TaskElement {

    JNIssue _jnIssue = null;
    JavanetTaskRepository _repo = null;
    JavanetTaskElementNotifier _notifier = null;
    JavanetTaskEditorProviderImpl _editorProvider = null;

    public JavanetTask(JavanetTaskRepository repo, JNIssue jnIssue) {
        _jnIssue = jnIssue;
        _repo = repo;
        _notifier = new JavanetTaskElementNotifier(this);
        _editorProvider = new JavanetTaskEditorProviderImpl(this);
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
        if (_jnIssue != null) {
            return _jnIssue.getShortDescription();
        } else {
            return null;
        }
    }

    public String getDescription() {

        if (_jnIssue != null) {
            List<JNIssue.Description> descs =_jnIssue.getDescriptions();
            JNIssue.Description desc = descs.get(0);
            return desc.getText();
        } else {
            return null;
        }
    }

    public String getSubComponent() {
        if (_jnIssue != null) {
            return _jnIssue.getSubComponent();
        } else {
            return null;
        }
    }

    public String getVersion() {
        if (_jnIssue != null) {
            return _jnIssue.getVersion().toString();
        }
        return null;
    }

    public String getMilestone() {
        if (_jnIssue != null) {
            return _jnIssue.getTargetMilestone();
        }
        return null;
    }

    public String getIssueType() {
        if (_jnIssue != null) {
            return _jnIssue.getType().toString();
        }
        return null;
    }

    public String getPriority() {
        if (_jnIssue != null) {
            return _jnIssue.getPriority().toString();
        }
        return null;
    }

    public String getPlatform() {
        return null;
    }

    public JavanetTaskRepository getTaskRepository() {
        return _repo;
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, _repo, _editorProvider, _notifier);
    }

    public boolean isCompleted() {
        if (_jnIssue.getStatus().equals(IssueStatus.RESOLVED)
                || (_jnIssue.getStatus().equals(IssueStatus.CLOSED)
                || (_jnIssue.getStatus().equals(IssueStatus.VERIFIED)))) {
            return true;
        }
        return false;
    }

    public Image getImage() {
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        //FIXME
        if (_jnIssue != null) {
            IssueType type = _jnIssue.getType();

            switch (type) {
                case DEFECT:
                    image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_defact.png"), 0, 0);
                    break;
                case ENHANCEMENT:
                    image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_enhancement.png"), 0, 0);
                    break;
                case FEATURE:
                    image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_feature.png"), 0, 0);
                    break;
                case TASK:
                    image = Utilities.mergeImages(image, Utilities.loadImage("org/netbeans/cubeon/local/bullet_task.png"), 0, 0);
                    break;
            }

        }
        return image;
    }

    public URL getUrl() {
        return null;
//        if (_jnIssue != null) {
//            return _jnIssue.getURL();
//        } else {
//            return null;
//        }
    }

    public boolean isModified() {
        return false;
    }

    public Calendar getCreationDate() {
        return _jnIssue.getCreationDate();
    }

    public Calendar getLastModified() {
        return _jnIssue.getLastModified();
    }

    public String getAssignedTo() {
        return _jnIssue.getAssignedTo();
    }

    public String getStatus() {
        return _jnIssue.getStatus().toString();
    }

    public String getComponent() {
        return _jnIssue.getComponent();
    }

    public void synchronize() {
        
    }

    public Notifier<TaskElementChangeAdapter> getNotifier() {
        return _notifier;
    }


}
