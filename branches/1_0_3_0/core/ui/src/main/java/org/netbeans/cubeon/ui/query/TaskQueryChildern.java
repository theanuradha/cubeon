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

import java.util.List;
import org.netbeans.cubeon.tasks.core.api.TagNode;
import org.netbeans.cubeon.tasks.spi.Notifier;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class TaskQueryChildern extends Children.Keys<TaskQuery> {

    private final TaskQuerySupportProvider provider;
    private final TaskRepository repository;
    private final DummyQuery dummyQuery = new DummyQuery();

    public TaskQueryChildern(TaskRepository repository) {
        this.provider = repository.getLookup().lookup(TaskQuerySupportProvider.class);
        assert provider != null;
        this.repository = repository;
    }

    @Override
    protected Node[] createNodes(TaskQuery query) {
        if (dummyQuery.equals(query)) {
            NewQueryWizardAction action = new NewQueryWizardAction(NbBundle.getMessage(TaskQueryChildern.class, "LBL_New_Query"));
            action.preferredRepository(repository);
            return new Node[]{TagNode.createNode(
                        NbBundle.getMessage(TaskQueryChildern.class, "LBL_Query_Information_Name",
                        repository.getName()), NbBundle.getMessage(TaskQueryChildern.class, "LBL_Query_Information_Description",
                        repository.getName()), action, action)
                    };
        }
        return new Node[]{new TaskQueryNode(query)};
    }

    @Override
    protected void addNotify() {
        List<TaskQuery> taskQuerys = provider.getTaskQuerys();
        if (taskQuerys.size() == 0) {
            taskQuerys.add(dummyQuery);
        }

        setKeys(taskQuerys);

    }

    public void refreshNodes() {
        addNotify();
    }

    /**
     * Dummy Query Object
     */
    private class DummyQuery implements TaskQuery {

        public String getId() {
            return null;
        }

        public String getName() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public TaskRepository getTaskRepository() {
            return null;
        }

        public Lookup getLookup() {
            return null;
        }

        public void synchronize() {
        }

        public List<TaskElement> getTaskElements() {
            return null;
        }

        public Notifier<TaskQueryEventAdapter> getNotifier() {
            return new Notifier<TaskQueryEventAdapter>();
        }
    }
}
