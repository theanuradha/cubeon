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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.netbeans.cubeon.common.ui.Group;
import org.netbeans.cubeon.common.ui.GroupPanel;

/**
 *
 * @author Anuradha
 */
public class TreeBaseGroupLayout implements GroupEditorLayout {

    private TreeGroupView leftSideView;
    private TreeGroupView rightSideView;

    public void setLeftSideGroups(JPanel container, Group... groups) {
        buildLayout(container, leftSideView, groups);
    }

    public void setRightSideGroups(JPanel container, Group... groups) {
        buildLayout(container, rightSideView, groups);
    }

    private void buildLayout(JPanel container, TreeGroupView view, Group... groups) {
        container.removeAll();
        if (groups.length > 0) {
            if (view == null) {
                view = new TreeGroupView();
            } else {
                view.clear();
            }
            for (Group group : groups) {
                view.addGroup(group);
            }
            container.add(view.getComponent(), BorderLayout.CENTER);
            container.setVisible(true);
        } else {
            container.setVisible(false);
        }
    }

    public void setLeftActiveGroup(Group group) {
        setActiveGroup(leftSideView, group);
    }

    public void setRightActiveGroup(Group group) {
        setActiveGroup(rightSideView, group);
    }

    private void setActiveGroup(TreeGroupView view, Group group) {
        if (view != null) {
            GroupPanel groupPanel = view.findGroupPanel(group);
            if (groupPanel != null) {
                view.setActiveGroupPanel(groupPanel);
            }
        }

    }
}
