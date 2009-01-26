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
package org.netbeans.cubeon.common.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public abstract class Group {

    private String name;
    private String description;
    private String summary;
    private boolean open = true;
    private Action[] haeaderActions = new Action[0];
    private Action[] toolbarActions = new Action[0];
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        fireChangeEvent();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fireChangeEvent();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        fireChangeEvent();
    }

    public abstract GroupPanel createGroupPanel(Lookup lookup);

    public Action[] getHaeaderActions() {
        return haeaderActions;
    }

    public void setHaeaderActions(Action[] haeaderActions) {
        this.haeaderActions = haeaderActions;
        fireChangeEvent();
    }

    public Action[] getToolbarActions() {
        return toolbarActions;
    }

    public void setToolbarActions(Action[] toolbarActions) {
        this.toolbarActions = toolbarActions;
        fireChangeEvent();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        fireChangeEvent();
    }

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
}
