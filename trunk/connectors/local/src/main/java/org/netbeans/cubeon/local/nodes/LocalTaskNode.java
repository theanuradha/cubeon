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
package org.netbeans.cubeon.local.nodes;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.cubeon.local.LocalTask;
import org.netbeans.cubeon.tasks.spi.TaskBadgeProvider;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskNode extends AbstractNode {

    private final Action openTask = new AbstractAction(
            NbBundle.getMessage(LocalTaskNode.class, "LBL_Open")) {

        public void actionPerformed(ActionEvent e) {

            final TaskElement taskElement = getLookup().lookup(TaskElement.class);
            assert taskElement != null;
            taskElement.open();
        }
    };

    public LocalTaskNode(LocalTask localTask) {
        super(Children.LEAF, Lookups.singleton(localTask));
        setDisplayName(localTask.getName());
        setShortDescription(localTask.getDescription());
    }

    @Override
    public Image getIcon(int arg0) {
        TaskElement taskElement = getLookup().lookup(TaskElement.class);
        Image image = Utilities.loadImage("org/netbeans/cubeon/local/nodes/task.png");
        //badging task element with bages
        Collection<? extends TaskBadgeProvider> badgeProviders =
                Lookup.getDefault().lookupAll(TaskBadgeProvider.class);

        for (TaskBadgeProvider provider : badgeProviders) {
            image = provider.bageTaskIcon(taskElement, image);

        }
        return image;
    }

    @Override
    public Action getPreferredAction() {
        return openTask;
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }
}
