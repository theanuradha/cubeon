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

import java.util.List;
import org.netbeans.cubeon.tasks.spi.TaskFolder;
import org.netbeans.cubeon.tasks.spi.TaskFolderOparations;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class DefaultFolder implements TaskFolder, TaskFolderOparations {

    private String uuid;
    private String name;
    private String description;
    private final TaskFolderNode folderNode;

    public DefaultFolder(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;

        folderNode = new TaskFolderNode(name, description);
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
        folderNode.setDisplayName(name);
    }

    public void setDescription(String description) {
        this.description = description;
        folderNode.setShortDescription(description);
    }

    public Lookup getLookup() {
        /**
         * lookup contain Node implementation and TaskFolderOparations
         */
        return Lookups.fixed(folderNode, this);
    }

    public boolean addFolder(TaskFolder folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeFolder(TaskFolder folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskFolder> getSubFolders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
