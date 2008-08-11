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
package org.netbeans.cubeon.tasks.core.api;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.UIManager;
import org.netbeans.cubeon.tasks.spi.repository.TaskPriorityProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class NodeUtils {



    /**
     * Returns default folder icon as {@link java.awt.Image}. Never returns
     * <code>null</code>.
     *
     * @param opened wheter closed or opened icon should be returned.
     * @return Image
     */
    public static Image getTreeFolderIcon(boolean opened) {
        Image base ;
        Icon baseIcon = UIManager.getIcon(opened ? OPENED_ICON_KEY_UIMANAGER : ICON_KEY_UIMANAGER); // #70263
        if (baseIcon != null) {
            base = Utilities.icon2Image(baseIcon);
        } else {
            base = (Image) UIManager.get(opened ? OPENED_ICON_KEY_UIMANAGER_NB : ICON_KEY_UIMANAGER_NB); // #70263
            if (base == null) { // fallback to our owns
                base = Utilities.loadImage(opened ? OPENED_ICON_PATH : ICON_PATH, true);
            }
        }
        assert base != null;
        return base;
    }
    public static Image incomingBadge(){
      return Utilities.loadImage("org/netbeans/cubeon/tasks/core/incoming.png");
    }

    public static Image outgoingBadge(){
      return Utilities.loadImage("org/netbeans/cubeon/tasks/core/outgoing.png");
    }

    public static Image getTaskPriorityImage(TaskPriority priority) {
        Image badge;
        TaskPriorityProvider provider = priority.getRepository().getLookup().lookup(TaskPriorityProvider.class);
        int indexOf = provider.getTaskPriorities().indexOf(priority);
        switch (indexOf) {
            case 0:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p1.png");
                break;
            case 1:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p2.png");
                break;
            case 2:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p3.gif");
                break;
            case 3:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p4.png");
                break;
            case 4:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p5.png");
                break;

            default:
                badge = Utilities.loadImage("org/netbeans/cubeon/tasks/core/priority/p5.gif");

        }
        return badge;
    }
    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    private static final String ICON_PATH = "org/netbeans/cubeon/tasks/core/defaultFolder.gif"; // NOI18N
    private static final String OPENED_ICON_PATH = "org/netbeans/cubeon/tasks/core/defaultFolderOpen.gif"; // NOI18N
}
