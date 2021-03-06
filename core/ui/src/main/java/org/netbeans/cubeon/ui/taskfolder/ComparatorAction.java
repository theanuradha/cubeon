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
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.cubeon.tasks.spi.task.TaskElementComparator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter.Menu;
import org.openide.util.actions.Presenter.Popup;

/**
 *
 * @author Anuradha G
 */
public class ComparatorAction extends AbstractAction implements Menu, Popup {

    private ComparatorSupport comparatorSupport;

    public ComparatorAction(ComparatorSupport comparatorSupport) {
        this.comparatorSupport = comparatorSupport;
        putValue(NAME, NbBundle.getMessage(ComparatorAction.class, "LBL_Sort_By"));
    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenu(this);
        Collection<? extends TaskElementComparator> collection =
                Lookup.getDefault().lookupAll(TaskElementComparator.class);
        boolean skip = true;
        for (TaskElementComparator tec : collection) {
            if (skip) {
                skip = false;
            } else {
                menu.addSeparator();
            }
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(new Comparator(tec, true));
            item.setSelected(tec.isEnable() && tec.isAscending());
            menu.add(item);
            item = new JCheckBoxMenuItem(new Comparator(tec, false));
            item.setSelected(tec.isEnable() && !tec.isAscending());
            menu.add(item);


        }
        return menu;
    }

    public JMenuItem getPopupPresenter() {

        return getMenuPresenter();
    }

    private class Comparator extends AbstractAction {

        private TaskElementComparator comparator;
        private boolean ascending;

        public Comparator(TaskElementComparator comparator, boolean ascending) {
            this.comparator = comparator;
            this.ascending = ascending;
            if (ascending) {
                putValue(NAME, comparator.getName() + " - " + NbBundle.getMessage(ComparatorAction.class, "LBL_Ascending"));
            } else {
                putValue(NAME, comparator.getName() + " - " + NbBundle.getMessage(ComparatorAction.class, "LBL_Descending"));
            }
        //putValue(SMALL_ICON, new ImageIcon(TaskPriority.getImage(priority)));
        }

        public void actionPerformed(ActionEvent e) {
            if (ascending == comparator.isAscending()) {
                comparator.setEnable(!(comparator.isEnable()));
            } else {
                comparator.setAscending(ascending);
            }
            comparatorSupport.doCompare();
        }
    }

    public static interface ComparatorSupport {

        void doCompare();
    }
}
