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

import org.netbeans.cubeon.common.ui.internals.GroupView;
import org.netbeans.cubeon.common.ui.internals.TaskEditorUI;

/**
 *
 * @author Anuradha
 */
public class TaskEditorSupport {

    private GroupView groupView;

    public TaskEditorSupport(Group... groups) {
        groupView = new GroupView();
        for (Group group : groups) {
            groupView.addGroup(group);
        }
    }

    public void setActive(Group group) {
        GroupPanel groupPanel = groupView.findGroupPanel(group);
        if (groupPanel != null) {
            groupView.setActiveGroupPanel(groupPanel);
        }
    }

    public TaskEditor createEditor() {
        return new TaskEditorUI(groupView);
    }
}
