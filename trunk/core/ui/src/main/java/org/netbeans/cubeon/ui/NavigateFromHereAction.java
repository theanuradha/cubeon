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
package org.netbeans.cubeon.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha
 */
public class NavigateFromHereAction extends AbstractAction {

    private TaskFolder folder;

    public NavigateFromHereAction(TaskFolder folder) {
        this.folder = folder;
        if (isGoInto()) {
            putValue(NAME, "Navigate From Here");
        } else {
            putValue(NAME, "Go Back To Root");
        }
//        Image image = NodeUtils.getTreeFolderIcon(true);
//        Image badge = Utilities.loadImage("org/netbeans/cubeon/ui/goInTo.png");
//        putValue(SMALL_ICON, new ImageIcon(Utilities.mergeImages(image, badge, 0, 0)));

    }

    public void actionPerformed(ActionEvent e) {
        if (isGoInto()) {
            TaskExplorerTopComponent.findInstance().goInto(folder);
            putValue(NAME, "Navigate From Here");
        } else {
            TaskExplorerTopComponent.findInstance().goToRoot();
            putValue(NAME, "Go Back To Root");
        }
    }

    private boolean isGoInto() {
        Node node = TaskExplorerTopComponent.findInstance().getExplorerManager().getRootContext();
        TaskFolder tf = node.getLookup().lookup(TaskFolder.class);
        return !folder.equals(tf);
    }
}
