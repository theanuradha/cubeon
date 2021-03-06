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
package org.netbeans.cubeon.tasks.core.views;

import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.core.spi.TaskNodeView;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class CategorizedTaskNodeView implements TaskNodeView {

    public String getId() {
        return "CategorizedTaskNodeView";//NOI18N
    }

    public String getName() {
        return NbBundle.getMessage(CategorizedTaskNodeView.class, "LBL_Categorized_Name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CategorizedTaskNodeView.class, "LBL_Categorized_Description");
    }

    public Node getRootContext() {

        final TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
        assert fileSystem!=null;
        return fileSystem.getRootTaskFolder().getLookup().lookup(Node.class);
    }
}
