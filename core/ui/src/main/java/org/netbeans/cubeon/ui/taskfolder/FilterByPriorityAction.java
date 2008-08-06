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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.CubeonContext;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TaskRepositoryHandler;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElementFilter;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.ui.TaskExplorerTopComponent;
import org.netbeans.cubeon.ui.filters.PriorityFilter;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
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
        CubeonContext context = Lookup.getDefault().lookup(CubeonContext.class);
        TaskRepositoryHandler handler = context.getLookup().lookup(TaskRepositoryHandler.class);
        List<TaskRepository> repositorys = handler.getTaskRepositorys();
        Collections.sort(repositorys, new Comparator<TaskRepository>() {

            public int compare(TaskRepository o1, TaskRepository o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        boolean addSeperator = false;
        for (TaskRepository repository : repositorys) {

            TaskPriorityProvider priorityProvider = repository.getLookup().lookup(TaskPriorityProvider.class);
            if (priorityProvider != null) {
                if (addSeperator) {
                    menu.addSeparator();
                }
                JMenuItem repoMenu = new JMenuItem( NbBundle.getMessage(FilterByPriorityAction.class,
                        "LBL_Priorities",repository.getName() ),
                        new ImageIcon(repository.getImage()));
                menu.add(repoMenu);

                List<TaskPriority> prioritys = priorityProvider.getTaskPrioritys();
                for (TaskPriority taskPriority : prioritys) {
                    JCheckBoxMenuItem p1 = new JCheckBoxMenuItem(new Filter(taskPriority));
                    p1.setSelected(filter.contains(taskPriority));
                    menu.add(p1);
                }
                addSeperator = true;
            }

        }

        return menu;
    }

    public JMenuItem getPopupPresenter() {



        return getMenuPresenter();
    }

    private class Filter extends AbstractAction {

        private TaskPriority priority;

        public Filter(TaskPriority priority) {
            this.priority = priority;
            putValue(NAME, priority.toString());
            putValue(SMALL_ICON, new ImageIcon(NodeUtils.getTaskPriorityImage(priority)));
        }

        public void actionPerformed(ActionEvent e) {
            if (filter.contains(priority)) {
                filter.removeFilter(priority);
            } else {
                filter.addFilter(priority);
            }

            TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
            assert refreshProvider != null;
            refreshProvider.refreshNode();
            if (folder.getParent() == null) {
                //folder.getParent() guess as root
                TaskExplorerTopComponent.findInstance().expand();
            }
        }
    }
}
