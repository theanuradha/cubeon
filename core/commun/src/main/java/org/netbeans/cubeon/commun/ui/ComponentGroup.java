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
package org.netbeans.cubeon.commun.ui;

import javax.swing.JComponent;
import org.netbeans.cubeon.commun.ui.internals.ComponentGroupPanel;
import org.netbeans.cubeon.commun.ui.internals.GroupView;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class ComponentGroup extends Group {

    private JComponent component;

    public ComponentGroup(String name, String description) {
        super(name, description);
    }

    public ComponentGroup(String name, String description, JComponent component) {
        this(name, description);
        this.component = component;
    }

    public ComponentGroup() {
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    @Override
    public GroupPanel createGroupPanel(Lookup lookup) {

        GroupView groupView = lookup.lookup(GroupView.class);
        assert groupView != null;
        ComponentGroupPanel componentGroupPanel = new ComponentGroupPanel(groupView, this);

        return componentGroupPanel;
    }
}
