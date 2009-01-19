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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.cubeon.common.ui.internals.ContainerGroupPanel;
import org.netbeans.cubeon.common.ui.internals.GroupView;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class ContainerGroup extends Group {

    private List<Group> groups = new ArrayList<Group>();
    boolean foldable = true;

    public ContainerGroup(String name, String description) {
        super(name, description);
    }

    public ContainerGroup(String name, String description, Group... groups) {
        super(name, description);
        for (Group group : groups) {
            this.groups.add(group);
        }
    }

    public ContainerGroup() {
    }

    public boolean removeGroup(Group o) {
        return groups.remove(o);
    }

    public boolean addAllGroups(Collection<? extends Group> c) {
        return groups.addAll(c);
    }

    public boolean addGroup(Group e) {
        return groups.add(e);
    }

    public List<Group> getGroups() {
        return new ArrayList<Group>(groups);
    }

    public boolean isFoldable() {
        return foldable;
    }

    public void setFoldable(boolean foldable) {
        this.foldable = foldable;
    }

    @Override
    public GroupPanel createGroupPanel(Lookup lookup) {
        
        GroupView groupView = lookup.lookup(GroupView.class);
        assert groupView != null;
        ContainerGroupPanel containerGroupPanel = new ContainerGroupPanel(groupView,this);
        
        return containerGroupPanel;
    }
}
