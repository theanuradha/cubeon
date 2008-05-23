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
package org.netbeans.cubeon.context.internals;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.context.api.TaskFolder;
import org.netbeans.cubeon.context.api.TaskFolderOparations;
import org.netbeans.cubeon.context.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class TaskFolderImpl implements TaskFolder, TaskFolderOparations {

    private String name;
    private String description;
    private TaskFolder parent;
    private Node folderNode;
    private TaskFolderRefreshable refreshable;
    private PersistenceHandler persistenceHandler;

    TaskFolderImpl(PersistenceHandler persistenceHandler, TaskFolder parent,
            String name, String description) {
        this.persistenceHandler = persistenceHandler;
        this.parent = parent;
        this.name = name;

        this.description = description;
        TaskFolderChildren folderChildren = new TaskFolderChildren(this);
        refreshable = folderChildren;
        folderNode = new TaskFolderNode(this, folderChildren);
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

        //TODO : add vaidations

        this.name = name;
        folderNode.setDisplayName(name);
        return true;


    }

    public void setDescription(String description) {
        this.description = description;
        folderNode.setShortDescription(description);
    }

    public Lookup getLookup() {
        /**
         * lookup contain Node implementation , TaskFolderOparations,RefreshProvider
         */
        return Lookups.fixed(folderNode, this, refreshable);
    }

    public TaskFolder addNewFolder(String name, String description) {
        TaskFolder taskFolder = persistenceHandler.addTaskFolder(this,
                new TaskFolderImpl(persistenceHandler, this, name, description));

        return taskFolder;
    }

    public boolean removeFolder(TaskFolder folder) {
        persistenceHandler.removeTaskFolder(this, folder);
        return true;
    }

    public List<TaskFolder> getSubFolders() {

        //FIXME need cache here  badly 
        return persistenceHandler.getTaskFolders(this);
    }

    public boolean moveTo(TaskFolder parent) {
        //TODO : add vaidations


        return false;
    }

    public boolean copyTo(TaskFolder parent) {
        //TODO : add vaidations


        return false;
    }

    public TaskElement addTaskElement(TaskElement element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeTaskElement(TaskElement element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskElement> getTaskElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void setFolderNode(Node folderNode) {
        this.folderNode = folderNode;
    }

    void setRefreshable(TaskFolderRefreshable refreshable) {
        this.refreshable = refreshable;
    }
}
