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
import org.netbeans.cubeon.ui.taskfolder.ComparatorAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Anuradha G
 */
public class ResultQueryNode extends AbstractNode {

    private TaskQuery query;
    private volatile Task task;

    public ResultQueryNode(Children children, TaskQuery query) {
        super(children);
        this.query = query;
        setInfomations(query);

    }

    void setInfomations(TaskQuery query) {
        setDisplayName(NbBundle.getMessage(ResultQueryNode.class,
                "LBL_ResultNode_Name", query.getName()));
        setShortDescription(query.getDescription());
    }

    void updateNodeTag(String tag) {
        setDisplayName(NbBundle.getMessage(ResultQueryNode.class,
                "LBL_ResultNode_Name", query.getName(), tag));
    }

    @Override
    public String getHtmlDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new QueryEditAction(query));
        actions.add(new ComparatorAction(new ComparatorAction.ComparatorSupport() {

            public void doCompare() {
                if (task != null && !task.isFinished()) {
                    task.cancel();
                }
                task = RequestProcessor.getDefault().create(new Runnable() {

                    public void run() {
                        getChildren().remove(getChildren().getNodes());
                        ResultsTopComponent.findInstance().loadQueries(getChildren(),
                                ResultQueryNode.this);
                    }
                });
                task.run();

            }
        }));
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
        return ImageUtilities.loadImage(ResultsTopComponent.ICON_PATH);
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }
}