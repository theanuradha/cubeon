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
package org.netbeans.cubeon.context.internal;

import org.netbeans.cubeon.context.api.TaskContextManager;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
import org.openide.nodes.Children.Array;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class ContextNodeView implements TaskNodeView {

    private final ContextRootNode rootNode = new ContextRootNode(new Array());

    public String getId() {
        return "CONTEXT_VIEW";//NOI18N
    }

    public String getName() {
        return NbBundle.getMessage(TaskContextManager.class, "LBL_ContextView");
    }

    public String getDescription() {
        return NbBundle.getMessage(TaskContextManager.class, "LBL_ContextView_Description");
    }

    public Node getRootContext() {
        rootNode.refresh();
        return rootNode;
    }
}
