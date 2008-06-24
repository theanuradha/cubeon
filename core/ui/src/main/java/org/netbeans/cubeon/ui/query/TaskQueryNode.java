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
import java.util.ArrayList;
import java.util.List;
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
    private boolean canDelete;

    public TaskQueryNode(TaskQuery query) {
        super(Children.LEAF);
        this.query = query;
        setDisplayName(query.getName());
    }

    public TaskQueryNode(Children children, TaskQuery query, boolean canDelete) {
        super(children);
        this.query = query;
        this.canDelete = canDelete;
        setDisplayName(query.getName());
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ResultsAction(query));
        actions.add(null);
        actions.add(new QueryEditAction(query));
        if (canDelete) {
            actions.add(new DeleteTaskQuery(query));
        }
        actions.add(null);
        actions.add(new SynchronizeWithAction(query));
        actions.add(null);
        actions.add(new SynchronizeQuery(query));

        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return new QueryEditAction(query);
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/netbeans/cubeon/ui/query.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }


}
