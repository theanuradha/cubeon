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
package org.netbeans.cubeon.ui.taskfolder;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.spi.task.TaskElementFilter;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority.PRIORITY;
import org.netbeans.cubeon.ui.TaskExplorerTopComponent;
import org.netbeans.cubeon.ui.filters.PriorityFilter;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha G
 */
public class FilterByPriorityAction extends AbstractAction implements Menu, Popup {

    private PriorityFilter filter;
    private TaskFolder folder;

    public FilterByPriorityAction(TaskFolder folder) {
        this.folder = folder;

        Lookup lookup = Lookup.getDefault();
        //FIXME wormup needed may be Lookup BUG ????
        lookup.lookup(TaskElementFilter.class);
        this.filter = lookup.lookup(PriorityFilter.class);
        putValue(NAME, filter.getName());
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/filterPriority.png")));


    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenu(this);
        JCheckBoxMenuItem p1 = new JCheckBoxMenuItem(new Filter(TaskPriority.PRIORITY.P1));
        p1.setSelected(filter.contains(TaskPriority.PRIORITY.P1));
        menu.add(p1);
        //--------------------------------------------------------------------------------
        JCheckBoxMenuItem p2 = new JCheckBoxMenuItem(new Filter(TaskPriority.PRIORITY.P2));
        p2.setSelected(filter.contains(TaskPriority.PRIORITY.P2));
        menu.add(p2);
        //--------------------------------------------------------------------------------
        JCheckBoxMenuItem p3 = new JCheckBoxMenuItem(new Filter(TaskPriority.PRIORITY.P3));
        p3.setSelected(filter.contains(TaskPriority.PRIORITY.P3));
        menu.add(p3);
        //--------------------------------------------------------------------------------
        JCheckBoxMenuItem p4 = new JCheckBoxMenuItem(new Filter(TaskPriority.PRIORITY.P4));
        p4.setSelected(filter.contains(TaskPriority.PRIORITY.P4));
        menu.add(p4);
        //--------------------------------------------------------------------------------
        JCheckBoxMenuItem p5 = new JCheckBoxMenuItem(new Filter(TaskPriority.PRIORITY.P5));
        p5.setSelected(filter.contains(TaskPriority.PRIORITY.P5));
        menu.add(p5);
        //--------------------------------------------------------------------------------
        return menu;
    }

    public JMenuItem getPopupPresenter() {

        return getMenuPresenter();
    }

    private class Filter extends AbstractAction {

        private TaskPriority.PRIORITY priority;

        public Filter(PRIORITY priority) {
            this.priority = priority;
            putValue(NAME, priority.toString());
            putValue(SMALL_ICON, new ImageIcon(TaskPriority.getImage(priority)));
        }

        public void actionPerformed(ActionEvent e) {
            if (filter.contains(priority)) {
                filter.removeFilter(priority);
            } else {
                filter.addFilter(priority);
            }

            TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshProvider != null;
            refreshProvider.refeshNode();
            if (folder.getParent() == null) {
                //folder.getParent() guess as root
                TaskExplorerTopComponent.findInstance().expand();
            }
        }
    }
}
