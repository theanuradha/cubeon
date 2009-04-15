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
package org.netbeans.cubeon.common.ui.internals;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.netbeans.cubeon.common.ui.Group;
import org.netbeans.cubeon.common.ui.GroupPanel;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public abstract class AbstractGroupView {

    private VisualTheme theme = new VisualTheme();
    private GroupPanel activePanel;
    private List<Group> groups = new ArrayList<Group>();
    private List<GroupPanel> groupPanels = new ArrayList<GroupPanel>();

    public abstract JComponent getComponent();

    public VisualTheme getTheme() {
        return theme;
    }

    /**
     * Sets given <code>activePanel</code> as the currently active panel.
     */
    public void setActiveGroupPanel(GroupPanel activePanel) {
        if (this.activePanel != null && this.activePanel != activePanel) {
            this.activePanel.setActive(false);
        }
        this.activePanel = activePanel;
        activePanel.scroll();
        activePanel.setActive(true);
    }

    public void clear() {
        for (Group group : new ArrayList<Group>(groups)) {
            remove(group);
        }
        activePanel = null;

    }

    public void remove(Group group) {
        groups.remove(group);
        GroupPanel findGroupPanel = findGroupPanel(group);
        if (findGroupPanel != null) {
            removeSection(findGroupPanel);
        }
    }

    public void addGroup(Group group) {
        groups.add(group);
        Lookup lookup = Lookups.fixed(this);
        GroupPanel groupPanel = group.createGroupPanel(lookup);
        addSection(groupPanel);
    }

    public void addSection(GroupPanel section) {
        groupPanels.add(section);
    }

    public void removeSection(GroupPanel panel) {
        groupPanels.remove(panel);
    }

    public void collapseAll() {
        for (GroupPanel groupPanel : groupPanels) {
            groupPanel.close();
        }
    }

    public void expandAll() {
        for (GroupPanel groupPanel : groupPanels) {
            groupPanel.open();
        }
    }

    /**
     * @return panel with the given <code>key</code> or null
     * if no matching panel was found.
     */
    public GroupPanel findGroupPanel(Group key) {

        for (GroupPanel groupPanel : groupPanels) {
            if (groupPanel.getGroup().equals(key)) {
                return groupPanel;
            }
        }

        return null;
    }

    public GroupPanel getActiveGroupPanel() {
        return activePanel;
    }
    
    public boolean isFoldable(){
      return false;
    }
}
