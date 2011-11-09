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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.netbeans.cubeon.common.ui.internals.AbstractGroupView;
import org.netbeans.cubeon.common.ui.internals.ComponentGroupPanel;
import org.netbeans.cubeon.common.ui.internals.TreeGroupView;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class ContainerGroup extends Group {

    private List<Group> groups = new ArrayList<Group>();
    private final TreeGroupView groupView = new TreeGroupView(false);
    private ComponentGroupPanel componentGroupPanel;
    public ContainerGroup(String name, String description) {
        super(name, description);
    }

    public ContainerGroup(String name, String description, Group... groups) {
        super(name, description);
        for (Group group : groups) {
            this.groups.add(group);
            groupView.addGroup(group);
        }
    }

    public ContainerGroup() {
    }

    public void removeGroup(Group o) {
        groups.remove(o);
        groupView.remove(o);
    }

    public void addAllGroups(Collection<? extends Group> c) {
        groups.addAll(c);
        for (Group group : c) {
            groupView.remove(group);
        }
    }

    public void addGroup(Group e) {
         groups.add(e);
         groupView.addGroup(e);
    }

    public List<Group> getGroups() {
        return new ArrayList<Group>(groups);
    }

    public void clearGroups() {
        groups.clear();
        groupView.clear();
    }

    @Override
    public Action[] getToolbarActions() {
        List<Action> toolbarActionList = new ArrayList();
        for (Action action : super.getToolbarActions()) {
            toolbarActionList.add(action);
        }
        Action expand= new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
               
               groupView.expandAll();
               if(componentGroupPanel!=null){
                   componentGroupPanel.open();
               }
            }
        };
        expand.putValue(AbstractAction.NAME,
                NbBundle.getMessage(TreeGroupView.class, "LBL_Expand_All"));
        expand.putValue(AbstractAction.SMALL_ICON,
                new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/internals/expandTree.png")));
        toolbarActionList.add(expand);
        Action collapse=new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                
               groupView.collapseAll();
               if(componentGroupPanel!=null){
                   componentGroupPanel.close();
               }
            }
        };
        collapse.putValue(AbstractAction.NAME,
                NbBundle.getMessage(TreeGroupView.class, "LBL_Collapse_All"));
        collapse.putValue(AbstractAction.SMALL_ICON,
                new ImageIcon(ImageUtilities.loadImage("org/netbeans/cubeon/common/ui/internals/colapseTree.png")));
        toolbarActionList.add(collapse);

        return toolbarActionList.toArray(new Action[toolbarActionList.size()]);
    }



    @Override
    public GroupPanel createGroupPanel(Lookup lookup) {

        AbstractGroupView gv = lookup.lookup(AbstractGroupView.class);
        assert gv != null;
        componentGroupPanel = new ComponentGroupPanel(gv, this);

        return componentGroupPanel;
    }

    @Override
    public JComponent getComponent() {
        return groupView.getComponent();
    }
}
