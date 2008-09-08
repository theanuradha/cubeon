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

import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.task.TaskEditorProvider.EditorAttributeHandler;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

/**
 *
 * @author Anuradha
 */
public class TracAttributeHandler implements EditorAttributeHandler{
    private final TracTask task;

    public TracAttributeHandler(TracTask task) {
        this.task = task;
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getShortDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addChangeListener(ChangeListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChangeListener(ChangeListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JComponent[] getComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TaskElement save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
