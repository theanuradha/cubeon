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
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.spi.TaskElementFilter;
import org.netbeans.cubeon.ui.TaskExplorerTopComponent;
import org.netbeans.cubeon.ui.filters.TaskCompletedFilter;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha G
 */
public class FilterCompletedAction extends AbstractAction implements Menu, Popup {

    private TaskCompletedFilter filter;
    private JCheckBoxMenuItem item;
    private TaskFolder folder;

    public FilterCompletedAction(TaskFolder folder) {
        this.folder = folder;
        putValue(NAME, NbBundle.getMessage(FilterCompletedAction.class, "LBL_Filter_Completed"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/cubeon/ui/completed_task.png")));
        Lookup lookup = Lookup.getDefault();
        //FIXME wormup needed may be Lookup BUG ????
        lookup.lookup(TaskElementFilter.class);
        this.filter = lookup.lookup(TaskCompletedFilter.class);
        item = new JCheckBoxMenuItem(this);
        item.setSelected(filter.isEnable());
    }

    public void actionPerformed(ActionEvent e) {
        boolean b = !filter.isEnable();
        filter.setEnable(b);
        item.setSelected(b);
        TaskFolderRefreshable refreshProvider = folder.getLookup().lookup(TaskFolderRefreshable.class);
        assert refreshProvider != null;
        refreshProvider.refeshNode();
        if (folder.getParent() == null) {
            //folder.getParent() guess as root
            TaskExplorerTopComponent.findInstance().expand();
        }
    }

    public JMenuItem getMenuPresenter() {
        item.setSelected(filter.isEnable());
        return item;
    }

    public JMenuItem getPopupPresenter() {
        item.setSelected(filter.isEnable());
        return item;
    }
}
