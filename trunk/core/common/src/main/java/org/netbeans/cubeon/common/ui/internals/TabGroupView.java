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

import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import org.netbeans.cubeon.common.ui.GroupPanel;

/**
 *
 * @author Anuradha
 */
public class TabGroupView extends AbstractGroupView {

    private final JTabbedPane tabbedPane;
    private Map<GroupPanel, JScrollPane> cache = new HashMap<GroupPanel, JScrollPane>();

    public TabGroupView() {
        Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
        UIManager.put("TabbedPane.contentBorderInsets", oldInsets);

        tabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    @Override
    public void clear() {
        super.clear();
        tabbedPane.removeAll();
        cache.clear();
    }

    @Override
    public JComponent getComponent() {
        return tabbedPane;
    }

    /**
     * Adds a section for this.
     * @param section the section to be added.
     */
    @Override
    public void addSection(GroupPanel section) {
        super.addSection(section);
        JScrollPane scrollPane = new JScrollPane(section.getComponent());
        scrollPane.setBorder(null);
        section.open();
        cache.put(section, scrollPane);
        tabbedPane.add(section.getGroup().getName(), scrollPane);

    }

    @Override
    public void removeSection(GroupPanel section) {
        super.removeSection(section);
        tabbedPane.remove(cache.get(section));
    }

    @Override
    public void setActiveGroupPanel(GroupPanel activePanel) {
        super.setActiveGroupPanel(activePanel);
        tabbedPane.setSelectedComponent(cache.get(activePanel));
    }
}
