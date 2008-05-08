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

import java.io.IOException;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.TaskFolder;
import org.netbeans.cubeon.tasks.spi.TaskFolderOparations;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
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
    private FileObject path;
    private TaskFolder parent;
    private final TaskFolderNode folderNode;

    DefaultFolder(DefaultFolder parent, String uuid, String name, FileObject path, String description) {
        this.parent = parent;
        this.uuid = uuid;
        this.name = name;
        this.path = path;
        this.description = description;

        folderNode = new TaskFolderNode(name, description);
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public FileObject getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public TaskFolder getParent() {
        return parent;
    }

    public boolean rename(String name) {
        try {
            //TODO : add vaidations
            path.rename(path.lock(), name, null);
            this.name = name;
            folderNode.setDisplayName(name);
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
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

    public boolean moveTo(TaskFolder parent) {
        //TODO : add vaidations
        FileObject parentPath = parent.getPath();
        try {
            path = FileUtil.moveFile(path, parentPath, name);
            this.parent = parent;
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        }

        return false;
    }

    public boolean copyTo(TaskFolder parent) {
        //TODO : add vaidations
        FileObject parentPath = parent.getPath();
        try {
            path = FileUtil.copyFile(path, parentPath, name);
            this.parent = parent;
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        }

        return false;
    }
}
