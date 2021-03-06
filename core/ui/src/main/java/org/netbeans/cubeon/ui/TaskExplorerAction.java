/*
 *  Copyright 2008 Cube°n Team.
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
package org.netbeans.cubeon.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows TaskExplorer component.
 */
public class TaskExplorerAction extends AbstractAction {

    public TaskExplorerAction() {
        this(NbBundle.getMessage(TaskExplorerAction.class, "CTL_TaskExplorerAction"));
    }

    public TaskExplorerAction(String name) {
        super(name);
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(TaskExplorerTopComponent.ICON_PATH, true)));

    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TaskExplorerTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
