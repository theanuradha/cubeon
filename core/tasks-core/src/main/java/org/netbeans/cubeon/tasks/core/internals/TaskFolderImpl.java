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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.RefreshableChildren;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.core.api.TasksFileSystem;
import org.netbeans.cubeon.tasks.spi.Notifier.NotifierReference;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQueryEventAdapter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
class TaskFolderImpl implements TaskFolder, TaskFolderRefreshable {

    private String name;
    private String description;
    protected TaskFolder parent;
    protected Node folderNode;
    protected RefreshableChildren folderChildren;
    protected final List<TaskFolderImpl> taskFolders = new ArrayList<TaskFolderImpl>();
    protected final List<TaskElement> taskElements = new ArrayList<TaskElement>();
    private TaskQuery taskQuery;
    private NotifierReference<TaskQueryEventAdapter> notifierReference;
    private boolean highlight;

    protected TaskFolderImpl(TaskFolderImpl parent, String name,
            String description, boolean basic) {
        this.parent = parent;
        this.name = name;
        this.description = description;



        if (!basic) {

            folderChildren = new TaskElementChilren(this);
            folderNode = new TaskFolderNode(this, folderChildren.getChildren());
        }


        registerEventAdapter();
    }

    TaskFolderImpl(TaskFolderImpl parent, String name,
            String description) {
        this(parent, name, description, false);

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskFolder getParent() {
        return parent;
    }

    public boolean rename(String name) {
        this.name = name;
        folderNode.setDisplayName(highlight ? "<html><b>" + name+ "</b></html>" : name);
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
        folderNode.setShortDescription(description);
    }

    public Lookup getLookup() {
        /**
         * lookup contain Node implementation , TaskFolderOparations,RefreshProvider
         */
        return Lookups.fixed(folderNode, this);
    }

    public TaskFolder addNewFolder(TaskFolder folder) {
        TaskFolderImpl impl = folder.getLookup().lookup(TaskFolderImpl.class);
        assert impl != null;
        taskFolders.add(impl);
        impl.parent = this;
        return impl;
    }

    public boolean removeFolder(TaskFolder folder) {

        TaskFolderImpl folderImpl = folder.getLookup().lookup(TaskFolderImpl.class);

        taskFolders.remove(folderImpl);
        return true;


    }

    public List<TaskFolder> getSubFolders() {

        return new ArrayList<TaskFolder>(taskFolders);
    }

    TaskElement addTaskElement(TaskElement element) {

        taskElements.add(element);
        return element;
    }

    boolean removeTaskElement(TaskElement element) {

        taskElements.remove(element);
        return true;
    }

    public List<TaskElement> getTaskElements() {
        return new ArrayList<TaskElement>(taskElements);
    }

    boolean setTaskElements(Collection<? extends TaskElement> c) {

        taskElements.clear();
        return taskElements.addAll(c);
    }

    public void refreshContent() {
        synchronized (this) {

            synchronize();
        }
    }

    public void synchronize() {

        refreshNode();
    }

    public void refreshNode() {
        folderChildren.refreshContent();
    }

    public boolean contains(TaskElement element) {
        return taskElements.contains(element);
    }

    void setAssociateTaskQuery(TaskQuery query, boolean sync) {
        deregisterEventAdapter();
        this.taskQuery = query;
        if (sync) {
            syncWithQuery();
        }
        if (query != null) {
            registerEventAdapter();
        }

        if (folderNode instanceof TaskFolderNode) {
            ((TaskFolderNode) folderNode).refreshIcon();
        }
    }

    public TaskQuery getTaskQuery() {
        return taskQuery;
    }

    private void registerEventAdapter() {
        if (taskQuery != null) {
            notifierReference = taskQuery.getNotifier().add(new TaskQueryEventAdapter() {

                @Override
                public void querySynchronized() {
                    syncWithQuery();
                }

                @Override
                public void removed() {
                    TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                    //remove query
                    fileSystem.associateWithQuery(TaskFolderImpl.this, null);
                }
            });
        }
    }

    private void syncWithQuery() {
        if (taskQuery != null) {

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    assert taskQuery != null;
                    TasksFileSystem fileSystem = Lookup.getDefault().lookup(TasksFileSystem.class);
                    List<TaskElement> taskElements = taskQuery.getTaskElements();
                    for (TaskElement taskElement : taskElements) {
                        if (!contains(taskElement)) {
                            //add task
                            fileSystem.addTaskElement(TaskFolderImpl.this, taskElement);
                        }
                    }
                    //remove task that not coming from query
                    List<TaskElement> current = getTaskElements();
                    current.removeAll(taskElements);
                    for (TaskElement taskElement : current) {
                        fileSystem.removeTaskElement(TaskFolderImpl.this, taskElement);
                    }
                    refreshNode();
                }
            });
        }
    }

    private void deregisterEventAdapter() {
        if (taskQuery != null && notifierReference != null) {

            taskQuery.getNotifier().remove(notifierReference);

        }
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
        if (folderNode != null) {
            folderNode.setDisplayName(highlight ? "<html><b>" + name+"</b></html>" : name);
        }
    }
}
