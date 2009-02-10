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

import org.netbeans.cubeon.common.ui.GroupPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import org.netbeans.cubeon.common.ui.Group;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 * this class is base on org.netbeans.modules.xml.multiview.ui.SectionView
 */
public class GroupView extends JPanel {

    private JPanel scrollPanel, filler;
    private javax.swing.JScrollPane scrollPane;
    private int sectionCount;
    private GroupPanel activePanel;
    private Group[] groups;
    private List<GroupPanel> groupPanels = new ArrayList<GroupPanel>();
    private VisualTheme theme = new VisualTheme();

    public GroupView(Group... groups) {

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        initialize();
        refreshGroups(groups);
    }

    public void refreshGroups(Group... groups) {
        this.groups = groups;
        groupPanels.clear();
        sectionCount = 0;
        filler.removeAll();
        Lookup lookup = Lookups.fixed(this);
        for (Group group : groups) {
            GroupPanel groupPanel = group.createGroupPanel(lookup);
            addSection(groupPanel, group.isOpen());
        }
    }

    void initialize() {
        sectionCount = 0;
        setLayout(new java.awt.BorderLayout());
        scrollPanel = new JPanel();
        scrollPanel.setLayout(new java.awt.GridBagLayout());
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.setViewportView(scrollPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        filler = new JPanel();
        filler.setBackground(theme.getDocumentBackgroundColor());
        add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * Adds a section for this.
     * @param section the section to be added.
     */
    public void addSection(GroupPanel section) {
        groupPanels.add(section);
        scrollPanel.remove(filler);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = sectionCount;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        //gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        scrollPanel.add((JPanel) section, gridBagConstraints);
        section.setIndex(sectionCount);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = sectionCount + 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 2.0;
        //gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 6);
        scrollPanel.add(filler, gridBagConstraints);


        sectionCount++;
    }

    /**
     * Removes given <code>panel</code> and moves up remaining panels.
     */
    public void removeSection(GroupPanel panel) {
        int panelIndex = panel.getIndex();
        scrollPanel.remove((JPanel) panel);

        // the rest components have to be moved up
        java.awt.Component[] components = scrollPanel.getComponents();
        java.util.AbstractList removedPanels = new java.util.ArrayList();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof GroupPanel) {
                GroupPanel pan = (GroupPanel) components[i];
                int index = pan.getIndex();
                if (index > panelIndex) {
                    scrollPanel.remove((JPanel) pan);
                    pan.setIndex(index - 1);
                    removedPanels.add(pan);
                }
            }
        }
        for (int i = 0; i < removedPanels.size(); i++) {
            GroupPanel pan = (GroupPanel) removedPanels.get(i);
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = pan.getIndex();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            //gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
            scrollPanel.add((JPanel) pan, gridBagConstraints);
        }
        groupPanels.remove(panel);
        sectionCount--;
    }

    /**
     * Adds a section for this.
     * @param section the section to be added.
     * @param open indicates whether given <code>section</code>
     * should be opened.
     */
    public void addSection(GroupPanel section, boolean open) {
        addSection(section);
        if (open) {
            section.open();
        }
    }

    /**
     * Opens the panel identified by given <code>key</code>.
     */
    public void openPanel(Group key) {
        if (key != null) {
            GroupPanel panel = findGroupPanel(key);
            if (panel != null) {
                //TODO add open notify
                openParents((JPanel) panel);
                panel.scroll();
                panel.setActive(true);
            }
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

    private void openParents(JPanel panel) {
        javax.swing.JScrollPane scrollP = null;
        GroupPanel parentSection = null;
        java.awt.Container ancestor = panel.getParent();
        while (ancestor != null && scrollP == null) {
            if (ancestor instanceof javax.swing.JScrollPane) {
                scrollP = (javax.swing.JScrollPane) ancestor;
            }
            if (ancestor instanceof GroupPanel) {
                parentSection = (GroupPanel) ancestor;
                parentSection.open();
            }
            ancestor = ancestor.getParent();
        }
    }

    VisualTheme getTheme() {
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

    public GroupPanel getActiveGroupPanel() {
        return activePanel;
    }
}
