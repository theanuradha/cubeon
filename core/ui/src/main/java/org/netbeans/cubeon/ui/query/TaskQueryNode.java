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
package org.netbeans.cubeon.ui.query;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class TaskQueryNode extends AbstractNode {

    private TaskQuery query;

    public TaskQueryNode(TaskQuery query) {
        super(Children.LEAF);
        this.query = query;
        setDisplayName(query.getName());
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
                    new QueryEditAction(query),
                    new DeleteTaskQuery(query),
                    null,
                    new SynchronizeWithAction(query),
                    null,
                    new SynchronizeQuery(query)
                };
    }

    @Override
    public Action getPreferredAction() {
        return new QueryEditAction(query);
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/netbeans/cubeon/ui/query.png");
    }
}
