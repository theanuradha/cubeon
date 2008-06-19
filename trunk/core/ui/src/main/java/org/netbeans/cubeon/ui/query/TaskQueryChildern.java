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

import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha
 */
public class TaskQueryChildern extends Children.Keys<TaskQuery> {

    private TaskQuerySupportProvider provider;

    public TaskQueryChildern(TaskQuerySupportProvider provider) {
        this.provider = provider;
    }

    @Override
    protected Node[] createNodes(TaskQuery query) {
        return new Node[]{new TaskQueryNode(query)};
    }

    @Override
    protected void addNotify() {
        setKeys(provider.getTaskQuerys());
    }
}
